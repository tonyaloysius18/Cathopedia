package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.liturgical.LiturgicalSeason
import com.ynotlabs.cathopedia.model.MysterySet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/** Abstraction over "what liturgical season is today", for the Sunday mystery-set override in [mysterySetForDate]. */
fun interface LiturgicalSeasonSource {
    fun seasonOn(date: LocalDate): LiturgicalSeason
}

/** Always ORDINARY_TIME, useful for schedules that do not vary by liturgical season. */
object WeekdayOnlySeasonSource : LiturgicalSeasonSource {
    override fun seasonOn(date: LocalDate): LiturgicalSeason = LiturgicalSeason.ORDINARY_TIME
}

/**
 * Data-driven day mapping. A community that follows a seasonal Sunday custom
 * can provide a different instance from settings without changing the dialog
 * or adding another inline weekday `when`.
 */
data class MysterySchedule(
    val byDay: Map<DayOfWeek, MysterySet>,
    val sundayBySeason: Map<LiturgicalSeason, MysterySet> = emptyMap(),
) {
    fun setFor(date: LocalDate, seasonSource: LiturgicalSeasonSource = WeekdayOnlySeasonSource): MysterySet {
        val weekday = byDay.getValue(date.dayOfWeek)
        return if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            sundayBySeason[seasonSource.seasonOn(date)] ?: weekday
        } else {
            weekday
        }
    }
}

/**
 * The standard weekday custom from the brief. It is a value, not UI logic, so
 * settings may replace it later (including optional seasonal Sunday rules).
 */
val DefaultMysterySchedule = MysterySchedule(
    byDay = mapOf(
        DayOfWeek.MONDAY to MysterySet.JOYFUL,
        DayOfWeek.TUESDAY to MysterySet.SORROWFUL,
        DayOfWeek.WEDNESDAY to MysterySet.GLORIOUS,
        DayOfWeek.THURSDAY to MysterySet.LUMINOUS,
        DayOfWeek.FRIDAY to MysterySet.SORROWFUL,
        DayOfWeek.SATURDAY to MysterySet.JOYFUL,
        DayOfWeek.SUNDAY to MysterySet.GLORIOUS,
    ),
)

fun mysterySetForDate(
    date: LocalDate,
    schedule: MysterySchedule = DefaultMysterySchedule,
    seasonSource: LiturgicalSeasonSource = WeekdayOnlySeasonSource,
): MysterySet = schedule.setFor(date, seasonSource)
