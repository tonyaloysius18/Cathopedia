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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import com.ynotlabs.cathopedia.model.HubArticleSummary
import com.ynotlabs.cathopedia.model.HubFactSheetDetail
import com.ynotlabs.cathopedia.model.HubSectionSummary
import com.ynotlabs.cathopedia.resources.*
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val FruitsBackground = Color(0xFF061A13)
private val FruitsHeader = Color(0xFF081F17)
private val FruitsSurface = Color(0xFF0A241B)
private val FruitsSurfaceRaised = Color(0xFF0C271E)
private val FruitsGold = Color(0xFFD6AE3D)
private val FruitsCream = Color(0xFFF4ECDD)
private val FruitsMuted = Color(0xFFB7B09D)

@Composable
fun FruitsOfHolySpiritScreen(
    hubId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onArticleSelected: (HubArticleSummary) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var section by remember(language) { mutableStateOf<HubSectionSummary?>(null) }
    var sheet by remember(language) { mutableStateOf<HubFactSheetDetail?>(null) }
    var articles by remember(language) { mutableStateOf<List<HubArticleSummary>>(emptyList()) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(hubId, language) {
        val loadedSection = repository.hubDetail(hubId)
            ?.sections
            ?.firstOrNull { it.id == "cat.fruits" }
            ?: return@LaunchedEffect
        val loadedSheet = loadedSection.factSheetId
            ?.let { repository.hubFactSheet(it) }
            ?: return@LaunchedEffect
        val loadedArticles = repository.hubArticlesForSection(loadedSection.id)
            .sortedBy { it.sortOrder }
        val keys = buildSet {
            add(loadedSection.titleKey)
            loadedSection.summaryKey?.let(::add)
            add(loadedSheet.titleKey)
            loadedSheet.facts.forEach { fact ->
                add(fact.labelKey)
                add(fact.valueKey)
            }
            loadedArticles.forEach { article ->
                add(article.titleKey)
                article.leadKey?.let(::add)
            }
        }

        section = loadedSection
        sheet = loadedSheet
        articles = loadedArticles
        strings = repository.resolveHubStrings(keys, language)
    }

    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }
    val facts = sheet?.facts.orEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FruitsBackground),
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                FruitsHeroCard()
            }

            section?.summaryKey?.let(strings::get)?.let { summary ->
                item {
                    FruitsIntroCard(summary)
                }
            }

            item {
                SacredDivider()
                Spacer(Modifier.height(4.dp))
            }

            if (facts.isNotEmpty()) {
                itemsIndexed(facts, key = { _, fact -> fact.id }) { index, fact ->
                    FruitCard(
                        number = index + 1,
                        title = strings[fact.labelKey].orEmpty(),
                        body = strings[fact.valueKey].orEmpty(),
                    )
                }
            }

            if (articles.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    FruitsArticleLabel("${articles.size} ${if (language == "fr") "Articles" else "Articles"}")
                }
                itemsIndexed(articles, key = { _, article -> article.id }) { _, article ->
                    FruitsArticleCard(
                        article = article,
                        title = strings[article.titleKey].orEmpty(),
                        lead = article.leadKey?.let(strings::get),
                        onClick = { onArticleSelected(article) },
                    )
                }
            }
        }

        FruitsHeaderPanel(
            title = section?.let { strings[it.titleKey] }.orEmpty(),
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun FruitsHeroCard() {
    Image(
        painter = painterResource(Res.drawable.fruits_holy_spirit_hero),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, FruitsGold.copy(alpha = 0.35f), RoundedCornerShape(22.dp)),
    )
}

@Composable
private fun FruitsIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = FruitsSurface,
        contentColor = FruitsCream,
        border = BorderStroke(1.dp, FruitsGold.copy(alpha = 0.42f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))
            Text(
                text = text,
                color = FruitsCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun FruitCard(number: Int, title: String, body: String) {
    val icon = fruitNumberIcon(number)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp),
        shape = RoundedCornerShape(20.dp),
        color = FruitsSurfaceRaised,
        contentColor = FruitsCream,
        border = BorderStroke(1.dp, FruitsGold.copy(alpha = 0.22f)),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .border(1.dp, FruitsGold, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = number.toString().padStart(2, '0'),
                        color = FruitsGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = FruitsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = body,
                    color = FruitsMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

private fun fruitNumberIcon(number: Int): DrawableResource? = when (number) {
    1 -> Res.drawable._01
    2 -> Res.drawable._02
    3 -> Res.drawable._03
    4 -> Res.drawable._04
    5 -> Res.drawable._05
    6 -> Res.drawable._06
    7 -> Res.drawable._07
    8 -> Res.drawable._08
    9 -> Res.drawable._09
    10 -> Res.drawable._10
    11 -> Res.drawable._11
    12 -> Res.drawable._12
    else -> null
}

@Composable
private fun FruitsArticleLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text.uppercase(),
            color = FruitsGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.4.sp,
        )
        Spacer(
            Modifier
                .padding(start = 10.dp)
                .height(1.dp)
                .weight(1f)
                .background(FruitsGold.copy(alpha = 0.35f)),
        )
    }
}

@Composable
private fun FruitsArticleCard(
    article: HubArticleSummary,
    title: String,
    lead: String?,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = FruitsSurface,
        contentColor = FruitsCream,
        border = BorderStroke(1.dp, FruitsGold.copy(alpha = 0.45f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    article.readingTimeMinutes?.let { minutes ->
                        Text(
                            text = "ARTICLE · $minutes MIN",
                            color = FruitsGold.copy(alpha = 0.78f),
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(5.dp))
                    }
                    Text(
                        text = title,
                        color = FruitsCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    if (!lead.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = lead,
                            color = FruitsMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = FruitsGold,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun FruitsHeaderPanel(
    title: String,
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
            .background(FruitsHeader)
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
                    color = FruitsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
