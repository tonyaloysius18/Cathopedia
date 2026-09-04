package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.holy_trinity_bg
import com.ynotlabs.cathopedia.resources.the_father
import com.ynotlabs.cathopedia.resources.the_holysprit
import com.ynotlabs.cathopedia.resources.the_son
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val TrinityBackground = Color(0xFF041710)
private val TrinitySurface = Color(0xFF09251B)
private val TrinitySurfaceDeep = Color(0xFF071E16)
private val TrinityGold = Color(0xFFD6A936)
private val TrinityCream = Color(0xFFF5EDDC)
private val TrinityMuted = Color(0xFFC7BEA8)
private val TrinityCallout = Color(0xFF0E3125)

private val TrinityStringKeys = setOf(
    "art.trinity.title",
    "art.trinity.lead",
    "art.trinity.p1",
    "art.trinity.li1",
    "art.trinity.li2",
    "art.trinity.li3",
    "art.trinity.callout.title",
    "art.trinity.callout.body",
    "art.trinity.quote",
    "art.trinity.quote.attribution",
)

private data class TrinityPerson(
    val textKey: String,
    val image: DrawableResource,
)

@Composable
fun HolyTrinityScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    scrollState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        strings = repository.resolveHubStrings(TrinityStringKeys, language)
    }

    fun text(key: String): String = strings[key].orEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TrinityBackground),
        state = scrollState,
        contentPadding = PaddingValues(bottom = 120.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
            ) {
                Image(
                    painter = painterResource(Res.drawable.holy_trinity_bg),
                    contentDescription = text("art.trinity.title"),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(top = 60.dp, bottom = 50.dp, start = 30.dp, end = 30.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.08f),
                                    Color.Transparent,
                                    TrinityBackground.copy(alpha = 0.18f),
                                    TrinityBackground,
                                ),
                            ),
                        ),
                )

                CathopediaBackButton(
                    onClick = onBack,
                    contentDescription = s.back,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(start = 18.dp, top = 10.dp),
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = text("art.trinity.title"),
                        color = TrinityCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 38.sp,
                        lineHeight = 42.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = text("art.trinity.lead"),
                        color = TrinityGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 19.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }


        item {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SacredDivider()

                Text(
                    text = text("art.trinity.p1"),
                    color = TrinityCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )

                listOf(
                    TrinityPerson("art.trinity.li1", Res.drawable.the_father),
                    TrinityPerson("art.trinity.li2", Res.drawable.the_son),
                    TrinityPerson("art.trinity.li3", Res.drawable.the_holysprit),
                ).forEach { person ->
                    TrinityPersonCard(
                        rawText = text(person.textKey),
                        image = person.image,
                    )
                }

                TrinityCalloutCard(
                    title = text("art.trinity.callout.title"),
                    body = text("art.trinity.callout.body"),
                )

                TrinityQuote(
                    quote = text("art.trinity.quote"),
                    attribution = text("art.trinity.quote.attribution"),
                )
            }
        }
    }
}

@Composable
private fun TrinityPersonCard(rawText: String, image: DrawableResource) {
    val parts = rawText.split(" — ", limit = 2)
    val title = parts.firstOrNull().orEmpty()
    val body = parts.getOrNull(1).orEmpty()
    val shape = RoundedCornerShape(20.dp)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = TrinitySurface,
        contentColor = TrinityCream,
        border = BorderStroke(1.dp, TrinityGold.copy(alpha = 0.48f)),
    ) {
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                    .background(TrinityGold),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 10.dp, end = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = TrinitySurfaceDeep,
                ) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(3.dp),
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = TrinityCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 21.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    if (body.isNotBlank()) {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = body,
                            color = TrinityGold,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrinityCalloutCard(title: String, body: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = TrinityCallout,
        border = BorderStroke(1.dp, TrinityGold.copy(alpha = 0.82f)),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TrinityGold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = body,
                    color = TrinityCream,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
            }
        }
    }
}

@Composable
private fun TrinityQuote(quote: String, attribution: String) {
    if (quote.isBlank()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "“",
            color = TrinityGold,
            fontFamily = FontFamily.Serif,
            fontSize = 34.sp,
            lineHeight = 28.sp,
        )
        Text(
            text = quote,
            color = TrinityCream,
            fontFamily = FontFamily.Serif,
            fontSize = 16.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center,
        )
        if (attribution.isNotBlank()) {
            Spacer(Modifier.height(7.dp))
            Text(
                text = attribution,
                color = TrinityGold,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
            )
        }
    }
}
