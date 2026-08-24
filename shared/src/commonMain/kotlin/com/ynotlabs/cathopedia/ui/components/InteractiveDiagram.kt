package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

/**
 * Zoom/pan/tap artwork viewer with tappable [hotspots]. Pinch-zooms and pans within [minZoom]..
 * [maxZoom], clamped so the artwork can never be dragged past its own edge. Tapping a hotspot
 * opens a bottom sheet with its label/blurb and, when [DiagramHotspot.hasTarget] is set, a
 * "read more" action via [onReadMore] — the caller resolves what the target actually is, this
 * component only knows the hotspot's own data. Hotspots sharing a non-null [DiagramHotspot.order]
 * get previous/next controls on the sheet for a guided tour.
 *
 * [onHotspotTap] fires immediately on every tap-selected hotspot, independent of the sheet — for
 * a caller like the Rosary screen where tapping a bead should advance the prayer right away, not
 * wait on a "read more" tap. Set [showDetailSheet] false to suppress the sheet entirely for that
 * kind of "the artwork itself is the control" use. [highlightedId], when set, draws that one
 * hotspot's hint outline emphasized (thicker, brighter) regardless of [showHotspotHints] — e.g.
 * the Rosary's current bead.
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
    showHotspotHints: Boolean = true,
    showDetailSheet: Boolean = true,
    highlightedId: String? = null,
    readMoreLabel: String = "",
    onReadMore: (DiagramHotspot) -> Unit = {},
    onHotspotTap: (DiagramHotspot) -> Unit = {},
) {
    var scale by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(Size.Zero) }
    var selected by remember { mutableStateOf<DiagramHotspot?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(18.dp))
            .onGloballyPositioned { containerSize = Size(it.size.width.toFloat(), it.size.height.toFloat()) }
            .pointerInput(minZoom, maxZoom) {
                detectTransformGestures { _, panChange, zoomChange, _ ->
                    val newScale = (scale * zoomChange).coerceIn(minZoom, maxZoom)
                    val maxPanX = (maxOf(0f, (newScale - 1f) * containerSize.width)) / 2f
                    val maxPanY = (maxOf(0f, (newScale - 1f) * containerSize.height)) / 2f
                    scale = newScale
                    pan = Offset(
                        (pan.x + panChange.x).coerceIn(-maxPanX, maxPanX),
                        (pan.y + panChange.y).coerceIn(-maxPanY, maxPanY),
                    )
                }
            }
            .pointerInput(hotspots, scale, pan, containerSize) {
                detectTapGestures { tap ->
                    if (containerSize.width <= 0f || containerSize.height <= 0f) return@detectTapGestures
                    val pivot = Offset(containerSize.width / 2f, containerSize.height / 2f)
                    val world = (tap - pan - pivot) / scale + pivot
                    val normalized = Offset(world.x / containerSize.width, world.y / containerSize.height)
                    val hit = hotspots.asReversed().firstOrNull { hitTest(it.shape, normalized) }
                    if (hit != null) {
                        onHotspotTap(hit)
                        if (showDetailSheet) selected = hit
                    }
                }
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = pan.x
                    translationY = pan.y
                },
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )

            if (showHotspotHints || highlightedId != null) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    hotspots.forEach { hotspot ->
                        val isHighlighted = hotspot.id == highlightedId
                        if (isHighlighted || showHotspotHints) {
                            drawHotspotHint(hotspot.shape, size, emphasized = isHighlighted)
                        }
                    }
                }
            }
        }
    }

    selected?.let { hotspot ->
        val tourHotspots = hotspots.filter { it.order != null }.sortedBy { it.order }
        val tourIndex = tourHotspots.indexOfFirst { it.id == hotspot.id }
        val hasPrev = tourIndex > 0
        val hasNext = tourIndex in 0 until tourHotspots.size - 1

        ModalBottomSheet(
            onDismissRequest = { selected = null },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp)) {
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
                        IconButton(onClick = { if (hasPrev) selected = tourHotspots[tourIndex - 1] }, enabled = hasPrev) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                        IconButton(onClick = { if (hasNext) selected = tourHotspots[tourIndex + 1] }, enabled = hasNext) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
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
        readMoreLabel = "Read more",
    )
}
