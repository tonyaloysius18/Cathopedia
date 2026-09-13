package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.HeadingBlock
import com.ynotlabs.cathopedia.content.model.ListBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGoldSoft
import com.ynotlabs.cathopedia.ui.screens.common.ArticleIntroCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurfaceRaised
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider

private const val PAPAL_TOMBS_ARTICLE_ID = "art.papal_tombs.overview"

private val PapalTombsExtraKeys = setOf(
    "art.papal_tombs.hero.alt",
    "art.papal_tombs.expand",
    "art.papal_tombs.collapse",
    "art.papal_tombs.grottoes.summary",
    "art.papal_tombs.outside.summary",
)

private data class TombSection(
    val title: String,
    val summary: String,
    val note: String,
    val entries: List<String>,
)

/**
 * Papal Tombs — Holy See.
 *
 * The long burial lists use progressive disclosure so the article remains easy
 * to scan on a phone. Both headers are full-width, accessible touch targets and
 * their chevrons rotate to make the open state visible without relying on colour.
 */
@Composable
fun PapalTombsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val commonStrings = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(PAPAL_TOMBS_ARTICLE_ID) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            addAll(PapalTombsExtraKeys)
            loaded.blocks.forEach { block ->
                when (block) {
                    is HeadingBlock -> add(block.textKey)
                    is ParagraphBlock -> add(block.textKey)
                    is ListBlock -> addAll(block.itemKeys)
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
    val paragraphs = blocks.filterIsInstance<ParagraphBlock>()
    val headings = blocks.filterIsInstance<HeadingBlock>()
    val notes = blocks.filterIsInstance<CalloutBlock>()
    val lists = blocks.filterIsInstance<ListBlock>()
    val intro = paragraphs.firstOrNull()?.let { strings[it.textKey] }.orEmpty()
    val conclusion = paragraphs.drop(1).lastOrNull()?.let { strings[it.textKey] }.orEmpty()
    val sections = headings.mapIndexedNotNull { index, heading ->
        val list = lists.getOrNull(index) ?: return@mapIndexedNotNull null
        TombSection(
            title = strings[heading.textKey].orEmpty(),
            summary = strings[
                if (index == 0) "art.papal_tombs.grottoes.summary"
                else "art.papal_tombs.outside.summary"
            ].orEmpty(),
            note = notes.getOrNull(index)?.let { strings[it.textKey] }.orEmpty(),
            entries = list.itemKeys.mapNotNull(strings::get),
        )
    }

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = commonStrings.back,
        onBack = onBack,
        listState = listState,
    ) {
        current?.heroAsset?.let { asset ->
            item(key = "papal-tombs-hero") {
                PapalTombsHero(
                    asset = asset,
                    contentDescription = strings["art.papal_tombs.hero.alt"],
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        if (intro.isNotBlank()) {
            item(key = "papal-tombs-intro") {
                ArticleIntroCard(intro)
                Spacer(Modifier.height(18.dp))
                SacredDivider()
                Spacer(Modifier.height(18.dp))
            }
        }

        sections.forEachIndexed { index, section ->
            item(key = "papal-tombs-section-$index") {
                PapalTombsAccordion(
                    section = section,
                    expandDescription = strings["art.papal_tombs.expand"].orEmpty(),
                    collapseDescription = strings["art.papal_tombs.collapse"].orEmpty(),
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        if (conclusion.isNotBlank()) {
            item(key = "papal-tombs-conclusion") {
                PapalTombsConclusion(conclusion)
            }
        }
    }
}

@Composable
private fun PapalTombsHero(asset: String, contentDescription: String?) {
    hubAssetPainter(asset)?.let { painter ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .clip(RoundedCornerShape(22.dp)),
        ) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.58f to Color.Transparent,
                            1f to Color(0xFF061A13).copy(alpha = 0.62f),
                        ),
                    ),
            )
        }
    }
}

@Composable
private fun PapalTombsAccordion(
    section: TombSection,
    expandDescription: String,
    collapseDescription: String,
) {
    var expanded by remember { mutableStateOf(false) }
    val actionDescription = if (expanded) collapseDescription else expandDescription
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "papal-tombs-chevron",
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = if (expanded) 0.72f else 0.38f)),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = actionDescription,
                        onClick = { expanded = !expanded },
                    )
                    .semantics { stateDescription = actionDescription }
                    .padding(start = 22.dp, top = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = section.title,
                        color = ArticleCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = section.summary,
                        color = ArticleMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
                Spacer(Modifier.size(12.dp))
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = ArticleSurfaceRaised,
                    border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.48f)),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = ArticleGold,
                            modifier = Modifier
                                .size(26.dp)
                                .rotate(chevronRotation),
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            ) {
                Column {
                    HorizontalDivider(color = ArticleGold.copy(alpha = 0.24f))
                    if (section.note.isNotBlank()) {
                        Text(
                            text = section.note,
                            color = ArticleGoldSoft,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.padding(start = 22.dp, top = 16.dp, end = 20.dp, bottom = 8.dp),
                        )
                    }
                    Column(modifier = Modifier.padding(start = 22.dp, end = 20.dp, bottom = 16.dp)) {
                        section.entries.forEachIndexed { index, entry ->
                            TombEntryRow(entry)
                            if (index != section.entries.lastIndex) {
                                HorizontalDivider(
                                    color = ArticleGold.copy(alpha = 0.12f),
                                    modifier = Modifier.padding(start = 18.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TombEntryRow(entry: String) {
    val primary = entry.substringBefore(" — ")
    val secondary = entry.substringAfter(" — ", missingDelimiterValue = "")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .background(ArticleGold, CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = primary,
                color = ArticleCream,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (secondary.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = secondary,
                    color = ArticleMuted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
            }
        }
    }
}

@Composable
private fun PapalTombsConclusion(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Text(
                text = text,
                color = ArticleCream.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            )
        }
    }
}
