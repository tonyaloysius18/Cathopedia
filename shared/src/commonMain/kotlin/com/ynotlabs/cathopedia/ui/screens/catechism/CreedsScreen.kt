package com.ynotlabs.cathopedia.ui.screens.catechism

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Church
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.resources.Res
import org.jetbrains.compose.resources.painterResource
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.EntityType
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton

private val CreedsBackground = Color(0xFF061A13)
private val CreedsHeader = Color(0xFF081F17)
private val CreedsSurface = Color(0xFF0C271E)
private val CreedsSurfaceRaised = Color(0xFF103126)
private val CreedsGold = Color(0xFFD6AE3D)
private val CreedsCream = Color(0xFFF4ECDD)
private val CreedsMuted = Color(0xFFB7B09D)

private val CreedsStringKeys = setOf(
    "art.creeds.title",
    "art.creeds.lead",
    "art.creeds.p1",
    "art.creeds.eyebrow",
    "art.creeds.section",
    "art.creeds.apostles.description",
    "art.creeds.nicene.description",
    "art.creeds.apostles.period",
    "art.creeds.nicene.period",
    "art.creeds.history.title",
    "art.creeds.history.body",
    "art.creeds.ref.apostles",
    "art.creeds.ref.nicene",
)

private data class CreedCardData(
    val titleKey: String,
    val descriptionKey: String,
    val periodKey: String,
    val prayerId: String,
    val icon: ImageVector,
)

@Composable
fun CreedsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    val s = LocalStrings.current
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    LaunchedEffect(language) {
        strings = repository.resolveHubStrings(CreedsStringKeys, language)
    }

    fun text(key: String): String = strings[key].orEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreedsBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeight + 12.dp,
                end = 20.dp,
                bottom = 120.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                CreedsIntroductionCard(text("art.creeds.p1"))
            }

            item {
                SacredDivider()
            }

            item {
                Text(
                    text = text("art.creeds.section"),
                    color = CreedsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 23.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            val cards = listOf(
                CreedCardData(
                    titleKey = "art.creeds.ref.apostles",
                    descriptionKey = "art.creeds.apostles.description",
                    periodKey = "art.creeds.apostles.period",
                    prayerId = "apostles-creed",
                    icon = Icons.Filled.AutoStories,
                ),
                CreedCardData(
                    titleKey = "art.creeds.ref.nicene",
                    descriptionKey = "art.creeds.nicene.description",
                    periodKey = "art.creeds.nicene.period",
                    prayerId = "nicene-creed",
                    icon = Icons.Filled.Church,
                ),
            )

            items(cards.size) { index ->
                val card = cards[index]
                CreedPrayerCard(
                    title = text(card.titleKey),
                    description = text(card.descriptionKey),
                    onClick = {
                        onEntityRefSelected(
                            EntityRef(
                                type = EntityType.PRAYER,
                                id = card.prayerId,
                                labelKey = card.titleKey,
                            ),
                        )
                    },
                )
            }

            item {
                CreedsHistoryCard(
                    title = text("art.creeds.history.title"),
                    body = text("art.creeds.history.body"),
                )
            }
        }

        CreedsHeaderPanel(
            title = text("art.creeds.title"),
            lead = text("art.creeds.lead"),
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun CreedsHeaderPanel(
    title: String,
    lead: String?,
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
            .background(CreedsHeader)
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
                    color = CreedsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                    onTextLayout = { titleLineCount = it.lineCount }
                )

                if (!lead.isNullOrBlank()) {
                    Spacer(Modifier.height(if (titleLineCount <= 1) 4.dp else 10.dp))
                    Text(
                        text = lead,
                        color = CreedsMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreedsIntroductionCard(body: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CreedsSurface)
            .border(1.dp, CreedsGold.copy(alpha = 0.16f), RoundedCornerShape(20.dp)),
    ) {
        GoldCardAccent(Modifier.align(Alignment.CenterStart))
        Text(
            text = body,
            color = CreedsCream,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            lineHeight = 25.sp,
            modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
        )
    }
}

@Composable
private fun CreedPrayerCard(
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = shape,
        color = CreedsSurface,
        border = BorderStroke(1.dp, CreedsGold.copy(alpha = 0.24f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Row(
                modifier = Modifier.padding(start = 18.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = CreedsCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 19.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = description,
                        color = CreedsMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }

                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = CreedsGold,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun CreedsHistoryCard(
    title: String,
    body: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CreedsSurfaceRaised,
        border = BorderStroke(1.dp, CreedsGold.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Filled.AutoStories,
                contentDescription = null,
                tint = CreedsGold,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    color = CreedsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = body,
                    color = CreedsMuted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
            }
        }
    }
}
