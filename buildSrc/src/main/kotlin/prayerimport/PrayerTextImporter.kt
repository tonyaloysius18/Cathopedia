@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package prayerimport

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import prayercontent.PrayerContentFile
import prayercontent.PrayerLocalizedTextFile
import java.io.File
import java.time.LocalDate

/**
 * Fetches prayer text from Wikisource per /content/sources/wikisource-map.json
 * and writes it into /content/prayers/<slug>.json. Never authors text itself
 * — every character either comes back from [MediaWikiClient] or the entry is
 * left alone. See cathopedia-task-3b-importer.md 3b.2.
 */
class PrayerTextImporter(
    private val prayersDir: File,
    private val sourceMapFile: File,
    private val client: MediaWikiClient,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val prettyJson = Json { prettyPrint = true; prettyPrintIndent = "  " }

    fun loadSourceMap(): SourceMap =
        json.decodeFromString<Map<String, Map<String, SourceMapEntry>>>(sourceMapFile.readText())

    /**
     * Runs the import for every slug/language in the source map. When
     * [onlySlugs] is non-null, restricts to those slugs (used by
     * `verifyPrayerImport`). When [write] is false, computes results without
     * touching /content/prayers (used by verification, which diffs instead
     * of overwriting).
     */
    fun run(onlySlugs: Set<String>? = null, write: Boolean = true): List<ImportResult> {
        val map = loadSourceMap()
        val results = mutableListOf<ImportResult>()

        for ((slug, langs) in map) {
            if (onlySlugs != null && slug !in onlySlugs) continue
            val prayerFile = File(prayersDir, "$slug.json")
            if (!prayerFile.exists()) {
                for (lang in langs.keys) {
                    results += ImportResult(slug, lang, ImportOutcome.FAILED, "no /content/prayers/$slug.json on disk")
                }
                continue
            }

            var prayer = json.decodeFromString(PrayerContentFile.serializer(), prayerFile.readText())
            var changed = false

            for ((lang, entry) in langs) {
                val result = importOne(slug, lang, entry, prayer)
                results += result

                if (result.outcome == ImportOutcome.IMPORTED && write) {
                    val newText = pendingWrites.remove(slug to lang)
                        ?: error("IMPORTED result without pending text for $slug/$lang")
                    prayer = prayer.copy(text = prayer.text + (lang to newText))
                    changed = true
                }
            }

            if (changed && write) {
                prayerFile.writeText(prettyJson.encodeToString(PrayerContentFile.serializer(), prayer))
            }
        }

        return results.sortedWith(compareBy({ it.slug }, { it.lang }))
    }

    // Import results are computed before we know whether the caller wants to
    // persist them (run() needs the converted text twice: once to classify
    // the outcome, once to write it) — stash it here rather than converting twice.
    private val pendingWrites = mutableMapOf<Pair<String, String>, PrayerLocalizedTextFile>()

    private fun importOne(
        slug: String,
        lang: String,
        entry: SourceMapEntry,
        prayer: PrayerContentFile,
    ): ImportResult {
        if (entry.manual) {
            return ImportResult(slug, lang, ImportOutcome.MANUAL)
        }
        if (entry.page.isNullOrBlank()) {
            return ImportResult(slug, lang, ImportOutcome.NO_MAP)
        }

        val existing = prayer.text[lang]
        if (existing != null && !existing.source.contains("Wikisource")) {
            return ImportResult(slug, lang, ImportOutcome.HAND_SOURCED, "existing source: ${existing.source}")
        }

        val wiki = entry.wiki ?: lang
        val fetched = try {
            client.fetch(slug, lang, wiki, entry.page, entry.section)
        } catch (e: WikisourceFetchException) {
            return ImportResult(slug, lang, ImportOutcome.FAILED, e.message)
        }

        val converted = WikitextConverter.convert(fetched.wikitext)
        val markdown = converted.getOrElse { throwable ->
            val failure = (throwable as? ConversionFailureException)?.failure
            val reason = if (failure != null) "${failure.reason}: \"${failure.fragment}\"" else throwable.message
            return ImportResult(slug, lang, ImportOutcome.FAILED, "markup conversion failed — $reason")
        }

        val source = composeSource(entry, wiki, fetched)
        val text = PrayerLocalizedTextFile(
            title = entry.page,
            subtitle = existing?.subtitle,
            bodyMd = markdown.markdown,
            attribution = existing?.attribution,
            source = source,
        )
        pendingWrites[slug to lang] = text
        return ImportResult(slug, lang, ImportOutcome.IMPORTED)
    }

    /** Peeks the converted text for a slug/lang without persisting — for verifyPrayerImport's diff. */
    fun peekConverted(slug: String, lang: String, entry: SourceMapEntry): Result<PrayerLocalizedTextFile> {
        val fetched = try {
            client.fetch(slug, lang, entry.wiki ?: lang, entry.page!!, entry.section)
        } catch (e: WikisourceFetchException) {
            return Result.failure(e)
        }
        val converted = WikitextConverter.convert(fetched.wikitext).getOrElse { return Result.failure(it) }
        return Result.success(
            PrayerLocalizedTextFile(
                title = entry.page,
                subtitle = null,
                bodyMd = converted.markdown,
                attribution = null,
                source = composeSource(entry, entry.wiki ?: lang, fetched),
            ),
        )
    }

    private fun composeSource(entry: SourceMapEntry, wiki: String, fetched: FetchedPage): String {
        val retrievedDate = LocalDate.now().toString()
        val access = "Wikisource ($wiki), \"${fetched.page}\", rev. ${fetched.revid}, retrieved $retrievedDate"
        return if (!entry.edition.isNullOrBlank()) "${entry.edition} — via $access" else access
    }
}
