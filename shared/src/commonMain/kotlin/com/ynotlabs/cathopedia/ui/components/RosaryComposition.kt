package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.rosary.BeadKind
import com.ynotlabs.cathopedia.rosary.RosaryBead
import com.ynotlabs.cathopedia.rosary.rosaryLayout
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.rosary_bead_hm_01
import com.ynotlabs.cathopedia.resources.rosary_bead_hm_02
import com.ynotlabs.cathopedia.resources.rosary_bead_hm_03
import com.ynotlabs.cathopedia.resources.rosary_bead_of_01
import com.ynotlabs.cathopedia.resources.rosary_centerpiece_medal
import com.ynotlabs.cathopedia.resources.rosary_cross_pearl
import com.ynotlabs.cathopedia.ui.theme.rosaryColors
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** The artwork the tuned coordinates were laid out against (portrait). */
private const val ROSARY_ASPECT = 600f / 900f

// Only three Hail Mary sprites ship so far (bead_hm_04..06 are pending — see
// tools/rosary_sprite_report.md). The six-way spriteVariant cycles onto the
// three available patterns so adjacent beads still differ and the loop reads as
// real beads rather than placeholders; drop in the missing PNGs and widen this
// list and they light up automatically.
private val hailMarySprites: List<DrawableResource> = listOf(
    Res.drawable.rosary_bead_hm_01,
    Res.drawable.rosary_bead_hm_02,
    Res.drawable.rosary_bead_hm_03,
)

/** The sprite for a bead, or null if its art is genuinely missing (drawn as a placeholder). */
fun beadSprite(bead: RosaryBead): DrawableResource? = when (bead.kind) {
    BeadKind.CROSS -> Res.drawable.rosary_cross_pearl
    BeadKind.CENTERPIECE -> Res.drawable.rosary_centerpiece_medal
    BeadKind.OUR_FATHER -> Res.drawable.rosary_bead_of_01
    BeadKind.HAIL_MARY -> hailMarySprites.getOrNull((bead.spriteVariant - 1) % hailMarySprites.size)
}

/**
 * The whole Rosary composed from the individual bead sprites (A4) — never a
 * single flat image. The cord is drawn in Compose behind the beads, following
 * the same [rosaryLayout] path. Sizes and positions are fractions of the box, so
 * this scales cleanly and the geometry is reusable (A8).
 *
 * A missing sprite falls back to a drawn placeholder disc rather than crashing.
 */
@Composable
fun RosaryComposition(
    modifier: Modifier = Modifier,
    beads: List<RosaryBead> = rosaryLayout,
) {
    val colors = rosaryColors()
    BoxWithConstraints(modifier.aspectRatio(ROSARY_ASPECT)) {
        val w = maxWidth
        val h = maxHeight

        Canvas(Modifier.size(w, h)) { drawCord(beads, colors.chain) }

        beads.forEach { bead ->
            val diameter = w * (bead.radius * 2f)
            val left = w * bead.x - diameter / 2
            val top = h * bead.y - diameter / 2
            val sprite = beadSprite(bead)
            val beadModifier = Modifier.offset(x = left, y = top).size(diameter)
            if (sprite != null) {
                Image(
                    painter = painterResource(sprite),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = beadModifier,
                )
            } else {
                // Placeholder disc — keeps the layout intact when a sprite is absent.
                Box(
                    beadModifier
                        .clip(CircleShape)
                        .drawBehind { drawPlaceholder(colors.marian) },
                )
            }
        }
    }
}

private fun DrawScope.drawCord(beads: List<RosaryBead>, color: Color) {
    if (beads.isEmpty()) return
    fun point(index: Int): Offset? = beads.firstOrNull { it.index == index }?.let {
        Offset(it.x * size.width, it.y * size.height)
    }

    val strokePx = 2.dp.toPx()
    val cord = Path()
    // Pendant: crucifix up to the centerpiece.
    point(0)?.let { cord.moveTo(it.x, it.y) }
    for (i in 1..6) point(i)?.let { cord.lineTo(it.x, it.y) }
    // Loop: centerpiece around the five decades and back to the centerpiece.
    point(6)?.let { cord.moveTo(it.x, it.y) }
    for (i in 7..61) point(i)?.let { cord.lineTo(it.x, it.y) }
    point(6)?.let { cord.lineTo(it.x, it.y) }

    drawPath(cord, color = color, style = Stroke(width = strokePx))
}

private fun DrawScope.drawPlaceholder(color: Color) {
    drawRoundRect(
        color = color.copy(alpha = 0.55f),
        cornerRadius = CornerRadius(size.minDimension / 2f, size.minDimension / 2f),
    )
}
