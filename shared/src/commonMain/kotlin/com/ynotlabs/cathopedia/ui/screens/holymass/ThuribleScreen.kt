package com.ynotlabs.cathopedia.ui.screens.holymass

import com.ynotlabs.cathopedia.ui.theme.CardBorder
import androidx.compose.material3.MaterialTheme

import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.ScreenText
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.mass.ThuribleData
import com.ynotlabs.cathopedia.mass.ThuribleImages
import com.ynotlabs.cathopedia.mass.ThuriblePart
import com.ynotlabs.cathopedia.mass.ThuribleType
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.mass_thurible
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.min

// Green + gold, matching the Cathopedia hub pages.
private val ThuribleBg: Color @Composable get() = MaterialTheme.colorScheme.background
private val ThuribleSurface: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
private val ThuribleSurfaceRaised: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerHighest
private val ThuribleGold: Color @Composable get() = MaterialTheme.colorScheme.primary
private val ThuribleGoldSoft: Color @Composable get() = MaterialTheme.colorScheme.secondary
private val ThuribleCream: Color @Composable get() = MaterialTheme.colorScheme.onBackground
private val ThuribleMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val ThuribleHeader: Color @Composable get() = MaterialTheme.colorScheme.surface

// Match the compact proportions used by the church-tower carousel.
private val TypeCardWidth = 180.dp
private val TypeCardHeight = 220.dp

