package com.ynotlabs.cathopedia.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.back_arrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/**
 * UI-layer hotspot for [InteractiveDiagram] — deliberately not the content-layer `Hotspot` type
 * (see HubContentModels.kt) so this component stays reusable outside the hub feature (the Rosary
 * screen is the next planned caller, per docs/briefs/topic-hubs.md T6). All shape coordinates are
 * normalized `0f..1f` against the artwork's own box, never pixels.
 */
data class DiagramHotspot(
    val id: String,
    val label: String,
    val blurb: String? = null,
    val order: Int? = null,
    val hasTarget: Boolean = false,
    val shape: DiagramHotspotShape,
)

sealed interface DiagramHotspotShape

data class DiagramRectShape(val x: Float, val y: Float, val w: Float, val h: Float) : DiagramHotspotShape

data class DiagramCircleShape(val cx: Float, val cy: Float, val r: Float) : DiagramHotspotShape

/** [points] are normalized `0f..1f`, wound in any order — hit-tested by ray casting. */
data class DiagramPolygonShape(val points: List<Offset>) : DiagramHotspotShape

/** Where the centre of a hotspot sits, in the artwork's own normalized space. */
private val DiagramHotspotShape.center: Offset
    get() = when (this) {
        is DiagramRectShape -> Offset(x + w / 2f, y + h / 2f)
        is DiagramCircleShape -> Offset(cx, cy)
        is DiagramPolygonShape -> points
            .fold(Offset.Zero) { acc, p -> acc + p }
            .let { if (points.isEmpty()) Offset(0.5f, 0.5f) else it / points.size.toFloat() }
    }

