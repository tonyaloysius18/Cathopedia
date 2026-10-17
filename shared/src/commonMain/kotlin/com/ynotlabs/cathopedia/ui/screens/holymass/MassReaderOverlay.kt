package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.ui.theme.LocalIsDarkTheme

/** Text continues behind the tinted controls, with clearance to scroll its last line above them. */
@Composable
internal fun MassReaderOverlay(
    content: @Composable (bottomClearance: Dp) -> Unit,
    controls: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val barColor = if (LocalIsDarkTheme.current) colors.surfaceContainerHigh.copy(alpha = 0.88f)
        else colors.surfaceContainerHighest.copy(alpha = 0.97f)
    val density = LocalDensity.current
    var bottomClearance by remember { mutableStateOf(96.dp) }
    Box(Modifier.fillMaxSize()) {
        content(bottomClearance)
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .onSizeChanged { bottomClearance = with(density) { it.height.toDp() } }
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(barColor)
                .background(Brush.horizontalGradient(listOf(
                    colors.primary.copy(alpha = 0.045f), colors.primary.copy(alpha = 0.015f),
                    colors.primary.copy(alpha = 0.035f),
                ))),
        ) {
            controls()
        }
    }
}
