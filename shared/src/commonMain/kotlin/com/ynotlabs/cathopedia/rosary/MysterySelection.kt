package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.liturgical.LiturgicalSeason
import com.ynotlabs.cathopedia.model.MysterySet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/** Abstraction over "what liturgical season is today", for the Sunday mystery-set override in [mysterySetForDate]. */
fun interface LiturgicalSeasonSource {
    fun seasonOn(date: LocalDate): LiturgicalSeason
}

/** Always ORDINARY_TIME, so [mysterySetForDate] falls back to the plain weekday table with no Sunday override. */
object WeekdayOnlySeasonSource : LiturgicalSeasonSource {
    override fun seasonOn(date: LocalDate): LiturgicalSeason = LiturgicalSeason.ORDINARY_TIME
}

/**
 * [com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar] already exists and
 * is already wired elsewhere in this app (Home's feast-of-the-day, the
 * theme's liturgical accent) — despite the brief describing the calendar as
 * not existing yet, it landed well before this task. This is what
 * [mysterySetForDate] actually defaults to; shipping [WeekdayOnlySeasonSource]
 * as the default instead would make the Rosary's mystery selection ignore the
 * season the rest of the app already gets right for the same date.
 */
object LiturgicalCalendarSeasonSource : LiturgicalSeasonSource {
    override fun seasonOn(date: LocalDate): LiturgicalSeason = LiturgicalCalendar.liturgicalDayFor(date).season
}

/**
 * Mon/Sat → Joyful, Tue/Fri → Sorrowful, Wed → Glorious, Thu → Luminous,
 * Sun → Glorious — except Advent/Christmastide Sundays (Joyful) and Lent
 * Sundays (Sorrowful). This only computes a *default*; the user can always
 * override manually by constructing [RosarySequence] with a different set.
 */
fun mysterySetForDate(
    date: LocalDate,
    seasonSource: LiturgicalSeasonSource = LiturgicalCalendarSeasonSource,
): MysterySet = when (date.dayOfWeek) {
    DayOfWeek.MONDAY, DayOfWeek.SATURDAY -> MysterySet.JOYFUL
    DayOfWeek.TUESDAY, DayOfWeek.FRIDAY -> MysterySet.SORROWFUL
    DayOfWeek.WEDNESDAY -> MysterySet.GLORIOUS
    DayOfWeek.THURSDAY -> MysterySet.LUMINOUS
    DayOfWeek.SUNDAY -> when (seasonSource.seasonOn(date)) {
        LiturgicalSeason.ADVENT, LiturgicalSeason.CHRISTMAS -> MysterySet.JOYFUL
        LiturgicalSeason.LENT -> MysterySet.SORROWFUL
        LiturgicalSeason.ORDINARY_TIME, LiturgicalSeason.EASTER -> MysterySet.GLORIOUS
    }
}
