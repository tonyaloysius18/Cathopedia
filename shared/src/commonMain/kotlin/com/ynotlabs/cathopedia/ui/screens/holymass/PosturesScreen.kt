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
import com.ynotlabs.cathopedia.mass.Posture
import com.ynotlabs.cathopedia.mass.PostureImages
import com.ynotlabs.cathopedia.mass.PosturesData
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import org.jetbrains.compose.resources.painterResource

// Green + gold, matching the Cathopedia hub pages.
private val PosturesBg = Color(0xFF061A13)
private val PosturesSurface = Color(0xFF0A241B)
private val PosturesSurfaceRaised = Color(0xFF0F2E22)
private val PosturesGold = Color(0xFFD6AE3D)
private val PosturesCream = Color(0xFFF4ECDD)
private val PosturesMuted = Color(0xFFB7B09D)
private val PosturesHeader = Color(0xFF081F17)

@Composable
fun PosturesScreen(
    @Suppress("UNUSED_PARAMETER") repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val isFrench = language == "fr"
    val postures = PosturesData.postures
    val intro = if (isFrench) PosturesData.INTRO_FR else PosturesData.INTRO_EN
    val responses = if (isFrench) PosturesData.RESPONSES_FR else PosturesData.RESPONSES_EN

    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PosturesBg),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeight + 20.dp,
                end = 20.dp,
                bottom = 120.dp,
            ),
        ) {
            item {
                PosturesIntroCard(intro)
                Spacer(Modifier.height(16.dp))
            }

            item {
                SacredDivider()
                Spacer(Modifier.height(18.dp))
            }

            // "The Postures" section label
            item {
                Text(
                    text = s.posturesLabel.uppercase(),
                    color = PosturesGold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            // Posture cards, two per row
            items(postures.chunked(2)) { rowPostures ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowPostures.forEach { posture ->
                        PostureCard(
                            posture = posture,
                            isFrench = isFrench,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    if (rowPostures.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            // Responses of the people
            item {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = s.posturesResponsesLabel.uppercase(),
                    color = PosturesGold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = PosturesSurface,
                    border = BorderStroke(1.dp, PosturesGold.copy(alpha = 0.35f)),
                ) {
                    Text(
                        text = responses,
                        color = PosturesCream.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }

        PosturesHeaderPanel(
            title = s.posturesScreenTitle,
            subtitle = s.posturesSubtitle,
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun PosturesIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = PosturesSurface,
        contentColor = PosturesCream,
        border = BorderStroke(1.dp, PosturesGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Text(
                text = text,
                color = PosturesCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun PosturesHeaderPanel(
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
            .background(PosturesHeader)
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
                    color = PosturesCream,
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
                        color = PosturesGold,
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
private fun PostureCard(
    posture: Posture,
    isFrench: Boolean,
    modifier: Modifier = Modifier,
) {
    val painter = PostureImages.forPosture(posture.id)?.let { painterResource(it) }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = PosturesSurface,
        border = BorderStroke(1.dp, PosturesGold.copy(alpha = 0.35f)),
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
                        .background(PosturesSurfaceRaised, RoundedCornerShape(14.dp)),
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
                    text = if (isFrench) posture.nameFr else posture.nameEn,
                    color = PosturesCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isFrench) posture.descFr else posture.descEn,
                    color = PosturesCream.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}
