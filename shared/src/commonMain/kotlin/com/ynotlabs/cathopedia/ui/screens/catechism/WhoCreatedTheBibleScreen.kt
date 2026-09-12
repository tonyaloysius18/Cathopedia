package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.Block
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.CalloutTone
import com.ynotlabs.cathopedia.content.model.HeadingBlock
import com.ynotlabs.cathopedia.content.model.ImageBlock
import com.ynotlabs.cathopedia.content.model.ListBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.content.model.QuoteBlock
import com.ynotlabs.cathopedia.content.model.Step
import com.ynotlabs.cathopedia.content.model.StepperRefBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.model.HubStepperDetail
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources._01
import com.ynotlabs.cathopedia.resources._02
import com.ynotlabs.cathopedia.resources._03
import com.ynotlabs.cathopedia.resources._04
import com.ynotlabs.cathopedia.resources._05
import com.ynotlabs.cathopedia.resources._06
import com.ynotlabs.cathopedia.resources._07
import com.ynotlabs.cathopedia.resources._08
import com.ynotlabs.cathopedia.resources._09
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCalloutCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGoldSoft
import com.ynotlabs.cathopedia.ui.screens.common.ArticleIntroCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleQuoteCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurfaceRaised
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private const val BIBLE_ARTICLE_ID = "art.cat.bible_origin"
private const val BIBLE_HERO_ASSET = "hub/catechism/cat_bible_hero.png"


private val JourneyCircleSize = 56.dp
private val JourneyCircleTopPadding = 6.dp
private val BiblePointImageSize = 112.dp

/**
 * "Who Created the Bible?" — Catechism → Sacred Scripture.
 *
 * The article's block order carries the page's structure, so this walks the blocks
 * rather than filtering by type. The one rule worth knowing: an `image` immediately
 * followed by a `callout` is one of the six numbered points, and the image's caption
 * is that point's scripture reference. Every other block renders on its own.
 */
@Composable
fun WhoCreatedTheBibleScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var stepper by remember(language) { mutableStateOf<HubStepperDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(BIBLE_ARTICLE_ID) ?: return@LaunchedEffect
        article = loaded

        val journey = loaded.blocks
            .filterIsInstance<StepperRefBlock>()
            .firstOrNull()
            ?.let { repository.hubStepper(it.stepperId) }
        stepper = journey

        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is HeadingBlock -> add(block.textKey)
                    is ParagraphBlock -> add(block.textKey)
                    is ListBlock -> addAll(block.itemKeys)
                    is ImageBlock -> block.captionKey?.let(::add)
                    is QuoteBlock -> {
                        add(block.textKey)
                        block.attributionKey?.let(::add)
                    }
                    is CalloutBlock -> {
                        block.titleKey?.let(::add)
                        add(block.textKey)
                    }
                    else -> Unit
                }
            }
            journey?.let {
                add(it.titleKey)
                it.introKey?.let(::add)
                it.steps.forEach { step ->
                    add(step.titleKey)
                    add(step.bodyKey)
                }
            }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    val blocks = current?.blocks.orEmpty()
    val introBlock = blocks.firstOrNull() as? ParagraphBlock
    val intro = introBlock?.let { strings[it.textKey] }.orEmpty()

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 20.dp,
    ) {
        item {
            BibleHeroIntro(
                intro = intro,
                contentDescription = strings[current?.titleKey].orEmpty(),
            )
        }

        var pointNumber = 0
        var index = if (introBlock != null) 1 else 0
        while (index < blocks.size) {
            val block = blocks[index]
            val next = blocks.getOrNull(index + 1)

            // An image followed by a callout is one of the six numbered points.
            if (block is ImageBlock && next is CalloutBlock) {
                pointNumber += 1
                val number = pointNumber
                item(key = "point-$number") {
                    BiblePointCard(
                        number = number,
                        asset = block.asset,
                        title = next.titleKey?.let(strings::get).orEmpty(),
                        body = strings[next.textKey].orEmpty(),
                        reference = block.captionKey?.let(strings::get).orEmpty(),
                    )
                    Spacer(Modifier.height(12.dp))
                }
                index += 2
                continue
            }

            renderStandaloneBlock(
                scope = this,
                key = "block-$index",
                block = block,
                strings = strings,
                stepper = stepper,
            )
            index += 1
        }
    
    }
}

private fun renderStandaloneBlock(
    scope: androidx.compose.foundation.lazy.LazyListScope,
    key: String,
    block: Block,
    strings: Map<String, String>,
    stepper: HubStepperDetail?,
) {
    when (block) {
        is ParagraphBlock -> scope.item(key = key) {
            ArticleIntroCard(strings[block.textKey].orEmpty())
            Spacer(Modifier.height(16.dp))
            SacredDivider()
            Spacer(Modifier.height(18.dp))
        }

        is HeadingBlock -> scope.item(key = key) {
            Text(
                text = strings[block.textKey].orEmpty().uppercase(),
                color = ArticleGold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
            )
        }

        is ListBlock -> scope.item(key = key) {
            BibleChecklistCard(block.itemKeys.map { strings[it].orEmpty() })
            Spacer(Modifier.height(12.dp))
        }

        is CalloutBlock -> scope.item(key = key) {
            ArticleCalloutCard(
                title = block.titleKey?.let(strings::get),
                body = strings[block.textKey].orEmpty(),
                emphasised = block.tone == CalloutTone.devotional,
            )
            Spacer(Modifier.height(12.dp))
        }

        is QuoteBlock -> scope.item(key = key) {
            ArticleQuoteCard(
                text = strings[block.textKey].orEmpty(),
                attribution = block.attributionKey?.let(strings::get).orEmpty(),
            )
            Spacer(Modifier.height(12.dp))
        }

        is StepperRefBlock -> {
            val journey = stepper ?: return
            scope.item(key = "$key-title") {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = strings[journey.titleKey].orEmpty().uppercase(),
                    color = ArticleGold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )
                journey.introKey?.let(strings::get)?.takeIf { it.isNotBlank() }?.let { intro ->
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = intro,
                        color = ArticleMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                    )
                }
                Spacer(Modifier.height(14.dp))
            }
            journey.steps.sortedBy { it.order }.forEachIndexed { i, step ->
                scope.item(key = "$key-${step.id}") {
                    BibleJourneyStep(
                        step = step,
                        title = strings[step.titleKey].orEmpty(),
                        body = strings[step.bodyKey].orEmpty(),
                        isFirst = i == 0,
                        isLast = i == journey.steps.lastIndex,
                    )
                }
            }
            scope.item(key = "$key-end") { Spacer(Modifier.height(20.dp)) }
        }

        else -> Unit
    }
}

