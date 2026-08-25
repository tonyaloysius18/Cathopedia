package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.ui.screens.rosaryCompletionCountsByDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RosaryMeterTest {

    @Test
    fun completionTimestampsGroupByLocalCalendarDay() {
        val first = LocalDate(2026, 8, 24).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        val second = LocalDate(2026, 8, 25).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        val counts = rosaryCompletionCountsByDay(
            timestamps = listOf(first, first + 60_000, second),
            timeZone = TimeZone.UTC,
        )
        assertEquals(2, counts[LocalDate(2026, 8, 24)])
        assertEquals(1, counts[LocalDate(2026, 8, 25)])
    }
}
