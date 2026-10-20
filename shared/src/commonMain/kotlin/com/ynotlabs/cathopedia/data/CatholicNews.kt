package com.ynotlabs.cathopedia.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ynotlabs.cathopedia.resources.Res
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

/** files/news/news.json: the curated Catholic News & Events feed. */
@Serializable
data class NewsFeed(
    val schemaVersion: Int = 1,
    /** ISO-8601 instant; the newer of the bundled, cached and remote copies wins. */
    val updated: String = "",
    val items: List<NewsItem> = emptyList(),
)

/** Text fields are keyed by language code and fall back to English. */
@Serializable
data class NewsItem(
    val id: String,
    /** "news" or "event". */
    val kind: String = NEWS,
    /** Picks the card's colours and glyph; see NewsTheme. */
    val theme: String = "default",
    val pinned: Boolean = false,
    val published: String,
    val start: String? = null,
    val end: String? = null,
    /** Last day the item is shown; after it the item drops out of the app. */
    val expires: String? = null,
    /** Our own art: a path under files/news/ (bundled or fetched beside the feed). */
    val image: String? = null,
    /** Credit for [image]: shown on the detail page as "Photo: author · licence · Wikimedia Commons". */
    val imageAuthor: String = "",
    val imageLicense: String = "",
    val imageSource: String = "",
    /**
     * An official logo shown on a white tile, only with the owner's consent.
     * Kept in the feed (not the code) so it can be withdrawn from every install.
     */
    val badge: String? = null,
    /** Credit line for [badge], e.g. "Logo © WYD Seoul 2027 Foundation". */
    val badgeCredit: String = "",
    val source: String = "",
    val url: Map<String, String> = emptyMap(),
    val title: Map<String, String> = emptyMap(),
    val location: Map<String, String> = emptyMap(),
    val summary: Map<String, String> = emptyMap(),
    /** A short line shown large on the detail banner, e.g. the event's theme verse. */
    val tagline: Map<String, String> = emptyMap(),
    val body: Map<String, String> = emptyMap(),
) {
    val isEvent: Boolean get() = kind == EVENT
    val publishedDate: LocalDate? get() = published.toDateOrNull()
    val startDate: LocalDate? get() = start?.toDateOrNull()
    val endDate: LocalDate? get() = (end ?: start)?.toDateOrNull()

    fun title(language: String) = title.localized(language)
    fun location(language: String) = location.localized(language)
    fun summary(language: String) = summary.localized(language)
    fun tagline(language: String) = tagline.localized(language)
    fun paragraphs(language: String) = body.localized(language).split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
    fun url(language: String) = url.localized(language).ifEmpty { null }

    fun isVisibleOn(today: LocalDate): Boolean {
        val lastDay = expires?.toDateOrNull() ?: return true
        return today <= lastDay
    }

    companion object {
        const val NEWS = "news"
        const val EVENT = "event"
    }
}

private fun Map<String, String>.localized(language: String): String =
    this[language]?.takeIf { it.isNotBlank() } ?: this["en"].orEmpty()

private fun String.toDateOrNull(): LocalDate? = runCatching { LocalDate.parse(this) }.getOrNull()

/**
 * The news feed is curated by hand, not scraped: one JSON file that ships in
 * the app and is also read from the public repo, so an edit merged to main
 * reaches every install without a release. The last good remote copy is kept
 * in preferences for offline use; a copy is only taken if it is newer.
 */
object CatholicNews {
    private const val FEED_PATH = "files/news/news.json"
    private const val REMOTE_BASE =
        "https://raw.githubusercontent.com/tonyaloysius18/Cathopedia/main/shared/src/commonMain/composeResources/files/news/"
    private const val CACHE_KEY = "news_feed_cache"

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var refreshed = false

    /** The current feed; null until [load] has run once. */
    var feed by mutableStateOf<NewsFeed?>(null)
        private set

    /** Items to show on [today]: pinned first (soonest first), then most recent. */
    fun visibleItems(today: LocalDate): List<NewsItem> =
        feed?.items.orEmpty()
            .filter { it.isVisibleOn(today) }
            .sortedWith(compareByDescending<NewsItem> { it.pinned }
                // Among pinned items, the soonest event first.
                .thenBy { if (it.pinned) it.start ?: it.published else "" }
                .thenByDescending { it.published })

    fun item(id: String): NewsItem? = feed?.items?.firstOrNull { it.id == id }

    private val images = mutableMapOf<String, ByteArray>()

    /**
     * The art for an item: bundled when it shipped with this build, otherwise
     * fetched from beside the remote feed (kept in memory for the session).
     * Null offline or when the art does not exist yet; cards then draw their theme.
     */
    @OptIn(ExperimentalResourceApi::class)
    suspend fun image(path: String): ByteArray? {
        images[path]?.let { return it }
        val bytes = runCatching { Res.readBytes("files/news/$path") }.getOrNull()
            ?: withContext(Dispatchers.IO) {
                runCatching {
                    val client = HttpClient { install(HttpTimeout) { requestTimeoutMillis = 15_000 } }
                    try {
                        val response = client.get(REMOTE_BASE + path)
                        if (response.status.isSuccess()) response.readRawBytes() else null
                    } finally {
                        client.close()
                    }
                }.getOrNull()
            }
        return bytes?.also { images[path] = it }
    }

    /** Loads the bundled and cached copies, then refreshes from the repo once per launch. */
    suspend fun load(repository: CathopediaRepository) {
        mutex.withLock {
            if (feed == null) feed = newer(bundled(), cached(repository))
        }
        refresh(repository)
    }

    private suspend fun refresh(repository: CathopediaRepository) = mutex.withLock {
        if (refreshed) return@withLock
        refreshed = true
        val text = withContext(Dispatchers.IO) {
            runCatching {
                val client = HttpClient { install(HttpTimeout) { requestTimeoutMillis = 10_000 } }
                try {
                    val response = client.get(REMOTE_BASE + "news.json")
                    if (response.status.isSuccess()) response.bodyAsText() else null
                } finally {
                    client.close()
                }
            }.getOrNull()
        } ?: return@withLock
        val remote = parse(text) ?: return@withLock
        if (remote.updated > feed?.updated.orEmpty()) {
            feed = remote
            repository.setPreference(CACHE_KEY, text)
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun bundled(): NewsFeed? =
        runCatching { Res.readBytes(FEED_PATH).decodeToString() }.getOrNull()?.let(::parse)

    private fun cached(repository: CathopediaRepository): NewsFeed? =
        repository.getPreference(CACHE_KEY)?.let(::parse)

    private fun parse(text: String): NewsFeed? =
        runCatching { json.decodeFromString<NewsFeed>(text) }.getOrNull()?.takeIf { it.schemaVersion == 1 }

    private fun newer(a: NewsFeed?, b: NewsFeed?): NewsFeed =
        listOfNotNull(a, b).maxByOrNull { it.updated } ?: NewsFeed()
}
