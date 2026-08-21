package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Whether the current screen should render in the ivory-and-antique-gold Pope
 * palette instead of the app's default dark-green/gold palette. Set true only
 * around the Popes list and detail screens — see [PopeBg] and siblings for
 * the actual colour swap. Mirrors [LocalMarianTheme]'s pattern for Apparitions.
 */
val LocalPopeTheme = compositionLocalOf { false }

val PopeBg = Color(0xFF231C0A)
val PopeHeader = Color(0xFF2E250D)
val PopeCard = Color(0xFF6B5A22)
val PopeCardPressed = Color(0xFF7D6A2A)
val PopeSurfaceElevated = Color(0xFF836E2C)
val PopeBorder = Color(0xFFD4B94A)
val PopeGold = Color(0xFFF0DFA0)
val PopeGoldSoft = Color(0xFFB89B3E)
val PopeCream = Color(0xFFFBF6E8)
val PopeMuted = Color(0xFFDCCB92)
val PopeTag = Color(0xFF5A4C1E)
val PopeSheet = Color(0xFF2A2210)
val PopeFieldContainer = Color(0xFF5A4C1E)
val PopeChipForeground = Color(0xFF231C0A)
val PopePlaceholderBg = Color(0xFF5A4C1E)
val PopeIconCircleBg = Color(0xFF6B5A22)
val PopeButtonContent = Color(0xFF231C0A)
val PopeActionBar = Color(0xFF2E250D)
