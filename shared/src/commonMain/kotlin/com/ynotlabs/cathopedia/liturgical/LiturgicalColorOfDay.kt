package com.ynotlabs.cathopedia.liturgical

import kotlinx.datetime.LocalDate

/**
 * The colour of the day's Mass, which the app's liturgical accent follows.
 *
 * Within the Ordo's years it is the colour of the day's principal celebration, so feasts and
 * obligatory memorials count (St Andrew red, the Immaculate Conception white, Holy Thursday
 * white). Optional memorials, and memorials in Lent and late Advent (commemorations), keep the
 * weekday's colour, because the Ordo puts the weekday first on those days. Outside the Ordo's
 * years it falls back to the season colour from [LiturgicalCalendar.liturgicalDayFor].
 */
object LiturgicalColorOfDay {
    suspend fun forDate(date: LocalDate, profile: OrdoProfile = OrdoProfile.GENERAL): LiturgicalColor =
        runCatching {
            val principal = OrdoCalendar.day(date, profile)?.principal
            val color = principal?.colors?.firstNotNullOfOrNull(::fromOrdo)
            // The rubrics allow gold on the most solemn days: Christmas, the Easter octave, Epiphany…
            if (color == LiturgicalColor.WHITE && principal.rank == OrdoRank.HIGHER_SOLEMNITY) LiturgicalColor.GOLD else color
        }.getOrNull()
            ?: LiturgicalCalendar.liturgicalDayFor(date).color

    private fun fromOrdo(name: String): LiturgicalColor? = when (name.lowercase()) {
        "green" -> LiturgicalColor.GREEN
        "purple", "violet" -> LiturgicalColor.VIOLET
        "rose", "pink" -> LiturgicalColor.ROSE
        "red" -> LiturgicalColor.RED
        "white" -> LiturgicalColor.WHITE
        "gold" -> LiturgicalColor.GOLD
        else -> null
    }
}
