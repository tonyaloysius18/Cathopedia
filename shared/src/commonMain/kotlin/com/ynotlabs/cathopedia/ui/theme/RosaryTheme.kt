package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Blue-and-white Marian colors shared by the Rosary artwork and bead carousel.
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
    chain = Color(0xFF8294A8),
    candle = Color(0xFF75AFE3),
    prayed = Color(0xFF89AED6),
)

// Keep the same Marian hues when the user chooses dark appearance.
private val DarkRosaryColors = RosaryColors(
    snow = Color(0xFFD8E4F2),
    glacier = Color(0xFF7FA0C8),
    marian = Color(0xFF3E6494),
    vespers = Color(0xFF16283F),
    chain = Color(0xFF9CAEC2),
    candle = Color(0xFF8AC3F2),
    prayed = Color(0xFF5A7CA8),
)

/**
 * The royal Marian blue of the Holy Rosary card on the Prayers screen. Rosary
 * cards keep it in both themes, so their text uses the dark theme's gold and
 * cream (the light theme's ink would vanish into the blue).
 */
object RosaryMarianCard {
    val deep = Color(0xFF002B5C)
    val blue = Color(0xFF0055A4)
    val shade = Color(0xFF003D7A)
    val gold = DarkGold
    val goldSoft = DarkTextMuted
    val cream = DarkText
    val muted = Color(0xFFB8D0EC)

    /** Page behind Marian cards on the praying screen: darker than [deep] so cards lift off it. */
    val night = Color(0xFF001830)
    val nightRaised = Color(0xFF002246)

    val gradient: Brush get() = Brush.horizontalGradient(listOf(deep, blue, shade))
    val verticalGradient: Brush get() = Brush.verticalGradient(listOf(shade, deep))
}

/** A rounded royal-blue panel with the soft gold border of the Prayers screen's Rosary card. */
@Composable
fun RosaryMarianPanel(
    modifier: Modifier = Modifier,
    padding: Dp = 20.dp,
    brush: Brush = RosaryMarianCard.verticalGradient,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(brush)
            .border(1.dp, RosaryMarianCard.goldSoft.copy(alpha = 0.52f), shape)
            .padding(padding),
        content = content,
    )
}

/** Uses the resolved appearance, including an explicit override from Settings. */
@Composable
fun rosaryColors(): RosaryColors =
    if (LocalIsDarkTheme.current) DarkRosaryColors else LightRosaryColors

/** Scope the Marian palette to this feature while respecting the app's appearance setting. */
@Composable
fun RosaryTheme(content: @Composable () -> Unit) {
    val dark = LocalIsDarkTheme.current
    val background = if (dark) Color(0xFF101D2D) else Color(0xFFF4F8FD)
    val surface = if (dark) Color(0xFF192A3F) else Color.White
    val raised = if (dark) Color(0xFF223850) else Color(0xFFE7F0FA)
    val ink = if (dark) Color(0xFFEAF3FF) else Color(0xFF203B5D)
    val muted = if (dark) Color(0xFFB2C8E0) else Color(0xFF536D8B)
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = if (dark) Color(0xFFA8CFFA) else Color(0xFF3269A8),
            onPrimary = if (dark) Color(0xFF122D4C) else Color.White,
            primaryContainer = raised,
            onPrimaryContainer = ink,
            secondary = muted,
            onSecondary = background,
            secondaryContainer = raised,
            onSecondaryContainer = ink,
            tertiary = if (dark) Color(0xFFA8CFFA) else Color(0xFF3269A8),
            tertiaryContainer = raised,
            onTertiaryContainer = ink,
            background = background,
            onBackground = ink,
            surface = surface,
            onSurface = ink,
            surfaceVariant = raised,
            onSurfaceVariant = muted,
            surfaceContainerLowest = background,
            surfaceContainerLow = background,
            surfaceContainer = surface,
            surfaceContainerHigh = raised,
            surfaceContainerHighest = raised,
            surfaceDim = background,
            surfaceBright = surface,
            outline = muted.copy(alpha = 0.45f),
            outlineVariant = muted.copy(alpha = 0.18f),
        ),
        content = content,
    )
}
