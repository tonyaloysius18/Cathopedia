package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.hubAssetPainter

private val CardinalStringKeys = setOf(
    "art.cardinals.title",
    "art.cardinals.lead",
    "art.cardinals.p1",
    "art.cardinals.h_orders",
    "art.cardinals.grid.bishop.label",
    "art.cardinals.grid.bishop.value",
    "art.cardinals.grid.priest.label",
    "art.cardinals.grid.priest.value",
    "art.cardinals.grid.deacon.label",
    "art.cardinals.grid.deacon.value",
    "art.cardinals.h_role",
    "art.cardinals.p3",
    "art.cardinals.callout.title",
    "art.cardinals.callout.body",
    "art.cardinals.orders.note",
    "art.cardinals.order.deacon.badge",
    "art.cardinals.order.priest.badge",
    "art.cardinals.order.bishop.badge",
    "cardinals.carousel.label",
)

@Composable
fun CardinalsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        strings = repository.resolveHubStrings(CardinalStringKeys, language)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(HolySeeBackground),
        contentPadding = PaddingValues(bottom = 56.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 8.dp, top = 8.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CathopediaBackButton(onClick = onBack, contentDescription = s.back)
                Text(
                    text = strings["art.cardinals.title"].orEmpty(),
                    color = HolySeeCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        if (strings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = CardinalScarlet)
                }
            }
        } else {
            item {
                Surface(
                    color = HolySeeSurface,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, CardinalScarlet.copy(alpha = 0.52f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.45f)
                            .clip(RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        hubAssetPainter("hub/holy_see/cardinals_hero.png")?.let { painter ->
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        0.45f to androidx.compose.ui.graphics.Color.Transparent,
                                        1f to HolySeeBackground.copy(alpha = 0.97f),
                                    )
                                )
                        )
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = strings["art.cardinals.h_orders"].orEmpty().uppercase(),
                                color = HolySeeGold,
                                fontSize = 10.sp,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = strings["art.cardinals.lead"].orEmpty(),
                                color = HolySeeCream,
                                fontFamily = FontFamily.Serif,
                                fontSize = 19.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }

                Text(
                    text = strings["art.cardinals.p1"].orEmpty(),
                    color = HolySeeCream.copy(alpha = 0.88f),
                    fontSize = 15.5.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                )

                HolySeePortraitCarousel(
                    label = strings["cardinals.carousel.label"].orEmpty(),
                    accent = CardinalScarlet,
                    items = listOf(
                        HolySeeCarouselItem(
                            id = "cardinal_deacon",
                            title = strings["art.cardinals.grid.deacon.label"].orEmpty(),
                            body = strings["art.cardinals.grid.deacon.value"].orEmpty(),
                            badge = strings["art.cardinals.order.deacon.badge"].orEmpty(),
                            imageAsset = "hub/holy_see/cardinal_order_deacon.png",
                            emblemAsset = "hub/holy_see/cardinal_emblem_deacon.png",
                        ),
                        HolySeeCarouselItem(
                            id = "cardinal_priest",
                            title = strings["art.cardinals.grid.priest.label"].orEmpty(),
                            body = strings["art.cardinals.grid.priest.value"].orEmpty(),
                            badge = strings["art.cardinals.order.priest.badge"].orEmpty(),
                            imageAsset = "hub/holy_see/cardinal_order_priest.png",
                            emblemAsset = "hub/holy_see/cardinal_emblem_priest.png",
                        ),
                        HolySeeCarouselItem(
                            id = "cardinal_bishop",
                            title = strings["art.cardinals.grid.bishop.label"].orEmpty(),
                            body = strings["art.cardinals.grid.bishop.value"].orEmpty(),
                            badge = strings["art.cardinals.order.bishop.badge"].orEmpty(),
                            imageAsset = "hub/holy_see/cardinal_order_bishop.png",
                            emblemAsset = "hub/holy_see/cardinal_emblem_bishop.png",
                        ),
                    )
                )

                Surface(
                    color = HolySeeSurfaceRaised,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, HolySeeGold.copy(alpha = 0.34f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                ) {
                    Text(
                        text = strings["art.cardinals.orders.note"].orEmpty(),
                        color = HolySeeMuted,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp),
                    )
                }

                CardinalInformationCard(
                    title = strings["art.cardinals.h_role"].orEmpty(),
                    body = strings["art.cardinals.p3"].orEmpty(),
                )

                Surface(
                    color = CardinalScarlet.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CardinalScarlet.copy(alpha = 0.62f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = strings["art.cardinals.callout.title"].orEmpty(),
                            color = HolySeeGold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = strings["art.cardinals.callout.body"].orEmpty(),
                            color = HolySeeCream.copy(alpha = 0.88f),
                            fontSize = 15.sp,
                            lineHeight = 23.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardinalInformationCard(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = title,
            color = HolySeeCream,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = body,
            color = HolySeeCream.copy(alpha = 0.86f),
            fontSize = 15.5.sp,
            lineHeight = 24.sp,
        )
    }
}
