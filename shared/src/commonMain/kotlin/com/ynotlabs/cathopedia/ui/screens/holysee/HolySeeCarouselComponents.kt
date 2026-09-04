package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import kotlin.math.abs
import kotlin.math.min

internal val HolySeeBackground = Color(0xFF061A13)
internal val HolySeeHeader = Color(0xFF081F17)
internal val HolySeeSurface = Color(0xFF0C271E)
internal val HolySeeSurfaceRaised = Color(0xFF123328)
internal val HolySeeGold = Color(0xFFD8B24C)
internal val HolySeeGoldSoft = Color(0xFFB89A58)
internal val HolySeeCream = Color(0xFFF4ECDD)
internal val HolySeeMuted = Color(0xFFB7B09D)
internal val CardinalScarlet = Color(0xFFE06B68)

@Composable
internal fun HolySeeHeaderPanel(
    title: String,
    subtitle: String? = null,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val useCompactTitle = title.length > 42

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(HolySeeHeader)
            .statusBarsPadding()
            .padding(start = 18.dp, top = 6.dp, end = 18.dp, bottom = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            CathopediaBackButton(
                onClick = onBack,
                contentDescription = backDescription,
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = HolySeeCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = if (useCompactTitle) 25.sp else 29.sp,
                    lineHeight = if (useCompactTitle) 29.sp else 32.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (!subtitle.isNullOrBlank()) {
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = subtitle,
                        color = HolySeeMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
internal fun HolySeeIntroCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = HolySeeSurface,
        contentColor = HolySeeCream,
        border = BorderStroke(1.dp, HolySeeGold.copy(alpha = 0.40f)),
    ) {
        Box {
            GoldCardAccent(
                modifier = Modifier.align(Alignment.CenterStart),
                color = HolySeeGold,
            )
            Text(
                text = text,
                color = HolySeeCream.copy(alpha = 0.90f),
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            )
        }
    }
}

private const val HOLY_SEE_CARD_WIDTH_DP = 224
private const val HOLY_SEE_CARD_HEIGHT_DP = 330

internal data class HolySeeCarouselItem(
    val id: String,
    val title: String,
    val body: String,
    val imageAsset: String,
    val badge: String? = null,
    val emblemAsset: String? = null,
)

@Composable
internal fun HolySeePortraitCarousel(
    label: String,
    items: List<HolySeeCarouselItem>,
    accent: Color = HolySeeGold,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val cardWidthPx = with(density) { HOLY_SEE_CARD_WIDTH_DP.dp.toPx() }
    var currentIndex by remember(items) { mutableIntStateOf(0) }

    LaunchedEffect(listState, items) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                if (layoutInfo.visibleItemsInfo.isEmpty()) return@collect
                val viewportCenter =
                    (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                layoutInfo.visibleItemsInfo.minByOrNull {
                    abs((it.offset + it.size / 2) - viewportCenter)
                }?.let { currentIndex = it.index.coerceIn(items.indices) }
            }
    }

    val current = items[currentIndex]

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            color = HolySeeGoldSoft,
            fontSize = 10.5.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(12.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val centeredPadding = (maxWidth - HOLY_SEE_CARD_WIDTH_DP.dp) / 2
            val horizontalPadding = if (centeredPadding > 20.dp) centeredPadding else 20.dp

            LazyRow(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(listState),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HOLY_SEE_CARD_HEIGHT_DP.dp),
            ) {
                itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                    HolySeePortraitCard(
                        item = item,
                        index = index,
                        listState = listState,
                        cardWidthPx = cardWidthPx,
                        accent = accent,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(items.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (index == currentIndex) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentIndex) accent
                            else HolySeeGold.copy(alpha = 0.24f)
                        ),
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            color = HolySeeSurface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.38f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                current.badge?.let { badge ->
                    Text(
                        text = badge.uppercase(),
                        color = accent,
                        fontSize = 10.sp,
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(7.dp))
                }
                Text(
                    text = current.title,
                    color = HolySeeCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 23.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = current.body,
                    color = HolySeeCream.copy(alpha = 0.86f),
                    fontSize = 15.5.sp,
                    lineHeight = 23.sp,
                )
            }
        }
    }
}

@Composable
private fun HolySeePortraitCard(
    item: HolySeeCarouselItem,
    index: Int,
    listState: androidx.compose.foundation.lazy.LazyListState,
    cardWidthPx: Float,
    accent: Color,
) {
    Box(
        modifier = Modifier
            .width(HOLY_SEE_CARD_WIDTH_DP.dp)
            .fillMaxHeight()
            .graphicsLayer {
                cameraDistance = 16f * density
                val layoutInfo = listState.layoutInfo
                val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                if (itemInfo != null) {
                    val viewportCenter =
                        (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                    val itemCenter = itemInfo.offset + itemInfo.size / 2f
                    val normalized =
                        ((itemCenter - viewportCenter) / cardWidthPx).coerceIn(-1.5f, 1.5f)
                    rotationY = normalized * 35f
                    val scale = 1f - min(abs(normalized), 1f) * 0.20f
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - min(abs(normalized), 1f) * 0.42f
                    translationX = -normalized * cardWidthPx * 0.15f
                }
            }
            .border(1.dp, accent.copy(alpha = 0.55f), RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(HolySeeSurfaceRaised, HolySeeSurface),
                )
            ),
        contentAlignment = Alignment.BottomStart,
    ) {
        hubAssetPainter(item.imageAsset)?.let { painter ->
            Image(
                painter = painter,
                contentDescription = item.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 42.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.60f to Color.Transparent,
                        1f to HolySeeBackground.copy(alpha = 0.98f),
                    )
                )
        )

        item.emblemAsset?.let { asset ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(58.dp),
                shape = CircleShape,
                color = HolySeeBackground.copy(alpha = 0.90f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.75f)),
            ) {
                hubAssetPainter(asset)?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(5.dp),
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(18.dp)) {
            item.badge?.let { badge ->
                Text(
                    text = badge.uppercase(),
                    color = accent,
                    fontSize = 9.5.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(5.dp))
            }
            Text(
                text = item.title,
                color = HolySeeCream,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
