package com.ynotlabs.cathopedia.speech

/**
 * Church Latin for an Italian voice. No platform has a Latin voice, and an Italian
 * voice reading Latin spelling gets the stress and several letters wrong. Italian
 * spelling already matches Church (ecclesiastical) Latin for c, g, sc, gn and qu,
 * so this respells the rest (ae/oe, ti before a vowel, xc, ex + vowel, ch, ph, th,
 * h, j, y) and marks each word's stress, from the lexicon in
 * tools/audio/latin_stress.txt, with Italian accents (Dóminus → Dòminus).
 *
 * tools/audio/generate_prayer_audio.py does exactly the same for the recorded
 * voices; LatinPronunciationTest checks the two agree.
 */
object LatinPronunciation {
    private val WORD = Regex("\\p{L}+")
    private const val VOWEL = "aeiouáéíóú"
    private const val FRONT = "eiéíyý"
    private val DIPHTHONG = Regex("([ao])([eé])")
    private val CH_HARD = Regex("ch(?![$FRONT])")
    private val EX_VOWEL = Regex("^ex(h?)(?=[$VOWEL])")
    private val XC_SOFT = Regex("xc(?=[$FRONT])")
    private val TI_VOWEL = Regex("(?<=[^stx])ti(?=[$VOWEL])")
    private val SILENT_H = Regex("(?<!c)h")
    private val ITALIAN_ACCENTS = mapOf('á' to 'à', 'é' to 'è', 'í' to 'ì', 'ó' to 'ò', 'ú' to 'ù')

    fun respellWord(word: String): String {
        val entry = LATIN_STRESS[word.lowercase()]
        var w: String
        if (entry != null && entry.startsWith("=")) {
            w = entry.drop(1)
        } else {
            w = (entry ?: word).lowercase()
                .replace("æ", "ae").replace("œ", "oe").replace("ë", "e")
            w = DIPHTHONG.replace(w) { if (it.groupValues[2] == "é") "è" else "e" }
            w = w.replace("ph", "f").replace("th", "t").replace("rh", "r")
            w = CH_HARD.replace(w, "c")
            w = EX_VOWEL.replace(w, "egz")
            w = XC_SOFT.replace(w, "ksc")
            w = TI_VOWEL.replace(w, "zi")
            w = SILENT_H.replace(w, "")
            w = w.replace("j", "i").replace("ý", "í").replace("y", "i")
            w = w.replace("gli", "ghli")
            w = w.map { ITALIAN_ACCENTS[it] ?: it }.joinToString("")
        }
        if (word.firstOrNull()?.isUpperCase() == true) w = w.replaceFirstChar { it.uppercase() }
        return w
    }

    /**
     * The respelled text, and for each of its characters the offset in [text] it
     * came from (a respelled word maps to the start of the original word), so the
     * voice's progress can be shown against the Latin on screen.
     */
    fun respell(text: String): Respelled {
        val out = StringBuilder()
        val origin = ArrayList<Int>(text.length + 8)
        var last = 0
        for (match in WORD.findAll(text)) {
            for (i in last until match.range.first) {
                out.append(text[i])
                origin += i
            }
            val spoken = respellWord(match.value)
            out.append(spoken)
            repeat(spoken.length) { origin += match.range.first }
            last = match.range.last + 1
        }
        for (i in last until text.length) {
            out.append(text[i])
            origin += i
        }
        return Respelled(out.toString(), origin)
    }

    class Respelled(val text: String, private val origin: List<Int>) {
        /** The offset in the original text that [spokenOffset] in [text] came from. */
        fun originalOffset(spokenOffset: Int): Int =
            if (origin.isEmpty()) 0 else origin[spokenOffset.coerceIn(0, origin.lastIndex)]
    }
}
