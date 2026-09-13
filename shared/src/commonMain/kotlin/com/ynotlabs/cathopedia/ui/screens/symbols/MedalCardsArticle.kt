package com.ynotlabs.cathopedia.ui.screens.symbols

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.CalloutTone
import com.ynotlabs.cathopedia.content.model.FactGridBlock
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
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurfaceRaised

/** One medal: both faces, what is struck on each, what it means, and its key words. */
private data class Medal(
    val title: String,
    val frontAsset: String,
    val backAsset: String,
    val frontLabel: String,
    val frontText: String,
    val backLabel: String,
    val backText: String,
    val meaning: String,
    val words: String,
    val wordsSource: String,
)

/**
 * "Catholic Medals and Their Meanings" — Sacred Symbols → Catholic Medals.
 *
 * A medal is a two-sided object, so each one gets a single card carrying both
 * faces side by side with what is struck on each written underneath, then the
 * meaning and the medal's key words. Block order is the structure: a `heading`
 * opens a medal, the two `image` blocks after it are its front and back, and the
 * `paragraph`, `factGrid` and `quote` that follow belong to that same card.
 * Callouts stand alone between cards.
 *
 * A face whose art has not been drawn yet renders as an empty frame rather than
 * collapsing the column, so the card keeps its shape and the gap is obvious.
 */
@Composable
fun MedalCardsArticle(
    articleId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(articleId, language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(articleId, language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(articleId, language) {
        val loaded = repository.hubArticle(articleId) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is HeadingBlock -> add(block.textKey)
                    is ParagraphBlock -> add(block.textKey)
                    is QuoteBlock -> {
                        add(block.textKey)
                        block.attributionKey?.let(::add)
                    }
                    is CalloutBlock -> {
                        block.titleKey?.let(::add)
                        add(block.textKey)
                    }
                    is FactGridBlock -> block.facts.forEach {
                        add(it.labelKey)
                        add(it.valueKey)
                    }
                    is ImageBlock -> block.captionKey?.let(::add)
                    else -> Unit
                }
            }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    val blocks = current?.blocks.orEmpty()

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 20.dp,
    ) {
        var index = 0
        while (index < blocks.size) {
            val block = blocks[index]

            if (block is HeadingBlock) {
                // Everything up to the next heading or callout belongs to this medal.
                var end = index + 1
                while (end < blocks.size && blocks[end] !is HeadingBlock && blocks[end] !is CalloutBlock) {
                    end += 1
                }
                val own = blocks.subList(index + 1, end)
                val images = own.filterIsInstance<ImageBlock>()
                val facts = own.filterIsInstance<FactGridBlock>().firstOrNull()?.facts.orEmpty()
                val quote = own.filterIsInstance<QuoteBlock>().firstOrNull()

                val medal = Medal(
                    title = strings[block.textKey].orEmpty(),
                    frontAsset = images.getOrNull(0)?.asset.orEmpty(),
                    backAsset = images.getOrNull(1)?.asset.orEmpty(),
                    frontLabel = facts.getOrNull(0)?.labelKey?.let(strings::get).orEmpty(),
                    frontText = facts.getOrNull(0)?.valueKey?.let(strings::get).orEmpty(),
                    backLabel = facts.getOrNull(1)?.labelKey?.let(strings::get).orEmpty(),
                    backText = facts.getOrNull(1)?.valueKey?.let(strings::get).orEmpty(),
                    meaning = own.filterIsInstance<ParagraphBlock>().firstOrNull()
                        ?.let { strings[it.textKey] }.orEmpty(),
                    words = quote?.let { strings[it.textKey] }.orEmpty(),
                    wordsSource = quote?.attributionKey?.let(strings::get).orEmpty(),
                )

                item(key = "medal-$index") {
                    MedalCard(medal)
                    Spacer(Modifier.height(14.dp))
                }
                index = end
                continue
            }

            if (block is CalloutBlock) {
                item(key = "callout-$index") {
                    if (block.tone == CalloutTone.warning) {
                        Spacer(Modifier.height(6.dp))
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
            }
            index += 1
        }
    }
}

@Composable
private fun MedalCard(medal: Medal) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.TopStart), height = 54.dp)
            Column(modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp)) {
                Text(
                    text = medal.title,
                    color = ArticleCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    MedalFace(
                        asset = medal.frontAsset,
                        label = medal.frontLabel,
                        text = medal.frontText,
                        modifier = Modifier.weight(1f),
                    )
                    MedalFace(
                        asset = medal.backAsset,
                        label = medal.backLabel,
                        text = medal.backText,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (medal.meaning.isNotBlank()) {
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = medal.meaning,
                        color = ArticleCream.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )
                }

                if (medal.words.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = ArticleSurfaceRaised,
                        contentColor = ArticleCream,
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = medal.words,
                                color = ArticleCream,
                                fontFamily = FontFamily.Serif,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                            )
                            if (medal.wordsSource.isNotBlank()) {
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    text = medal.wordsSource,
                                    color = ArticleMuted,
                                    fontSize = 11.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** One face of a medal: the art, which side it is, and what is struck on it. */
@Composable
private fun MedalFace(
    asset: String,
    label: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val painter = hubAssetPainter(asset)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(ArticleSurfaceRaised),
            contentAlignment = Alignment.Center,
        ) {
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = text.ifBlank { label },
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                )
            }
        }

        if (label.isNotBlank()) {
            Spacer(Modifier.height(9.dp))
            Text(
                text = label.uppercase(),
                color = ArticleGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
            )
        }
        if (text.isNotBlank()) {
            Spacer(Modifier.height(5.dp))
            Text(
                text = text,
                color = ArticleCream.copy(alpha = 0.82f),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
