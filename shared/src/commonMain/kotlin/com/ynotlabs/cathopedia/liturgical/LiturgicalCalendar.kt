package com.ynotlabs.cathopedia.liturgical

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** The five seasons of the General Roman Calendar. */
enum class LiturgicalSeason {
    ADVENT,
    CHRISTMAS,
    ORDINARY_TIME,
    LENT,
    EASTER,
}

/** The liturgical colours used across the year — maps onto the app's Liturgical* accent tokens in ui/theme/Color.kt. */
enum class LiturgicalColor {
    GREEN,
    VIOLET,
    GOLD,
    RED,
    ROSE,
}

data class LiturgicalDay(
    val date: LocalDate,
    val season: LiturgicalSeason,
    val color: LiturgicalColor,
)

/**
 * Pure calendar math for the General Roman Calendar — no database dependency.
 *
 * The feast content data (see the JSON files under content/feasts) only flags a feast's date as
 * either a fixed "MM-DD" string or the literal string "Movable"; it carries no
 * offset-from-Easter formula. [resolveDate] is the bridge: it hardcodes the
 * Easter-relative offset for each movable feast actually present in the seed data.
 */
object LiturgicalCalendar {

    /** Anonymous Gregorian algorithm (Meeus/Jones/Butcher) for the date of Easter Sunday. */
    fun easter(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = (h + l - 7 * m + 114) % 31 + 1
        return LocalDate(year, month, day)
    }

    /**
     * Resolves one feast's actual calendar date in [year] from the raw `date` string
     * stored in the content data — either a fixed "MM-DD", or the literal "Movable"
     * for the handful of feasts whose date is computed relative to Easter.
     */
    fun resolveDate(rawDate: String?, feastId: String, year: Int): LocalDate? {
        if (rawDate == null) return null
        if (rawDate != "Movable") {
            val parts = rawDate.split("-")
            if (parts.size != 2) return null
            val month = parts[0].toIntOrNull() ?: return null
            val day = parts[1].toIntOrNull() ?: return null
            return runCatching { LocalDate(year, month, day) }.getOrNull()
        }
        val e = easter(year)
        return when (feastId) {
            "easter" -> e
            "ascension" -> e.plusDays(39)
            "pentecost" -> pentecost(year)
            "trinity-sunday" -> trinitySunday(year)
            "corpus-christi" -> corpusChristi(year)
            "sacred-heart" -> sacredHeart(year)
            "christ-the-king" -> christTheKing(year)
            "baptism-of-the-lord" -> baptismOfTheLord(year)
            "holy-family" -> holyFamily(year)
            "ash-wednesday" -> ashWednesday(year)
            "palm-sunday" -> palmSunday(year)
            "good-friday" -> goodFriday(year)
            else -> null
        }
    }

    /** Season and liturgical colour for any date — independent of the feast content data. */
    fun liturgicalDayFor(date: LocalDate): LiturgicalDay {
        val year = date.year
        val baptism = baptismOfTheLord(year)
        val ash = ashWednesday(year)
        val easterDate = easter(year)
        val pentecostDate = pentecost(year)
        val advent1 = adventFirstSunday(year)
        val christmas = LocalDate(year, 12, 25)

        return when {
            date <= baptism -> LiturgicalDay(date, LiturgicalSeason.CHRISTMAS, LiturgicalColor.GOLD)
            date < ash -> ordinaryTimeDay(date, year)
            date < easterDate -> lentDay(date, easterDate)
            date <= pentecostDate -> easterSeasonDay(date, pentecostDate)
            date < advent1 -> ordinaryTimeDay(date, year)
            date < christmas -> adventDay(date, advent1)
            else -> LiturgicalDay(date, LiturgicalSeason.CHRISTMAS, LiturgicalColor.GOLD)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun today(): LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun ordinaryTimeDay(date: LocalDate, year: Int): LiturgicalDay {
        val color = when (date) {
            trinitySunday(year), corpusChristi(year), sacredHeart(year), christTheKing(year) -> LiturgicalColor.GOLD
            else -> LiturgicalColor.GREEN
        }
        return LiturgicalDay(date, LiturgicalSeason.ORDINARY_TIME, color)
    }

    private fun lentDay(date: LocalDate, easterDate: LocalDate): LiturgicalDay {
        val year = easterDate.year
        val laetare = easterDate.minusDays(21)
        val color = when (date) {
            palmSunday(year), goodFriday(year) -> LiturgicalColor.RED
            laetare -> LiturgicalColor.ROSE
            else -> LiturgicalColor.VIOLET
        }
        return LiturgicalDay(date, LiturgicalSeason.LENT, color)
    }

    private fun easterSeasonDay(date: LocalDate, pentecostDate: LocalDate): LiturgicalDay {
        val color = if (date == pentecostDate) LiturgicalColor.RED else LiturgicalColor.GOLD
        return LiturgicalDay(date, LiturgicalSeason.EASTER, color)
    }

    private fun adventDay(date: LocalDate, advent1: LocalDate): LiturgicalDay {
        val gaudete = advent1.plusDays(14)
        val color = if (date == gaudete) LiturgicalColor.ROSE else LiturgicalColor.VIOLET
        return LiturgicalDay(date, LiturgicalSeason.ADVENT, color)
    }

    private fun ashWednesday(year: Int) = easter(year).minusDays(46)
    private fun palmSunday(year: Int) = easter(year).minusDays(7)
    private fun goodFriday(year: Int) = easter(year).minusDays(2)
    private fun pentecost(year: Int) = easter(year).plusDays(49)
    private fun trinitySunday(year: Int) = easter(year).plusDays(56)
    private fun corpusChristi(year: Int) = easter(year).plusDays(60)
    private fun sacredHeart(year: Int) = easter(year).plusDays(68)

    private fun adventFourthSunday(year: Int) = sundayOnOrBefore(LocalDate(year, 12, 24))
    private fun adventFirstSunday(year: Int) = adventFourthSunday(year).minusDays(21)
    private fun christTheKing(year: Int) = adventFirstSunday(year).minusDays(7)

    private fun baptismOfTheLord(year: Int) = sundayStrictlyAfter(LocalDate(year, 1, 6))

    /** Sunday within the Christmas octave (26–31 Dec); falls back to 30 Dec if Christmas Day is itself a Sunday. */
    private fun holyFamily(year: Int): LocalDate {
        val christmas = LocalDate(year, 12, 25)
        if (christmas.dayOfWeek == DayOfWeek.SUNDAY) return LocalDate(year, 12, 30)
        val candidate = sundayOnOrAfter(christmas.plusDays(1))
        return if (candidate.month == christmas.month) candidate else LocalDate(year, 12, 30)
    }

    // DayOfWeek.ordinal is 0 (Monday) .. 6 (Sunday) in kotlinx-datetime.
    private fun sundayOnOrBefore(date: LocalDate): LocalDate = date.minusDays((date.dayOfWeek.ordinal + 1) % 7)

    private fun sundayOnOrAfter(date: LocalDate): LocalDate = date.plusDays((6 - date.dayOfWeek.ordinal) % 7)

    private fun sundayStrictlyAfter(date: LocalDate): LocalDate = sundayOnOrAfter(date.plusDays(1))

    private fun LocalDate.plusDays(days: Int): LocalDate = this.plus(days, DateTimeUnit.DAY)
    private fun LocalDate.minusDays(days: Int): LocalDate = this.minus(days, DateTimeUnit.DAY)
}
