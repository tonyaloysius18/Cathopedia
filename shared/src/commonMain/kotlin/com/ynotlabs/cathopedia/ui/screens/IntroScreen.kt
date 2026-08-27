package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.cathopedia_app_logo_transparent
import org.jetbrains.compose.resources.painterResource

private val IntroBg = Color(0xFF061A13)
private val IntroSurface = Color(0xFF0C271E)
private val IntroSurfaceRaised = Color(0xFF123127)
private val IntroBorder = Color(0xFF315444)
private val IntroGold = Color(0xFFD8B24C)
private val IntroGoldSoft = Color(0xFF9D8858)
private val IntroCream = Color(0xFFF4ECDD)
private val IntroMuted = Color(0xFFB4AD98)

private data class IntroPage(
    val title: String,
    val body: String,
    val kind: IntroKind,
)

private enum class IntroKind {
    CONNECT,
    OFFLINE,
    SEARCH,
}

private fun introPages(s: Strings) = listOf(
    IntroPage(
        title = s.introPage1Title,
        body = s.introPage1Body,
        kind = IntroKind.CONNECT,
    ),
    IntroPage(
        title = s.introPage2Title,
        body = s.introPage2Body,
        kind = IntroKind.OFFLINE,
    ),
    IntroPage(
        title = s.introPage3Title,
        body = s.introPage3Body,
        kind = IntroKind.SEARCH,
    ),
)

@Composable
fun IntroScreen(
    onDone: () -> Unit,
) {
    val s = LocalStrings.current
    val introPages = remember(s) { introPages(s) }
    var page by remember { mutableStateOf(0) }
    val current = introPages[page]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IntroBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        IntroTopBar(
            showBrand = page == 0,
            onSkip = onDone,
        )

        Spacer(Modifier.height(6.dp))

        IntroArtwork(
            kind = current.kind,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = current.title,
            modifier = Modifier.fillMaxWidth(),
            color = IntroCream,
            fontFamily = FontFamily.Serif,
            fontSize = 31.sp,
            lineHeight = 35.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = current.body,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            color = IntroMuted,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        IntroProgress(
            selectedPage = page,
            pageCount = introPages.size,
        )

        Spacer(Modifier.height(18.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clickable {
                    if (page < introPages.lastIndex) {
                        page += 1
                    } else {
                        onDone()
                    }
                },
            shape = RoundedCornerShape(29.dp),
            color = IntroGold,
            shadowElevation = 5.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (page < introPages.lastIndex) s.introNext else s.introGetStarted,
                    color = Color(0xFF101913),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF101913),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)
                        .size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun IntroTopBar(
    showBrand: Boolean,
    onSkip: () -> Unit,
) {
    val s = LocalStrings.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBrand) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(Res.drawable.cathopedia_app_logo_transparent),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit,
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Cathopedia",
                    color = IntroCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Surface(
            modifier = Modifier.clickable(onClick = onSkip),
            shape = RoundedCornerShape(18.dp),
            color = IntroSurface.copy(alpha = 0.80f),
            border = BorderStroke(
                width = 2.dp,
                color = IntroGoldSoft.copy(alpha = 0.45f),
            ),
        ) {
            Text(
                text = s.skip,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                color = IntroGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun IntroArtwork(
    kind: IntroKind,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        IntroSurfaceRaised.copy(alpha = 0.96f),
                        IntroSurface.copy(alpha = 0.88f),
                    ),
                ),
            )
            .border(
                width = 2.dp,
                color = IntroBorder.copy(alpha = 0.76f),
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            IntroGold.copy(alpha = 0.16f),
                            IntroGold.copy(alpha = 0.035f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        when (kind) {
            IntroKind.CONNECT -> ConnectArtwork()
            IntroKind.OFFLINE -> OfflineArtwork()
            IntroKind.SEARCH -> SearchArtwork()
        }
    }
}

@Composable
private fun ConnectArtwork() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ConnectorLine(
            modifier = Modifier
                .width(170.dp)
                .height(1.dp)
                .background(IntroGold.copy(alpha = 0.28f)),
        )

        ConnectorLine(
            modifier = Modifier
                .height(170.dp)
                .width(1.dp)
                .background(IntroGold.copy(alpha = 0.28f)),
        )

        SacredIconBubble(
            icon = Icons.Filled.AutoStories,
            size = 104,
            iconSize = 46,
            selected = true,
        )

        SacredIconBubble(
            icon = Icons.Filled.Church,
            size = 70,
            iconSize = 31,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 36.dp),
        )

        SacredIconBubble(
            icon = Icons.Filled.Event,
            size = 70,
            iconSize = 31,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 38.dp),
        )

        SacredIconBubble(
            icon = Icons.Filled.Language,
            size = 70,
            iconSize = 31,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 38.dp),
        )

        SacredIconBubble(
            icon = Icons.Filled.TravelExplore,
            size = 70,
            iconSize = 31,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
        )
    }
}

