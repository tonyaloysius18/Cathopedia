package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.ynotlabs.cathopedia.liturgical.LiturgicalColor

/**
 * The one accent that's meant to follow the liturgical calendar (Today header,
 * active tab, season indicator) — everything else in the palette stays fixed.
 * Driven by [LiturgicalCalendar.liturgicalDayFor][com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar.liturgicalDayFor]
 * via [toAccentColor]; swap the value provided to [LocalLiturgicalAccent] at the
 * call site (App.kt), not any of its other consumers.
 */
val LocalLiturgicalAccent = compositionLocalOf { LiturgicalOrdinary }

fun LiturgicalColor.toAccentColor(): Color = when (this) {
    LiturgicalColor.GREEN -> LiturgicalOrdinary
    LiturgicalColor.VIOLET -> LiturgicalAdventLent
    LiturgicalColor.GOLD -> LiturgicalChristmasEaster
    LiturgicalColor.RED -> LiturgicalPentecostMartyrs
    LiturgicalColor.ROSE -> LiturgicalGaudeteLaetare
}
