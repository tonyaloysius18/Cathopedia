package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.liturgical.LiturgicalSeason
import com.ynotlabs.cathopedia.model.MysterySet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class MysterySelectionTest {

    @Test
    fun standardScheduleMatchesEveryWeekday() {
        val expected = mapOf(
            DayOfWeek.MONDAY to MysterySet.JOYFUL,
            DayOfWeek.TUESDAY to MysterySet.SORROWFUL,
            DayOfWeek.WEDNESDAY to MysterySet.GLORIOUS,
            DayOfWeek.THURSDAY to MysterySet.LUMINOUS,
            DayOfWeek.FRIDAY to MysterySet.SORROWFUL,
            DayOfWeek.SATURDAY to MysterySet.JOYFUL,
            DayOfWeek.SUNDAY to MysterySet.GLORIOUS,
        )
        val monday = LocalDate(2026, 8, 24)
        expected.entries.forEachIndexed { offset, (day, set) ->
            val date = LocalDate.fromEpochDays(monday.toEpochDays() + offset)
            assertEquals(day, date.dayOfWeek)
            assertEquals(set, mysterySetForDate(date))
        }
    }

    @Test
    fun aCustomScheduleCanOverrideSundayBySeason() {
        val custom = DefaultMysterySchedule.copy(
            sundayBySeason = mapOf(LiturgicalSeason.LENT to MysterySet.SORROWFUL),
        )
        val sunday = LocalDate(2026, 8, 30)
        assertEquals(
            MysterySet.SORROWFUL,
            mysterySetForDate(sunday, custom, LiturgicalSeasonSource { LiturgicalSeason.LENT }),
        )
    }
}
