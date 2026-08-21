package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Whether the current screen should render in the shiny metallic-bronze Feast
 * palette instead of the app's default dark-green/gold palette. Set true only
 * around the Feasts list and detail screens — see [FeastBg] and siblings for
 * the actual colour swap. Mirrors [LocalMarianTheme]'s pattern for Apparitions.
 */
val LocalFeastTheme = compositionLocalOf { false }

val FeastBg = Color(0xFF1F1408)
val FeastHeader = Color(0xFF2B1C0C)
val FeastCard = Color(0xFF7A5222)
val FeastCardPressed = Color(0xFF8F6530)
val FeastSurfaceElevated = Color(0xFF96692F)
val FeastBorder = Color(0xFFCE9A46)
val FeastGold = Color(0xFFEBCB6B)
val FeastGoldSoft = Color(0xFFB98A3E)
val FeastCream = Color(0xFFF7EEDD)
val FeastMuted = Color(0xFFDDBE84)
val FeastTag = Color(0xFF6B4A20)
val FeastSheet = Color(0xFF241806)
val FeastFieldContainer = Color(0xFF6B4A20)
val FeastChipForeground = Color(0xFF1F1408)
val FeastPlaceholderBg = Color(0xFF6B4A20)
val FeastIconCircleBg = Color(0xFF7A5222)
val FeastButtonContent = Color(0xFF1F1408)
val FeastActionBar = Color(0xFF2B1C0C)
