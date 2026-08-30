package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubDetail
import com.ynotlabs.cathopedia.model.HubSectionSummary
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.hubAssetPainter

private val HubBackground = Color(0xFF061A13)
private val HubCard = Color(0xFF0A241B)
private val HubCardDeep = Color(0xFF081F17)
private val HubGold = Color(0xFFD6AE3D)
private val HubCream = Color(0xFFF4ECDD)
private val HubMuted = Color(0xFFB7B09D)

/**
 * Premium hub screen used by Cathopedia topic hubs.
 *
 * The screen remains completely data driven:
 * - titles/subtitles/intro are resolved from hub string keys
 * - sections come from HubDetail
 * - STUB sections remain disabled
 * - PUBLISHED sections preserve the existing navigation flow
 */
@Composable
fun HubScreen(
    hubId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onSectionSelected: (HubSectionSummary) -> Unit,
    onArticleSelected: (String) -> Unit,
) {
    val s = LocalStrings.current

    var detail by remember(hubId) { mutableStateOf<HubDetail?>(null) }
    var strings by remember(hubId) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(hubId, language) {
        val loaded = repository.hubDetail(hubId) ?: return@LaunchedEffect
        detail = loaded

        val keys = buildSet {
            add(loaded.summary.titleKey)
            loaded.summary.subtitleKey?.let(::add)
            loaded.introKey?.let(::add)

            loaded.sections.forEach { section ->
                add(section.titleKey)
                section.summaryKey?.let(::add)
            }
        }

        strings = repository.resolveHubStrings(keys, language)
    }

    val hub = detail
    val accent = hub?.summary?.accentColor
        ?.toComposeColorOrNull()
        ?: HubGold
    var headerHeightPx by remember(hubId, language) { mutableIntStateOf(0) }
    val headerHeightDp = with(LocalDensity.current) { headerHeightPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HubBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeightDp + 28.dp,
                end = 20.dp,
                bottom = 132.dp,
            ),
        ) {
            if (hub != null) {
                val intro = strings[hub.introKey].orEmpty()
                if (intro.isNotBlank()) {
                    item {
                        HubIntroCard(intro)
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    SacredDivider()
                    Spacer(Modifier.height(18.dp))
                }

                val rows = hub.sections.chunked(2)

                rows.forEachIndexed { rowIndex, rowSections ->
                    item(key = "hub-row-$rowIndex") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            rowSections.forEach { section ->
                                HubSectionCard(
                                    modifier = Modifier.weight(1f),
                                    section = section,
                                    title = strings[section.titleKey].orEmpty(),
                                    summary = section.summaryKey?.let { strings[it] },
                                    accent = accent,
                                    comingSoon = s.hubSectionComingSoon,
                                    onClick = {
                                        section.directArticleId
                                            ?.let(onArticleSelected)
                                            ?: onSectionSelected(section)
                                    },
                                )
                            }

                            if (rowSections.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }

        HubHeaderPanel(
            title = hub?.let { strings[it.summary.titleKey] }.orEmpty(),
            subtitle = hub?.summary?.subtitleKey?.let { strings[it] }.orEmpty(),
            accent = accent,
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                val measuredHeight = it.size.height
                if (measuredHeight != headerHeightPx) headerHeightPx = measuredHeight
            },
        )
    }
}

@Composable
private fun HubIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = HubCard,
        contentColor = HubCream,
        border = BorderStroke(1.dp, HubGold.copy(alpha = 0.35f)),
    ) {
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(HubGold),
            )

            Text(
                text = text,
                color = HubCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun HubHeaderPanel(
    title: String,
    subtitle: String,
    accent: Color,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(HubCardDeep)
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
                    color = HubCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                )

                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        color = accent,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun HubSectionCard(
    modifier: Modifier,
    section: HubSectionSummary,
    title: String,
    summary: String?,
    accent: Color,
    comingSoon: String,
    onClick: () -> Unit,
) {
    val isStub = section.status == "STUB"

    Surface(
        modifier = modifier
            .height(140.dp)
            .alpha(if (isStub) 0.58f else 1f)
            .clickable(
                enabled = !isStub,
                onClick = onClick,
            ),
        shape = RoundedCornerShape(23.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (isStub) {
                HubMuted.copy(alpha = 0.18f)
            } else {
                accent.copy(alpha = 0.40f)
            },
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            HubCard.copy(alpha = 0.78f),
                            HubCardDeep.copy(alpha = 0.94f),
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(accent),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
            ) {
                Text(
                    text = title,
                    color = HubCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    modifier = Modifier.padding(end = 20.dp),
                )

                if (!summary.isNullOrBlank()) {
                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = summary,
                        color = HubMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        maxLines = 3,
                    )
                }

                if (isStub) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = comingSoon,
                        color = HubMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (!isStub) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = accent.copy(alpha = 0.86f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 18.dp, end = 14.dp)
                        .size(14.dp),
                )
            }
        }
    }
}

/** `#RRGGBB` or `#AARRGGBB` hex string → [Color]; null for anything invalid. */
private fun String.toComposeColorOrNull(): Color? {
    val hex = removePrefix("#")
    if (hex.length != 6 && hex.length != 8) return null

    val value = hex.toLongOrNull(16) ?: return null

    return if (hex.length == 6) {
        Color(0xFF000000 or value)
    } else {
        Color(value)
    }
}
