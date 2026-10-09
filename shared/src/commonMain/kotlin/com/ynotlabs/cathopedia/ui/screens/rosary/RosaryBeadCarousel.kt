package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.rosary.BeadKind
import com.ynotlabs.cathopedia.rosary.RosaryBead
import com.ynotlabs.cathopedia.rosary.RosaryState
import com.ynotlabs.cathopedia.rosary.rosaryLayout
import com.ynotlabs.cathopedia.ui.components.beadSprite
import com.ynotlabs.cathopedia.ui.components.drawRosaryChain
import com.ynotlabs.cathopedia.ui.theme.rosaryColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/** Physical elements appear once, with the crucifix hanging below the pendant. */
@Composable
internal fun RosaryBeadCarousel(
    state: RosaryState,
    strings: Map<String, String>,
    enabled: Boolean,
    onNodeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val beads = remember { rosaryLayout.asReversed() }
    val currentIndex = beads.indexOfFirst { it.index == state.currentStep.beadIndex }.coerceAtLeast(0)
    val density = LocalDensity.current
    fun centerOffset(index: Int) = with(density) { ((slotHeight(beads[index]) - 48.dp) / 2).roundToPx() }
    val listState = rememberLazyListState(currentIndex, centerOffset(currentIndex))
    val latestSelection by rememberUpdatedState(onNodeSelected)
    val latestIndex by rememberUpdatedState(currentIndex)
    val scope = rememberCoroutineScope()
    var alignmentJob by remember { mutableStateOf<Job?>(null) }
    val surface = MaterialTheme.colorScheme.background
    val glow = rosaryColors().candle
    val metal = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)

    fun alignTo(index: Int) {
        alignmentJob?.cancel()
        alignmentJob = scope.launch {
            listState.animateScrollToItem(index, centerOffset(index))
        }
    }

    fun neighborIndex(index: Int, forward: Boolean): Int = beads.indexOfFirst {
        it.index == adjacentCarouselBead(beads[index].index, forward)
    }

    fun distanceBetween(first: Int, second: Int): Float {
        if (first == second) return 0f
        val range = minOf(first, second)..maxOf(first, second)
        val distance = range.sumOf { index ->
            val fraction = if (index == first || index == second) 0.5 else 1.0
            slotHeight(beads[index]).value.toDouble() * fraction
        }
        return with(density) { distance.toFloat().dp.toPx() }
    }

    LaunchedEffect(currentIndex) { alignTo(currentIndex) }

    BoxWithConstraints(
        modifier = modifier.clipToBounds().semantics {
            contentDescription = strings[RosaryPrayingStringKeys.CarouselDescription].orEmpty()
            if (enabled) {
                customActions = listOf(
                    CustomAccessibilityAction(strings[RosaryPrayingStringKeys.Previous].orEmpty()) {
                        latestSelection(beads[neighborIndex(latestIndex, forward = false)].index)
                        true
                    },
                    CustomAccessibilityAction(strings[RosaryPrayingStringKeys.Next].orEmpty()) {
                        latestSelection(beads[neighborIndex(latestIndex, forward = true)].index)
                        true
                    },
                )
            }
        }.pointerInput(enabled, density) {
            if (!enabled) return@pointerInput
            var startIndex = latestIndex
            var dragOffset = 0f
            var forwardLimit = 0f
            var backwardLimit = 0f
            val threshold = with(density) { 6.dp.toPx() }
            detectVerticalDragGestures(
                onDragStart = {
                    alignmentJob?.cancel()
                    startIndex = latestIndex
                    dragOffset = 0f
                    forwardLimit = distanceBetween(startIndex, neighborIndex(startIndex, forward = true))
                    backwardLimit = distanceBetween(startIndex, neighborIndex(startIndex, forward = false))
                    // Start every gesture at the selected bead, including a swipe
                    // that interrupts the previous bead's settling animation.
                    listState.requestScrollToItem(startIndex, centerOffset(startIndex))
                },
                onVerticalDrag = { change, amount ->
                    change.consume()
                    // Direct manipulation stays within one neighboring bead;
                    // long drags and fast flicks cannot fling through the rosary.
                    val nextOffset = (dragOffset + amount).coerceIn(-backwardLimit, forwardLimit)
                    listState.dispatchRawDelta(dragOffset - nextOffset)
                    dragOffset = nextOffset
                },
                onDragEnd = {
                    val target = when {
                        dragOffset >= threshold -> neighborIndex(startIndex, forward = true)
                        dragOffset <= -threshold -> neighborIndex(startIndex, forward = false)
                        else -> startIndex
                    }
                    if (target == latestIndex) alignTo(target)
                    else latestSelection(beads[target].index)
                },
                onDragCancel = { alignTo(latestIndex) },
            )
        },
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = ((maxHeight - slotHeight(beads.first())) / 2).coerceAtLeast(0.dp),
                bottom = ((maxHeight - slotHeight(beads.last())) / 2).coerceAtLeast(0.dp),
            ),
            // The parent handles one gesture at a time instead of allowing
            // the list's velocity-based fling to skip physical beads.
            userScrollEnabled = false,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(beads, key = { _, bead -> bead.index }) { index, bead ->
                val current = bead.index == state.currentStep.beadIndex
                val highlight by animateFloatAsState(
                    targetValue = if (current) 1f else 0f,
                    animationSpec = tween(180),
                    label = "beadHighlight",
                )
                Box(
                    modifier = Modifier.fillMaxWidth().height(slotHeight(bead))
                        .clickable(enabled = enabled) { latestSelection(bead.index) }
                        .semantics(mergeDescendants = true) { selected = current },
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val x = size.width / 2
                        drawRosaryChain(
                            Offset(x, if (index == 0) size.height / 2 else 0f),
                            Offset(x, if (index == beads.lastIndex) size.height / 2 else size.height),
                            metal,
                        )
                    }
                    if (highlight > 0f) {
                        Canvas(Modifier.fillMaxSize()) {
                            val radius = (if (bead.kind == BeadKind.HAIL_MARY) 38.dp else 46.dp).toPx()
                            drawCircle(
                                brush = Brush.radialGradient(
                                    0f to glow.copy(alpha = 0.65f * highlight),
                                    0.5f to glow.copy(alpha = 0.38f * highlight),
                                    1f to Color.Transparent,
                                    center = center,
                                    radius = radius,
                                ),
                                radius = radius,
                            )
                        }
                    }
                    beadSprite(bead)?.let { sprite ->
                        val brightness = 0.65f + 0.35f * highlight
                        Image(
                            painterResource(sprite),
                            contentDescription = beadDescription(bead, strings),
                            contentScale = ContentScale.Fit,
                            // Preserve the bead's opacity so dimming does not
                            // reveal the chain running behind its body.
                            colorFilter = ColorFilter.tint(
                                Color(brightness, brightness, brightness),
                                BlendMode.Modulate,
                            ),
                            modifier = Modifier.size(spriteSize(bead)).graphicsLayer {
                                scaleX = 1f + 0.08f * highlight
                                scaleY = 1f + 0.08f * highlight
                            },
                        )
                    }
                }
            }
        }
        Box(Modifier.align(Alignment.TopCenter).fillMaxWidth().height(28.dp)
            .background(Brush.verticalGradient(listOf(surface, Color.Transparent))))
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(28.dp)
            .background(Brush.verticalGradient(listOf(Color.Transparent, surface))))
    }
}

