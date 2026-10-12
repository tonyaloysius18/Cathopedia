package com.ynotlabs.cathopedia.saints

import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.ContentType
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SaintsGalleryTest {
    private fun saint(id: String, feast: String?) = ContentSummary(ContentType.SAINT, id, id, "", feastDay = feast)

    @Test fun allSaintsOnTheSameDayAreIncluded() {
        val selection = saintsFeastSelection(listOf(saint("one", "10-04"), saint("two", "10-04"),
            saint("later", "10-12")), LocalDate(2026, 10, 4))!!
        assertTrue(selection.isToday)
        assertEquals(listOf("one", "two"), selection.saints.map { it.id })
    }

    @Test fun noMatchUsesNextAnniversaryAcrossYearBoundary() {
        val selection = saintsFeastSelection(listOf(saint("past", "12-20"), saint("next", "01-01")),
            LocalDate(2026, 12, 31))!!
        assertFalse(selection.isToday)
        assertEquals(LocalDate(2027, 1, 1), selection.date)
        assertEquals("next", selection.saints.single().id)
    }

    @Test fun malformedDatesAreIgnoredAndLeapDayUsesNextLeapYear() {
        val today = LocalDate(2026, 3, 1)
        val invalid = listOf(saint("unknown", null), saint("movable", "Movable"), saint("bad", "02-30"))
        assertNull(saintsFeastSelection(invalid, today))
        assertEquals(LocalDate(2028, 2, 29), saintsFeastSelection(invalid + saint("leap", "02-29"), today)!!.date)
    }

    @Test fun featuredSaintsExistInContentAndExcludeTheFeastCard() {
        val saints = listOf(saint("francis-of-assisi", "10-04"), saint("padre-pio", "09-23"), saint("other", "10-05"))
        assertEquals(listOf("padre-pio"), saintsFeatured(saints, setOf("francis-of-assisi")).map { it.id })
    }
}
