package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.HeadingBlock
import com.ynotlabs.cathopedia.content.model.ImageBlock
import com.ynotlabs.cathopedia.content.model.ListBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCalloutCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGoldSoft
import com.ynotlabs.cathopedia.ui.screens.common.ArticleIntroCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSectionLabel
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurfaceRaised
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.theme.LiturgicalAdventLent
import com.ynotlabs.cathopedia.ui.theme.LiturgicalChristmasEaster
import com.ynotlabs.cathopedia.ui.theme.LiturgicalGaudeteLaetare
import com.ynotlabs.cathopedia.ui.theme.LiturgicalOrdinary
import com.ynotlabs.cathopedia.ui.theme.LiturgicalPentecostMartyrs

private const val LITURGICAL_YEAR_ARTICLE_ID = "art.mass.liturgical_year"


/** Vestment white, which the accent palette has no token for. */
private val VestmentWhite = Color(0xFFF6F1E4)

/**
 * The vestment colours of each season, in the article's own order. The seasons are
 * a closed set fixed by the General Roman Calendar rather than editable content, so
 * they live here; five of the six swatches reuse the app's liturgical accent tokens.
 */
private val SeasonSwatches: List<List<Color>> = listOf(
    listOf(LiturgicalAdventLent, LiturgicalGaudeteLaetare),      // Advent — purple, rose
    listOf(VestmentWhite, LiturgicalChristmasEaster),            // Christmas — white, gold
    listOf(LiturgicalOrdinary),                                  // Ordinary Time — green
    listOf(LiturgicalAdventLent),                                // Lent — purple
    listOf(LiturgicalPentecostMartyrs, VestmentWhite),           // Triduum — red, white
    listOf(VestmentWhite, LiturgicalChristmasEaster),            // Easter — white, gold
)

private data class Season(
    val number: Int,
    val asset: String,
    val title: String,
    val body: String,
    val colourLabel: String,
    val swatches: List<Color>,
)

/**
 * "The Liturgical Year" — The Holy Mass → The Liturgical Year.
 *
 * Six seasons, then the cycle they run in. Block order carries the structure, as in
 * the other illustrated articles: an `image` followed by a `callout` is one season,
 * and the image's caption is that season's vestment colours in words — the coloured
 * swatches beside it come from [SeasonSwatches], keyed by position.
 *
 * The app already computes the current season in `LiturgicalCalendar` for the Home
 * hero and the theme accent; this screen is the explanation behind that label.
 */
@Composable
fun LiturgicalYearScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(LITURGICAL_YEAR_ARTICLE_ID) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is HeadingBlock -> add(block.textKey)
                    is ParagraphBlock -> add(block.textKey)
                    is ListBlock -> addAll(block.itemKeys)
                    is ImageBlock -> block.captionKey?.let(::add)
                    is CalloutBlock -> {
                        block.titleKey?.let(::add)
                        add(block.textKey)
                    }
                    else -> Unit
                }
            }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    val blocks = current?.blocks.orEmpty()
    val intro = blocks.filterIsInstance<ParagraphBlock>().firstOrNull()?.let { strings[it.textKey] }.orEmpty()

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 20.dp,
    ) {
        if (intro.isNotBlank()) {
            item {
                ArticleIntroCard(intro)
                Spacer(Modifier.height(16.dp))
                SacredDivider()
                Spacer(Modifier.height(18.dp))
            }
        }

        var number = 0
        var index = 0
        while (index < blocks.size) {
            val block = blocks[index]
            val next = blocks.getOrNull(index + 1)

            if (block is ImageBlock && next is CalloutBlock) {
                val position = number
                number += 1
                val season = Season(
                    number = number,
                    asset = block.asset,
                    title = next.titleKey?.let(strings::get).orEmpty(),
                    body = strings[next.textKey].orEmpty(),
                    colourLabel = block.captionKey?.let(strings::get).orEmpty(),
                    swatches = SeasonSwatches.getOrElse(position) { emptyList() },
                )
                item(key = "season-$number") {
                    SeasonCard(season)
                    Spacer(Modifier.height(12.dp))
                }
                index += 2
                continue
            }

            when (block) {
                is HeadingBlock -> item(key = "heading-$index") {
                    Spacer(Modifier.height(10.dp))
                    SacredDivider()
                    Spacer(Modifier.height(18.dp))
                    ArticleSectionLabel(strings[block.textKey].orEmpty())
                }

                is ListBlock -> item(key = "order-$index") {
                    YearCycleCard(block.itemKeys.map { strings[it].orEmpty() })
                    Spacer(Modifier.height(16.dp))
                }

                is CalloutBlock -> item(key = "callout-$index") {
                    ArticleCalloutCard(
                        title = block.titleKey?.let(strings::get),
                        body = strings[block.textKey].orEmpty(),
                    )
                    Spacer(Modifier.height(12.dp))
                }

                else -> Unit
            }
            index += 1
        }
    
    }
}

@Composable
private fun SeasonCard(season: Season) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ArticleSurfaceRaised),
                        contentAlignment = Alignment.Center,
                    ) {
                        hubAssetPainter(season.asset)?.let { painter ->
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(42.dp),
                            )
                        } ?: Text(
                            text = season.number.toString(),
                            color = ArticleGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = season.number.toString().padStart(2, '0'),
                                color = ArticleGoldSoft,
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = season.title,
                                color = ArticleGold,
                                fontFamily = FontFamily.Serif,
                                fontSize = 17.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = season.body,
                            color = ArticleCream.copy(alpha = 0.88f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }

                if (season.colourLabel.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    LiturgicalColourRow(
                        label = season.colourLabel,
                        swatches = season.swatches,
                    )
                }
            }
        }
    }
}

/** The season's vestment colours, as dots beside their names. */
@Composable
private fun LiturgicalColourRow(label: String, swatches: List<Color>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        swatches.forEach { colour ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(colour)
                    .border(1.dp, ArticleGold.copy(alpha = 0.5f), CircleShape),
            )
            Spacer(Modifier.width(6.dp))
        }
        if (swatches.isNotEmpty()) Spacer(Modifier.width(2.dp))
        Text(
            text = label.uppercase(),
            color = ArticleMuted,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/** The year as a loop: each season, then the arrow back round to Advent. */
@Composable
private fun YearCycleCard(steps: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            ) {
                steps.forEachIndexed { index, step ->
                    val isLast = index == steps.lastIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val x = size.width / 2f
                                val dotY = 9.dp.toPx()
                                if (index > 0) {
                                    drawLine(
                                        color = ArticleGold.copy(alpha = 0.35f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, dotY - 5.dp.toPx()),
                                        strokeWidth = 1.5.dp.toPx(),
                                    )
                                }
                                if (!isLast) {
                                    drawLine(
                                        color = ArticleGold.copy(alpha = 0.35f),
                                        start = Offset(x, dotY + 5.dp.toPx()),
                                        end = Offset(x, size.height),
                                        strokeWidth = 1.5.dp.toPx(),
                                    )
                                }
                                drawCircle(
                                    color = if (isLast) ArticleGoldSoft else ArticleGold,
                                    radius = 3.5.dp.toPx(),
                                    center = Offset(x, dotY),
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = step,
                            color = if (isLast) ArticleMuted else ArticleCream,
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = if (isLast) 0.dp else 12.dp),
                        )
                    }
                }
            }
        }
    }
}

