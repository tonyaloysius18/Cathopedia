package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Whether the current screen should render in the shiny metallic-blue Marian
 * palette instead of the app's default dark-green/gold palette. Set true only
 * around the Apparitions list and detail screens — see [MarianBg] and
 * siblings for the actual colour swap, and each screen's private EntityXxx
 * / DetailXxx accessors for where it's consumed.
 */
val LocalMarianTheme = compositionLocalOf { false }

val MarianBg = Color(0xFF0A1F3D)
val MarianHeader = Color(0xFF0F2A52)
val MarianCard = Color(0xFF2C5490)
val MarianCardPressed = Color(0xFF3A6BB0)
val MarianSurfaceElevated = Color(0xFF3D6DB5)
val MarianBorder = Color(0xFF6FA8DC)
val MarianGold = Color(0xFFE0C848)
val MarianGoldSoft = Color(0xFF9FC1E8)
val MarianCream = Color(0xFFF2F0E4)
val MarianMuted = Color(0xFFB8D0EC)
val MarianTag = Color(0xFF234B85)
val MarianSheet = Color(0xFF0F2A52)
val MarianFieldContainer = Color(0xFF234B85)
val MarianChipForeground = Color(0xFF0A1F3D)
val MarianPlaceholderBg = Color(0xFF234B85)
val MarianIconCircleBg = Color(0xFF2C5490)
val MarianButtonContent = Color(0xFF0A1F3D)
val MarianActionBar = Color(0xFF0F2A52)
