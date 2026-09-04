package com.ynotlabs.cathopedia.ui.screens.stations

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.stations.Station
import com.ynotlabs.cathopedia.stations.StationImages
import com.ynotlabs.cathopedia.stations.StationsData
import kotlin.math.abs
import kotlin.math.min
import org.jetbrains.compose.resources.painterResource

private val StationsBg = Color(0xFF1A0505)
private val StationsSurface = Color(0xFF2B0A0A)
private val StationsSurfaceRaised = Color(0xFF3D0F0F)
private val StationsGold = Color(0xFFD8B24C)
private val StationsGoldSoft = Color(0xFFB08D57)
private val StationsProgressActive = Color(0xFFE07A5F)
private val StationsCream = Color(0xFFF4ECDD)

private const val CARD_WIDTH_DP = 220
private const val CARD_HEIGHT_DP = 330
private const val MAX_ROTATION_DEG = 42f
private const val MAX_SCALE_DROP = 0.26f
private const val MAX_ALPHA_DROP = 0.55f

/**
 * The Stations of the Cross carousel. Reachable via
 * [com.ynotlabs.cathopedia.ui.navigation.Destination.StationsScreen].
 */
@Composable
fun StationsScreen(
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    scrollState: ScrollState = rememberScrollState(),
) {
    val stations = StationsData.stations
    val density = LocalDensity.current
    val cardWidthPx = with(density) { CARD_WIDTH_DP.dp.toPx() }

    var currentIndex by remember { mutableStateOf(0) }
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val headerHeightDp = with(density) { headerHeightPx.toDp() }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                if (layoutInfo.visibleItemsInfo.isEmpty()) return@collect
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                val closest = layoutInfo.visibleItemsInfo.minByOrNull {
                    abs((it.offset + it.size / 2) - viewportCenter)
                }
                closest?.let { currentIndex = it.index }
            }
    }

    val current = stations.getOrNull(currentIndex) ?: stations.first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StationsBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = headerHeightDp + 16.dp),
        ) {
            StationTextPanel(
                station = current,
                language = language,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
        }

        StationsHeaderCard(
            currentIndex = currentIndex,
            stations = stations,
            listState = listState,
            cardWidthPx = cardWidthPx,
            language = language,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                headerHeightPx = it.size.height
            }
        )
    }
}

@Composable
private fun StationsHeaderCard(
    currentIndex: Int,
    stations: List<Station>,
    listState: LazyListState,
    cardWidthPx: Float,
    language: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                clip = false
            ),
        color = StationsSurface,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        border = BorderStroke(1.dp, StationsGold.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, end = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CathopediaBackButton(
                    onClick = onBack,
                    contentDescription = s.back,
                )

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = s.stationsScreenTitle,
                        color = StationsCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = s.stationsIndicator.replace("{number}", (currentIndex + 1).toString()),
                        color = StationsGoldSoft,
                        fontSize = 14.sp,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val sidePaddingDp = 90.dp
            LazyRow(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(listState),
                contentPadding = PaddingValues(horizontal = sidePaddingDp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CARD_HEIGHT_DP.dp),
            ) {
                itemsIndexed(stations, key = { _, station -> station.id }) { index, station ->
                    StationCard(
                        station = station,
                        language = language,
                        index = index,
                        listState = listState,
                        cardWidthPx = cardWidthPx,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            StationDots(count = stations.size, currentIndex = currentIndex)
        }
    }
}

@Composable
private fun StationCard(
    station: Station,
    language: String,
    index: Int,
    listState: LazyListState,
    cardWidthPx: Float,
) {
    val s = LocalStrings.current
    Box(
        modifier = Modifier
            .width(CARD_WIDTH_DP.dp)
            .fillMaxHeight()
            .graphicsLayer {
                cameraDistance = 16f * (density)
                val layoutInfo = listState.layoutInfo
                val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                if (itemInfo != null) {
                    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
                    val itemCenter = itemInfo.offset + itemInfo.size / 2f
                    val normalized = ((itemCenter - viewportCenter) / cardWidthPx).coerceIn(-1.6f, 1.6f)

                    rotationY = normalized * MAX_ROTATION_DEG
                    val scale = 1f - min(abs(normalized), 1f) * MAX_SCALE_DROP
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - min(abs(normalized), 1f) * MAX_ALPHA_DROP
                    translationX = -normalized * cardWidthPx * 0.18f
                }
            }
            .border(
                width = 1.dp,
                color = StationsGold.copy(alpha = 0.45f),
                shape = RoundedCornerShape(26.dp)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(StationsSurfaceRaised, StationsSurface),
                ),
            ),
        contentAlignment = Alignment.BottomStart,
    ) {
        StationImages.forNumber(station.number)?.let { image ->
            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, top = 10.dp, end = 15.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.62f to Color.Transparent,
                        1f to StationsSurface.copy(alpha = 0.96f),
                    ),
                ),
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .size(36.dp),
            shape = CircleShape,
            color = StationsGold.copy(alpha = 0.14f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = station.number.toString(),
                    color = StationsGold,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }
        }

        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = s.stationsNumberLabel.replace("{number}", station.number.toString()).uppercase(),
                color = StationsGoldSoft,
                fontSize = 10.5.sp,
                letterSpacing = 1.1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (language == "fr") station.titleFr else station.titleEn,
                color = StationsCream,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun StationDots(count: Int, currentIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(count) { i ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (i == currentIndex) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i == currentIndex) StationsProgressActive else StationsGold.copy(alpha = 0.22f)),
            )
        }
    }
}

@Composable
private fun StationTextPanel(
    station: Station,
    language: String,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val isFrench = language == "fr"
    val title = if (isFrench) station.titleFr else station.titleEn
    val meditation = if (isFrench) station.meditationFr else station.meditationEn
    val versicle = if (isFrench) station.versicleFr else station.versicleEn
    val response = if (isFrench) station.responseFr else station.responseEn

    Column(modifier = modifier) {
        Text(
            text = title,
            color = StationsCream,
            fontFamily = FontFamily.Serif,
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = StationsSurface,
            border = BorderStroke(1.dp, StationsGold.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = s.stationsVersicleLabel.uppercase(),
                    color = StationsGoldSoft,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = versicle,
                    color = StationsCream,
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = s.stationsResponseLabel.uppercase(),
                    color = StationsGoldSoft,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = response,
                    color = StationsCream,
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = s.stationsMeditationLabel.uppercase(),
            color = StationsGoldSoft,
            fontSize = 10.5.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = meditation,
            color = StationsCream.copy(alpha = 0.88f),
            fontSize = 15.sp,
            lineHeight = 23.sp,
        )

        Spacer(Modifier.height(40.dp))
    }
}
