package com.ynotlabs.cathopedia.speech

/**
 * One stretch of a prayer read in a single utterance. [section] and [paragraph]
 * point back at what is on screen so it can be highlighted; paragraph -1 is a
 * section's heading.
 */
data class SpeechUnit(val text: String, val section: Int, val paragraph: Int)

/**
 * Splits a prayer body into (heading, text) sections at its `#` heading lines;
 * a body with no headings is one untitled section. The prayer screen draws one
 * card per section.
 */
fun prayerSections(bodyMd: String): List<Pair<String?, String>> {
    if (!bodyMd.contains("#")) return listOf(null to bodyMd)
    val sections = mutableListOf<Pair<String?, String>>()
    var title: String? = null
    val body = StringBuilder()
    for (line in bodyMd.lines()) {
        if (line.trim().startsWith("#")) {
            if (body.isNotEmpty() || title != null) sections += title to body.toString().trim()
            title = line.trim().trimStart('#').trim()
            body.clear()
        } else {
            body.append(line).append("\n")
        }
    }
    if (body.isNotEmpty() || title != null) sections += title to body.toString().trim()
    return sections
}

/** Splits a section body into paragraphs exactly as PrayerBodyText lays them out. */
fun prayerParagraphs(bodyMd: String): List<String> =
    bodyMd.trim().split(Regex("\n\\s*\n")).filter { it.isNotBlank() }

/**
 * What the read-aloud player says for a prayer: only the prayer itself, i.e.
 * each section's heading and paragraphs (not the prayer's title), with the
 * markdown and the V./R. versicle marks taken out so they aren't read as letters.
 */
fun prayerSpeechScript(sections: List<Pair<String?, String>>): List<SpeechUnit> {
    val units = mutableListOf<SpeechUnit>()
    sections.forEachIndexed { s, (heading, body) ->
        heading?.let(::cleanForSpeech)?.takeIf { it.isNotBlank() }?.let { units += SpeechUnit(it, s, -1) }
        prayerParagraphs(body).forEachIndexed { p, paragraph ->
            val text = paragraph.lines().map(::cleanForSpeech).filter { it.isNotBlank() }.joinToString("\n")
            if (text.isNotBlank()) units += SpeechUnit(text, s, p)
        }
    }
    return units
}

private val VERSICLE_MARK = Regex("^(?:[VR]\\.|[℣℟])\\s*")
private val LIST_MARK = Regex("^(?:[-•]\\s+)")

internal fun cleanForSpeech(line: String): String = line.trim()
    .removePrefix(">").trim()
    .replace(LIST_MARK, "")
    .replace(VERSICLE_MARK, "")
    .replace("**", "")
    .replace("*", "")
    .replace("_", " ")
    .trimStart('#')
    .trim()
