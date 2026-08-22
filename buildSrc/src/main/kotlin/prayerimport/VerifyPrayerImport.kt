package prayerimport

import kotlinx.serialization.json.Json
import prayercontent.PrayerContentFile
import java.io.File

sealed class VerifyOutcome {
    data class Match(val slug: String, val lang: String) : VerifyOutcome()
    data class Differs(val slug: String, val lang: String, val diffAt: Int, val expectedContext: String, val actualContext: String) : VerifyOutcome()
    data class NotComparable(val slug: String, val lang: String, val reason: String) : VerifyOutcome()
}

/**
 * The task-3b.5 gate: before the source map gets filled in for real, prove
 * the importer reproduces the three already hand-sourced prayers
 * byte-identically. Never overwrites /content/prayers — this only fetches,
 * converts, and diffs against what's already committed there.
 */
class VerifyPrayerImport(
    private val prayersDir: File,
    private val importer: PrayerTextImporter,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun run(slugs: Set<String>): List<VerifyOutcome> {
        val map = importer.loadSourceMap()
        val results = mutableListOf<VerifyOutcome>()

        for (slug in slugs.sorted()) {
            val prayerFile = File(prayersDir, "$slug.json")
            if (!prayerFile.exists()) {
                results += VerifyOutcome.NotComparable(slug, "*", "no /content/prayers/$slug.json on disk")
                continue
            }
            val committed = json.decodeFromString(PrayerContentFile.serializer(), prayerFile.readText())
            val langs = map[slug]
            if (langs == null) {
                results += VerifyOutcome.NotComparable(slug, "*", "not present in wikisource-map.json")
                continue
            }

            for ((lang, entry) in langs.toSortedMap()) {
                val committedText = committed.text[lang]
                if (committedText == null) {
                    results += VerifyOutcome.NotComparable(slug, lang, "no committed text for this language")
                    continue
                }
                if (entry.manual) {
                    results += VerifyOutcome.NotComparable(slug, lang, "map entry is manual:true — nothing to fetch")
                    continue
                }
                if (entry.page.isNullOrBlank()) {
                    results += VerifyOutcome.NotComparable(slug, lang, "map entry has no page yet (page is blank by design until filled in)")
                    continue
                }

                val fetched = importer.peekConverted(slug, lang, entry)
                val converted = fetched.getOrElse { e ->
                    results += VerifyOutcome.NotComparable(slug, lang, "fetch/convert failed: ${e.message}")
                    continue
                }

                results += diff(slug, lang, expected = committedText.bodyMd, actual = converted.bodyMd)
            }
        }

        return results
    }

    private fun diff(slug: String, lang: String, expected: String, actual: String): VerifyOutcome {
        if (expected == actual) return VerifyOutcome.Match(slug, lang)

        val firstMismatch = (0 until minOf(expected.length, actual.length)).firstOrNull { expected[it] != actual[it] }
            ?: minOf(expected.length, actual.length)

        fun context(s: String, at: Int): String {
            val start = maxOf(0, at - 20)
            val end = minOf(s.length, at + 20)
            return s.substring(start, end).replace("\n", "\\n")
        }

        return VerifyOutcome.Differs(
            slug,
            lang,
            firstMismatch,
            expectedContext = context(expected, firstMismatch) + " (expected len=${expected.length})",
            actualContext = context(actual, firstMismatch) + " (actual len=${actual.length})",
        )
    }
}
