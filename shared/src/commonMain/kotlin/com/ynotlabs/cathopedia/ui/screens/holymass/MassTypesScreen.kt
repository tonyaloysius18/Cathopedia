package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.ynotlabs.cathopedia.ui.screens.common.WrappedImageTitleBody
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter

private const val MASS_TYPES_ARTICLE_ID = "art.mass.types"
// Its own asset, not the procession's mass_priest.png: that one is the sixth
// walking figure of the Entrance Procession stepper and has to stay in profile.
private const val MASS_TYPES_HERO_ASSET = "hub/mass/mass_celebrant_altar.png"


/** One kind of Mass: its number, its symbol, and what distinguishes it. */
private data class MassType(
    val number: Int,
    val asset: String,
    val title: String,
    val body: String,
    val reference: String,
)

/**
 * "Types of Mass" — The Holy Mass → Types of Mass.
 *
 * Seventeen entries, kept in the order of the source and split into five groups
 * by `heading` blocks. As in the Scripture article, block order carries the
 * structure: an `image` immediately followed by a `callout` is one numbered
 * entry, and the image's caption is its Catechism reference (blank where there
 * is none).
 *
 * Entry 16 is Good Friday, which has no Mass at all — it is listed because it
 * belongs in an account of the year's liturgies, and both its title and the
 * intro paragraph say so plainly.
 *
 * Every symbol is an existing Sacred Symbols drawable; only the hero is its own.
 */
@Composable
fun MassTypesScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(MASS_TYPES_ARTICLE_ID) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is HeadingBlock -> add(block.textKey)
                    is ParagraphBlock -> add(block.textKey)
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
        item {
            MassTypesHero(
                intro = intro,
                contentDescription = strings[current?.titleKey].orEmpty(),
            )
        }

        var number = 0
        var index = 0
        while (index < blocks.size) {
            val block = blocks[index]
            val next = blocks.getOrNull(index + 1)

            if (block is ImageBlock && next is CalloutBlock) {
                number += 1
                val type = MassType(
                    number = number,
                    asset = block.asset,
                    title = next.titleKey?.let(strings::get).orEmpty(),
                    body = strings[next.textKey].orEmpty(),
                    reference = block.captionKey?.let(strings::get).orEmpty(),
                )
                item(key = "type-$number") {
                    MassTypeCard(type)
                    Spacer(Modifier.height(12.dp))
                }
                index += 2
                continue
            }

            when (block) {
                is HeadingBlock -> item(key = "heading-$index") {
                    Spacer(Modifier.height(8.dp))
                    ArticleSectionLabel(strings[block.textKey].orEmpty())
                }

                is CalloutBlock -> item(key = "callout-$index") {
                    Spacer(Modifier.height(8.dp))
                    SacredDivider()
                    Spacer(Modifier.height(18.dp))
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
private fun MassTypesHero(intro: String, contentDescription: String) {
    hubAssetPainter(MASS_TYPES_HERO_ASSET)?.let { painter ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Spacer(Modifier.height(16.dp))
    }
    if (intro.isNotBlank()) {
        ArticleIntroCard(intro)
        Spacer(Modifier.height(16.dp))
        SacredDivider()
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun MassTypeCard(type: MassType) {
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
                WrappedImageTitleBody(
                    imageSize = 58.dp,
                    body = type.body,
                    bodyColor = ArticleCream.copy(alpha = 0.88f),
                    bodyFontSize = 13.sp,
                    bodyLineHeight = 20.sp,
                    image = {
                    Box(
                        modifier = Modifier.size(58.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        hubAssetPainter(type.asset)?.let { painter ->
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } ?: Text(
                            text = type.number.toString(),
                            color = ArticleGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    },
                    title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = type.number.toString().padStart(2, '0'),
                            color = ArticleGoldSoft,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = type.title,
                            color = ArticleGold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 17.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    },
                )
                if (type.reference.isNotBlank()) {
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = type.reference,
                        color = ArticleGoldSoft,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
        }
    }
}
