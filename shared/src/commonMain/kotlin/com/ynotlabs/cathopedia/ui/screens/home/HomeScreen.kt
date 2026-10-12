package com.ynotlabs.cathopedia.ui.screens.home

import com.ynotlabs.cathopedia.ui.theme.LocalIsDarkTheme
import com.ynotlabs.cathopedia.ui.theme.imageFade
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.liturgical.DailyMassReadings
import com.ynotlabs.cathopedia.liturgical.DailyReadingsCalendar
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.liturgical.LiturgicalSeason
import com.ynotlabs.cathopedia.liturgical.ReadingKind
import com.ynotlabs.cathopedia.liturgical.ScriptureReference
import com.ynotlabs.cathopedia.liturgical.fullBookNameCitation
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.cathopedia_app_logo_transparent
import com.ynotlabs.cathopedia.resources.daily_readings_card_bg
import com.ynotlabs.cathopedia.resources.settings_header_bg
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.singularLabel
import com.ynotlabs.cathopedia.ui.theme.LocalLiturgicalAccent
import kotlinx.datetime.LocalDate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import org.jetbrains.compose.resources.painterResource

private val HomeBg: Color @Composable get() = MaterialTheme.colorScheme.background
private val HomeSurface: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
private val HomeBorder: Color @Composable get() = MaterialTheme.colorScheme.outline
private val HomeGold: Color @Composable get() = MaterialTheme.colorScheme.primary
private val HomeGoldSoft: Color @Composable get() = MaterialTheme.colorScheme.secondary
private val HomeCream: Color @Composable get() = MaterialTheme.colorScheme.onBackground
private val HomeMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun HomeScreen(
    repository: CathopediaRepository,
    language: String,
    onItemSelected: (ContentSummary) -> Unit,
    onOpenPrayer: (String) -> Unit,
    onOpenRosary: () -> Unit,
    onExploreClick: () -> Unit,
    onReadingSelected: ((ScriptureReference) -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val today = remember { LiturgicalCalendar.today() }
    val season = remember(today) { LiturgicalCalendar.liturgicalDayFor(today).season }
    val dateLabel = t["screen.saints.date"].replace("{day}", today.day.toString())
        .replace("{month}", t["screen.saints.month.${today.month.ordinal + 1}"])

    var continueReading by remember(language) { mutableStateOf<ContentSummary?>(null) }
    var feastOfTheDay by remember(today, language) { mutableStateOf<ContentSummary?>(null) }
    var saintsOfToday by remember(today, language) { mutableStateOf<List<ContentSummary>>(emptyList()) }
    var dailyReadings by remember(today, language) { mutableStateOf<DailyMassReadings?>(null) }
    var dailyReadingsLoaded by remember(today, language) { mutableStateOf(false) }

    LaunchedEffect(today, language) {
        dailyReadings = DailyReadingsCalendar.readingsFor(today, language)
        dailyReadingsLoaded = true
        feastOfTheDay = repository.feastOfToday(language, today)
        saintsOfToday = repository.saintsOfToday(language)
    }
    LaunchedEffect(language) { continueReading = repository.mostRecentlyViewed(language) }

    Column(
        modifier = Modifier.fillMaxSize().background(HomeBg).verticalScroll(scrollState)
            // Reserve the existing floating bar's full resting height at the end of the scroll.
            .padding(bottom = 142.dp),
    ) {
        HomeHero(seasonLabel = season.label(s))
        Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
            DailyReadingsCard(dailyReadings, dailyReadingsLoaded, language, onReadingSelected)
            Spacer(Modifier.height(16.dp))

            feastOfTheDay?.let { feast ->
                HomeSectionHeader(s.homeFeastOfTheDay, dateLabel)
                Spacer(Modifier.height(6.dp))
                CompactHomeItem(feast, { onItemSelected(feast) })
                Spacer(Modifier.height(16.dp))
            }
            if (saintsOfToday.isNotEmpty()) {
                HomeSectionHeader(
                    if (saintsOfToday.size == 1) t["screen.saints.today"] else s.homeSaintsOfToday,
                    dateLabel,
                )
                Spacer(Modifier.height(6.dp))
                saintsOfToday.forEachIndexed { index, saint ->
                    if (index > 0) Spacer(Modifier.height(6.dp))
                    CompactHomeItem(saint, { onItemSelected(saint) })
                }
                Spacer(Modifier.height(16.dp))
            }

            HomeSectionHeader(t["screen.home.prayer_moment"])
            Spacer(Modifier.height(6.dp))
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val stack = maxWidth < 300.dp || LocalDensity.current.fontScale > 1.15f
                if (stack) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HomePrayerShortcut(t["screen.home.morning_offering"], false,
                            { onOpenPrayer("morning-offering") }, Modifier.fillMaxWidth())
                        HomePrayerShortcut(t["screen.home.pray_rosary"], true, onOpenRosary, Modifier.fillMaxWidth())
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HomePrayerShortcut(t["screen.home.morning_offering"], false,
                            { onOpenPrayer("morning-offering") }, Modifier.weight(1f))
                        HomePrayerShortcut(t["screen.home.pray_rosary"], true, onOpenRosary, Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            continueReading?.let { item ->
                HomeSectionHeader(s.homeContinueReading)
                Spacer(Modifier.height(6.dp))
                HorizontalDivider(color = HomeBorder.copy(alpha = 0.65f))
                CompactHomeItem(item, { onItemSelected(item) }, outlined = false)
                HorizontalDivider(color = HomeBorder.copy(alpha = 0.65f))
                Spacer(Modifier.height(6.dp))
            }
            TextButton(onClick = onExploreClick, contentPadding = PaddingValues(vertical = 10.dp),
                modifier = Modifier.heightIn(min = 44.dp)) {
                Text(t["screen.home.browse"], color = HomeGold, fontFamily = FontFamily.Serif,
                    fontSize = 16.sp, lineHeight = 22.sp, modifier = Modifier.weight(1f, fill = false))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = HomeGold,
                    modifier = Modifier.padding(start = 8.dp).size(18.dp))
            }
        }
    }
}