/**
 * Zoom/pan/tap artwork viewer with tappable [hotspots]. Pinch-zooms and pans within [minZoom]..
 * [maxZoom], clamped so the artwork can never be dragged past its own edge. Tapping a hotspot
 * animates the view onto it and opens a bottom sheet with its label/blurb and, when
 * [DiagramHotspot.hasTarget] is set, a "read more" action via [onReadMore] — the caller resolves
 * what the target actually is, this component only knows the hotspot's own data.
 *
 * By default the viewer takes the artwork's own [aspectRatio], which is right for a roughly square
 * diagram. Pass [viewportHeight] for artwork whose proportions would otherwise run off the screen —
 * the Sistine ceiling is three times taller than it is wide — and the artwork is fitted inside a
 * viewport of that height instead, letterboxed and centred. Hotspot coordinates stay normalized to
 * the artwork, so the hit-testing accounts for the letterbox rather than the viewport.
 *
 * Hotspots with a non-null [DiagramHotspot.order] form a guided tour: previous/next controls on the
 * sheet, and — with [showTourRail] — a rail of numbered stops beneath the artwork. The rail is also
 * the accessible path through the diagram, since a hotspot drawn on a canvas is invisible to a
 * screen reader in a way a row of labelled buttons is not.
 *
 * [onHotspotTap] fires immediately on every tap-selected hotspot, independent of the sheet — for
 * a caller like the Rosary screen where tapping a bead should advance the prayer right away, not
 * wait on a "read more" tap. Set [showDetailSheet] false to suppress the sheet entirely for that
 * kind of "the artwork itself is the control" use. [highlightedId], when set, draws that one
 * hotspot's hint outline emphasized (thicker, brighter) regardless of [showHotspotHints].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveDiagram(
    painter: Painter,
    aspectRatio: Float,
    hotspots: List<DiagramHotspot>,
    modifier: Modifier = Modifier,
    minZoom: Float = 1f,
    maxZoom: Float = 5f,
    viewportHeight: Dp? = null,
    showHotspotHints: Boolean = true,
    showDetailSheet: Boolean = true,
    showTourRail: Boolean = false,
    highlightedId: String? = null,
    readMoreLabel: String = "",
    resetLabel: String = "",
    hintText: String = "",
    accessibilityLabel: String = "",
    onReadMore: (DiagramHotspot) -> Unit = {},
    onHotspotTap: (DiagramHotspot) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val pan = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var viewportSize by remember { mutableStateOf(Size.Zero) }
    var selected by remember { mutableStateOf<DiagramHotspot?>(null) }
    var hintDismissed by remember { mutableStateOf(false) }

    val tour = remember(hotspots) { hotspots.filter { it.order != null }.sortedBy { it.order } }
    val railState = rememberLazyListState()

    // The artwork keeps its own proportions inside the viewport; when the two differ the
    // remainder is letterbox, and every coordinate below is relative to the artwork, not the box.
    val content = remember(viewportSize, aspectRatio) { fitInside(viewportSize, aspectRatio) }
    val contentOrigin = remember(viewportSize, content) {
        Offset((viewportSize.width - content.width) / 2f, (viewportSize.height - content.height) / 2f)
    }

    fun clampPan(target: Offset, atScale: Float): Offset {
        val maxX = maxOf(0f, (atScale - 1f) * content.width) / 2f
        val maxY = maxOf(0f, (atScale - 1f) * content.height) / 2f
        return Offset(target.x.coerceIn(-maxX, maxX), target.y.coerceIn(-maxY, maxY))
    }

    /** Moves [normalized] (artwork space) to the centre of the viewport at [targetScale]. */
    fun focusOn(normalized: Offset, targetScale: Float) {
        if (content.width <= 0f || content.height <= 0f) return
        val pivot = Offset(content.width / 2f, content.height / 2f)
        val point = Offset(normalized.x * content.width, normalized.y * content.height)
        val wanted = clampPan(-(point - pivot) * targetScale, targetScale)
        scope.launch { scale.animateTo(targetScale, tween(420)) }
        scope.launch { pan.animateTo(wanted, tween(420)) }
    }

    fun reset() {
        scope.launch { scale.animateTo(1f, tween(320)) }
        scope.launch { pan.animateTo(Offset.Zero, tween(320)) }
    }

    fun select(hotspot: DiagramHotspot, focus: Boolean) {
        onHotspotTap(hotspot)
        if (focus) focusOn(hotspot.shape.center, FOCUS_ZOOM.coerceIn(minZoom, maxZoom))
        if (showDetailSheet) selected = hotspot
        hintDismissed = true
    }

    /** Viewport point -> artwork-normalized point, undoing the letterbox then the transform. */
    fun normalize(point: Offset): Offset? {
        if (content.width <= 0f || content.height <= 0f) return null
        val pivot = Offset(content.width / 2f, content.height / 2f)
        val local = (point - contentOrigin - pan.value - pivot) / scale.value + pivot
        return Offset(local.x / content.width, local.y / content.height)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (viewportHeight != null) Modifier.height(viewportHeight) else Modifier.aspectRatio(aspectRatio))
                .clip(RoundedCornerShape(18.dp))
                .semantics { if (accessibilityLabel.isNotEmpty()) contentDescription = accessibilityLabel }
                .onGloballyPositioned { viewportSize = Size(it.size.width.toFloat(), it.size.height.toFloat()) }
                .pointerInput(minZoom, maxZoom, content) {
                    detectTransformGestures { _, panChange, zoomChange, _ ->
                        val next = (scale.value * zoomChange).coerceIn(minZoom, maxZoom)
                        scope.launch { scale.snapTo(next) }
                        scope.launch { pan.snapTo(clampPan(pan.value + panChange, next)) }
                        hintDismissed = true
                    }
                }
                .pointerInput(hotspots, content, contentOrigin) {
                    detectTapGestures(
                        onDoubleTap = { tap ->
                            if (scale.value > 1.05f) {
                                reset()
                            } else {
                                normalize(tap)?.let { focusOn(it, DOUBLE_TAP_ZOOM.coerceIn(minZoom, maxZoom)) }
                            }
                            hintDismissed = true
                        },
                        onTap = { tap ->
                            val normalized = normalize(tap) ?: return@detectTapGestures
                            val hit = hotspots.asReversed().firstOrNull { hitTest(it.shape, normalized) }
                            if (hit != null) select(hit, focus = true) else hintDismissed = true
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio, matchHeightConstraintsFirst = viewportHeight != null)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        translationX = pan.value.x
                        translationY = pan.value.y
                    },
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                )

                if (showHotspotHints || highlightedId != null || selected != null) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        hotspots.forEach { hotspot ->
                            val emphasized = hotspot.id == highlightedId || hotspot.id == selected?.id
                            if (emphasized || showHotspotHints) {
                                drawHotspotHint(hotspot.shape, size, emphasized = emphasized)
                            }
                        }
                    }
                }
            }

            // Once zoomed into a six-times enlargement there is no obvious way back out, so the
            // way back is an explicit control rather than a gesture the reader has to guess.
            if (scale.value > 1.05f) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.55f),
                    contentColor = Color.White,
                    onClick = { reset() },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        if (resetLabel.isNotEmpty()) {
                            Spacer(Modifier.width(6.dp))
                            Text(text = resetLabel, fontSize = 11.sp)
                        }
                    }
                }
            }

            if (!hintDismissed && hintText.isNotEmpty()) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.55f),
                    contentColor = Color.White,
                ) {
                    Text(
                        text = hintText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    )
                }
            }
        }

        if (showTourRail && tour.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            LazyRow(
                state = railState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
            ) {
                itemsIndexed(tour, key = { _, h -> h.id }) { index, hotspot ->
                    TourChip(
                        number = index + 1,
                        label = hotspot.label,
                        selected = hotspot.id == selected?.id,
                        onClick = { select(hotspot, focus = true) },
                    )
                }
            }
        }
    }

    // Keep the rail's scroll position with the tour, so stepping through the sheet does not leave
    // the corresponding chip somewhere off-screen.
    LaunchedEffect(selected?.id) {
        val index = tour.indexOfFirst { it.id == selected?.id }
        if (index >= 0) railState.animateScrollToItem(index)
    }

    selected?.let { hotspot ->
        val tourIndex = tour.indexOfFirst { it.id == hotspot.id }
        val hasPrev = tourIndex > 0
        val hasNext = tourIndex in 0 until tour.size - 1

        ModalBottomSheet(
            onDismissRequest = { selected = null },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp)) {
                if (tourIndex >= 0) {
                    Text(
                        text = "${tourIndex + 1} / ${tour.size}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(6.dp))
                }

                Text(text = hotspot.label, fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Medium)

                hotspot.blurb?.let { blurb ->
                    Spacer(Modifier.height(8.dp))
                    Text(text = blurb, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (hotspot.hasTarget && readMoreLabel.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = readMoreLabel,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onReadMore(hotspot) },
                    )
                }

                if (tourIndex >= 0 && (hasPrev || hasNext)) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { if (hasPrev) select(tour[tourIndex - 1], focus = true) },
                            enabled = hasPrev,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.back_arrow),
                                contentDescription = if (hasPrev) tour[tourIndex - 1].label else null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        // Naming the next stop turns a bare arrow into a reason to press it.
                        Text(
                            text = if (hasNext) tour[tourIndex + 1].label else "",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false).padding(horizontal = 8.dp),
                        )
                        IconButton(
                            onClick = { if (hasNext) select(tour[tourIndex + 1], focus = true) },
                            enabled = hasNext,
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = if (hasNext) tour[tourIndex + 1].label else null,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Zoom applied when a hotspot is selected, and by a double tap on empty artwork. */
private const val FOCUS_ZOOM = 2.6f
private const val DOUBLE_TAP_ZOOM = 2.2f

/** The largest box of [aspectRatio] (w/h) that fits inside [outer], centred. */
private fun fitInside(outer: Size, aspectRatio: Float): Size {
    if (outer.width <= 0f || outer.height <= 0f || aspectRatio <= 0f) return Size.Zero
    return if (outer.width / outer.height > aspectRatio) {
        Size(outer.height * aspectRatio, outer.height)
    } else {
        Size(outer.width, outer.width / aspectRatio)
    }
}

@Composable
private fun TourChip(number: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, accent.copy(alpha = if (selected) 0.7f else 0.25f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = if (selected) 0.9f else 0.35f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.surface,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

private fun hitTest(shape: DiagramHotspotShape, point: Offset): Boolean = when (shape) {
    is DiagramRectShape -> point.x in shape.x..(shape.x + shape.w) && point.y in shape.y..(shape.y + shape.h)
    is DiagramCircleShape -> {
        val dx = point.x - shape.cx
        val dy = point.y - shape.cy
        (dx * dx + dy * dy) <= shape.r * shape.r
    }
    is DiagramPolygonShape -> pointInPolygon(point, shape.points)
}

/** Standard ray-casting point-in-polygon test — points/edges compared in normalized space. */
private fun pointInPolygon(point: Offset, points: List<Offset>): Boolean {
    if (points.size < 3) return false
    var inside = false
    var j = points.size - 1
    for (i in points.indices) {
        val pi = points[i]
        val pj = points[j]
        val intersects = (pi.y > point.y) != (pj.y > point.y) &&
            point.x < (pj.x - pi.x) * (point.y - pi.y) / (pj.y - pi.y) + pi.x
        if (intersects) inside = !inside
        j = i
    }
    return inside
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHotspotHint(
    shape: DiagramHotspotShape,
    canvasSize: Size,
    emphasized: Boolean = false,
) {
    val strokeColor = if (emphasized) Color(0xFFE0C848) else Color.White.copy(alpha = 0.55f)
    val stroke = Stroke(width = if (emphasized) 3.5f else 1.5f)
    when (shape) {
        is DiagramRectShape -> drawRect(
            color = strokeColor,
            topLeft = Offset(shape.x * canvasSize.width, shape.y * canvasSize.height),
            size = Size(shape.w * canvasSize.width, shape.h * canvasSize.height),
            style = stroke,
        )
        is DiagramCircleShape -> drawCircle(
            color = strokeColor,
            radius = shape.r * minOf(canvasSize.width, canvasSize.height),
            center = Offset(shape.cx * canvasSize.width, shape.cy * canvasSize.height),
            style = stroke,
        )
        is DiagramPolygonShape -> if (shape.points.size >= 3) {
            val path = androidx.compose.ui.graphics.Path().apply {
                val first = shape.points.first()
                moveTo(first.x * canvasSize.width, first.y * canvasSize.height)
                shape.points.drop(1).forEach { p -> lineTo(p.x * canvasSize.width, p.y * canvasSize.height) }
                close()
            }
            drawPath(path, strokeColor, style = stroke)
        }
    }
}

@Preview
@Composable
private fun InteractiveDiagramPreview() {
    InteractiveDiagram(
        painter = ColorPainter(Color(0xFF2B2620)),
        aspectRatio = 1.4f,
        hotspots = listOf(
            DiagramHotspot(
                id = "nave",
                label = "The Nave",
                blurb = "The long central hall where the faithful gather.",
                order = 1,
                hasTarget = true,
                shape = DiagramRectShape(x = 0.1f, y = 0.35f, w = 0.3f, h = 0.2f),
            ),
            DiagramHotspot(
                id = "dome",
                label = "The Dome",
                blurb = "Rising above the high altar.",
                order = 2,
                shape = DiagramCircleShape(cx = 0.65f, cy = 0.3f, r = 0.12f),
            ),
        ),
        showTourRail = true,
        readMoreLabel = "Read more",
        resetLabel = "Fit",
        hintText = "Pinch to zoom · tap a panel",
    )
}
