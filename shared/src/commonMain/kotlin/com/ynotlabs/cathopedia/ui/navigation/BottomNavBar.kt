package com.ynotlabs.cathopedia.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.ui.theme.DarkGoldBright
import com.ynotlabs.cathopedia.ui.theme.DarkPillSurface
import com.ynotlabs.cathopedia.ui.theme.LightGoldText
import com.ynotlabs.cathopedia.ui.theme.LightPillSurface
import kotlin.math.abs

/**
 * Ported from Itinera's `SlidingPillBar` — a floating pill with a raised
 * bubble that slides to the selected tab, drawn as one Canvas path rather
 * than a moving indicator view. Settings occupies the fourth slot (Saved now
 * lives inside Settings as its own card, reached by a push rather than a
 * tab). The selected glyph uses the theme's metallic-gold `primary` token
 * (the liturgical accent is reserved for the seasonal Today header/indicator,
 * not this bar).
 */
@Composable
fun BottomNavBar(selected: Tab, onSelect: (Tab) -> Unit) {
    val items = Tab.entries
    val count = items.size
    val selectedIndex = items.indexOf(selected).coerceAtLeast(0)

    val isLightMode = MaterialTheme.colorScheme.background.red > 0.5f
    // Dedicated pill tokens, not colorScheme.surface — the generic surface tone
    // sits too close to the page background once it's a thin floating shape
    // rather than a full-bleed screen; see the comments in Color.kt.
    val barBackgroundColor = if (isLightMode) LightPillSurface else DarkPillSurface
    // Same fill as the pill itself — the bubble reads as a raised bump (via its
    // drop shadow) rather than a differently-coloured badge.
    val activeCircleColor = barBackgroundColor
    val rimColor = if (isLightMode) LightGoldText.copy(alpha = 0.18f) else DarkGoldBright.copy(alpha = 0.22f)
    val selectedIconColor = MaterialTheme.colorScheme.primary
    val unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant

    val targetBias = if (count <= 1) 0f else -1f + 2f * selectedIndex / (count - 1)
    val bias by animateFloatAsState(
        targetValue = targetBias,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 350f),
        label = "pillSlide",
    )
    val step = if (count <= 1) 2f else 2f / (count - 1)

    val barPulse = remember { Animatable(1f) }
    LaunchedEffect(selected) {
        barPulse.snapTo(1f)
        barPulse.animateTo(1.02f, animationSpec = tween(durationMillis = 80))
        barPulse.animateTo(targetValue = 1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f))
    }

    val animatedIndexFloat = (bias + 1f) / 2f * (count - 1)
    val sidePaddingPx = with(LocalDensity.current) { 32.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(96.dp)
            .graphicsLayer {
                scaleX = barPulse.value
                scaleY = barPulse.value
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(96.dp)) {
            val width = size.width
            val height = size.height
            val cornerRadius = 28.dp.toPx()

            val barHeightPx = 56.dp.toPx()
            val barTopY = height - barHeightPx

            val centerX = sidePaddingPx + (width - 2 * sidePaddingPx) * (animatedIndexFloat + 0.5f) / count

            val dipWidth = 52.dp.toPx()
            val dipDepth = 40.dp.toPx()

            val bubbleRadius = 24.dp.toPx()
            val bubbleCenterY = barTopY + 8.dp.toPx()

            val path = Path().apply {
                val dipStart = centerX - dipWidth
                val dipEnd = centerX + dipWidth

                moveTo(0f, height - cornerRadius)
                quadraticTo(0f, height, cornerRadius, height)
                lineTo(width - cornerRadius, height)
                quadraticTo(width, height, width, height - cornerRadius)

                lineTo(width, barTopY + cornerRadius)
                if (dipEnd < width - cornerRadius) {
                    quadraticTo(width, barTopY, width - cornerRadius, barTopY)
                    lineTo(dipEnd, barTopY)
                } else {
                    lineTo(width, barTopY)
                    lineTo(dipEnd, barTopY)
                }

                cubicTo(
                    x1 = dipEnd - (dipWidth * 0.42f), y1 = barTopY,
                    x2 = centerX + (dipWidth * 0.52f), y2 = barTopY + dipDepth,
                    x3 = centerX, y3 = barTopY + dipDepth,
                )
                cubicTo(
                    x1 = centerX - (dipWidth * 0.52f), y1 = barTopY + dipDepth,
                    x2 = dipStart + (dipWidth * 0.42f), y2 = barTopY,
                    x3 = dipStart, y3 = barTopY,
                )

                if (dipStart > cornerRadius) {
                    lineTo(cornerRadius, barTopY)
                    quadraticTo(0f, barTopY, 0f, barTopY + cornerRadius)
                } else {
                    lineTo(0f, barTopY)
                    lineTo(0f, barTopY + cornerRadius)
                }
                close()
            }

            val shadowPath = Path().apply {
                addPath(path)
                addOval(
                    Rect(
                        centerX - bubbleRadius - 6.dp.toPx(), bubbleCenterY - bubbleRadius - 6.dp.toPx(),
                        centerX + bubbleRadius + 6.dp.toPx(), bubbleCenterY + bubbleRadius + 6.dp.toPx(),
                    ),
                )
            }

            drawPillShadow(path = shadowPath, radius = 14.dp.toPx(), dy = 4.dp.toPx(), isLightMode = isLightMode)
            drawPath(path = path, color = barBackgroundColor)
            // A hairline gold rim gives the pill a defined edge on top of the
            // fill-colour contrast, ties back to the metallic-gold identity.
            drawPath(path = path, color = rimColor, style = Stroke(width = 1.2.dp.toPx()))
            drawCircle(color = activeCircleColor, radius = bubbleRadius, center = Offset(centerX, bubbleCenterY))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, tab ->
                val itemBias = if (count <= 1) 0f else -1f + 2f * index / (count - 1)
                val selectedness = (1f - abs(bias - itemBias) / step).coerceIn(0f, 1f)
                val tint = lerp(unselectedIconColor, selectedIconColor, selectedness)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSelect(tab) },
                    contentAlignment = Alignment.Center,
                ) {
                    val verticalOffset = with(LocalDensity.current) { (-20.dp * selectedness).toPx() }
                    Text(
                        tab.glyph,
                        fontSize = 20.sp,
                        color = tint,
                        modifier = Modifier.graphicsLayer { translationY = verticalOffset },
                    )
                }
            }
        }
    }
}
