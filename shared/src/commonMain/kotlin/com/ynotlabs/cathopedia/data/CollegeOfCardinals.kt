package com.ynotlabs.cathopedia.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ynotlabs.cathopedia.resources.Res
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.yearsUntil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

/** files/cardinals/cardinals.json: every living cardinal, built by tools/cardinals/build_cardinals.py. */
@Serializable
data class CardinalsFile(
    val schemaVersion: Int = 1,
    /** ISO-8601 instant; the newer of the bundled, cached and remote copies wins. */
    val updated: String = "",
    /** Bump when the photos are re-edited, so installs drop their cached copies and fetch the new ones. */
    val photosVersion: Int = 1,
    /** English country name → name in each app language. */
    val countries: Map<String, Map<String, String>> = emptyMap(),
    val cardinals: List<Cardinal> = emptyList(),
)

@Serializable
data class Cardinal(
    val id: String,
    val name: String,
    /** English country name; a key of [CardinalsFile.countries]. */
    val country: String,
    val born: String,
    /** "CB", "CP" or "CD": cardinal bishop, priest or deacon. */
    val order: String,
    val consistory: String,
    /** "francis", "benedict_xvi" or "john_paul_ii". */
    val createdBy: String,
    /** Current office, in English as the Holy See publishes it. */
    val office: String,
    val pressBio: String = "",
    val wikipedia: String = "",
    val photo: CardinalPhoto? = null,
    /** Set when a cardinal under 80 has no vote, e.g. "renounced". */
    val nonElector: String? = null,
) {
    val birthDate: LocalDate? get() = runCatching { LocalDate.parse(born) }.getOrNull()
    val consistoryDate: LocalDate? get() = runCatching { LocalDate.parse(consistory) }.getOrNull()

    fun age(today: LocalDate): Int? = birthDate?.yearsUntil(today)

    /** Electors are the cardinals under 80 (Universi Dominici Gregis 33), so this changes on birthdays. */
    fun isElector(today: LocalDate): Boolean = nonElector == null && (age(today) ?: 80) < 80
}

/** A freely licensed photo from Wikimedia Commons, credited wherever it is shown. */
@Serializable
data class CardinalPhoto(
    val path: String,
    val author: String = "",
    val license: String = "",
    val licenseUrl: String = "",
    val source: String = "",
)

/**
 * The College of Cardinals list. Like the news feed it ships in the app and is
 * also read from the public repo, so a death or a new consistory only needs an
 * edit merged to main. Photos are not bundled: each downloads on first view
 * through [RemoteCache].
 */
object CollegeOfCardinals {
    private const val FILE_PATH = "files/cardinals/cardinals.json"
    private const val REMOTE_FILE =
        RemoteCache.REPO_RAW + "shared/src/commonMain/composeResources/files/cardinals/cardinals.json"
    private const val PHOTO_DIR = "remote/cardinals/photos/"
    private const val CACHE_KEY = "cardinals_feed_cache"

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var refreshed = false

    var data by mutableStateOf<CardinalsFile?>(null)
        private set

    fun country(cardinal: Cardinal, language: String): String =
        data?.countries?.get(cardinal.country)?.let { it[language] ?: it["en"] } ?: cardinal.country

    suspend fun photo(cardinal: Cardinal): ByteArray? = cardinal.photo?.let {
        RemoteCache.bytes(PHOTO_DIR + it.path, cacheName = "cardinals/v${data?.photosVersion ?: 1}/${it.path}")
    }

    suspend fun load(repository: CathopediaRepository) {
        mutex.withLock {
            if (data == null) data = listOfNotNull(bundled(), cached(repository)).maxByOrNull { it.updated } ?: CardinalsFile()
        }
        refresh(repository)
    }

    private suspend fun refresh(repository: CathopediaRepository) = mutex.withLock {
        if (refreshed) return@withLock
        refreshed = true
        val text = withContext(Dispatchers.IO) {
            runCatching {
                val client = HttpClient { install(HttpTimeout) { requestTimeoutMillis = 15_000 } }
                try {
                    val response = client.get(REMOTE_FILE)
                    if (response.status.isSuccess()) response.bodyAsText() else null
                } finally {
                    client.close()
                }
            }.getOrNull()
        } ?: return@withLock
        val remote = parse(text) ?: return@withLock
        if (remote.updated > data?.updated.orEmpty()) {
            data = remote
            repository.setPreference(CACHE_KEY, text)
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun bundled(): CardinalsFile? =
        runCatching { Res.readBytes(FILE_PATH).decodeToString() }.getOrNull()?.let(::parse)

    private fun cached(repository: CathopediaRepository): CardinalsFile? =
        repository.getPreference(CACHE_KEY)?.let(::parse)

    private fun parse(text: String): CardinalsFile? =
        runCatching { json.decodeFromString<CardinalsFile>(text) }.getOrNull()
            ?.takeIf { it.schemaVersion == 1 && it.cardinals.isNotEmpty() }
}
