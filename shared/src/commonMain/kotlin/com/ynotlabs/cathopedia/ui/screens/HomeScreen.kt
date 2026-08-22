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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.liturgical.LiturgicalSeason
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.cathopedia_app_logo_transparent
import com.ynotlabs.cathopedia.resources.settings_header_bg
import com.ynotlabs.cathopedia.resources.settings_quote_bg
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.singularLabel
import com.ynotlabs.cathopedia.ui.theme.LocalLiturgicalAccent
import org.jetbrains.compose.resources.painterResource

private val HomeBg = Color(0xFF061A13)
private val HomeSurface = Color(0xFF0C271E)
private val HomeSurfaceRaised = Color(0xFF123127)
private val HomeBorder = Color(0xFF315444)
private val HomeGold = Color(0xFFD8B24C)
private val HomeGoldSoft = Color(0xFF9D8858)
private val HomeCream = Color(0xFFF4ECDD)
private val HomeMuted = Color(0xFFB4AD98)

@Composable
fun HomeScreen(
    repository: CathopediaRepository,
    language: String,
    onItemSelected: (ContentSummary) -> Unit,
) {
    val s = LocalStrings.current
    val today = remember { LiturgicalCalendar.today() }
    val season = remember(today) { LiturgicalCalendar.liturgicalDayFor(today).season }

    var continueReading by remember { mutableStateOf<ContentSummary?>(null) }
    var discover by remember { mutableStateOf<ContentSummary?>(null) }
    var feastOfTheDay by remember { mutableStateOf<ContentSummary?>(null) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(language) {
        continueReading = repository.mostRecentlyViewed()
        discover = repository.discoverPick(language)
        feastOfTheDay = repository.feastOfToday(language, today)
        loaded = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 118.dp),
    ) {
        HomeHero(seasonLabel = season.label(s))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        ) {
            DailyFaithCard()

            Spacer(Modifier.height(22.dp))

            if (feastOfTheDay != null) {
                HomeSectionHeader(
                    title = s.homeFeastOfTheDay,
                    icon = Icons.Filled.Church,
                )

                Spacer(Modifier.height(9.dp))

                EditorialHomeCard(
                    item = feastOfTheDay!!,
                    onClick = { onItemSelected(feastOfTheDay!!) },
                )

                Spacer(Modifier.height(22.dp))
            }

            if (continueReading != null) {
                HomeSectionHeader(
                    title = s.homeContinueReading,
                    icon = Icons.Filled.AutoStories,
                )

                Spacer(Modifier.height(9.dp))

                EditorialHomeCard(
                    item = continueReading!!,
                    onClick = { onItemSelected(continueReading!!) },
                )

                Spacer(Modifier.height(22.dp))
            }

            if (discover != null) {
                HomeSectionHeader(
                    title = s.homeDiscover,
                    icon = Icons.Filled.Language,
                )

                Spacer(Modifier.height(9.dp))

                EditorialHomeCard(
                    item = discover!!,
                    onClick = { onItemSelected(discover!!) },
                )

                Spacer(Modifier.height(22.dp))
            }

            //QuickExploreSection()

            if (loaded && continueReading == null && discover == null && feastOfTheDay == null) {
                Spacer(Modifier.height(18.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = HomeSurface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = HomeBorder.copy(alpha = 0.72f),
                    ),
                ) {
                    Text(
                        text = s.homeNothingToShowYet,
                        modifier = Modifier.padding(18.dp),
                        color = HomeMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeHero(seasonLabel: String) {
    val liturgicalAccent = LocalLiturgicalAccent.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .statusBarsPadding(),
    ) {
        Image(
            painter = painterResource(Res.drawable.settings_header_bg),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.67f)
                .height(190.dp)
                .padding(top = 6.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopEnd,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.00f to HomeBg,
                            0.48f to HomeBg.copy(alpha = 0.96f),
                            0.72f to HomeBg.copy(alpha = 0.62f),
                            1.00f to Color.Transparent,
                        ),
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            HomeBg.copy(alpha = 0.16f),
                            HomeBg,
                        ),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.cathopedia_app_logo_transparent),
                contentDescription = "Cathopedia",
                modifier = Modifier.size(66.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cathopedia",
                    color = HomeCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "Faith • Knowledge • Tradition",
                    color = HomeGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = HomeSurface.copy(alpha = 0.80f),
                border = BorderStroke(
                    width = 1.dp,
                    color = HomeGoldSoft.copy(alpha = 0.48f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = HomeGold,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        Text(
            text = "Your Catholic encyclopedia\nfor faith and life",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 18.dp),
            color = HomeCream.copy(alpha = 0.90f),
            fontSize = 14.sp,
            lineHeight = 19.sp,
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 18.dp),
            shape = RoundedCornerShape(50),
            color = HomeSurface.copy(alpha = 0.80f),
            border = BorderStroke(width = 1.dp, color = liturgicalAccent.copy(alpha = 0.55f)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(liturgicalAccent, CircleShape),
                )

                Spacer(Modifier.width(7.dp))

                Text(
                    text = seasonLabel.uppercase(),
                    color = HomeCream,
                    fontSize = 10.sp,
                    letterSpacing = 0.7.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun DailyFaithCard() {
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp)
            .clip(shape)
            .background(HomeSurface)
            .border(
                width = 1.dp,
                color = HomeGoldSoft.copy(alpha = 0.50f),
                shape = shape,
            ),
    ) {
        Image(
            painter = painterResource(Res.drawable.settings_quote_bg),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.00f to HomeBg.copy(alpha = 0.98f),
                            0.42f to HomeBg.copy(alpha = 0.94f),
                            0.72f to HomeBg.copy(alpha = 0.54f),
                            1.00f to HomeBg.copy(alpha = 0.08f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
        ) {
            Text(
                text = "“",
                color = HomeGold,
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                lineHeight = 28.sp,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Faith is the realization of what is hoped for and evidence of things not seen.",
                modifier = Modifier.fillMaxWidth(0.68f),
                color = HomeCream,
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                lineHeight = 23.sp,
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "Hebrews 11:1",
                color = HomeGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == 0) 7.dp else 6.dp)
                        .background(
                            if (index == 0) HomeGold else HomeMuted.copy(alpha = 0.28f),
                            CircleShape,
                        ),
                )
            }
        }
    }
}

private fun LiturgicalSeason.label(s: Strings): String = when (this) {
    LiturgicalSeason.ADVENT -> s.seasonAdvent
    LiturgicalSeason.CHRISTMAS -> s.seasonChristmas
    LiturgicalSeason.ORDINARY_TIME -> s.seasonOrdinaryTime
    LiturgicalSeason.LENT -> s.seasonLent
    LiturgicalSeason.EASTER -> s.seasonEaster
}

@Composable
private fun HomeSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HomeGold,
            modifier = Modifier.size(20.dp),
        )

        Spacer(Modifier.width(9.dp))

        Text(
            text = title.uppercase(),
            color = HomeGold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            HomeGoldSoft.copy(alpha = 0.42f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun EditorialHomeCard(
    item: ContentSummary,
    onClick: () -> Unit,
) {
    val s = LocalStrings.current
    val portrait = Portraits.fullForEntity(item.type, item.id)
    val shape = RoundedCornerShape(24.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        HomeSurfaceRaised,
                        HomeSurface,
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = HomeBorder.copy(alpha = 0.78f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (portrait != null) {
            Image(
                painter = painterResource(portrait),
                contentDescription = item.name,
                modifier = Modifier
                    .width(118.dp)
                    .height(166.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        width = 1.dp,
                        color = HomeGoldSoft.copy(alpha = 0.50f),
                        shape = RoundedCornerShape(18.dp),
                    ),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
            )

            Spacer(Modifier.width(14.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .height(166.dp),
        ) {
            Text(
                text = item.type.singularLabel(s).uppercase(),
                color = HomeGold,
                fontSize = 10.5.sp,
                letterSpacing = 0.9.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = item.name,
                color = HomeCream,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = item.summary,
                color = HomeMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
