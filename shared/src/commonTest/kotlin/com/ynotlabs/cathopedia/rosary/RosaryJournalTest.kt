package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.RosaryCompletion
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.ui.screens.rosary.RosaryJournalStringKeys
import com.ynotlabs.cathopedia.ui.screens.rosary.journalWeekLabel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class RosaryJournalTest {
    @Test
    fun sundayBelongsToTheMondayWeekAcrossYearBoundary() {
        val start = rosaryJournalWeekStart(LocalDate(2027, 1, 3))
        assertEquals(LocalDate(2026, 12, 28), start)
        assertEquals(LocalDate(2027, 1, 3), rosaryJournalWeekDays(start).last())
        assertEquals(7, rosaryJournalWeekDays(start).size)
        assertEquals(LocalDate(2027, 1, 4), rosaryJournalWeekStart(LocalDate(2027, 1, 4)))
    }

    @Test
    fun journalGroupsActualSetsByLocalDayAndExcludesOtherWeeks() {
        val completions = listOf(
            RosaryCompletion(millis("2026-10-09T10:00:00Z"), MysterySet.SORROWFUL),
            RosaryCompletion(millis("2026-10-09T23:30:00Z"), MysterySet.JOYFUL),
            RosaryCompletion(millis("2026-10-10T08:00:00Z"), MysterySet.SORROWFUL),
            RosaryCompletion(millis("2026-10-04T08:00:00Z"), MysterySet.GLORIOUS),
        )
        val meter = meter(completions)
        val entries = rosaryJournalEntries(meter, LocalDate(2026, 10, 5), TimeZone.of("Europe/Paris"))
        assertEquals(listOf(LocalDate(2026, 10, 10), LocalDate(2026, 10, 9)), entries.map { it.date })
        assertEquals(2, entries.first().count)
        assertEquals(mapOf(MysterySet.JOYFUL to 1, MysterySet.SORROWFUL to 1), entries.first().byMysterySet)
        assertEquals(mapOf(MysterySet.SORROWFUL to 1), entries.last().byMysterySet)
        assertTrue(rosaryJournalEntries(meter, LocalDate(2026, 10, 12), TimeZone.UTC).isEmpty())
    }

    @Test
    fun timestampOnlyHistoryKeepsCountsWithoutInventingMysterySets() {
        val timestamp = millis("2026-10-09T10:00:00Z")
        val meter = RosaryMeter(2, 2, mapOf(MysterySet.SORROWFUL to 2), listOf(timestamp, timestamp))
        val entry = rosaryJournalEntries(meter, LocalDate(2026, 10, 5), TimeZone.UTC).single()
        assertEquals(2, entry.count)
        assertTrue(entry.byMysterySet.isEmpty())
    }

    @Test
    fun rangeLabelsDisambiguateMonthsAndYears() {
        val strings = mapOf(
            RosaryJournalStringKeys.month(9) to "September",
            RosaryJournalStringKeys.month(10) to "October",
            RosaryJournalStringKeys.month(12) to "December",
            RosaryJournalStringKeys.month(1) to "January",
        )
        val today = LocalDate(2026, 10, 11)
        assertEquals("5–11 October", journalWeekLabel(LocalDate(2026, 10, 5), today, today, strings))
        assertEquals("28 September – 4 October", journalWeekLabel(LocalDate(2026, 9, 28), LocalDate(2026, 10, 4), today, strings))
        assertEquals("29 December 2025 – 4 January 2026", journalWeekLabel(LocalDate(2025, 12, 29), LocalDate(2026, 1, 4), today, strings))
    }

    private fun millis(value: String) = Instant.parse(value).toEpochMilliseconds()
    private fun meter(completions: List<RosaryCompletion>) = RosaryMeter(
        completions.size.toLong(), completions.size.toLong(), emptyMap(), completions.map { it.completedAt }, completions,
    )
}
