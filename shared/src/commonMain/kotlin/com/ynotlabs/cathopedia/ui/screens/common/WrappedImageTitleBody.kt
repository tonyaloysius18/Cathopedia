package com.ynotlabs.cathopedia.ui.screens.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlin.math.floor

/** A floated image: title and opening copy beside it, with only overflow continuing below. */
@Composable
fun WrappedImageTitleBody(
    imageSize: Dp,
    body: String,
    bodyColor: Color,
    bodyFontSize: TextUnit,
    bodyLineHeight: TextUnit,
    image: @Composable () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    bodyFontWeight: FontWeight? = null,
    imageTextGap: Dp = 14.dp,
    titleBodyGap: Dp = 5.dp,
) {
    val density = LocalDensity.current
    var titleHeightPx by remember { mutableIntStateOf(0) }
    var continuationStart by remember(body) { mutableStateOf(body.length) }

    val sideLineCount = with(density) {
        val availablePx = imageSize.toPx() - titleHeightPx - titleBodyGap.toPx()
        maxOf(1, floor(availablePx / bodyLineHeight.toPx()).toInt())
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(imageSize), contentAlignment = Alignment.Center) {
            image()
        }
        Spacer(Modifier.width(imageTextGap))
        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.onSizeChanged { titleHeightPx = it.height }) {
                title()
            }
            Spacer(Modifier.height(titleBodyGap))
            Text(
                text = body,
                color = bodyColor,
                fontSize = bodyFontSize,
                lineHeight = bodyLineHeight,
                fontWeight = bodyFontWeight,
                maxLines = sideLineCount,
                onTextLayout = { result ->
                    val nextStart = if (result.hasVisualOverflow && result.lineCount > 0) {
                        result.getLineEnd(result.lineCount - 1, visibleEnd = true)
                    } else {
                        body.length
                    }
                    if (continuationStart != nextStart) continuationStart = nextStart
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (continuationStart < body.length) {
        Text(
            text = body.substring(continuationStart).trimStart(),
            color = bodyColor,
            fontSize = bodyFontSize,
            lineHeight = bodyLineHeight,
            fontWeight = bodyFontWeight,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
