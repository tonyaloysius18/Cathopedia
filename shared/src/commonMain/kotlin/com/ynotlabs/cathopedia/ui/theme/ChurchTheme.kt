package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Whether the current screen should render in the slate-grey stone and brass
 * Church palette instead of the app's default dark-green/gold palette —
 * evoking cathedral granite and marble. Set true only around the Churches
 * list and detail screens — see [ChurchBg] and siblings for the actual
 * colour swap. Mirrors [LocalMarianTheme]'s pattern for Apparitions.
 */
val LocalChurchTheme = compositionLocalOf { false }

val ChurchBg = Color(0xFF14181D)
val ChurchHeader = Color(0xFF1C222A)
val ChurchCard = Color(0xFF48576B)
val ChurchCardPressed = Color(0xFF54647A)
val ChurchSurfaceElevated = Color(0xFF5D6E85)
val ChurchBorder = Color(0xFF93A4B8)
val ChurchGold = Color(0xFFC9A24A)
val ChurchGoldSoft = Color(0xFF7D6530)
val ChurchCream = Color(0xFFEDEDE8)
val ChurchMuted = Color(0xFFB6C0CC)
val ChurchTag = Color(0xFF2C3846)
val ChurchSheet = Color(0xFF10141A)
val ChurchFieldContainer = Color(0xFF2C3846)
val ChurchChipForeground = Color(0xFF14181D)
val ChurchPlaceholderBg = Color(0xFF2C3846)
val ChurchIconCircleBg = Color(0xFF48576B)
val ChurchButtonContent = Color(0xFF14181D)
val ChurchActionBar = Color(0xFF1C222A)
