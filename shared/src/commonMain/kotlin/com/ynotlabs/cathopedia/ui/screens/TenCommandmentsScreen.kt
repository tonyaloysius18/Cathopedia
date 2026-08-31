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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.Fact
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleSummary
import com.ynotlabs.cathopedia.model.HubFactSheetDetail
import com.ynotlabs.cathopedia.model.HubSectionSummary
import com.ynotlabs.cathopedia.resources.*
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val CommandmentsBackground = Color(0xFF061A13)
private val CommandmentsHeader = Color(0xFF081F17)
private val CommandmentsSurface = Color(0xFF0A241B)
private val CommandmentsSurfaceRaised = Color(0xFF0C271E)
private val CommandmentsGold = Color(0xFFD6AE3D)
private val CommandmentsCream = Color(0xFFF4ECDD)
private val CommandmentsMuted = Color(0xFFB7B09D)

private data class CommandmentsCopy(
    val subtitle: String,
    val heroCaption: String,
    val loveOfGod: String,
    val loveOfNeighbour: String,
    val articleLabel: String,
    val titles: List<String>,
)

private fun commandmentsCopy(language: String) = if (language == "fr") {
    CommandmentsCopy(
        subtitle = "La loi de l'amour",
        heroCaption = "Une alliance gravée dans la pierre",
        loveOfGod = "Amour de Dieu · 1–3",
        loveOfNeighbour = "Amour du prochain · 4–10",
        articleLabel = "Articles",
        titles = listOf(
            "Pas d'autres dieux",
            "Honorer le nom de Dieu",
            "Sanctifier le jour du Seigneur",
            "Honorer père et mère",
            "Respecter la vie humaine",
            "Fidélité dans le mariage",
            "Ne pas voler",
            "Dire la vérité",
            "Pureté du cœur",
            "Liberté face à la convoitise",
        ),
    )
} else {
    CommandmentsCopy(
        subtitle = "The law of love",
        heroCaption = "A covenant written in stone",
        loveOfGod = "Love of God · 1–3",
        loveOfNeighbour = "Love of neighbour · 4–10",
        articleLabel = "Articles",
        titles = listOf(
            "No other gods",
            "Honour God's name",
            "Keep the Lord's Day holy",
            "Honour father and mother",
            "Respect human life",
            "Faithfulness in marriage",
            "Do not steal",
            "Speak the truth",
            "Purity of heart",
            "Freedom from greed",
        ),
    )
}

