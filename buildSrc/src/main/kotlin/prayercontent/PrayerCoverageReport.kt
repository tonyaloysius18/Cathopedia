package prayercontent

import kotlinx.serialization.json.Json
import java.io.File

/**
 * Prints a slug × language table: present / missing / empty-source, grouped
 * by category with summary counts — the working checklist for filling in
 * /content/prayers by hand. `la` is optional everywhere, so a missing `la`
 * row is rendered as "-" rather than counted as a gap.
 */
class PrayerCoverageReport(private val contentDir: File) {
    private val json = Json { ignoreUnknownKeys = true }
    private val languages = listOf("en", "fr", "la")

    fun print(out: (String) -> Unit) {
        val files = contentDir
            .listFiles { f -> f.isFile && f.extension == "json" }
            ?.sortedBy { it.name }
            .orEmpty()

        val prayers = files.mapNotNull { file ->
            try {
                json.decodeFromString(PrayerContentFile.serializer(), file.readText())
            } catch (e: Exception) {
                out("!! ${file.name} did not parse (${e.message}) — excluded from coverage, see validatePrayerContent")
                null
            }
        }

        var present = 0
        var missingRequired = 0
        var emptySource = 0
        var unsourced = 0

        for (category in KNOWN_PRAYER_CATEGORIES.sorted()) {
            val inCategory = prayers.filter { it.category == category }.sortedBy { it.sortOrder }
            if (inCategory.isEmpty()) continue

            out("")
            out("== $category (${inCategory.size}) ==")
            for (p in inCategory) {
                if (p.text.isEmpty()) {
                    unsourced++
                    out("  ${p.id.padEnd(34)} not yet sourced")
                    continue
                }
                val cells = languages.joinToString("  ") { lang ->
                    val text = p.text[lang]
                    val state = when {
                        text == null && lang == "la" -> "  -  "
                        text == null -> {
                            if (lang in REQUIRED_LANGUAGES) missingRequired++
                            " MISS"
                        }
                        text.source.isBlank() -> {
                            emptySource++
                            " !SRC"
                        }
                        else -> {
                            present++
                            "  ok "
                        }
                    }
                    "$lang:$state"
                }
                out("  ${p.id.padEnd(34)} $cells")
            }
        }

        out("")
        out(
            "Summary: ${prayers.size} prayers · $present language rows present · " +
                "$missingRequired missing en/fr · $emptySource empty source · $unsourced not yet sourced at all",
        )
    }
}
