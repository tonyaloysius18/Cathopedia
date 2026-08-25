package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.rosary_bead_hm_01
import com.ynotlabs.cathopedia.resources.rosary_bead_of_01
import com.ynotlabs.cathopedia.resources.rosary_centerpiece_medal
import com.ynotlabs.cathopedia.resources.rosary_cross_pearl
import com.ynotlabs.cathopedia.ui.theme.CathopediaTheme
import com.ynotlabs.cathopedia.ui.theme.ThemeMode
import com.ynotlabs.cathopedia.ui.theme.rosaryColors
import kotlin.math.abs
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** UI-only input for a Rosary sprite; it intentionally carries no Rosary sequence/domain node. */
data class RosarySpriteUiModel(
    val sprite: DrawableResource?,
    val displaySize: Dp,
    val contentDescription: String,
    val isCurrent: Boolean = false,
    val isPrayed: Boolean = false,
)

/**
 * Draws one sprite with the carousel's cylindrical transform. A null sprite is represented by a
 * stable placeholder, allowing hub diagrams and the Rosary screen to share the same safe renderer.
 */
@Composable
fun RosarySpriteRenderer(
    model: RosarySpriteUiModel,
    distanceFromCenter: Float,
    carouselOnLeft: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = rosaryColors()
    val density = LocalDensity.current
    val clamped = distanceFromCenter.coerceIn(-3f, 3f)
    val fade = (1f - abs(clamped) * 0.18f).coerceIn(0.28f, 1f)
    val scale = (1f - abs(clamped) * 0.09f).coerceIn(0.7f, 1f)
    val inward = if (carouselOnLeft) 1f else -1f
    val desaturated = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.28f) })
    }
    val transform = Modifier.graphicsLayer {
        cameraDistance = 12f * density.density
        rotationX = (clamped * 22f).coerceIn(-70f, 70f)
        scaleX = scale
        scaleY = scale
        alpha = fade
        translationX = inward * abs(clamped) * 5.dp.toPx()
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (model.isCurrent) {
            Box(
                Modifier
                    .size(66.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                0f to colors.candle.copy(alpha = 0.42f),
                                1f to colors.candle.copy(alpha = 0f),
                            ),
                        )
                    },
            )
        }

        if (model.sprite != null) {
            Image(
                painter = painterResource(model.sprite),
                contentDescription = model.contentDescription,
                contentScale = ContentScale.Fit,
                colorFilter = if (model.isPrayed) desaturated else null,
                modifier = Modifier.size(model.displaySize).then(transform),
            )
        } else {
            Box(
                Modifier
                    .size(model.displaySize)
                    .then(transform)
                    .clip(CircleShape)
                    .background(colors.marian.copy(alpha = if (model.isPrayed) 0.38f else 0.72f))
                    .semantics { contentDescription = model.contentDescription },
            )
        }
    }
}

@Preview
@Composable
private fun RosarySpriteRendererPreview() {
    CathopediaTheme(themeMode = ThemeMode.LIGHT) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                RosarySpriteUiModel(Res.drawable.rosary_cross_pearl, 72.dp, "Cross", isPrayed = true),
                RosarySpriteUiModel(Res.drawable.rosary_bead_of_01, 52.dp, "Our Father"),
                RosarySpriteUiModel(Res.drawable.rosary_bead_hm_01, 44.dp, "Hail Mary", isCurrent = true),
                RosarySpriteUiModel(Res.drawable.rosary_centerpiece_medal, 60.dp, "Centerpiece"),
                RosarySpriteUiModel(null, 44.dp, "Missing sprite"),
            ).forEachIndexed { index, model ->
                RosarySpriteRenderer(
                    model = model,
                    distanceFromCenter = (index - 2) * 0.75f,
                    carouselOnLeft = false,
                    modifier = Modifier.size(76.dp),
                )
            }
        }
    }
}
