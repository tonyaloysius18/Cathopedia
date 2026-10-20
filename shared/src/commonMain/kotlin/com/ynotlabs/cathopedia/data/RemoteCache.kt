package com.ynotlabs.cathopedia.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

/**
 * Files published in the public repo but not shipped in the app (the
 * cardinals' photos): fetched from `main` on first use and kept in app
 * storage, so each one downloads once and then works offline.
 */
object RemoteCache {
    /** Raw files on main; anything merged there is live for every install. */
    const val REPO_RAW = "https://raw.githubusercontent.com/tonyaloysius18/Cathopedia/main/"

    /** App storage folder for cached files; set by the platform at launch. */
    var directory: String? = null

    private val memory = mutableMapOf<String, ByteArray>()
    // A full list scrolls past dozens of photos; keep a few downloads at a time.
    private val downloads = Semaphore(4)

    /**
     * The bytes of [repoPath] (relative to the repo root), or null offline before it was ever cached.
     * [cacheName] is where the copy is kept; put a version in it to make installs fetch a new edition.
     */
    suspend fun bytes(repoPath: String, cacheName: String = repoPath): ByteArray? {
        memory[cacheName]?.let { return it }
        val file = directory?.let { Path(it, cacheName) }
        val bytes = withContext(Dispatchers.IO) {
            file?.takeIf { SystemFileSystem.exists(it) }?.let { cached ->
                runCatching { SystemFileSystem.source(cached).buffered().use { it.readByteArray() } }.getOrNull()
            } ?: downloads.withPermit { download(repoPath) }?.also { data -> file?.let { save(it, data) } }
        }
        return bytes?.also { memory[cacheName] = it }
    }

    private suspend fun download(repoPath: String): ByteArray? = runCatching {
        val client = HttpClient { install(HttpTimeout) { requestTimeoutMillis = 20_000 } }
        try {
            val response = client.get(REPO_RAW + repoPath)
            if (response.status.isSuccess()) response.readRawBytes() else null
        } finally {
            client.close()
        }
    }.getOrNull()

    private fun save(file: Path, data: ByteArray) {
        runCatching {
            file.parent?.let { SystemFileSystem.createDirectories(it) }
            val part = Path(file.toString() + ".part")
            SystemFileSystem.sink(part).buffered().use { it.write(data) }
            SystemFileSystem.atomicMove(part, file)
        }
    }
}