@Composable
private fun HomeHero(seasonLabel: String) {
    val s = LocalStrings.current
    val liturgicalAccent = LocalLiturgicalAccent.current
    Box(Modifier.fillMaxWidth()) {
        Box(Modifier.matchParentSize()) {
            Image(painterResource(Res.drawable.settings_header_bg), null,
                Modifier.align(Alignment.TopEnd).fillMaxWidth(0.67f).fillMaxSize(),
                contentScale = ContentScale.Fit, alignment = Alignment.TopEnd)
            Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(
                0f to HomeBg, 0.48f to HomeBg.imageFade(0.96f),
                0.72f to HomeBg.imageFade(0.45f), 1f to Color.Transparent)))
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
                listOf(Color.Transparent, HomeBg.imageFade(0.16f), HomeBg))))
        }
        Column(Modifier.statusBarsPadding().padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(Res.drawable.cathopedia_app_logo_transparent), "Cathopedia",
                    Modifier.size(48.dp), contentScale = ContentScale.Fit)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Cathopedia", color = HomeCream, fontFamily = FontFamily.Serif,
                        fontSize = 29.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold)
                    Text(s.brandTagline, color = HomeGold, fontSize = 11.5.sp, lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 3.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(s.homeHeroDescription, Modifier.weight(1f), color = HomeCream.copy(alpha = 0.9f),
                    fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(Modifier.width(10.dp))
                Surface(shape = RoundedCornerShape(50), color = HomeSurface.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, liturgicalAccent.copy(alpha = 0.55f)),
                    modifier = Modifier.widthIn(max = 126.dp)) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(liturgicalAccent, CircleShape))
                        Text(seasonLabel.uppercase(), color = HomeCream, fontSize = 9.sp, lineHeight = 13.sp,
                            letterSpacing = 0.5.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyReadingsCard(
    readings: DailyMassReadings?,
    loaded: Boolean,
    language: String,
    onReadingSelected: ((ScriptureReference) -> Unit)?,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val uriHandler = LocalUriHandler.current
    var openFailed by remember(readings?.date) { mutableStateOf(false) }
    val sourceUrl = readings?.date?.let { value ->
        runCatching { fullReadingsUrl(LocalDate.parse(value)) }.getOrNull()
    }
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HomeSurface)
            .border(
                width = 2.dp,
                color = CardBorder,
                shape = shape,
            ),
    ) {
        Image(
            painter = painterResource(Res.drawable.daily_readings_card_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd,
            // Keep the candle and Bible visible while the text has its own scrim.
            alpha = if (LocalIsDarkTheme.current) 0.85f else 0.90f
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.00f to HomeBg.imageFade(0.99f),
                            0.56f to HomeBg.imageFade(0.94f),
                            1.00f to HomeBg.imageFade(0.08f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(HomeGold.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoStories,
                        contentDescription = null,
                        tint = HomeGold,
                        modifier = Modifier.size(17.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = s.homeTodaysReadings.uppercase(),
                    color = HomeGold,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(7.dp))

            Text(
                text = readings?.title ?: if (loaded) s.homeTodaysReadings else s.loading,
                color = HomeCream,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(6.dp))

            when {
                readings == null -> Text(
                    text = if (loaded) s.homeReadingsUnavailable else "${s.loading}…",
                    color = HomeMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )

                readings.readings.isEmpty() -> Text(
                    text = s.homeReadingsVary,
                    color = HomeMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )

                else -> readings.readings.forEachIndexed { index, reference ->
                    ReadingReferenceRow(
                        reference = reference,
                        language = language,
                        onClick = onReadingSelected?.let { callback -> { callback(reference) } },
                    )
                    if (index != readings.readings.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(HomeBorder.copy(alpha = 0.42f)),
                        )
                    }
                }
            }

            readings?.featuredVerse?.let { verse ->
                Spacer(Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(HomeGoldSoft.copy(alpha = 0.38f)),
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${s.homeVerseOfTheDay.uppercase()}  •  ${fullBookNameCitation(verse.citation, language)}",
                    color = HomeGold,
                    fontSize = 9.sp,
                    letterSpacing = 0.7.sp,
                    fontWeight = FontWeight.Bold,
                )

                if (verse.text.isNotBlank()) {
                Spacer(Modifier.height(5.dp))

                Text(
                    text = "“${verse.text}”",
                    color = HomeCream.copy(alpha = 0.94f),
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.5.sp,
                    lineHeight = 15.5.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                // The Tamil text is CC BY-SA, which requires the credit to travel with it.
                if (verse.translation.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = verse.translation,
                        color = HomeGoldSoft.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                    )
                }
                }
            }
            sourceUrl?.let { url ->
                TextButton(
                    onClick = {
                        openFailed = try { uriHandler.openUri(url); false } catch (_: Exception) { true }
                    },
                    contentPadding = PaddingValues(top = 8.dp),
                    modifier = Modifier.heightIn(min = 44.dp),
                ) {
                    Text(t["screen.home.full_readings"], color = HomeGold, fontFamily = FontFamily.Serif,
                        fontSize = 15.sp, lineHeight = 20.sp)
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = HomeGold,
                        modifier = Modifier.padding(start = 8.dp).size(17.dp))
                }
                if (openFailed) Text(t["screen.home.readings_error"], color = HomeCream,
                    fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

internal fun fullReadingsUrl(date: LocalDate): String {
    val key = "${(date.month.ordinal + 1).toString().padStart(2, '0')}${date.day.toString().padStart(2, '0')}${(date.year % 100).toString().padStart(2, '0')}"
    return "https://bible.usccb.org/bible/readings/$key.cfm"
}

@Composable
private fun ReadingReferenceRow(
    reference: ScriptureReference,
    language: String,
    onClick: (() -> Unit)?,
) {
    val s = LocalStrings.current
    val interactionModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(interactionModifier)
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = when (reference.kind) {
                ReadingKind.FIRST_READING -> s.homeFirstReading
                ReadingKind.SECOND_READING -> s.homeSecondReading
                ReadingKind.GOSPEL -> s.homeGospel
            }.uppercase(),
            modifier = Modifier.widthIn(min = 108.dp),
            color = HomeGoldSoft,
            fontSize = 9.sp,
            letterSpacing = 0.65.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = fullBookNameCitation(reference.citation, language),
            modifier = Modifier.weight(1f),
            color = HomeCream,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
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
private fun HomeSectionHeader(title: String, date: String? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = HomeCream, fontFamily = FontFamily.Serif, fontSize = 21.sp,
            lineHeight = 26.sp, modifier = Modifier.weight(1f))
        date?.let {
            Text(it, color = HomeGold, fontFamily = FontFamily.Serif, fontSize = 13.sp,
                lineHeight = 18.sp, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Composable
private fun CompactHomeItem(item: ContentSummary, onClick: () -> Unit, outlined: Boolean = true) {
    val s = LocalStrings.current
    val shape = RoundedCornerShape(16.dp)
    val portraitShape = if (outlined) RoundedCornerShape(12.dp) else CircleShape
    val pictureSize = if (outlined) 60.dp else 48.dp
    val portrait = Portraits.fullForEntity(item.type, item.id)
    val decoration = if (outlined) Modifier.clip(shape).background(HomeSurface.copy(alpha = 0.55f))
        .border(1.dp, HomeGold.copy(alpha = 0.5f), shape) else Modifier
    Row(Modifier.fillMaxWidth().then(decoration).semantics(mergeDescendants = true) {}
        .clickable(role = Role.Button, onClick = onClick).padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(pictureSize).clip(portraitShape).background(HomeSurface)
            .border(1.dp, HomeGold.copy(alpha = 0.55f), portraitShape), contentAlignment = Alignment.Center) {
            if (portrait != null) Image(painterResource(portrait), null, Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop, alignment = Alignment.TopCenter)
            else Icon(Icons.Filled.AutoStories, null, tint = HomeGold, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, color = HomeCream, fontFamily = FontFamily.Serif, fontSize = 19.sp,
                lineHeight = 24.sp, fontWeight = FontWeight.Medium)
            Text(if (outlined) item.summary else item.type.singularLabel(s), color = HomeMuted,
                fontSize = 12.sp, lineHeight = 17.sp, maxLines = if (outlined) 2 else 1,
                overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = HomeGold,
            modifier = Modifier.padding(start = 8.dp).size(20.dp))
    }
}

@Composable
private fun HomePrayerShortcut(title: String, rosary: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val gold = HomeGold
    Row(modifier.heightIn(min = 56.dp).clip(RoundedCornerShape(12.dp))
        .border(1.dp, gold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        .semantics(mergeDescendants = true) {}.clickable(role = Role.Button, onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(30.dp)) {
            val stroke = 1.5.dp.toPx()
            if (rosary) {
                val center = Offset(size.width * 0.5f, size.height * 0.36f)
                val radius = size.width * 0.28f
                for (index in 0 until 12) {
                    val angle = index * 2.0 * PI / 12
                    drawCircle(gold, radius = size.width * 0.047f,
                        center = Offset(center.x + cos(angle).toFloat() * radius,
                            center.y + sin(angle).toFloat() * radius))
                }
                drawLine(gold, Offset(center.x, size.height * 0.66f), Offset(center.x, size.height * 0.96f), stroke)
                drawLine(gold, Offset(size.width * 0.39f, size.height * 0.82f), Offset(size.width * 0.61f, size.height * 0.82f), stroke)
            } else {
                drawArc(gold, 180f, 180f, useCenter = true, topLeft = Offset(size.width * 0.28f, size.height * 0.36f),
                    size = Size(size.width * 0.44f, size.height * 0.44f))
                drawLine(gold, Offset(size.width * 0.08f, size.height * 0.60f), Offset(size.width * 0.92f, size.height * 0.60f), stroke)
                drawLine(gold, Offset(size.width * 0.20f, size.height * 0.77f), Offset(size.width * 0.80f, size.height * 0.77f), stroke)
                for (degrees in listOf(210, 240, 270, 300, 330)) {
                    val angle = degrees * PI / 180
                    drawLine(gold,
                        Offset(size.width * 0.5f + cos(angle).toFloat() * size.width * 0.32f,
                            size.height * 0.58f + sin(angle).toFloat() * size.height * 0.32f),
                        Offset(size.width * 0.5f + cos(angle).toFloat() * size.width * 0.44f,
                            size.height * 0.58f + sin(angle).toFloat() * size.height * 0.44f), stroke)
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(title, color = HomeCream, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = gold,
            modifier = Modifier.padding(start = 4.dp).size(16.dp))
    }
}
