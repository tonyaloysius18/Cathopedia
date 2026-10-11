package com.ynotlabs.cathopedia.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.parseHubMarkup
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

internal data class IllustratedArticleCarouselItem(
    val asset: String,
    val title: String,
    val body: String,
)

/** A snapping artwork shelf and one complete, synchronized description. */
@Composable
internal fun IllustratedArticleCarousel(
    label: String,
    items: List<IllustratedArticleCarouselItem>,
    viewportHeight: Dp,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    if (items.isEmpty()) return
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val currentIndex by remember(items, listState) {
        derivedStateOf {
            val layout = listState.layoutInfo
            val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
            layout.visibleItemsInfo.minByOrNull {
                abs(it.offset + it.size / 2 - center)
            }?.index?.coerceIn(items.indices) ?: 0
        }
    }
    val current = items[currentIndex]
    val titleStyle = TextStyle(
        color = ArticleCream,
        fontFamily = FontFamily.Serif,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontWeight = FontWeight.Medium,
    )
    val bodyStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 21.sp,
    )

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val textWidthPx = with(density) { (maxWidth - 40.dp).roundToPx().coerceAtLeast(1) }
        // Size the artwork for the longest entry so the shelf and dots stay
        // stable while each description card wraps its own content.
        val maxDetailsHeightPx = items.maxOf { item ->
            textMeasurer.measure(
                text = item.title,
                style = titleStyle,
                constraints = Constraints(maxWidth = textWidthPx),
            ).size.height + textMeasurer.measure(
                text = parseHubMarkup(item.body),
                style = bodyStyle,
                constraints = Constraints(maxWidth = textWidthPx),
            ).size.height
        }
        val maxDetailsHeight = with(density) { maxDetailsHeightPx.toDp() } + 44.dp
        // A readable fallback remains scrollable at unusually large font sizes.
        val imageHeight = (viewportHeight - maxDetailsHeight - 110.dp).coerceIn(96.dp, 224.dp)
        val dotWidth = minOf(24.dp, maxWidth / items.size)
        val cardWidth = minOf(224.dp, maxWidth * 0.68f)
        val cardWidthPx = with(density) { cardWidth.toPx() }
        val sidePadding = ((maxWidth - cardWidth) / 2).coerceAtLeast(0.dp)

        Column {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label.uppercase(),
                    color = ArticleGold,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${currentIndex + 1} / ${items.size}",
                    color = ArticleMuted,
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(12.dp))

            LazyRow(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(listState),
                contentPadding = PaddingValues(horizontal = sidePadding),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth().height(imageHeight),
            ) {
                itemsIndexed(items, key = { _, item -> item.asset }) { index, item ->
                    val shape = RoundedCornerShape(24.dp)
                    Box(
                        modifier = Modifier
                            .width(cardWidth)
                            .height(imageHeight)
                            .graphicsLayer {
                                cameraDistance = 16f * this.density
                                val layout = listState.layoutInfo
                                val info = layout.visibleItemsInfo.firstOrNull { it.index == index }
                                if (info != null) {
                                    val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2f
                                    val offset = ((info.offset + info.size / 2f - center) / cardWidthPx)
                                        .coerceIn(-1.5f, 1.5f)
                                    rotationY = offset * 35f
                                    val scale = 1f - min(abs(offset), 1f) * 0.20f
                                    scaleX = scale
                                    scaleY = scale
                                    alpha = 1f - min(abs(offset), 1f) * 0.42f
                                    translationX = -offset * cardWidthPx * 0.15f
                                }
                            }
                            .border(1.dp, ArticleGold.copy(alpha = 0.55f), shape)
                            .clip(shape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceContainerHigh,
                                        MaterialTheme.colorScheme.surfaceContainerLow,
                                    ),
                                ),
                            )
                            .clickable(role = Role.Button) {
                                scope.launch { listState.animateScrollToItem(index) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        hubAssetPainter(item.asset)?.let { painter ->
                            Image(
                                painter = painter,
                                contentDescription = item.title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize().padding(12.dp),
                            )
                        } ?: Text(
                            text = item.title,
                            style = titleStyle,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .width(dotWidth)
                            .height(48.dp)
                            .semantics { contentDescription = item.title }
                            .selectable(
                                selected = index == currentIndex,
                                role = Role.Tab,
                                onClick = { scope.launch { listState.animateScrollToItem(index) } },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(if (index == currentIndex) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(ArticleGold.copy(alpha = if (index == currentIndex) 1f else 0.24f)),
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ArticleSurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CardBorder),
            ) {
                Box {
                    GoldCardAccent(Modifier.align(Alignment.CenterStart))
                    Column(Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 20.dp)) {
                        Text(text = current.title, style = titleStyle)
                        Spacer(Modifier.height(8.dp))
                        SymbolMarkupText(
                            raw = current.body,
                            onEntityRefSelected = onEntityRefSelected,
                            prominent = true,
                        )
                    }
                }
            }
        }
    }
}