@Composable
fun ThuribleScreen(
    @Suppress("UNUSED_PARAMETER") repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val parts = ThuribleData.parts
    val types = ThuribleData.types
    val intro = t[ThuribleData.INTRO]

    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ThuribleBg),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeight + 20.dp,
                end = 20.dp,
                bottom = 120.dp,
            ),
        ) {
            item {
                ThuribleIntroCard(intro)
                Spacer(Modifier.height(16.dp))
            }

            item {
                SacredDivider()
            }

            // The whole thurible, in its upright orientation
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.mass_thurible),
                        contentDescription = s.thuribleScreenTitle,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            item {
                ThuribleTypesCarousel(types = types, t = t)
                Spacer(Modifier.height(22.dp))
                SacredDivider()
                Spacer(Modifier.height(18.dp))
            }

            // "The Parts" section label
            item {
                Text(
                    text = s.thuriblePartsLabel.uppercase(),
                    color = ThuribleGold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            // Part cards, two per row
            items(parts.chunked(2)) { rowParts ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowParts.forEach { part ->
                        PartCard(
                            part = part,
                            t = t,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    if (rowParts.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        ThuribleHeaderPanel(
            title = s.thuribleScreenTitle,
            subtitle = s.thuribleSubtitle,
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun ThuribleTypesCarousel(
    types: List<ThuribleType>,
    t: ScreenText,
) {
    val rowState = rememberLazyListState()
    val currentIndex by remember(rowState) {
        derivedStateOf {
            val layoutInfo = rowState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo
                .minByOrNull { item -> abs((item.offset + item.size / 2) - viewportCenter) }
                ?.index
                ?.coerceIn(types.indices)
                ?: 0
        }
    }
    val currentType = types[currentIndex]
    val density = LocalDensity.current
    val cardWidthPx = with(density) { TypeCardWidth.toPx() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = t[ThuribleData.TYPES_TITLE].uppercase(),
                color = ThuribleGold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(ThuribleGold.copy(alpha = 0.36f)),
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val sidePadding = ((maxWidth - TypeCardWidth) / 2).coerceAtLeast(20.dp)
            LazyRow(
                state = rowState,
                flingBehavior = rememberSnapFlingBehavior(rowState),
                contentPadding = PaddingValues(horizontal = sidePadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TypeCardHeight),
            ) {
                itemsIndexed(types, key = { _, type -> type.id }) { index, type ->
                    ThuribleTypeCard(
                        type = type,
                        index = index,
                        isCurrent = index == currentIndex,
                        listState = rowState,
                        cardWidthPx = cardWidthPx,
                        t = t,
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            types.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(if (index == currentIndex) 18.dp else 6.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentIndex) ThuribleGold
                            else ThuribleMuted.copy(alpha = 0.45f),
                        ),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = ThuribleSurface,
            border = BorderStroke(1.dp, CardBorder),
        ) {
            Box {
                GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))
                Column(
                    modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
                ) {
                    Text(
                        text = t[ThuribleData.TYPES_INDICATOR]
                            .replace("{number}", (currentIndex + 1).toString())
                            .replace("{count}", types.size.toString()),
                        color = ThuribleGold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = t["${currentType.keyPrefix}.name"],
                        color = ThuribleCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 19.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ThuribleFact(
                            label = t[ThuribleData.STRUCTURE_LABEL],
                            value = t["${currentType.keyPrefix}.structure"],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                        ThuribleFact(
                            label = t[ThuribleData.SETTING_LABEL],
                            value = t["${currentType.keyPrefix}.setting"],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = t["${currentType.keyPrefix}.desc"],
                        color = ThuribleCream.copy(alpha = 0.86f),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ThuribleFact(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, ThuribleGold.copy(alpha = 0.22f)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)) {
            Text(
                text = label.uppercase(),
                color = ThuribleGold,
                fontSize = 9.sp,
                lineHeight = 11.sp,
                letterSpacing = 0.7.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                color = ThuribleCream.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ThuribleTypeCard(
    type: ThuribleType,
    index: Int,
    isCurrent: Boolean,
    listState: LazyListState,
    cardWidthPx: Float,
    t: ScreenText,
) {
    Surface(
        modifier = Modifier
            .width(TypeCardWidth)
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
                        ((itemCenter - viewportCenter) / cardWidthPx).coerceIn(-1.4f, 1.4f)
                    rotationY = normalized * 28f
                    val distance = min(abs(normalized), 1f)
                    scaleX = 1f - distance * 0.16f
                    scaleY = 1f - distance * 0.16f
                    alpha = 1f - distance * 0.35f
                    translationX = -normalized * cardWidthPx * 0.1f
                }
            },
        shape = RoundedCornerShape(22.dp),
        color = ThuribleSurface,
        border = BorderStroke(
            width = if (isCurrent) 2.dp else 1.dp,
            color = if (isCurrent) ThuribleGold else CardBorder,
        ),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                contentAlignment = Alignment.Center,
            ) {
                ThuribleImages.forType(type.id)?.let { image ->
                    Image(
                        painter = painterResource(image),
                        contentDescription = t["${type.keyPrefix}.name"],
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(26.dp),
                    shape = CircleShape,
                    color = ThuribleGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ThuribleGold.copy(alpha = 0.38f)),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (index + 1).toString(),
                            color = ThuribleGold,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = t["${type.keyPrefix}.name"],
                color = if (isCurrent) ThuribleGold else ThuribleCream,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun ThuribleIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ThuribleSurface,
        contentColor = ThuribleCream,
        border = BorderStroke(1.dp, CardBorder),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Text(
                text = text,
                color = ThuribleCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun ThuribleHeaderPanel(
    title: String,
    subtitle: String,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var titleLineCount by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(ThuribleHeader)
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

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = ThuribleCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                    onTextLayout = { titleLineCount = it.lineCount }
                )

                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(if (titleLineCount <= 1) 4.dp else 10.dp))
                    Text(
                        text = subtitle.uppercase(),
                        color = ThuribleGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.4.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun PartCard(
    part: ThuriblePart,
    t: ScreenText,
    modifier: Modifier = Modifier,
) {
    val painter = ThuribleImages.forPart(part.id)?.let { painterResource(it) }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = ThuribleSurface,
        border = BorderStroke(1.dp, CardBorder),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Column(
                modifier = Modifier.padding(start = 18.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(ThuribleSurfaceRaised, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (painter != null) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = t["${part.keyPrefix}.name"],
                    color = ThuribleCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = t["${part.keyPrefix}.desc"],
                    color = ThuribleCream.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
