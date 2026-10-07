package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.CalloutTone
import com.ynotlabs.cathopedia.content.model.HeadingBlock
import com.ynotlabs.cathopedia.content.model.ImageBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.content.model.QuoteBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCalloutCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleIntroCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleQuoteCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSectionLabel
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import com.ynotlabs.cathopedia.ui.theme.CardBorder

/** One chalice: a level-3 heading, its image and its description. */
private data class Chalice(val name: String, val asset: String, val description: String)

/** How far a shelf row reaches past the page margin on each side. */
private val ShelfBleed = 20.dp

/** Lets a child extend [amount] past its parent's padding on both sides. */
private fun Modifier.bleed(amount: Dp): Modifier = layout { measurable, constraints ->
    val extra = (amount * 2).roundToPx()
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.maxWidth + extra,
            maxWidth = constraints.maxWidth + extra,
        ),
    )
    layout(constraints.maxWidth, placeable.height) {
        placeable.place(-extra / 2, 0)
    }
}

/** A shelf of chalices under a level-2 heading ("By use", "By style"). */
private data class ChaliceGroup(val label: String, val chalices: List<Chalice>)

/**
 * "Types of Chalices" — The Holy Mass → Sacred Vessels, second article.
 *
 * The page's point is that four of the eight are uses and four are styles, so the
 * chalices sit on two shelves, one per level-2 heading. Each shelf is a row of
 * large image tiles that scrolls sideways; tapping a tile selects it and its
 * description shows in the card beneath the row. Text before the first shelf is
 * the intro; callouts and the quote after the last shelf close the page.
 *
 * Block convention: intro `paragraph`, then for each shelf a level-2 `heading`
 * followed by (level-3 `heading`, `image`, `paragraph`) triples, then `callout`s
 * and a `quote`.
 */
@Composable
fun ChalicesScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val articleId = "art.mass.chalices"
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(articleId) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is HeadingBlock -> add(block.textKey)
                    is ParagraphBlock -> add(block.textKey)
                    is CalloutBlock -> {
                        block.titleKey?.let(::add)
                        add(block.textKey)
                    }
                    is QuoteBlock -> {
                        add(block.textKey)
                        block.attributionKey?.let(::add)
                    }
                    else -> Unit
                }
            }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    val blocks = current?.blocks.orEmpty()

    // Split the blocks into intro, shelves and closing.
    val firstShelf = blocks.indexOfFirst { it is HeadingBlock && it.level <= 2 }
    val intro = if (firstShelf < 0) emptyList() else blocks.take(firstShelf).filterIsInstance<ParagraphBlock>()
    val groups = mutableListOf<ChaliceGroup>()
    var closingStart = blocks.size
    if (firstShelf >= 0) {
        var i = firstShelf
        while (i < blocks.size) {
            val shelf = blocks[i] as? HeadingBlock
            if (shelf == null || shelf.level > 2) {
                closingStart = i
                break
            }
            val items = mutableListOf<Chalice>()
            var j = i + 1
            while (j + 2 <= blocks.lastIndex) {
                val name = blocks[j] as? HeadingBlock ?: break
                if (name.level <= 2) break
                val image = blocks[j + 1] as? ImageBlock ?: break
                val text = blocks[j + 2] as? ParagraphBlock ?: break
                items += Chalice(
                    name = strings[name.textKey].orEmpty(),
                    asset = image.asset,
                    description = strings[text.textKey].orEmpty(),
                )
                j += 3
            }
            groups += ChaliceGroup(strings[shelf.textKey].orEmpty(), items)
            i = j
            closingStart = j
        }
    }
    val closing = blocks.drop(closingStart)

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 20.dp,
    ) {
        intro.forEachIndexed { index, block ->
            item(key = "intro-$index") {
                ArticleIntroCard(strings[block.textKey].orEmpty())
                Spacer(Modifier.height(22.dp))
            }
        }

        groups.forEachIndexed { index, group ->
            item(key = "shelf-$index") {
                ChaliceShelf(group)
                Spacer(Modifier.height(24.dp))
            }
        }

        closing.forEachIndexed { index, block ->
            item(key = "closing-$index") {
                when (block) {
                    is CalloutBlock -> {
                        if (block.tone == CalloutTone.warning) {
                            SacredDivider()
                            Spacer(Modifier.height(18.dp))
                        }
                        ArticleCalloutCard(
                            title = block.titleKey?.let(strings::get),
                            body = strings[block.textKey].orEmpty(),
                            emphasised = block.tone == CalloutTone.warning,
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                    is QuoteBlock -> ArticleQuoteCard(
                        text = strings[block.textKey].orEmpty(),
                        attribution = block.attributionKey?.let(strings::get).orEmpty(),
                    )
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun ChaliceShelf(group: ChaliceGroup) {
    var selected by remember(group.label) { mutableIntStateOf(0) }

    Column(Modifier.fillMaxWidth()) {
        ArticleSectionLabel("${group.label} · ${group.chalices.size}")

        // The row bleeds past the page's 20dp margin to the screen edges, so tiles
        // slide out of view at the edge of the phone rather than at the margin.
        LazyRow(
            modifier = Modifier.bleed(ShelfBleed),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = ShelfBleed),
        ) {
            itemsIndexed(group.chalices, key = { _, c -> c.asset }) { index, chalice ->
                ChaliceTile(
                    chalice = chalice,
                    isSelected = index == selected,
                    onClick = { selected = index },
                )
            }
        }

        group.chalices.getOrNull(selected)?.let { chalice ->
            Spacer(Modifier.height(14.dp))
            ChaliceDetailCard(chalice)
        }
    }
}

@Composable
private fun ChaliceTile(chalice: Chalice, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = Modifier
            .width(148.dp)
            .semantics { selected = isSelected }
            .clickable(role = Role.Tab, onClick = onClick),
        shape = shape,
        color = ArticleSurface,
        border = if (isSelected) BorderStroke(2.dp, ArticleGold) else BorderStroke(1.dp, CardBorder),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                hubAssetPainter(chalice.asset)?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = chalice.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = chalice.name,
                color = if (isSelected) ArticleGold else ArticleCream,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                // Two lines reserved for every name, so one-line and two-line
                // names make tiles of the same height.
                minLines = 2,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun ChaliceDetailCard(chalice: Chalice) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        border = BorderStroke(1.dp, CardBorder),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(Modifier.padding(start = 22.dp, top = 16.dp, end = 18.dp, bottom = 18.dp)) {
                Text(
                    text = chalice.name,
                    color = ArticleGold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = chalice.description,
                    color = ArticleMuted,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }
        }
    }
}