@Composable
fun TenCommandmentsScreen(
    hubId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onArticleSelected: (HubArticleSummary) -> Unit,
) {
    val s = LocalStrings.current
    val copy = remember(language) { commandmentsCopy(language) }
    var section by remember(language) { mutableStateOf<HubSectionSummary?>(null) }
    var sheet by remember(language) { mutableStateOf<HubFactSheetDetail?>(null) }
    var articles by remember(language) { mutableStateOf<List<HubArticleSummary>>(emptyList()) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(hubId, language) {
        val loadedSection = repository.hubDetail(hubId)
            ?.sections
            ?.firstOrNull { it.id == "cat.commandments" }
            ?: return@LaunchedEffect
        val loadedSheet = loadedSection.factSheetId
            ?.let { repository.hubFactSheet(it) }
            ?: return@LaunchedEffect
        val loadedArticles = repository.hubArticlesForSection(loadedSection.id)
            .sortedBy { it.sortOrder }

        val keys = buildSet {
            add(loadedSection.titleKey)
            loadedSection.summaryKey?.let(::add)
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
            .background(CommandmentsBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeight + 20.dp,
                end = 20.dp,
                bottom = 120.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                CommandmentsHeroCard(copy.heroCaption)
            }

            section?.summaryKey?.let(strings::get)?.let { summary ->
                item {
                    CommandmentsIntroCard(summary)
                    Spacer(Modifier.height(4.dp))
                }
            }

            if (facts.isNotEmpty()) {
                item { CommandmentsSectionLabel(copy.loveOfGod) }
                itemsIndexed(facts.take(3), key = { _, fact -> fact.id }) { index, fact ->
                    CommandmentCard(
                        number = index + 1,
                        title = copy.titles.getOrElse(index) { strings[fact.labelKey].orEmpty() },
                        body = strings[fact.valueKey].orEmpty(),
                    )
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    CommandmentsSectionLabel(copy.loveOfNeighbour)
                }
                itemsIndexed(facts.drop(3), key = { _, fact -> fact.id }) { index, fact ->
                    val absoluteIndex = index + 3
                    CommandmentCard(
                        number = absoluteIndex + 1,
                        title = copy.titles.getOrElse(absoluteIndex) { strings[fact.labelKey].orEmpty() },
                        body = strings[fact.valueKey].orEmpty(),
                    )
                }
            }

            if (articles.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    CommandmentsArticleLabel(
                        text = "${articles.size} ${copy.articleLabel}",
                    )
                }
                itemsIndexed(articles, key = { _, article -> article.id }) { _, article ->
                    CommandmentsArticleCard(
                        article = article,
                        title = strings[article.titleKey].orEmpty(),
                        lead = article.leadKey?.let(strings::get),
                        onClick = { onArticleSelected(article) },
                    )
                }
            }
        }

        CommandmentsHeaderPanel(
            title = section?.let { strings[it.titleKey] }.orEmpty(),
            subtitle = copy.subtitle,
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun CommandmentsHeroCard(caption: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.72f)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, CommandmentsGold.copy(alpha = 0.38f), RoundedCornerShape(22.dp)),
    ) {
        Image(
            painter = painterResource(Res.drawable.ten_commandments_hero),
            contentDescription = caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.56f to Color.Transparent,
                        1f to CommandmentsBackground.copy(alpha = 0.94f),
                    ),
                ),
        )
        Text(
            text = caption,
            color = CommandmentsCream,
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp),
        )
    }
}

@Composable
private fun CommandmentsIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CommandmentsSurface,
        contentColor = CommandmentsCream,
        border = BorderStroke(1.dp, CommandmentsGold.copy(alpha = 0.42f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))
            Text(
                text = text,
                color = CommandmentsCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun CommandmentsSectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = CommandmentsGold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
    )
}

@Composable
private fun CommandmentCard(
    number: Int,
    title: String,
    body: String,
) {
    val icon = commandmentNumberIcon(number)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp),
        shape = RoundedCornerShape(20.dp),
        color = CommandmentsSurfaceRaised,
        contentColor = CommandmentsCream,
        border = BorderStroke(1.dp, CommandmentsGold.copy(alpha = 0.22f)),
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
                        .border(1.dp, CommandmentsGold, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = number.toString().padStart(2, '0'),
                        color = CommandmentsGold,
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
                    color = CommandmentsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = body,
                    color = CommandmentsMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

private fun commandmentNumberIcon(number: Int): DrawableResource? = when (number) {
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
    else -> null
}

@Composable
private fun CommandmentsArticleLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text.uppercase(),
            color = CommandmentsGold,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.4.sp,
        )
        Spacer(
            Modifier
                .padding(start = 10.dp)
                .height(1.dp)
                .weight(1f)
                .background(CommandmentsGold.copy(alpha = 0.35f)),
        )
    }
}

@Composable
private fun CommandmentsArticleCard(
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
        color = CommandmentsSurface,
        contentColor = CommandmentsCream,
        border = BorderStroke(1.dp, CommandmentsGold.copy(alpha = 0.45f)),
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
                            color = CommandmentsGold.copy(alpha = 0.78f),
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(5.dp))
                    }
                    Text(
                        text = title,
                        color = CommandmentsCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    if (!lead.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = lead,
                            color = CommandmentsMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = CommandmentsGold,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun CommandmentsHeaderPanel(
    title: String,
    subtitle: String,
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
            .background(CommandmentsHeader)
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
                    color = CommandmentsCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle.uppercase(),
                    color = CommandmentsGold,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.4.sp,
                )
            }
        }
    }
}
