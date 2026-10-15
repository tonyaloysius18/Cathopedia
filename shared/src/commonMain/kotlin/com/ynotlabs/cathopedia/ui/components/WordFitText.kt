package com.ynotlabs.cathopedia.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * Text for narrow slots that shrinks, at most to [minScale], until its longest word fits on one
 * line. Compose otherwise breaks a word wider than the slot mid-word ("ஒப்புக்கொடுத்த / ல்"),
 * which long Tamil or German compounds do in half-width cells.
 *
 * The slot width is read in a layout modifier rather than BoxWithConstraints, so this also works
 * inside rows sized with IntrinsicSize.
 */
@Composable
fun WordFitText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit,
    lineHeight: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    minScale: Float = 0.7f,
) {
    val measurer = rememberTextMeasurer()
    var available by remember { mutableIntStateOf(0) }
    // Measure with the style Text will actually use (theme letter spacing included).
    val style = LocalTextStyle.current.merge(
        TextStyle(fontSize = fontSize, fontFamily = fontFamily, fontWeight = fontWeight),
    )
    val scale = remember(text, available, style) {
        if (available <= 0) return@remember 1f
        val widest = text.split(' ', '\n').filter { it.isNotEmpty() }
            .maxOfOrNull { measurer.measure(it, style, softWrap = false).size.width } ?: 0
        // 3% margin: letter spacing is fixed in sp and does not shrink with the font.
        if (widest <= available) 1f else (available * 0.97f / widest).coerceAtLeast(minScale)
    }
    Text(
        text,
        modifier = modifier.layout { measurable, constraints ->
            if (constraints.hasBoundedWidth && constraints.maxWidth != available) available = constraints.maxWidth
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) { placeable.place(0, 0) }
        },
        color = color,
        fontSize = fontSize * scale,
        lineHeight = if (lineHeight == TextUnit.Unspecified) lineHeight else lineHeight * scale,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
    )
}
