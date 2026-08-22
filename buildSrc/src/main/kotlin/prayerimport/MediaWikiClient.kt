@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package prayerimport

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

class WikisourceFetchException(message: String) : Exception(message)

/**
 * Talks to the MediaWiki Action API on <wiki>.wikisource.org and caches raw
 * responses to /content/sources/fetched/<slug>.<lang>.json. Never called
 * during a normal build — only from the opt-in `importPrayerTexts`/
 * `verifyPrayerImport` Gradle tasks (see cathopedia-task-3b-importer.md,
 * 3b.2). Repeated runs must make zero network calls for an entry that's
 * already cached, unless [refresh] is set.
 */
class MediaWikiClient(
    private val cacheDir: File,
    private val refresh: Boolean = false,
    private val userAgent: String = "CathopediaApp/1.0 (prayer text importer; contact: tony.aloysius18@gmail.com)",
    private val minIntervalMs: Long = 1000L,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val prettyJson = Json { ignoreUnknownKeys = true; prettyPrint = true; prettyPrintIndent = "  " }
    private val http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    private var lastRequestAt = 0L

    /**
     * Returns the cached (or freshly fetched) page. Throws
     * [WikisourceFetchException] with a human-readable reason on any failure
     * — missing page, redirect, disambiguation, or transport error. Callers
     * must record that as a FAILED result, never substitute anything.
     */
    fun fetch(slug: String, lang: String, wiki: String, page: String, section: String?): FetchedPage {
        val cacheFile = File(cacheDir, "$slug.$lang.json")
        if (cacheFile.exists() && !refresh) {
            return json.decodeFromString(FetchedPage.serializer(), cacheFile.readText())
        }

        rateLimit()
        val revision = queryRevision(wiki, page)
        val fetched = FetchedPage(
            slug = slug,
            lang = lang,
            wiki = wiki,
            page = page,
            revid = revision.revid,
            timestamp = revision.timestamp,
            wikitext = revision.wikitext,
            retrievedAt = Instant.now().toString(),
        )

        cacheDir.mkdirs()
        cacheFile.writeText(prettyJson.encodeToString(FetchedPage.serializer(), fetched))
        return fetched
    }

    private fun rateLimit() {
        val elapsed = System.currentTimeMillis() - lastRequestAt
        if (elapsed < minIntervalMs) Thread.sleep(minIntervalMs - elapsed)
        lastRequestAt = System.currentTimeMillis()
    }

    private data class Revision(val revid: Long, val timestamp: String, val wikitext: String)

    private fun queryRevision(wiki: String, page: String): Revision {
        val url = "https://$wiki.wikisource.org/w/api.php" +
            "?action=query&prop=revisions&rvslots=main&rvprop=content|ids|timestamp" +
            "&titles=${urlEncode(page)}&format=json&formatversion=2"

        val request = HttpRequest.newBuilder(URI.create(url))
            .header("User-Agent", userAgent)
            .timeout(Duration.ofSeconds(15))
            .GET()
            .build()

        val response = try {
            http.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            throw WikisourceFetchException("network error fetching $wiki.wikisource.org/$page: ${e.message}")
        }

        if (response.statusCode() != 200) {
            throw WikisourceFetchException("HTTP ${response.statusCode()} fetching $wiki.wikisource.org/$page")
        }

        val root = json.parseToJsonElement(response.body()).jsonObject
        val pages = root["query"]?.jsonObject?.get("pages")?.jsonArray
            ?: throw WikisourceFetchException("unexpected API response shape for '$page' on $wiki.wikisource")

        if (pages.isEmpty()) {
            throw WikisourceFetchException("no page returned for '$page' on $wiki.wikisource")
        }
        val pageObj = pages[0].jsonObject

        if (pageObj.containsKey("missing")) {
            throw WikisourceFetchException("page '$page' does not exist on $wiki.wikisource")
        }
        if (pageObj.containsKey("redirect")) {
            throw WikisourceFetchException(
                "page '$page' on $wiki.wikisource is a redirect — resolve to the target page title in the map, not here",
            )
        }
        if (pageObj["pageprops"]?.jsonObject?.containsKey("disambiguation") == true) {
            throw WikisourceFetchException("page '$page' on $wiki.wikisource is a disambiguation page")
        }

        val revisions = pageObj["revisions"]?.jsonArray
            ?: throw WikisourceFetchException("no revisions for '$page' on $wiki.wikisource")
        if (revisions.isEmpty()) {
            throw WikisourceFetchException("empty revision list for '$page' on $wiki.wikisource")
        }
        val rev = revisions[0].jsonObject
        val revid = rev["revid"]?.jsonPrimitive?.content?.toLongOrNull()
            ?: throw WikisourceFetchException("missing revid for '$page' on $wiki.wikisource")
        val timestamp = rev["timestamp"]?.jsonPrimitive?.content
            ?: throw WikisourceFetchException("missing timestamp for '$page' on $wiki.wikisource")
        val content = rev["slots"]?.jsonObject?.get("main")?.jsonObject?.get("content")?.jsonPrimitive?.content
            ?: throw WikisourceFetchException("missing content slot for '$page' on $wiki.wikisource")

        return Revision(revid, timestamp, content)
    }

    private fun urlEncode(s: String): String = java.net.URLEncoder.encode(s, "UTF-8").replace("+", "%20")
}
