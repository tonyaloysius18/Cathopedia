package prayercontent

import kotlinx.serialization.json.Json
import org.commonmark.parser.Parser
import java.io.File

enum class Severity { ERROR, WARNING }

data class ValidationIssue(
    val file: String,
    val field: String,
    val message: String,
    val severity: Severity,
) {
    override fun toString(): String = "[$file] $field: $message"
}

/**
 * Validates every /content/prayers/<id>.json file. Never authors or repairs
 * content — an empty or missing field is reported, not filled in.
 *
 * `text: {}` (no language sourced yet) is deliberately tolerated as a distinct
 * "not yet sourced" state: a WARNING normally, promoted to an ERROR only when
 * [strict] is set (the CI `--strict` gate). Every other problem is always an
 * ERROR regardless of strict mode.
 */
class PrayerContentValidator(private val contentDir: File) {
    private val json = Json { ignoreUnknownKeys = false }
    private val markdown = Parser.builder().build()

    fun validate(strict: Boolean): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()
        val files = contentDir
            .listFiles { f -> f.isFile && f.extension == "json" }
            ?.sortedBy { it.name }
            .orEmpty()

        val seenIds = mutableMapOf<String, String>()
        val sortOrderByCategory = mutableMapOf<String, MutableMap<Long, String>>()

        for (file in files) {
            val name = file.name
            val parsed = try {
                json.decodeFromString(PrayerContentFile.serializer(), file.readText())
            } catch (e: Exception) {
                issues += ValidationIssue(name, "<root>", "failed to parse JSON: ${e.message}", Severity.ERROR)
                continue
            }

            if (parsed.id != file.nameWithoutExtension) {
                issues += ValidationIssue(
                    name, "id",
                    "id '${parsed.id}' does not match filename (expected '${file.nameWithoutExtension}')",
                    Severity.ERROR,
                )
            }

            val previousFile = seenIds.putIfAbsent(parsed.id, name)
            if (previousFile != null) {
                issues += ValidationIssue(name, "id", "duplicate id '${parsed.id}', also used in $previousFile", Severity.ERROR)
            }

            if (parsed.category !in KNOWN_PRAYER_CATEGORIES) {
                issues += ValidationIssue(
                    name, "category",
                    "unknown category '${parsed.category}' — must be one of ${KNOWN_PRAYER_CATEGORIES.sorted()}",
                    Severity.ERROR,
                )
            } else {
                val bucket = sortOrderByCategory.getOrPut(parsed.category) { mutableMapOf() }
                val previousInCategory = bucket.putIfAbsent(parsed.sortOrder, name)
                if (previousInCategory != null) {
                    issues += ValidationIssue(
                        name, "sortOrder",
                        "sortOrder ${parsed.sortOrder} collides with $previousInCategory within category '${parsed.category}'",
                        Severity.ERROR,
                    )
                }
            }

            if (parsed.isSequence && parsed.id !in KNOWN_SEQUENCE_SLUGS) {
                issues += ValidationIssue(
                    name, "isSequence",
                    "isSequence=true on '${parsed.id}', which is not in the known sequence list $KNOWN_SEQUENCE_SLUGS",
                    Severity.ERROR,
                )
            }

            if (parsed.text.isEmpty()) {
                issues += ValidationIssue(
                    name, "text",
                    "no language sourced yet",
                    if (strict) Severity.ERROR else Severity.WARNING,
                )
                continue
            }

            for (lang in REQUIRED_LANGUAGES) {
                if (lang !in parsed.text) {
                    issues += ValidationIssue(name, "text.$lang", "missing — a prayer must have both en and fr to ship", Severity.ERROR)
                }
            }

            for ((lang, text) in parsed.text) {
                if (text.title.isBlank()) {
                    issues += ValidationIssue(name, "text.$lang.title", "title is blank", Severity.ERROR)
                }
                if (text.bodyMd.isBlank()) {
                    issues += ValidationIssue(name, "text.$lang.bodyMd", "bodyMd is blank", Severity.ERROR)
                } else {
                    try {
                        markdown.parse(text.bodyMd)
                    } catch (e: Exception) {
                        issues += ValidationIssue(name, "text.$lang.bodyMd", "markdown failed to parse: ${e.message}", Severity.ERROR)
                    }
                }
                if (text.source.isBlank()) {
                    issues += ValidationIssue(name, "text.$lang.source", "source is missing or empty", Severity.ERROR)
                }
            }
        }

        return issues
    }
}