/** Every physical element, including the Marian medal, is a separate swipe stop. */
internal fun adjacentCarouselBead(nodeIndex: Int, forward: Boolean): Int {
    val current = rosaryLayout.indexOfFirst { it.index == nodeIndex }
    if (current < 0) return nodeIndex
    val direction = if (forward) 1 else -1
    val next = (current + direction).coerceIn(rosaryLayout.indices)
    return rosaryLayout[next].index
}

private fun slotHeight(bead: RosaryBead): Dp = when (bead.kind) {
    BeadKind.CROSS -> 112.dp
    BeadKind.CENTERPIECE -> 84.dp
    BeadKind.OUR_FATHER -> 72.dp
    BeadKind.HAIL_MARY -> 48.dp
}

private fun spriteSize(bead: RosaryBead): Dp = when (bead.kind) {
    BeadKind.CROSS -> 100.dp
    BeadKind.CENTERPIECE -> 76.dp
    BeadKind.OUR_FATHER -> 64.dp
    BeadKind.HAIL_MARY -> 36.dp
}

private fun beadDescription(bead: RosaryBead, strings: Map<String, String>): String = strings[
    when (bead.kind) {
        BeadKind.CROSS -> RosaryPrayingStringKeys.BeadCross
        BeadKind.OUR_FATHER -> RosaryPrayingStringKeys.BeadOurFather
        BeadKind.CENTERPIECE -> RosaryPrayingStringKeys.BeadCenterpiece
        BeadKind.HAIL_MARY -> RosaryPrayingStringKeys.BeadHailMary
    },
].orEmpty()
