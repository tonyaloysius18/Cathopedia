package com.ynotlabs.cathopedia.data

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * The "completed this month" boundary must land on midnight of the 1st in the
 * chosen zone — a session finished at 23:59 on the last day of the previous
 * month must not leak into this month's count.
 */
@OptIn(ExperimentalTime::class)
class RosarySessionMonthTest {

    private val utc = TimeZone.UTC

    @Test
    fun firstOfMonthIsMidnightOfTheFirst() {
        val now = Instant.parse("2026-08-25T13:45:00Z").toEpochMilliseconds()
        val expected = Instant.parse("2026-08-01T00:00:00Z").toEpochMilliseconds()
        assertEquals(expected, RosarySessionRepository.firstOfMonthMillis(now, utc))
    }

    @Test
    fun firstOfMonthOnTheFirstItselfIsThatMidnight() {
        val now = Instant.parse("2026-12-01T00:00:00Z").toEpochMilliseconds()
        val expected = Instant.parse("2026-12-01T00:00:00Z").toEpochMilliseconds()
        assertEquals(expected, RosarySessionRepository.firstOfMonthMillis(now, utc))
    }

    @Test
    fun lastInstantOfPreviousMonthIsBeforeTheBoundary() {
        val boundary = RosarySessionRepository.firstOfMonthMillis(
            Instant.parse("2026-08-10T00:00:00Z").toEpochMilliseconds(), utc,
        )
        val julyLastMoment = Instant.parse("2026-07-31T23:59:59Z").toEpochMilliseconds()
        assertEquals(true, julyLastMoment < boundary)
    }
}
