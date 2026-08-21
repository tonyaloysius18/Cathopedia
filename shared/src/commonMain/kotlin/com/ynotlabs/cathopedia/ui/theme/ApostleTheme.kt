package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Whether the current screen should render in the deep royal-purple and
 * antique-gold Apostle palette instead of the app's default dark-green/gold
 * palette — the color of authority and apostolic succession. Set true only
 * around the Apostles list and detail screens — see [ApostleBg] and siblings
 * for the actual colour swap. Mirrors [LocalMarianTheme]'s pattern for
 * Apparitions.
 */
val LocalApostleTheme = compositionLocalOf { false }

val ApostleBg = Color(0xFF160A28)
val ApostleHeader = Color(0xFF211137)
val ApostleCard = Color(0xFF4B2A82)
val ApostleCardPressed = Color(0xFF593297)
val ApostleSurfaceElevated = Color(0xFF663BA8)
val ApostleBorder = Color(0xFFA07DDA)
val ApostleGold = Color(0xFFE8C468)
val ApostleGoldSoft = Color(0xFF8560C2)
val ApostleCream = Color(0xFFF0E8FA)
val ApostleMuted = Color(0xFFC5AAE8)
val ApostleTag = Color(0xFF3A1F63)
val ApostleSheet = Color(0xFF1C0E30)
val ApostleFieldContainer = Color(0xFF3A1F63)
val ApostleChipForeground = Color(0xFF160A28)
val ApostlePlaceholderBg = Color(0xFF3A1F63)
val ApostleIconCircleBg = Color(0xFF4B2A82)
val ApostleButtonContent = Color(0xFF160A28)
val ApostleActionBar = Color(0xFF211137)
