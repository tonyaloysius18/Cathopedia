package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.ui.components.isReduceMotionEnabled
import com.ynotlabs.cathopedia.ui.theme.RosaryColors
import com.ynotlabs.cathopedia.ui.theme.rosaryColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// Model
// ─────────────────────────────────────────────────────────────────────────────

enum class BeadKind { CRUCIFIX, MEDAL, OUR_FATHER, HAIL_MARY }

/**
 * One tappable element of the rosary. [index] is *prayer order*, not draw order:
 * 0 = crucifix, 1..5 = the tail, 6 = the centrepiece medal, 7..61 = the loop.
 */
data class RosaryBead(
    val index: Int,
    val kind: BeadKind,
    val decade: Int?,           // 1..5 for loop beads, null on the tail
    val positionInDecade: Int?, // 1..10 for Hail Marys inside a decade
    val center: Offset,
    val radius: Float,
)

data class RosaryLayout(
    val beads: List<RosaryBead>,
    val loopPath: Path,
    val tailPath: Path,
) {
    /** Nearest-bead hit test. No dead zones: any tap inside [slop] picks a bead. */
    fun beadAt(point: Offset, slop: Float): RosaryBead? =
        beads.minByOrNull { (it.center - point).getDistanceSquared() }
            ?.takeIf { (it.center - point).getDistance() <= maxOf(it.radius * 2.4f, slop) }
}

private const val DECADES = 5
private const val BEADS_PER_DECADE = 11   // 1 Our Father + 10 Hail Marys
private const val LOOP_BEADS = DECADES * BEADS_PER_DECADE   // 55
private const val LOOP_SLOTS = LOOP_BEADS + 1               // + the medal

// ─────────────────────────────────────────────────────────────────────────────
// Geometry
//
// The loop is a circle whose radius is modulated by three summed sinusoids, so
// it reads as a rosary laid down on a table rather than a wireframe circle.
// The phases derive from [seed], so the shape is *stable* for a given seed —
// hit targets never move under the user's thumb mid-decade. Pass the day-of-year
// as the seed if you want the shape to change once a day.
// ─────────────────────────────────────────────────────────────────────────────

