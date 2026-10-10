package com.ynotlabs.cathopedia.speech

import androidx.compose.runtime.mutableStateMapOf
import com.ynotlabs.cathopedia.resources.Res
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.get
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

/** files/prayer_audio/packs.json, shipped in the app: which recorded packs exist and where. */
@Serializable
data class PrayerAudioIndex(val baseUrl: String = "", val packs: Map<String, PackInfo> = emptyMap())

@Serializable
data class PackInfo(val version: Int, val bytes: Long, val voice: String = "")

/** <lang>-<version>.json beside the pack: key -> [offset, length] of each MP3 in the pack. */
@Serializable
private data class PackUnits(val units: Map<String, List<Long>>)

sealed interface PackState {
    data object Missing : PackState
    data class Downloading(val progress: Float) : PackState
    data object Ready : PackState
    data object Failed : PackState
}

/** One recorded part: [length] bytes of MP3 at [offset] inside the pack file at [path]. */
data class AudioSegment(val path: String, val offset: Long, val length: Long)

/**
 * Recorded prayer audio, one pack per language, downloaded on first use from
 * the GitHub release named in packs.json and kept in app storage for offline
 * use. When packs.json lists no pack for a language, that language keeps using
 * the device voice.
 */
object PrayerAudioPacks {
    /** App storage folder for packs; set by the platform before first use. */
    internal var directory: String? = null

    val states = mutableStateMapOf<String, PackState>()

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var index: PrayerAudioIndex? = null
    private val loaded = mutableMapOf<String, Pair<String, Map<String, List<Long>>>>()

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun index(): PrayerAudioIndex = index ?: runCatching {
        json.decodeFromString<PrayerAudioIndex>(Res.readBytes("files/prayer_audio/packs.json").decodeToString())
    }.getOrDefault(PrayerAudioIndex()).also { index = it }

    /** The pack published for [language], or null when it only has the device voice. */
    suspend fun info(language: String): PackInfo? = index().packs[language]

    /** The recording of [text], when that language's pack is loaded and has it. */
    fun segment(language: String, text: String): AudioSegment? {
        val (path, units) = loaded[language] ?: return null
        val range = units[speechKey(language, text)] ?: return null
        return AudioSegment(path, range[0], range[1])
    }

    fun hasPack(language: String): Boolean = language in loaded

    /** True when a pack exists for [language] but is not on the device yet. */
    suspend fun needsDownload(language: String): Boolean {
        if (info(language) == null) return false
        return !loadInstalled(language)
    }

    /** Loads an already-downloaded pack without touching the network. */
    suspend fun loadInstalled(language: String): Boolean = mutex.withLock { loadLocked(language) }

    /**
     * Makes sure [language]'s pack is on the device, downloading it if needed.
     * Returns false when there is no pack or the download failed (offline).
     */
    suspend fun ensure(language: String): Boolean = mutex.withLock {
        if (loadLocked(language)) return@withLock true
        val info = index().packs[language] ?: return@withLock false
        val dir = directory ?: return@withLock false
        val base = "${index().baseUrl.trimEnd('/')}/$language-${info.version}"
        states[language] = PackState.Downloading(0f)
        val ok = runCatching {
            withContext(Dispatchers.IO) {
                SystemFileSystem.createDirectories(Path(dir))
                // Old versions of this language's pack are dropped first.
                SystemFileSystem.list(Path(dir))
                    .filter { it.name.startsWith("$language-") }
                    .forEach { SystemFileSystem.delete(it, mustExist = false) }
                val client = HttpClient()
                try {
                    val unitsResponse = client.get("$base.json")
                    check(unitsResponse.status.isSuccess()) { "index ${unitsResponse.status}" }
                    val unitsText = unitsResponse.bodyAsText()
                    json.decodeFromString<PackUnits>(unitsText)
                    download(client, "$base.pack", Path(dir, "$language-${info.version}.pack.part"), info.bytes) {
                        states[language] = PackState.Downloading(it)
                    }
                    SystemFileSystem.atomicMove(
                        Path(dir, "$language-${info.version}.pack.part"),
                        Path(dir, "$language-${info.version}.pack"),
                    )
                    SystemFileSystem.sink(Path(dir, "$language-${info.version}.json")).buffered().use {
                        it.writeString(unitsText)
                    }
                } finally {
                    client.close()
                }
            }
            loadLocked(language)
        }.getOrDefault(false)
        if (!ok) states[language] = PackState.Failed
        ok
    }

    private suspend fun loadLocked(language: String): Boolean {
        if (language in loaded) return true
        val info = index().packs[language] ?: return false.also { states[language] = PackState.Missing }
        val dir = directory ?: return false
        val pack = Path(dir, "$language-${info.version}.pack")
        val unitsFile = Path(dir, "$language-${info.version}.json")
        val units = withContext(Dispatchers.IO) {
            if (!SystemFileSystem.exists(unitsFile)) return@withContext null
            if (SystemFileSystem.metadataOrNull(pack)?.size != info.bytes) return@withContext null
            runCatching {
                json.decodeFromString<PackUnits>(SystemFileSystem.source(unitsFile).buffered().use { it.readString() })
            }.getOrNull()
        }
        if (units == null) {
            if (states[language] !is PackState.Downloading) states[language] = PackState.Missing
            return false
        }
        loaded[language] = pack.toString() to units.units
        states[language] = PackState.Ready
        return true
    }

    private suspend fun download(
        client: HttpClient,
        url: String,
        target: Path,
        expectedBytes: Long,
        onProgress: (Float) -> Unit,
    ) {
        client.prepareGet(url).execute { response ->
            check(response.status.isSuccess()) { "pack ${response.status}" }
            val total = response.contentLength() ?: expectedBytes
            val channel = response.bodyAsChannel()
            val buffer = ByteArray(64 * 1024)
            var written = 0L
            var lastReported = -1
            SystemFileSystem.sink(target).buffered().use { sink ->
                while (!channel.isClosedForRead) {
                    val read = channel.readAvailable(buffer, 0, buffer.size)
                    if (read <= 0) continue
                    sink.write(buffer, 0, read)
                    written += read
                    val percent = ((written * 100) / total.coerceAtLeast(1)).toInt()
                    if (percent != lastReported) {
                        lastReported = percent
                        onProgress(percent / 100f)
                    }
                }
            }
            check(written == expectedBytes) { "pack size $written != $expectedBytes" }
        }
    }
}
