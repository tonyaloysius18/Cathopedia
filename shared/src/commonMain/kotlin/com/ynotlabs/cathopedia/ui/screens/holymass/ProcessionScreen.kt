package com.ynotlabs.cathopedia.ui.screens.holymass


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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ynotlabs.cathopedia.mass.Minister
import com.ynotlabs.cathopedia.mass.ProcessionData
import com.ynotlabs.cathopedia.mass.ProcessionImages
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import kotlin.math.abs
import kotlin.math.min
import org.jetbrains.compose.resources.painterResource

// Green + gold, matching the Cathopedia hub pages and the Sacraments carousel.
private val ProcessionBg = Color(0xFF061A13)
private val ProcessionSurface = Color(0xFF0A241B)
private val ProcessionSurfaceRaised = Color(0xFF0F2E22)
private val ProcessionGold = Color(0xFFD6AE3D)
private val ProcessionGoldSoft = Color(0xFFB08D57)
private val ProcessionProgressActive = Color(0xFF4FA97B)
private val ProcessionCream = Color(0xFFF4ECDD)

private const val CARD_WIDTH_DP = 220
private const val CARD_HEIGHT_DP = 400
private const val MAX_ROTATION_DEG = 42f
private const val MAX_SCALE_DROP = 0.26f
private const val MAX_ALPHA_DROP = 0.55f

/**
 * The Entrance Procession carousel — a sibling of [SacramentsScreen]. Reached
 * from the Holy Mass hub's "Entrance Procession" section. See
 * [com.ynotlabs.cathopedia.ui.navigation.Destination.ProcessionScreen].
 */
@Composable
fun ProcessionScreen(
    language: String,
    onBack: () -> Unit,
) {
    val ministers = ProcessionData.ministers
    val listState = rememberLazyListState()
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

    val current = ministers.getOrNull(currentIndex) ?: ministers.first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProcessionBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = headerHeightDp + 16.dp),
        ) {
            ProcessionTextPanel(
                minister = current,
                language = language,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
        }

        ProcessionHeaderCard(
            currentIndex = currentIndex,
            ministers = ministers,
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
private fun ProcessionHeaderCard(
    currentIndex: Int,
    ministers: List<Minister>,
    listState: androidx.compose.foundation.lazy.LazyListState,
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
        color = ProcessionSurface,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(bottom = 12.dp),
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
                        text = s.processionScreenTitle,
                        color = ProcessionCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = s.processionIndicator.replace("{number}", (currentIndex + 1).toString()),
                        color = ProcessionGoldSoft,
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
                itemsIndexed(ministers, key = { _, m -> m.id }) { index, minister ->
                    ProcessionCard(
                        minister = minister,
                        language = language,
                        index = index,
                        listState = listState,
                        cardWidthPx = cardWidthPx,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            ProcessionDots(count = ministers.size, currentIndex = currentIndex)
        }
    }
}

@Composable
private fun ProcessionCard(
    minister: Minister,
    language: String,
    index: Int,
    listState: androidx.compose.foundation.lazy.LazyListState,
    cardWidthPx: Float,
) {
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
                color = ProcessionGold.copy(alpha = 0.45f),
                shape = RoundedCornerShape(26.dp)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(ProcessionSurfaceRaised, ProcessionSurface),
                ),
            ),
        contentAlignment = Alignment.BottomStart,
    ) {
        ProcessionImages.forNumber(minister.number)?.let { image ->
            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = 1.25f, scaleY = 1.25f),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.62f to Color.Transparent,
                        1f to ProcessionSurface.copy(alpha = 0.96f),
                    ),
                ),
        )

        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = (if (language == "fr") minister.carriesFr else minister.carriesEn).uppercase(),
                color = ProcessionGoldSoft,
                fontSize = 10.5.sp,
                letterSpacing = 1.1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (language == "fr") minister.titleFr else minister.titleEn,
                color = ProcessionCream,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ProcessionDots(count: Int, currentIndex: Int) {
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
                    .background(if (i == currentIndex) ProcessionProgressActive else ProcessionGold.copy(alpha = 0.22f)),
            )
        }
    }
}

@Composable
private fun ProcessionTextPanel(
    minister: Minister,
    language: String,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val isFrench = language == "fr"
    val title = if (isFrench) minister.titleFr else minister.titleEn
    val description = if (isFrench) minister.descriptionFr else minister.descriptionEn

    Column(modifier = modifier) {
        Text(
            text = title,
            color = ProcessionCream,
            fontFamily = FontFamily.Serif,
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ProcessionSurface,
            border = BorderStroke(1.dp, ProcessionGold.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = s.processionMeaningLabel.uppercase(),
                    color = ProcessionGoldSoft,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = description,
                    color = ProcessionCream,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}
