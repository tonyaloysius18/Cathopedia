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

/** files/nuncios/nuncios.json: every envoy of the Holy See and the posts he holds, built by tools/nuncios/build_nuncios.py. */
@Serializable
data class NunciosFile(
    val schemaVersion: Int = 1,
    /** ISO-8601 instant; the newer of the bundled, cached and remote copies wins. */
    val updated: String = "",
    /** Bump when the photos are re-edited, so installs fetch the new ones instead of the bundled copies. */
    val photosVersion: Int = 1,
    /** English place name (a country, region or organisation) → its name in each app language. */
    val places: Map<String, Map<String, String>> = emptyMap(),
    val envoys: List<Envoy> = emptyList(),
    /** Posts with no envoy named today. */
    val vacant: List<NunciaturePost> = emptyList(),
)

@Serializable
data class Envoy(
    val id: String,
    val name: String,
    val born: String = "",
    /** English country name; a key of [NunciosFile.places]. */
    val nationality: String = "",
    val wikipedia: String = "",
    /** The post he lives at comes first, then the countries he is also accredited to. */
    val posts: List<NunciaturePost> = emptyList(),
    val photo: CardinalPhoto? = null,
) {
    val birthDate: LocalDate? get() = runCatching { LocalDate.parse(born) }.getOrNull()
    fun age(today: LocalDate): Int? = birthDate?.yearsUntil(today)

    /** A nuncio to at least one state, as opposed to a delegate, observer or other representative. */
    val isNuncio: Boolean get() = posts.any { it.kind == "nunciature" || it.kind == "nunciature_nonres" }
}

@Serializable
data class NunciaturePost(
    /** English place name; a key of [NunciosFile.places]. */
    val place: String,
    /** nunciature, nunciature_nonres, delegation, observer, representative or pontifical_representative. */
    val kind: String,
    /** Date of the appointment, or of the vacancy for a vacant post. */
    val since: String = "",
    /** Where a delegation is seated, when it is outside the territory it serves. */
    val seat: String? = null,
    /** "charge_d_affaires" where a vacant nunciature is run by a chargé d'affaires (Taipei). */
    val headedBy: String? = null,
) {
    val sinceDate: LocalDate? get() = runCatching { LocalDate.parse(since) }.getOrNull()
}

/**
 * The apostolic nuncios list. Like the cardinals list it ships in the app and is also read from
 * the public repo, so a new appointment only needs an edit merged to main. The photos are bundled;
 * a later edition with a higher photosVersion, or an envoy the bundle lacks, downloads them.
 */
object ApostolicNuncios {
    private const val FILE_PATH = "files/nuncios/nuncios.json"
    private const val REMOTE_FILE = RemoteCache.REPO_RAW + "shared/src/commonMain/composeResources/files/nuncios/nuncios.json"
    private const val PHOTO_DIR = "shared/src/commonMain/composeResources/files/nuncios/photos/"
    private const val BUNDLED_PHOTO_DIR = "files/nuncios/photos/"
    private const val CACHE_KEY = "nuncios_feed_cache"

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var refreshed = false

    var data by mutableStateOf<NunciosFile?>(null)
        private set

    fun place(name: String, language: String): String =
        data?.places?.get(name)?.let { it[language] ?: it["en"] } ?: name

    @OptIn(ExperimentalResourceApi::class)
    suspend fun photo(envoy: Envoy): ByteArray? {
        val path = envoy.photo?.path ?: return null
        val version = data?.photosVersion ?: 1
        val bundledBytes = if (version <= bundledPhotosVersion()) {
            runCatching { Res.readBytes(BUNDLED_PHOTO_DIR + path) }.getOrNull()
        } else null
        return bundledBytes ?: RemoteCache.bytes(PHOTO_DIR + path, cacheName = "nuncios/v$version/$path")
    }

    private var bundledVersion: Int? = null
    private suspend fun bundledPhotosVersion(): Int =
        bundledVersion ?: (bundled()?.photosVersion ?: 0).also { bundledVersion = it }

    suspend fun load(repository: CathopediaRepository) {
        mutex.withLock {
            if (data == null) data = listOfNotNull(bundled(), cached(repository)).maxByOrNull { it.updated } ?: NunciosFile()
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
    private suspend fun bundled(): NunciosFile? =
        runCatching { Res.readBytes(FILE_PATH).decodeToString() }.getOrNull()?.let(::parse)

    private fun cached(repository: CathopediaRepository): NunciosFile? =
        repository.getPreference(CACHE_KEY)?.let(::parse)

    private fun parse(text: String): NunciosFile? =
        runCatching { json.decodeFromString<NunciosFile>(text) }.getOrNull()
            ?.takeIf { it.schemaVersion == 1 && it.envoys.isNotEmpty() }
}
