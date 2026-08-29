package com.ynotlabs.cathopedia.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.ui.theme.DarkGoldBright
import com.ynotlabs.cathopedia.ui.theme.DarkPillSurface
import com.ynotlabs.cathopedia.ui.theme.LightGoldText
import androidx.compose.ui.layout.ContentScale
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.nav_explore
import com.ynotlabs.cathopedia.resources.nav_home
import com.ynotlabs.cathopedia.resources.nav_prayers
import com.ynotlabs.cathopedia.resources.nav_search
import com.ynotlabs.cathopedia.resources.nav_settings
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

@Composable
fun BottomNavBar(
    selected: Tab?,
    onSelect: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = Tab.entries
    val count = items.size
    val selectedIndex = if (selected != null) items.indexOf(selected) else -1

    val isLightMode = MaterialTheme.colorScheme.background.red > 0.5f
    val barBackgroundColor = if (isLightMode) {
        Color(0x732E5A44) // ~45% opaque green
    } else {
        DarkPillSurface.copy(alpha = 0.45f)
    }
    val activeCircleColor = barBackgroundColor
    val rimColor = if (isLightMode) LightGoldText.copy(alpha = 0.45f) else DarkGoldBright.copy(alpha = 0.55f)
    // When nothing is selected, we bias towards the center or maintain previous?
    // Let's hide the bubble if selectedIndex is -1.
    val hasSelection = selectedIndex != -1
    
    val targetBias = if (!hasSelection) 0f else if (count <= 1) 0f else (-1f + 2f * selectedIndex / (count - 1))
    val bias by animateFloatAsState(
        targetValue = targetBias,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 350f),
        label = "pillSlide",
    )
    val step = if (count <= 1) 2f else 2f / (count - 1)

    // Animate bubble visibility
    val bubbleAlpha by animateFloatAsState(
        targetValue = if (hasSelection) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "bubbleAlpha"
    )

    val barPulse = remember { Animatable(1f) }
    LaunchedEffect(selected) {
        if (hasSelection) {
            barPulse.snapTo(1f)
            barPulse.animateTo(1.02f, animationSpec = tween(durationMillis = 80))
            barPulse.animateTo(targetValue = 1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f))
        }
    }

    val animatedIndexFloat = (bias + 1f) / 2f * (count - 1)
    val sidePaddingPx = with(LocalDensity.current) { 32.dp.toPx() }

    Box(
        modifier = modifier
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
            val dipDepth = 40.dp.toPx() * bubbleAlpha

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

            drawPillShadow(
                path = shadowPath,
                radius = 14.dp.toPx(),
                dy = 4.dp.toPx(),
                isLightMode = isLightMode,
            )

            // Gold ambience around the ENTIRE sliding pill shape.
            // Multiple wide translucent strokes create a soft outer glow
            // without changing the original geometry of the bar.
            // Soft outer gold ambience around the complete pill.
            drawPath(
                path = path,
                color = DarkGoldBright.copy(alpha = 0.04f),
                style = Stroke(width = 12.dp.toPx()),
            )
            drawPath(
                path = path,
                color = DarkGoldBright.copy(alpha = 0.07f),
                style = Stroke(width = 7.dp.toPx()),
            )

            // Base fill.
            drawPath(path = path, color = barBackgroundColor)

            // Diffuse gold illumination INSIDE the entire pill.
            // This gives the full bar a warm glow rather than only outlining it.
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        DarkGoldBright.copy(alpha = 0.11f),
                        DarkGoldBright.copy(alpha = 0.045f),
                        DarkGoldBright.copy(alpha = 0.08f),
                    ),
                ),
            )

            // Very soft vertical sheen across the pill surface.
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkGoldBright.copy(alpha = 0.06f),
                        Color.Transparent,
                        DarkGoldBright.copy(alpha = 0.03f),
                    ),
                ),
            )

            // Strong selected-item halo. Drawing it here (in the full 96dp
            // canvas) keeps the glow from being constrained by the icon row.
            if (bubbleAlpha > 0f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DarkGoldBright.copy(alpha = 0.64f * bubbleAlpha),
                            DarkGoldBright.copy(alpha = 0.34f * bubbleAlpha),
                            DarkGoldBright.copy(alpha = 0.13f * bubbleAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(centerX, bubbleCenterY),
                        radius = 39.dp.toPx(),
                    ),
                    radius = 39.dp.toPx(),
                    center = Offset(centerX, bubbleCenterY),
                )

                // A second tighter glow makes the active bubble visibly luminous.
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DarkGoldBright.copy(alpha = 0.52f * bubbleAlpha),
                            DarkGoldBright.copy(alpha = 0.16f * bubbleAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(centerX, bubbleCenterY),
                        radius = 29.dp.toPx(),
                    ),
                    radius = 29.dp.toPx(),
                    center = Offset(centerX, bubbleCenterY),
                )
            }

            // Original pill outline/shape remains unchanged.
            drawPath(
                path = path,
                color = rimColor,
                style = Stroke(width = 1.2.dp.toPx()),
            )

            if (bubbleAlpha > 0f) {
                drawCircle(
                    color = activeCircleColor.copy(alpha = bubbleAlpha),
                    radius = bubbleRadius,
                    center = Offset(centerX, bubbleCenterY),
                )

                // Bright gold rim around the raised active circle.
                drawCircle(
                    color = DarkGoldBright.copy(alpha = 0.82f * bubbleAlpha),
                    radius = bubbleRadius + 1.5.dp.toPx(),
                    center = Offset(centerX, bubbleCenterY),
                    style = Stroke(width = 1.5.dp.toPx()),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, tab ->
                val itemBias = if (count <= 1) 0f else -1f + 2f * index / (count - 1)
                val selectedness = (1f - abs(bias - itemBias) / step).coerceIn(0f, 1f)

                val visualSelectedness = if (hasSelection) selectedness else 0f

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
                    val density = LocalDensity.current
                    val verticalOffset = with(density) {
                        ((-20).dp * visualSelectedness).toPx()
                    }

                    val iconSize = 44.dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.graphicsLayer {
                            translationY = verticalOffset
                            alpha = if (hasSelection) (0.82f + (0.18f * selectedness)) else 1f
                        },
                    ) {
                        Image(
                            painter = painterResource(bottomNavIcon(tab)),
                            contentDescription = tab.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(iconSize),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingSearchButton(
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val isLightMode = MaterialTheme.colorScheme.background.red > 0.5f
    val barBackgroundColor = if (isLightMode) {
        Color(0x732E5A44) // ~45% opaque green
    } else {
        DarkPillSurface.copy(alpha = 0.45f)
    }
    val rimColor = if (isLightMode) LightGoldText.copy(alpha = 0.45f) else DarkGoldBright.copy(alpha = 0.55f)

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(barBackgroundColor)
            .border(2.dp, if (isSelected) DarkGoldBright else rimColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Halo for selected search button
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            DarkGoldBright.copy(alpha = 0.45f),
                            DarkGoldBright.copy(alpha = 0.15f),
                            Color.Transparent,
                        ),
                        center = center,
                        radius = size.width / 1.2f,
                    ),
                    radius = size.width / 1.2f,
                    center = center,
                )
            }
        }

        Image(
            painter = painterResource(Res.drawable.nav_search),
            contentDescription = s.searchDesc,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(44.dp),
            alpha = if (isSelected) 1f else 0.82f
        )
    }
}

private fun bottomNavIcon(tab: Tab): DrawableResource =
    when (tab.name.lowercase()) {
        "home" -> Res.drawable.nav_home
        "explore" -> Res.drawable.nav_explore
        "prayers" -> Res.drawable.nav_prayers
        "search" -> Res.drawable.nav_search
        "settings" -> Res.drawable.nav_settings
        else -> Res.drawable.nav_home
    }