fun buildRosaryLayout(
    size: Size,
    seed: Int = 0,
    organic: Float = 1f,   // 0f = perfect circle, 1f = natural slack
): RosaryLayout {
    val loopCenter = Offset(size.width / 2f, size.height * 0.355f)
    val baseRadius = min(size.width * 0.42f, size.height * 0.30f)

    val p1 = seed * 0.7391f
    val p2 = seed * 1.6180f + 1.3f
    val p3 = seed * 2.4142f + 2.7f

    fun radiusAt(theta: Double): Float {
        val wobble = 0.045 * sin(3 * theta + p1) +
            0.030 * sin(5 * theta + p2) +
            0.020 * sin(2 * theta + p3)
        return baseRadius * (1f + organic * wobble.toFloat())
    }

    val hailMaryR = baseRadius * 0.052f
    val ourFatherR = hailMaryR * 1.55f
    val medalR = hailMaryR * 2.05f

    // ── Loop ────────────────────────────────────────────────────────────────
    // Slot 0 is the medal, sitting at the bottom of the loop (screen y grows
    // downward, so PI/2 is "south"). Beads run clockwise from there.
    val step = 2 * PI / LOOP_SLOTS
    val start = PI / 2

    val loopPoints = ArrayList<Offset>(LOOP_SLOTS)
    for (slot in 0 until LOOP_SLOTS) {
        val theta = start + slot * step
        val r = radiusAt(theta)
        loopPoints += Offset(
            loopCenter.x + (r * cos(theta)).toFloat(),
            loopCenter.y + (r * sin(theta)).toFloat(),
        )
    }

    val medalCenter = loopPoints[0]

    // ── Tail ────────────────────────────────────────────────────────────────
    // A cubic that falls from the medal with a slight sway, seeded like the loop.
    val sway = baseRadius * 0.16f * (if (seed % 2 == 0) 1f else -1f)
    val tailLength = size.height * 0.30f
    val tailEnd = Offset(medalCenter.x - sway * 0.35f, medalCenter.y + tailLength)
    val c1 = Offset(medalCenter.x + sway, medalCenter.y + tailLength * 0.30f)
    val c2 = Offset(medalCenter.x - sway * 0.8f, medalCenter.y + tailLength * 0.68f)

    fun onTail(t: Float): Offset {
        val u = 1 - t
        val x = u * u * u * medalCenter.x + 3 * u * u * t * c1.x + 3 * u * t * t * c2.x + t * t * t * tailEnd.x
        val y = u * u * u * medalCenter.y + 3 * u * u * t * c1.y + 3 * u * t * t * c2.y + t * t * t * tailEnd.y
        return Offset(x, y)
    }

    // Going *down* from the medal: Our Father, 3 Hail Marys, Our Father, crucifix.
    // Prayer order runs the other way — crucifix first — so we reverse below.
    val tailDown = listOf(
        Triple(0.20f, BeadKind.OUR_FATHER, ourFatherR),
        Triple(0.38f, BeadKind.HAIL_MARY, hailMaryR),
        Triple(0.52f, BeadKind.HAIL_MARY, hailMaryR),
        Triple(0.66f, BeadKind.HAIL_MARY, hailMaryR),
        Triple(0.84f, BeadKind.OUR_FATHER, ourFatherR),
    )

    // ── Assemble in prayer order ────────────────────────────────────────────
    val beads = ArrayList<RosaryBead>(62)

    beads += RosaryBead(
        index = 0,
        kind = BeadKind.CRUCIFIX,
        decade = null,
        positionInDecade = null,
        center = onTail(1f),
        radius = ourFatherR * 1.7f,
    )

    tailDown.reversed().forEachIndexed { i, (t, kind, r) ->
        beads += RosaryBead(
            index = 1 + i,
            kind = kind,
            decade = null,
            positionInDecade = null,
            center = onTail(t),
            radius = r,
        )
    }

    beads += RosaryBead(
        index = 6,
        kind = BeadKind.MEDAL,
        decade = null,
        positionInDecade = null,
        center = medalCenter,
        radius = medalR,
    )

    for (slot in 1 until LOOP_SLOTS) {
        val i = slot - 1                       // 0..54
        val decade = i / BEADS_PER_DECADE + 1  // 1..5
        val within = i % BEADS_PER_DECADE      // 0 = Our Father, 1..10 = Hail Marys
        val isOurFather = within == 0
        beads += RosaryBead(
            index = 7 + i,
            kind = if (isOurFather) BeadKind.OUR_FATHER else BeadKind.HAIL_MARY,
            decade = decade,
            positionInDecade = if (isOurFather) null else within,
            center = loopPoints[slot],
            radius = if (isOurFather) ourFatherR else hailMaryR,
        )
    }

    return RosaryLayout(
        beads = beads,
        loopPath = smoothClosedPath(loopPoints),
        tailPath = Path().apply {
            moveTo(medalCenter.x, medalCenter.y)
            cubicTo(c1.x, c1.y, c2.x, c2.y, tailEnd.x, tailEnd.y)
        },
    )
}

/** Quadratic smoothing through midpoints — cheap, and the chain reads as thread. */
private fun smoothClosedPath(points: List<Offset>): Path {
    val path = Path()
    if (points.size < 3) return path
    val first = midpoint(points.last(), points[0])
    path.moveTo(first.x, first.y)
    for (i in points.indices) {
        val current = points[i]
        val next = points[(i + 1) % points.size]
        val mid = midpoint(current, next)
        path.quadraticBezierTo(current.x, current.y, mid.x, mid.y)
    }
    path.close()
    return path
}

private fun midpoint(a: Offset, b: Offset) = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f)

