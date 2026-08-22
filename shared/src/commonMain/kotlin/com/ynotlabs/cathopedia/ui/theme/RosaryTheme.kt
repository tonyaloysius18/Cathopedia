package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * "Snow-blue Marian" — the design tokens from the Prayers brief, pulled into
 * the theme instead of living as a standalone object (as the original
 * RosaryCanvas.kt had it). [rosaryCandle] is the single warm note in an
 * otherwise entirely blue field, so the eye always finds the active bead —
 * don't reuse it anywhere else on the Rosary screen.
 */
data class RosaryColors(
    val snow: Color,
    val glacier: Color,
    val marian: Color,
    val vespers: Color,
    val chain: Color,
    val candle: Color,
    val prayed: Color,
)

private val LightRosaryColors = RosaryColors(
    snow = Color(0xFFF4F8FD),
    glacier = Color(0xFFBBD4EE),
    marian = Color(0xFF5E8CC4),
    vespers = Color(0xFF2C4870),
    chain = Color(0xFFC8D6E4),
    candle = Color(0xFFE8C77A),
    prayed = Color(0xFF89AED6),
)

// Same hues, deepened — the gold stays roughly as-is per the brief; every
// blue moves toward the "church at night" end of the app's existing palette.
private val DarkRosaryColors = RosaryColors(
    snow = Color(0xFFD8E4F2),
    glacier = Color(0xFF7FA0C8),
    marian = Color(0xFF3E6494),
    vespers = Color(0xFF16283F),
    chain = Color(0xFF4A5F74),
    candle = Color(0xFFE8C77A),
    prayed = Color(0xFF5A7CA8),
)

/** Same light/dark heuristic BottomNavBar.kt already uses — accounts for the user's explicit theme override, not just system dark mode. */
@Composable
fun rosaryColors(): RosaryColors =
    if (MaterialTheme.colorScheme.background.red > 0.5f) LightRosaryColors else DarkRosaryColors
