package prayerimport

/** Conversion succeeded; [markdown] is the app's rendered markdown subset. */
data class ConversionSuccess(val markdown: String)

/**
 * Conversion hit something the converter doesn't recognise. Per
 * cathopedia-task-3b-importer.md 3b.3, that's a failure for the entry, never
 * something to silently drop or paraphrase — [fragment] is the offending
 * text so a human can decide what to do with it.
 */
data class ConversionFailure(val reason: String, val fragment: String)

/**
 * Wikitext -> the narrow markdown subset PrayerBodyText renders. Deliberately
 * conservative: known, well-defined transforms are applied; anything left
 * over that still looks like markup is reported as a [ConversionFailure]
 * rather than guessed at. See 3b.3 for the exact rule list this implements.
 */
object WikitextConverter {

    private val refBlock = Regex("<ref[^>]*?/>|<ref[^>]*?>.*?</ref>", RegexOption.DOT_MATCHES_ALL)
    private val htmlComment = Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL)
    private val noInclude = Regex("<noinclude>.*?</noinclude>", RegexOption.DOT_MATCHES_ALL)
    private val includeOnlyTags = Regex("</?includeonly>")
    private val categoryLink = Regex("""\[\[Category:[^]]*]]""", RegexOption.IGNORE_CASE)
    private val templateCall = Regex("""\{\{[^{}]*}}""")
    private val pipedLink = Regex("""\[\[([^|\]]+)\|([^]]+)]]""")
    private val bareLink = Regex("""\[\[([^]|]+)]]""")
    private val boldItalic = Regex("'''''(.+?)'''''")
    private val bold = Regex("'''(.+?)'''")
    private val italic = Regex("''(.+?)''")
    private val pageAnchor = Regex("""\{\{Page break[^}]*}}""", RegexOption.IGNORE_CASE)
    private val navRule = Regex("""^-{4,}$""", RegexOption.MULTILINE)
    private val versicleLine = Regex("""^\s*(?:[V℣]\W?\.?|[R℟]\W?\.?)\s*(.*)$""")

    /** Repeat template stripping to absorb one level of nesting; templates deeper than that are reported unrecognised. */
    private const val MAX_TEMPLATE_PASSES = 3

    fun convert(wikitext: String): Result<ConversionSuccess> {
        var text = wikitext

        text = htmlComment.replace(text, "")
        text = noInclude.replace(text, "")
        text = includeOnlyTags.replace(text, "")
        text = refBlock.replace(text, "")
        text = categoryLink.replace(text, "")
        text = pageAnchor.replace(text, "")

        repeat(MAX_TEMPLATE_PASSES) { text = templateCall.replace(text, "") }

        text = pipedLink.replace(text) { it.groupValues[2] }
        text = bareLink.replace(text) { it.groupValues[1] }

        text = boldItalic.replace(text) { "*${it.groupValues[1]}*" }
        text = bold.replace(text) { it.groupValues[1] }
        text = italic.replace(text) { "*${it.groupValues[1]}*" }

        text = navRule.replace(text, "")

        text = text.lines().joinToString("\n") { line ->
            versicleLine.find(line)?.let { m -> "> ${m.groupValues[1].trim()}" } ?: line
        }

        text = normaliseWhitespaceAndQuotes(text)

        val leftover = findUnrecognisedMarkup(text)
        if (leftover != null) {
            return Result.failure(ConversionFailureException(ConversionFailure(leftover.first, leftover.second)))
        }

        return Result.success(ConversionSuccess(text.trim() + "\n"))
    }

    private fun normaliseWhitespaceAndQuotes(input: String): String {
        val collapsedBlankLines = input.replace(Regex("\n{3,}"), "\n\n")
        val trimmedLines = collapsedBlankLines.lines().joinToString("\n") { it.trimEnd() }

        val sb = StringBuilder(trimmedLines.length)
        var prevChar: Char? = null
        for (c in trimmedLines) {
            when (c) {
                '"' -> sb.append(if (prevChar == null || prevChar.isWhitespace()) '“' else '”')
                '\'' -> sb.append(if (prevChar == null || prevChar.isWhitespace()) '‘' else '’')
                else -> sb.append(c)
            }
            prevChar = c
        }
        return sb.toString()
    }

    /** Anything still resembling wikitext markup after the known transforms above is a failure, not a guess. */
    private fun findUnrecognisedMarkup(text: String): Pair<String, String>? {
        val suspiciousPatterns = listOf(
            "double braces (unhandled template)" to Regex("\\{\\{|}}"),
            "double brackets (unhandled link)" to Regex("\\[\\[|]]"),
            "leftover HTML tag" to Regex("</?[a-zA-Z][^>]*>"),
            "leftover wiki heading markup" to Regex("^=+.*=+$", RegexOption.MULTILINE),
            "leftover table markup" to Regex("^\\{\\||^\\|}|^\\|-", RegexOption.MULTILINE),
        )
        for ((reason, pattern) in suspiciousPatterns) {
            val match = pattern.find(text) ?: continue
            val start = maxOf(0, match.range.first - 20)
            val end = minOf(text.length, match.range.last + 1 + 20)
            return reason to text.substring(start, end)
        }
        return null
    }
}

class ConversionFailureException(val failure: ConversionFailure) : Exception(failure.reason)
