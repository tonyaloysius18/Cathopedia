package com.ynotlabs.cathopedia.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.ceil

/** Alternating face-on and edge-on metal links, shared by the strand and full Rosary. */
internal fun DrawScope.drawRosaryChain(start: Offset, end: Offset, metal: Color) {
    val delta = end - start
    val length = delta.getDistance()
    if (length < 1f) return
    val count = ceil(length / 4.dp.toPx()).toInt().coerceAtLeast(1)
    val angle = (atan2(delta.y, delta.x) * 180f / kotlin.math.PI.toFloat()) - 90f
    repeat(count) { index ->
        val center = start + delta * ((index + 0.5f) / count)
        val width = if (index % 2 == 0) 3.6.dp.toPx() else 1.7.dp.toPx()
        val height = length / count + 1.5.dp.toPx()
        rotate(angle, center) {
            val corner = center - Offset(width / 2, height / 2)
            drawOval(metal.copy(alpha = 0.9f), corner, Size(width, height), style = Stroke(0.85.dp.toPx()))
            drawOval(
                Color.White.copy(alpha = 0.65f),
                corner + Offset(-0.25.dp.toPx(), -0.25.dp.toPx()),
                Size(width, height),
                style = Stroke(0.3.dp.toPx()),
            )
        }
    }
}
