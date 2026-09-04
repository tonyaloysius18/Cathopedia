package com.ynotlabs.cathopedia.ui.screens.sacraments


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
import com.ynotlabs.cathopedia.sacraments.Sacrament
import com.ynotlabs.cathopedia.sacraments.SacramentImages
import com.ynotlabs.cathopedia.sacraments.SacramentsData
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import kotlin.math.abs
import kotlin.math.min
import org.jetbrains.compose.resources.painterResource

// Green + gold, matching the Cathopedia hub pages (see HubScreen).
private val SacramentsBg = Color(0xFF061A13)
private val SacramentsSurface = Color(0xFF0A241B)
private val SacramentsSurfaceRaised = Color(0xFF0F2E22)
private val SacramentsGold = Color(0xFFD6AE3D)
private val SacramentsGoldSoft = Color(0xFFB08D57)
private val SacramentsProgressActive = Color(0xFF4FA97B)
private val SacramentsCream = Color(0xFFF4ECDD)

private const val CARD_WIDTH_DP = 220
private const val CARD_HEIGHT_DP = 300
private const val MAX_ROTATION_DEG = 42f
private const val MAX_SCALE_DROP = 0.26f
private const val MAX_ALPHA_DROP = 0.55f

@Composable
fun SacramentsScreen(
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    scrollState: ScrollState = rememberScrollState(),
) {
    val sacraments = SacramentsData.sacraments
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

    val current = sacraments.getOrNull(currentIndex) ?: sacraments.first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SacramentsBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = headerHeightDp + 16.dp),
        ) {
            SacramentTextPanel(
                sacrament = current,
                language = language,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            )
        }

        SacramentsHeaderCard(
            currentIndex = currentIndex,
            sacraments = sacraments,
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
private fun SacramentsHeaderCard(
    currentIndex: Int,
    sacraments: List<Sacrament>,
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
        color = SacramentsSurface,
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
                        text = s.sacramentsScreenTitle,
                        color = SacramentsCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = s.sacramentsIndicator.replace("{number}", (currentIndex + 1).toString()),
                        color = SacramentsGoldSoft,
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
                itemsIndexed(sacraments, key = { _, sacrament -> sacrament.id }) { index, sacrament ->
                    SacramentCard(
                        sacrament = sacrament,
                        language = language,
                        index = index,
                        listState = listState,
                        cardWidthPx = cardWidthPx,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            SacramentDots(count = sacraments.size, currentIndex = currentIndex)
        }
    }
}

@Composable
private fun SacramentCard(
    sacrament: Sacrament,
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
                color = SacramentsGold.copy(alpha = 0.45f),
                shape = RoundedCornerShape(26.dp)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(SacramentsSurfaceRaised, SacramentsSurface),
                ),
            ),
        contentAlignment = Alignment.BottomStart,
    ) {
        SacramentImages.forNumber(sacrament.number)?.let { image ->
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
                        1f to SacramentsSurface.copy(alpha = 0.96f),
                    ),
                ),
        )

        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = (if (language == "fr") sacrament.groupFr else sacrament.groupEn).uppercase(),
                color = SacramentsGoldSoft,
                fontSize = 10.5.sp,
                letterSpacing = 1.1.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (language == "fr") sacrament.titleFr else sacrament.titleEn,
                color = SacramentsCream,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SacramentDots(count: Int, currentIndex: Int) {
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
                    .background(if (i == currentIndex) SacramentsProgressActive else SacramentsGold.copy(alpha = 0.22f)),
            )
        }
    }
}

@Composable
private fun SacramentTextPanel(
    sacrament: Sacrament,
    language: String,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val isFrench = language == "fr"
    val title = if (isFrench) sacrament.titleFr else sacrament.titleEn
    val sign = if (isFrench) sacrament.signFr else sacrament.signEn
    val grace = if (isFrench) sacrament.graceFr else sacrament.graceEn
    val description = if (isFrench) sacrament.descriptionFr else sacrament.descriptionEn

    Column(modifier = modifier) {
        Text(
            text = title,
            color = SacramentsCream,
            fontFamily = FontFamily.Serif,
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(14.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SacramentsSurface,
            border = BorderStroke(1.dp, SacramentsGold.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = s.sacramentsSignLabel.uppercase(),
                    color = SacramentsGoldSoft,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = sign,
                    color = SacramentsCream,
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = s.sacramentsGraceLabel.uppercase(),
                    color = SacramentsGoldSoft,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = grace,
                    color = SacramentsCream,
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = s.sacramentsMeaningLabel.uppercase(),
            color = SacramentsGoldSoft,
            fontSize = 10.5.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = description,
            color = SacramentsCream.copy(alpha = 0.88f),
            fontSize = 15.sp,
            lineHeight = 23.sp,
        )

        Spacer(Modifier.height(40.dp))
    }
}
