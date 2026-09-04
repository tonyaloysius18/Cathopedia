package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.mass.AltarData
import com.ynotlabs.cathopedia.mass.AltarImages
import com.ynotlabs.cathopedia.mass.AltarItem
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.altar_five_crosses
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import org.jetbrains.compose.resources.painterResource

// Green + gold, matching the Cathopedia hub pages.
private val AltarBg = Color(0xFF061A13)
private val AltarSurface = Color(0xFF0A241B)
private val AltarSurfaceRaised = Color(0xFF0F2E22)
private val AltarGold = Color(0xFFD6AE3D)
private val AltarGoldSoft = Color(0xFFB08D57)
private val AltarCream = Color(0xFFF4ECDD)
private val AltarMuted = Color(0xFFB7B09D)
private val AltarHeader = Color(0xFF081F17)

@Composable
fun AltarScreen(
    @Suppress("UNUSED_PARAMETER") repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    val isFrench = language == "fr"
    val intro = if (isFrench) AltarData.INTRO_FR else AltarData.INTRO_EN
    val markings = if (isFrench) AltarData.MARKINGS_FR else AltarData.MARKINGS_EN
    val kissTitle = if (isFrench) AltarData.KISS_TITLE_FR else AltarData.KISS_TITLE_EN
    val kissBody = if (isFrench) AltarData.KISS_BODY_FR else AltarData.KISS_BODY_EN

    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AltarBg),
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
                AltarIntroCard(intro)
                Spacer(Modifier.height(16.dp))
            }

            item {
                SacredDivider()
                Spacer(Modifier.height(18.dp))
            }

            // How the altar is dressed
            item { SectionLabel(s.altarDecorationLabel) }
            items(AltarData.decoration.chunked(2)) { row ->
                CardRow(row, isFrench)
            }

            // The altar cloths
            item {
                Spacer(Modifier.height(10.dp))
                SectionLabel(s.altarClothsLabel)
            }
            items(AltarData.cloths.chunked(2)) { row ->
                CardRow(row, isFrench)
            }

            // The five crosses (markings)
            item {
                Spacer(Modifier.height(10.dp))
                SectionLabel(s.altarMarkingsLabel)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = AltarSurface,
                    border = BorderStroke(1.dp, AltarGold.copy(alpha = 0.35f)),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(AltarSurfaceRaised, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.altar_five_crosses),
                                contentDescription = s.altarMarkingsLabel,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = markings,
                            color = AltarCream.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = kissTitle.uppercase(),
                            color = AltarGoldSoft,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = kissBody,
                            color = AltarCream.copy(alpha = 0.82f),
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                        )
                    }
                }
            }
        }

        AltarHeaderPanel(
            title = s.altarScreenTitle,
            subtitle = s.altarSubtitle,
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = AltarGold,
        fontSize = 12.sp,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun CardRow(row: List<AltarItem>, isFrench: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        row.forEach { item ->
            ItemCard(
                item = item,
                isFrench = isFrench,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
        if (row.size == 1) Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun AltarIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = AltarSurface,
        contentColor = AltarCream,
        border = BorderStroke(1.dp, AltarGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Text(
                text = text,
                color = AltarCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun AltarHeaderPanel(
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
            .background(AltarHeader)
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
                    color = AltarCream,
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
                        color = AltarGold,
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
private fun ItemCard(
    item: AltarItem,
    isFrench: Boolean,
    modifier: Modifier = Modifier,
) {
    val painter = AltarImages.forItem(item.id)?.let { painterResource(it) }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = AltarSurface,
        border = BorderStroke(1.dp, AltarGold.copy(alpha = 0.35f)),
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
                        .background(AltarSurfaceRaised, RoundedCornerShape(14.dp)),
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
                    text = if (isFrench) item.nameFr else item.nameEn,
                    color = AltarCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isFrench) item.descFr else item.descEn,
                    color = AltarCream.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
