package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.stations.Station
import com.ynotlabs.cathopedia.stations.StationsData
import kotlin.math.abs
import kotlin.math.min

private val StationsBg = Color(0xFF061A13)
private val StationsSurface = Color(0xFF0C271E)
private val StationsSurfaceRaised = Color(0xFF123127)
private val StationsBorder = Color(0xFF315444)
private val StationsGold = Color(0xFFD8B24C)
private val StationsGoldSoft = Color(0xFF9D8858)
private val StationsCream = Color(0xFFF4ECDD)

private const val CARD_WIDTH_DP = 220
private const val CARD_HEIGHT_DP = 300
private const val MAX_ROTATION_DEG = 42f
private const val MAX_SCALE_DROP = 0.26f
private const val MAX_ALPHA_DROP = 0.55f

/**
 * The Stations of the Cross carousel. Reachable via
 * [com.ynotlabs.cathopedia.ui.navigation.Destination.StationsScreen]. Each
 * card is a plain color panel today — [Station] carries no image yet, only
 * text — swap in real artwork per station once it's generated (see
 * content/prayers/rosary-hero-image-prompt.md-style prompts to be written).
 */
@Composable
fun StationsScreen(
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val stations = StationsData.stations
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val cardWidthPx = with(density) { CARD_WIDTH_DP.dp.toPx() }

    var currentIndex by remember { mutableStateOf(0) }

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
    val sidePaddingDp = 90.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StationsBg)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 18.dp, top = 6.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.back,
                    tint = StationsCream,
                )
            }

            Spacer(Modifier.width(4.dp))

            Column {
                Text(
                    text = s.stationsScreenTitle,
                    color = StationsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = s.stationsIndicator.replace("{number}", (currentIndex + 1).toString()),
                    color = StationsGoldSoft,
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(horizontal = sidePaddingDp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth().height(CARD_HEIGHT_DP.dp),
        ) {
            itemsIndexed(stations, key = { _, station -> station.id }) { index, station ->
                StationCard(
                    station = station,
                    index = index,
                    listState = listState,
                    cardWidthPx = cardWidthPx,
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        StationDots(count = stations.size, currentIndex = currentIndex)

        Spacer(Modifier.height(18.dp))

        StationTextPanel(
            station = current,
            language = language,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        )
    }
}

@Composable
private fun StationCard(
    station: Station,
    index: Int,
    listState: androidx.compose.foundation.lazy.LazyListState,
    cardWidthPx: Float,
) {
    Box(
        modifier = Modifier
            .width(CARD_WIDTH_DP.dp)
            .fillMaxSize()
            .graphicsLayer {
                cameraDistance = 16f * density
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
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(StationsSurfaceRaised, StationsSurface),
                ),
            ),
        contentAlignment = Alignment.BottomStart,
    ) {
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
                text = "STATION ${station.number}",
                color = StationsGoldSoft,
                fontSize = 10.5.sp,
                letterSpacing = 1.1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = station.titleEn,
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
                    .background(if (i == currentIndex) StationsGold else StationsBorder),
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
