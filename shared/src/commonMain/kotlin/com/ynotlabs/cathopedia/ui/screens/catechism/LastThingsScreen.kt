package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.CalloutTone
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
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSectionLabel
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter

private const val LAST_THINGS_ARTICLE_ID = "art.cat.last_things"


/**
 * A quiet accent per state, in the article's own order — earth, purgatory, heaven,
 * hell. The source infographic colour-codes its four columns; these are the same
 * idea held well down in saturation so the page stays reverent rather than lurid.
 */
private val StateAccents = listOf(
    Color(0xFF6E9464), // earth — living green
    Color(0xFFC08A46), // purgatory — refining amber
    Color(0xFF8FB6D6), // heaven — pale sky
    Color(0xFF9E5A50), // hell — banked ember
)

private data class LastThing(
    val accent: Color,
    val asset: String,
    val title: String,
    val body: String,
    val reference: String,
)

/**
 * "The Last Things" — Catechism → What We Believe.
 *
 * Four states (earth, purgatory, heaven, hell), then the sequence that runs
 * through them and what waits at the end of time. Block order carries the
 * structure as in the other illustrated articles: an `image` followed by a
 * `callout` is one state, and the image's caption is its scripture or Catechism
 * reference.
 *
 * This replaced a four-bullet stub of the same doctrine; the traditional four
 * last things are named in the opening paragraph so the term is not lost.
 */
@Composable
fun LastThingsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(LAST_THINGS_ARTICLE_ID) ?: return@LaunchedEffect
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

        var stateIndex = 0
        var index = 0
        while (index < blocks.size) {
            val block = blocks[index]
            val next = blocks.getOrNull(index + 1)

            if (block is ImageBlock && next is CalloutBlock) {
                val position = stateIndex
                stateIndex += 1
                val thing = LastThing(
                    accent = StateAccents.getOrElse(position) { ArticleGold },
                    asset = block.asset,
                    title = next.titleKey?.let(strings::get).orEmpty(),
                    body = strings[next.textKey].orEmpty(),
                    reference = block.captionKey?.let(strings::get).orEmpty(),
                )
                item(key = "state-$position") {
                    LastThingCard(thing)
                    Spacer(Modifier.height(14.dp))
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

                is ListBlock -> item(key = "sequence-$index") {
                    SequenceCard(block.itemKeys.map { strings[it].orEmpty() })
                    Spacer(Modifier.height(16.dp))
                }

                is CalloutBlock -> item(key = "callout-$index") {
                    ArticleCalloutCard(
                        title = block.titleKey?.let(strings::get),
                        body = strings[block.textKey].orEmpty(),
                        emphasised = block.tone == CalloutTone.devotional,
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
private fun LastThingCard(thing: LastThing) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, thing.accent.copy(alpha = 0.45f)),
    ) {
        Box {
            // The card's own accent rail, in place of the shared gold one.
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .align(Alignment.CenterStart),
            ) {
                drawRoundRect(
                    color = thing.accent,
                    topLeft = Offset(0f, size.height * 0.22f),
                    size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.56f),
                )
            }

            Column(
                modifier = Modifier.padding(start = 22.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            ) {
                val painter = hubAssetPainter(thing.asset)
                if (painter != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        )
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            LastThingTitle(thing)
                        }
                    }
                } else {
                    LastThingTitle(thing)
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = thing.body,
                    color = ArticleCream.copy(alpha = 0.88f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (thing.reference.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = thing.reference,
                        color = ArticleGoldSoft,
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
        }
    }
}

@Composable
private fun LastThingTitle(thing: LastThing) {
    Text(
        text = thing.title,
        color = thing.accent,
        fontFamily = FontFamily.Serif,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

/** Earth, death, judgement and what follows — as one thread running down the page. */
@Composable
private fun SequenceCard(steps: List<String>) {
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
                                .width(24.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val x = size.width / 2f
                                val dotY = 9.dp.toPx()
                                val gap = 5.dp.toPx()
                                if (index > 0) {
                                    drawLine(
                                        color = ArticleGold.copy(alpha = 0.3f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, dotY - gap),
                                        strokeWidth = 1.5.dp.toPx(),
                                    )
                                }
                                if (!isLast) {
                                    drawLine(
                                        color = ArticleGold.copy(alpha = 0.3f),
                                        start = Offset(x, dotY + gap),
                                        end = Offset(x, size.height),
                                        strokeWidth = 1.5.dp.toPx(),
                                    )
                                }
                                drawCircle(
                                    color = ArticleGold,
                                    radius = 3.5.dp.toPx(),
                                    center = Offset(x, dotY),
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = step,
                            color = ArticleCream.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
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
