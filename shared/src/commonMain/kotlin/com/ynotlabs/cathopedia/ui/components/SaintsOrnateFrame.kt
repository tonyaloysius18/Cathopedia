package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.saints_feast_ornate_frame
import org.jetbrains.compose.resources.imageResource
import kotlin.math.roundToInt

/** Decorative alpha artwork: only the straight rails stretch as the card's text grows. */
@Composable
internal fun SaintsOrnateFrame(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val frame = imageResource(Res.drawable.saints_feast_ornate_frame)
    // These coordinates describe the transparent window and ornament bands in the asset.
    val sourceWidth = 1484f
    val bands = listOf(0, 400, 550, 660, 810, 1060)
    val fixedHeight = 400 + 110 + 250
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val sideInset = maxWidth * (60f / sourceWidth)
        val topInset = maxWidth * (262f / sourceWidth)
        val bottomInset = maxWidth * (100f / sourceWidth)
        val minimumWindowHeight = maxWidth * ((fixedHeight - 262f - 100f) / sourceWidth) + 1.dp
        Box(
            Modifier.fillMaxWidth()
                .padding(start = sideInset, end = sideInset, top = topInset, bottom = bottomInset)
                .heightIn(min = minimumWindowHeight),
            propagateMinConstraints = true,
            content = content,
        )
        Canvas(Modifier.matchParentSize()) {
            // Compose may briefly return a 1px placeholder while the PNG loads.
            if (frame.width <= 1 || frame.height <= 1) return@Canvas
            val scale = size.width / sourceWidth
            val sourceScale = frame.height / 1060f
            val flexibleHeight = ((size.height - fixedHeight * scale) / 2f).coerceAtLeast(0f)
            var top = 0f
            for (index in 0 until bands.lastIndex) {
                val sourceHeight = bands[index + 1] - bands[index]
                val height = if (index == 1 || index == 3) flexibleHeight else sourceHeight * scale
                val bottom = if (index == bands.lastIndex - 1) size.height else top + height
                val topPx = top.roundToInt()
                val bottomPx = bottom.roundToInt()
                if (bottomPx > topPx) {
                    val sourceTop = (bands[index] * sourceScale).roundToInt()
                    val sourceBottom = (bands[index + 1] * sourceScale).roundToInt()
                    drawImage(
                        image = frame,
                        srcOffset = IntOffset(0, sourceTop),
                        srcSize = IntSize(frame.width, sourceBottom - sourceTop),
                        dstOffset = IntOffset(0, topPx),
                        dstSize = IntSize(size.width.roundToInt(), bottomPx - topPx),
                        filterQuality = FilterQuality.High,
                    )
                }
                top = bottom
            }
        }
    }
}
