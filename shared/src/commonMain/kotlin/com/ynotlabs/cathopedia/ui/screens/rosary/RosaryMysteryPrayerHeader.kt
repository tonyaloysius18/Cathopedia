package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.model.MysteryDetail
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.theme.RosaryMarianCard

/** Mystery details and its unframed cutout on a decade's blue Our Father bead. */
@Composable
internal fun RosaryMysteryPrayerHeader(
    mystery: MysteryDetail,
    fruitLabel: String,
) {
    val artwork = hubAssetPainter("rosary_mystery_${mystery.id.replace('-', '_')}.webp")
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val titleStyle = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)
    val longestWord = mystery.title.split(Regex("\\s+")).maxByOrNull { word ->
        textMeasurer.measure(word, titleStyle, softWrap = false).size.width
    }.orEmpty()
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        // Keep enough room for complete title words as the artwork grows.
        val minimumTitleWidth = with(density) {
            textMeasurer.measure(longestWord, titleStyle.copy(fontSize = 14.sp), softWrap = false)
                .size.width.toDp()
        }
        val artworkWidth = (maxWidth * 0.44f).coerceIn(80.dp, 124.dp)
            .coerceAtMost((maxWidth - minimumTitleWidth - 28.dp).coerceAtLeast(48.dp))
        val titleWidth = if (artwork != null) maxWidth - artworkWidth - 28.dp else maxWidth - 20.dp
        val fullTitleWordWidth = textMeasurer.measure(longestWord, titleStyle, softWrap = false).size.width
        val titleScale = (with(density) { titleWidth.toPx() } / fullTitleWordWidth.coerceAtLeast(1))
            .coerceIn(0.875f, 1f)
        val artworkHeight = artwork?.intrinsicSize?.let { size ->
            artworkWidth * (size.height / size.width).takeIf { it.isFinite() && it > 0f }.let { it ?: 1.25f }
        } ?: artworkWidth * 1.25f
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                modifier = if (artwork != null) Modifier.weight(1f).fillMaxHeight() else Modifier,
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                border = BorderStroke(1.dp, RosaryMarianCard.gold.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(14.dp),
            ) {
                Column(
                    Modifier.fillMaxHeight().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                ) {
                    Text(
                        mystery.title,
                        style = titleStyle.copy(fontSize = titleStyle.fontSize * titleScale, lineHeight = 20.sp),
                        color = RosaryMarianCard.gold,
                    )
                    mystery.scriptureRef?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = RosaryMarianCard.muted)
                    }
                    Text(
                        fruitLabel.replace("{fruit}", mystery.fruit),
                        style = MaterialTheme.typography.bodyMedium,
                        color = RosaryMarianCard.cream,
                    )
                }
            }
            if (artwork != null) {
                Image(
                    painter = artwork,
                    contentDescription = mystery.title,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterEnd,
                    modifier = Modifier.width(artworkWidth).heightIn(min = artworkHeight).fillMaxHeight(),
                )
            }
        }
    }
}
