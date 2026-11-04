package com.ynotlabs.cathopedia.liturgical

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AelfTextsTest {
    @Test
    fun ourBooksMapToAelfAbbreviations() {
        assertEquals("Sg" to 6, citationKey("Wis 6:12-16"))
        assertEquals("Ct" to 2, citationKey("Sg 2:8-14"))
        assertEquals("1 Co" to 8, citationKey("1 Cor 8:7"))
        assertEquals("Mc" to 1, citationKey("Mk 1:29-39"))
        assertEquals("2 Jn" to 0, citationKey("2 Jn 4-9"))
    }

    @Test
    fun aelfReferenceFormatsAreRead() {
        val psalm = "Ps" to 63
        assertEquals(listOf(psalm), referenceKeys("Ps 62 (63), 2, 3-4", "psaume"))
        assertEquals(listOf("Ps" to 98), referenceKeys("97(98), 1, 2-3ab", "psaume"))
        assertEquals(listOf("Ps" to 19), referenceKeys("18B (19), 8, 9", "psaume"))
        assertEquals(listOf("Ps" to 147), referenceKeys("146 (147A), 1-2", "psaume"))
        assertEquals(listOf("Ps" to 30, "Ps" to 31), referenceKeys("Ps 30, 3bc.4b", "psaume"))
        assertEquals(listOf("2 Co" to 8), referenceKeys("2Co 8, 7.9.13-15", "lecture_2"))
        assertEquals(listOf("Is" to 12), referenceKeys("Isaïe 12, 2, 3", "cantique"))
        assertEquals(listOf("Lc" to 1), referenceKeys("[CANTIQUE] Lc 1, 46b- 47", "cantique"))
        assertEquals(listOf("2 Jn" to 0), referenceKeys("2 Jn 1a. 4-9", "lecture_1"))
    }

    private fun reading(kind: LectionaryKind, citation: String) = LectionaryReading(kind, listOf(LectionaryOption(citation)))

    private val sunday = AelfMassEntry("Messe du jour", listOf(
        AelfLecture("lecture_1", "Sg 6, 12-16", contenu = "<p>La Sagesse</p>"),
        AelfLecture("psaume", "Ps 62 (63), 2", contenu = "<p>Dieu</p>", refrain_psalmique = "<p><strong>Mon âme</strong></p>"),
        AelfLecture("lecture_2", "1 Th 4, 13-18", contenu = "<p>Frères, version longue</p>"),
        AelfLecture("lecture_2", "1 Th 4, 13-14", contenu = "<p>Frères</p>"),
        AelfLecture("evangile", "Mt 25, 1-13", contenu = "<p>En ce temps-là</p>", verset_evangile = "<p>Alléluia.</p>"),
    ))

    @Test
    fun alignedMassGivesEveryPart() {
        val mass = assertNotNull(matchMass(listOf(sunday), listOf(
            reading(LectionaryKind.FIRST_READING, "Wis 6:12-16"), reading(LectionaryKind.GOSPEL, "Mt 25:1-13"))))
        assertEquals("La Sagesse", mass.passage(LectionaryKind.FIRST_READING, "Wis 6:12-16", false)?.text)
        // A psalm numbered differently from ours is still this Mass's psalm.
        assertEquals("Mon âme", mass.passage(LectionaryKind.PSALM, "Ps 64:1", false)?.refrain)
        assertEquals("Alléluia.", mass.passage(LectionaryKind.ACCLAMATION, "Mt 24:42", false)?.text)
        assertEquals("Frères, version longue", mass.passage(LectionaryKind.SECOND_READING, "1 Thes 4:13-18", false)?.text)
        assertEquals("Frères", mass.passage(LectionaryKind.SECOND_READING, "1 Thes 4:13-14", true)?.text)
    }

    @Test
    fun aDifferentCelebrationIsNeverShown() {
        assertNull(matchMass(listOf(sunday), listOf(
            reading(LectionaryKind.FIRST_READING, "Is 1:1"), reading(LectionaryKind.GOSPEL, "Jn 3:16"))))
        // Same Gospel but a different first reading: only what matches by reference is used.
        val partial = assertNotNull(matchMass(listOf(sunday), listOf(
            reading(LectionaryKind.FIRST_READING, "Is 1:1"), reading(LectionaryKind.GOSPEL, "Mt 25:1-13"))))
        assertNull(partial.passage(LectionaryKind.FIRST_READING, "Is 1:1", false))
        assertNull(partial.passage(LectionaryKind.PSALM, "Ps 64:1", false))
        assertNull(partial.passage(LectionaryKind.ACCLAMATION, "Mt 24:42", false))
    }

    @Test
    fun htmlBecomesPlainText() {
        assertEquals(
            "Dieu, viens à mon aide,\nSeigneur.\n\n« Gloire » l’an",
            aelfHtmlToText("<p>Dieu, viens à mon aide,\n   <br />Seigneur.</p>\n<p><span class=\"verse_number\">1</span> &laquo;&nbsp;Gloire&nbsp;&raquo; l&#8217;an</p>"),
        )
        assertEquals("", aelfHtmlToText(null))
    }

    @Test
    fun hoursKeepTheirOrderAndSkipEmptyParts() {
        val hour = AelfTexts.parseHour(
            """{"informations":{"jour_liturgique_nom":"Mardi"},"complies":{"introduction":"<p>V/ Dieu</p>",
            "hymne":{"auteur":"P. Griolet","editeur":"Levain","titre":"Vienne la nuit","texte":"<p>R/ Vienne</p>"},
            "antienne_2":null,"psaume_2":[],"psaume_1":{"reference":"4","texte":"<p>Quand je crie</p>"},"notre_pere":"Notre Père","oraison":"<p>Seigneur</p>"}}""",
            "complies",
        )
        assertEquals("Mardi", hour.dayName)
        assertEquals(listOf("Introduction", "Vienne la nuit", "Psaume", "Notre Père", "Oraison"), hour.sections.map { it.heading })
        assertEquals("P. Griolet · Levain", hour.sections[1].credit)
        assertTrue(hour.sections[2].reference == "4")
    }
}