@Composable
private fun BibleHeroIntro(
    intro: String,
    contentDescription: String,
) {
    val painter = hubAssetPainter(BIBLE_HERO_ASSET)
    if (painter != null && intro.isNotBlank()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = ArticleSurface,
            contentColor = ArticleCream,
            border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.35f)),
        ) {
            Box {
                GoldCardAccent(Modifier.align(Alignment.CenterStart))
                FlowingImageText(
                    painter = painter,
                    imageDescription = contentDescription,
                    text = intro,
                    imageSize = 124.dp,
                    textColor = ArticleCream,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
                )
            }
        }
    } else if (painter != null) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )
    } else if (intro.isNotBlank()) {
        ArticleIntroCard(intro)
    }
    Spacer(Modifier.height(16.dp))
    SacredDivider()
    Spacer(Modifier.height(18.dp))
}

@Composable
private fun BiblePointCard(
    number: Int,
    asset: String,
    title: String,
    body: String,
    reference: String,
) {
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
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 16.dp, bottom = 18.dp),
            ) {
                val pointPainter = hubAssetPainter(asset)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    pointPainter?.let { painter ->
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(BiblePointImageSize)
                                .clip(RoundedCornerShape(16.dp)),
                        )
                        Spacer(Modifier.width(14.dp))
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(numberDrawable(number) ?: Res.drawable._01),
                            contentDescription = number.toString(),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = title,
                            color = ArticleGold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = body,
                    color = ArticleCream.copy(alpha = 0.88f),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
                BiblePointReference(reference)
            }
        }
    }
}

@Composable
private fun BiblePointReference(reference: String) {
    if (reference.isBlank()) return
    Spacer(Modifier.height(8.dp))
    Text(
        text = reference,
        color = ArticleGoldSoft,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        fontStyle = FontStyle.Italic,
    )
}

/** Places artwork first and gives all copy the full card width below it. */
@Composable
private fun FlowingImageText(
    painter: Painter,
    imageDescription: String?,
    text: String,
    imageSize: Dp,
    textColor: Color,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painter,
            contentDescription = imageDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(16.dp)),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            lineHeight = lineHeight,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** One stop on the journey: a picture on the left, joined to the next by a gold thread. */
@Composable
private fun BibleJourneyStep(
    step: Step,
    title: String,
    body: String,
    isFirst: Boolean,
    isLast: Boolean,
) {
    // IntrinsicSize.Min lets the icon column stretch to the height the text needs, so the
    // thread between two stops is drawn as one continuous line rather than a stub.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val x = size.width / 2f
                val circleTop = JourneyCircleTopPadding.toPx()
                val circleBottom = circleTop + JourneyCircleSize.toPx()
                val stroke = 2.dp.toPx()
                if (!isFirst) {
                    drawLine(
                        color = ArticleGold.copy(alpha = 0.35f),
                        start = Offset(x, 0f),
                        end = Offset(x, circleTop),
                        strokeWidth = stroke,
                    )
                }
                if (!isLast) {
                    drawLine(
                        color = ArticleGold.copy(alpha = 0.35f),
                        start = Offset(x, circleBottom),
                        end = Offset(x, size.height),
                        strokeWidth = stroke,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(top = JourneyCircleTopPadding)
                    .size(JourneyCircleSize)
                    .clip(CircleShape)
                    .background(ArticleSurfaceRaised)
                    .border(1.dp, ArticleGold.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                hubAssetPainter(step.asset)?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(44.dp),
                    )
                } ?: numberDrawable(step.order)?.let { numberResource ->
                    Image(
                        painter = painterResource(numberResource),
                        contentDescription = step.order.toString(),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(44.dp),
                    )
                } ?: Text(
                    text = step.order.toString(),
                    color = ArticleGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.width(6.dp))

        Column(modifier = Modifier.weight(1f).padding(top = 10.dp, bottom = 18.dp)) {
            Text(
                text = title,
                color = ArticleCream,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = body,
                color = ArticleMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}

private fun numberDrawable(number: Int): DrawableResource? = when (number) {
    1 -> Res.drawable._01
    2 -> Res.drawable._02
    3 -> Res.drawable._03
    4 -> Res.drawable._04
    5 -> Res.drawable._05
    6 -> Res.drawable._06
    7 -> Res.drawable._07
    8 -> Res.drawable._08
    9 -> Res.drawable._09
    else -> null
}

@Composable
private fun BibleChecklistCard(items: List<String>) {
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
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ArticleGold,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = item,
                            color = ArticleCream,
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                        )
                    }
                }
            }
        }
    }
}
