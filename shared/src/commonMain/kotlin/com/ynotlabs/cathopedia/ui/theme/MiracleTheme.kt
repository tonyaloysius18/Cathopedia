package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Whether the current screen should render in the deep wine-red and antique-gold
 * Miracle palette instead of the app's default dark-green/gold palette — evoking
 * the Precious Blood. Set true only around the Miracles list and detail screens —
 * see [MiracleBg] and siblings for the actual colour swap. Mirrors [LocalMarianTheme]'s
 * pattern for Apparitions.
 */
val LocalMiracleTheme = compositionLocalOf { false }

val MiracleBg = Color(0xFF1C0505)
val MiracleHeader = Color(0xFF2A0808)
val MiracleCard = Color(0xFF7A1414)
val MiracleCardPressed = Color(0xFF8F1818)
val MiracleSurfaceElevated = Color(0xFFA31D1D)
val MiracleBorder = Color(0xFFD65C4A)
val MiracleGold = Color(0xFFE8B84A)
val MiracleGoldSoft = Color(0xFFB93A2E)
val MiracleCream = Color(0xFFF7E5E0)
val MiracleMuted = Color(0xFFE08C7D)
val MiracleTag = Color(0xFF5C1010)
val MiracleSheet = Color(0xFF210606)
val MiracleFieldContainer = Color(0xFF5C1010)
val MiracleChipForeground = Color(0xFF1C0505)
val MiraclePlaceholderBg = Color(0xFF5C1010)
val MiracleIconCircleBg = Color(0xFF7A1414)
val MiracleButtonContent = Color(0xFF1C0505)
val MiracleActionBar = Color(0xFF2A0808)