@Composable
private fun OfflineArtwork() {
    val s = LocalStrings.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(190.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                IntroGold.copy(alpha = 0.20f),
                                Color.Transparent,
                            ),
                        ),
                        CircleShape,
                    ),
            )

            Surface(
                modifier = Modifier.size(132.dp),
                shape = CircleShape,
                color = IntroBg.copy(alpha = 0.62f),
                border = BorderStroke(
                    width = 2.dp,
                    color = IntroGold.copy(alpha = 0.58f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Church,
                        contentDescription = null,
                        tint = IntroGold,
                        modifier = Modifier.size(68.dp),
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(58.dp),
                shape = CircleShape,
                color = IntroGold,
                shadowElevation = 5.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.CloudOff,
                        contentDescription = null,
                        tint = Color(0xFF102119),
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = s.introAvailableWhereverYouGo,
            color = IntroGold,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SearchArtwork() {
    val s = LocalStrings.current
    Column(
        modifier = Modifier.padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SacredIconBubble(
                icon = Icons.Filled.Language,
                size = 56,
                iconSize = 25,
            )
            Spacer(Modifier.width(18.dp))
            SacredIconBubble(
                icon = Icons.Filled.Church,
                size = 64,
                iconSize = 29,
                selected = true,
            )
            Spacer(Modifier.width(18.dp))
            SacredIconBubble(
                icon = Icons.Filled.Event,
                size = 56,
                iconSize = 25,
            )
        }

        Spacer(Modifier.height(26.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            color = IntroBg.copy(alpha = 0.70f),
            border = BorderStroke(
                width = 2.dp,
                color = IntroGold.copy(alpha = 0.48f),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = IntroGold,
                    modifier = Modifier.size(25.dp),
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = s.searchCathopedia,
                    color = IntroMuted,
                    fontSize = 14.sp,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SacredIconBubble(
                icon = Icons.Filled.AutoStories,
                size = 56,
                iconSize = 25,
            )
            Spacer(Modifier.width(18.dp))
            SacredIconBubble(
                icon = Icons.Filled.TravelExplore,
                size = 56,
                iconSize = 25,
            )
        }
    }
}

@Composable
private fun SacredIconBubble(
    icon: ImageVector,
    size: Int,
    iconSize: Int,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(size.dp),
        shape = CircleShape,
        color = if (selected) IntroSurfaceRaised else IntroBg.copy(alpha = 0.70f),
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) IntroGold else IntroGold.copy(alpha = 0.30f),
        ),
        shadowElevation = if (selected) 6.dp else 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IntroGold,
                modifier = Modifier.size(iconSize.dp),
            )
        }
    }
}

@Composable
private fun ConnectorLine(
    modifier: Modifier,
) {
    Box(modifier = modifier)
}

@Composable
private fun IntroProgress(
    selectedPage: Int,
    pageCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.weight(1f))

        repeat(pageCount) { index ->
            val selected = index == selectedPage

            Box(
                modifier = Modifier
                    .width(if (selected) 28.dp else 8.dp)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            IntroGold
                        } else {
                            IntroBorder.copy(alpha = 0.78f)
                        },
                    ),
            )

            if (index != pageCount - 1) {
                Spacer(Modifier.width(7.dp))
            }
        }

        Spacer(Modifier.weight(1f))
    }
}




