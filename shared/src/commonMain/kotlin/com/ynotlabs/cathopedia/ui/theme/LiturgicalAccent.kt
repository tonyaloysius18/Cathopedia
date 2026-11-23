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

/**
 * Bright enough to glow on the dark bar and to read as a dot or border on both themes; the darker
 * `Liturgical*` swatches in Color.kt stay for places that fill whole surfaces. White is a warm
 * ivory, which pure white would not be on the dark theme (it would read as plain text); on the
 * light theme it becomes a pale silver so it still shows against the cream background.
 */
fun LiturgicalColor.toAccentColor(darkTheme: Boolean = true): Color = when (this) {
    // The chasuble fabrics (see LiturgicalTint), lifted where the fabric is too dark to glow.
    LiturgicalColor.GREEN -> Color(0xFF2FA046)
    LiturgicalColor.VIOLET -> Color(0xFFA33DB0)
    LiturgicalColor.GOLD -> Color(0xFFD18D1D)
    LiturgicalColor.RED -> Color(0xFFD02A2F)
    LiturgicalColor.ROSE -> Color(0xFFE15661)
    LiturgicalColor.WHITE -> if (darkTheme) Color(0xFFE0D8D0) else Color(0xFFA9A293)
}