// ─────────────────────────────────────────────────────────────────────────────
// Canvas
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun RosaryCanvas(
    currentIndex: Int,
    onBeadTap: (RosaryBead) -> Unit,
    modifier: Modifier = Modifier,
    seed: Int = 0,
    organic: Float = 1f,
    minTouchSlopPx: Float = 48f,
) {
    val s = LocalStrings.current
    val colors = rosaryColors()
    val reduceMotion = isReduceMotionEnabled()

    var scale by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var layout by remember(seed, organic) { mutableStateOf<RosaryLayout?>(null) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val pulseTransition by rememberInfiniteTransition(label = "bead-pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "bead-pulse",
    )
    // Respect reduced-motion: no pulsing, just a steady active bead.
    val pulse = if (reduceMotion) 1f else pulseTransition

    Canvas(
        modifier = modifier
            .fillMaxSize()
            // A Canvas has no child semantics nodes of its own — TalkBack/VoiceOver
            // would otherwise announce nothing at all here. One summarising,
            // skippable description stands in for the whole map instead of
            // exposing (or trying to expose) 62 individual bead taps.
            .clearAndSetSemantics { contentDescription = s.rosaryCanvasSummary }
            .pointerInput(Unit) {
                detectTransformGestures { _, panChange, zoomChange, _ ->
                    scale = (scale * zoomChange).coerceIn(1f, 3.5f)
                    pan = if (scale <= 1.001f) Offset.Zero else pan + panChange
                }
            }
            .pointerInput(layout, scale, pan) {
                detectTapGestures { tap ->
                    val current = layout ?: return@detectTapGestures
                    val pivot = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
                    // Invert the draw transform: screen = (world - pivot) * scale + pivot + pan
                    val world = (tap - pan - pivot) / scale + pivot
                    current.beadAt(world, minTouchSlopPx / scale)?.let(onBeadTap)
                }
            },
    ) {
        if (canvasSize != size || layout == null) {
            canvasSize = size
            layout = buildRosaryLayout(size, seed, organic)
        }
        val current = layout ?: return@Canvas

        withTransform({
            translate(pan.x, pan.y)
            scale(scale, scale, pivot = center)
        }) {
            drawPath(current.loopPath, colors.chain, style = Stroke(width = 2f))
            drawPath(current.tailPath, colors.chain, style = Stroke(width = 2f))

            current.beads.forEach { bead ->
                when (bead.kind) {
                    BeadKind.CRUCIFIX -> drawCrucifix(bead, bead.index == currentIndex, pulse, colors)
                    BeadKind.MEDAL -> drawMedal(bead, bead.index == currentIndex, pulse, colors)
                    else -> drawBead(bead, currentIndex, pulse, colors)
                }
            }
        }
    }
}

private fun DrawScope.drawBead(bead: RosaryBead, currentIndex: Int, pulse: Float, colors: RosaryColors) {
    val isCurrent = bead.index == currentIndex
    val isPrayed = bead.index < currentIndex

    if (isCurrent) {
        drawCircle(
            color = colors.candle.copy(alpha = 0.22f),
            radius = bead.radius * 2.6f * pulse,
            center = bead.center,
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colors.snow, if (isPrayed) colors.prayed else colors.glacier),
            center = bead.center - Offset(bead.radius * 0.35f, bead.radius * 0.35f),
            radius = bead.radius * 1.8f,
        ),
        radius = bead.radius,
        center = bead.center,
    )
    drawCircle(
        color = if (isCurrent) colors.candle else colors.marian.copy(alpha = 0.55f),
        radius = bead.radius,
        center = bead.center,
        style = Stroke(width = if (isCurrent) 2.4f else 1.2f),
    )
}

private fun DrawScope.drawMedal(bead: RosaryBead, isCurrent: Boolean, pulse: Float, colors: RosaryColors) {
    if (isCurrent) {
        drawCircle(colors.candle.copy(alpha = 0.20f), bead.radius * 2.1f * pulse, bead.center)
    }
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colors.snow, colors.marian),
            center = bead.center - Offset(bead.radius * 0.3f, bead.radius * 0.3f),
            radius = bead.radius * 1.9f,
        ),
        radius = bead.radius,
        center = bead.center,
    )
    drawCircle(colors.vespers.copy(alpha = 0.7f), bead.radius, bead.center, style = Stroke(1.6f))
    drawCircle(colors.snow.copy(alpha = 0.8f), bead.radius * 0.55f, bead.center, style = Stroke(1f))
}

private fun DrawScope.drawCrucifix(bead: RosaryBead, isCurrent: Boolean, pulse: Float, colors: RosaryColors) {
    val r = bead.radius
    val c = bead.center
    val arm = r * 0.95f
    val thickness = r * 0.42f

    if (isCurrent) {
        drawCircle(colors.candle.copy(alpha = 0.18f), r * 2.4f * pulse, c)
    }

    val body = Path().apply {
        // Vertical
        addRect(
            androidx.compose.ui.geometry.Rect(
                left = c.x - thickness / 2, top = c.y - r * 1.15f,
                right = c.x + thickness / 2, bottom = c.y + r * 1.45f,
            )
        )
        // Transverse, set high in the classic proportion
        addRect(
            androidx.compose.ui.geometry.Rect(
                left = c.x - arm, top = c.y - r * 0.35f,
                right = c.x + arm, bottom = c.y - r * 0.35f + thickness,
            )
        )
    }
    drawPath(body, colors.vespers)
    drawPath(body, if (isCurrent) colors.candle else colors.marian, style = Stroke(1.4f))
}
