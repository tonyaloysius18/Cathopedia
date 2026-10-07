package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import kotlin.math.pow
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// lightColorScheme()/darkColorScheme() silently fill any container/inverse/
// tertiary token left unset from Material3's own baseline (purple-tinted)
// scheme — that leak was showing up as a lavender tint on Card backgrounds.
// Setting the full surfaceContainer* family here (the tokens filled Card and
// FilterChip actually read) closes the gap for every component, not just
// the ones we'd already noticed.
private val DarkColors = darkColorScheme(
    primary = DarkGold,
    onPrimary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkRaised,
    onSurfaceVariant = DarkTextMuted,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkRaised,
    surfaceContainerHighest = DarkPillBubble,
    surfaceDim = DarkBackground,
    surfaceBright = DarkRaised,
    secondary = DarkTextMuted,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = DarkText,
    tertiary = DarkGoldBright,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkRaised,
    onTertiaryContainer = DarkGoldBright,
    inverseSurface = DarkText,
    inverseOnSurface = DarkBackground,
    inversePrimary = DarkGold,
    outline = DarkLine,
    error = AlertRed,
)

private val LightColors = lightColorScheme(
    // Bright gold fails contrast on parchment — light mode uses the darkened
    // gold everywhere Material3 renders text/icons from `primary`.
    primary = LightGoldText,
    onPrimary = LightBackground,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightSurface,
    onSurfaceVariant = LightTextMuted,
    surfaceContainerLowest = LightBackground,
    surfaceContainerLow = LightBackground,
    surfaceContainer = LightSurface,
    surfaceContainerHigh = LightSurface,
    surfaceContainerHighest = LightPillBubble,
    surfaceDim = LightPillSurface,
    surfaceBright = LightPillBubble,
    secondary = LightGreenDeep,
    secondaryContainer = LightSurface,
    onSecondaryContainer = LightText,
    tertiary = LightGoldText,
    onTertiary = LightBackground,
    tertiaryContainer = LightSurface,
    onTertiaryContainer = LightGoldText,
    inverseSurface = LightText,
    inverseOnSurface = LightBackground,
    inversePrimary = DarkGold,
    outline = Color(0x21221C14),
    error = AlertRed,
)

/** The resolved app appearance, including an explicit override from Settings. */
val LocalIsDarkTheme = staticCompositionLocalOf { true }

@Composable
fun CathopediaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    liturgicalAccent: Color = LiturgicalOrdinary,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    CompositionLocalProvider(
        LocalLiturgicalAccent provides liturgicalAccent,
        LocalIsDarkTheme provides useDarkTheme,
    ) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) DarkColors else LightColors,
            typography = CathopediaTypography,
            content = content,
        )
    }
}

/**
 * The one border colour every card uses: a soft parchment-grey in light mode (the
 * Explore cards' look) and a clear gold in dark mode.
 */
val CardBorder: Color
    @Composable @ReadOnlyComposable
    get() = if (LocalIsDarkTheme.current) MetallicGold.copy(alpha = 0.45f)
    else MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)

/**
 * Alpha for a page- or card-coloured fade drawn over artwork. Dark mode keeps the
 * value as written. On parchment the same wash bleaches the picture, so light mode
 * squares it: the near-solid part behind text barely changes (0.94 → 0.87) while
 * the soft wash across the image mostly lifts (0.42 → 0.15).
 */
@Composable
@ReadOnlyComposable
fun Color.imageFade(alpha: Float): Color =
    copy(alpha = if (LocalIsDarkTheme.current) alpha else alpha.pow(2.2f))
