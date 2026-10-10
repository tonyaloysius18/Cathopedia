package com.ynotlabs.cathopedia.speech

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LatinPronunciationTest {
    @Test
    fun appRespellsLatinExactlyAsTheAudioGenerator() {
        assertTrue(LatinRespellCases.size > 50)
        for ((latin, expected) in LatinRespellCases) {
            assertEquals(expected, LatinPronunciation.respell(latin).text, latin)
        }
    }

    @Test
    fun churchLatinRules() {
        val r = { s: String -> LatinPronunciation.respell(s).text }
        assertEquals("Pater noster, qui es in celis,", r("Pater noster, qui es in caelis,"))
        assertEquals("Ave Marìa, gràzia plena,", r("Ave Maria, gratia plena,"))
        assertEquals("Glòria in ekscèlsis Deo", r("Gloria in excelsis Deo"))
        assertEquals("Fiat mìki", r("Fiat mihi"))
        assertEquals("et in sècula seculòrum", r("et in saecula saeculorum"))
        assertEquals("Cristi", r("Christi"))
    }

    @Test
    fun progressMapsBackToTheLatinOnScreen() {
        val latin = "Ave Maria, gratia plena"
        val spoken = LatinPronunciation.respell(latin)
        // "gràzia" in the respelled text starts where "gratia" starts in the Latin.
        assertEquals(latin.indexOf("gratia"), spoken.originalOffset(spoken.text.indexOf("gràzia") + 3))
        assertEquals(latin.indexOf(","), spoken.originalOffset(spoken.text.indexOf(",")))
    }
}
