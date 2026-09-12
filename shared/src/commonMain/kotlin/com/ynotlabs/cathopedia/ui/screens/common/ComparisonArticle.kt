package com.ynotlabs.cathopedia.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.ynotlabs.cathopedia.content.model.ListBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter


/** One row of the comparison: the same question answered for each side. */
private data class ComparisonRow(
    val label: String,
    val left: String,
    val right: String,
    val leftIcon: String?,
    val rightIcon: String?,
)

/**
 * The shared layout behind Cathopedia's side-by-side comparison articles.
 *
 * The article is read positionally rather than by block type: the first two `image` blocks are
 * the two portraits, the two `factGrid` blocks are the two columns (left side first) sharing one
 * set of labels, and the two `list` blocks are each side's key points. A fact's optional `icon`
 * is an asset path drawn beside that cell. Captions read "Name — what they are"; the part before
 * the dash names the column.
 */
@Composable
fun ComparisonArticle(
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
                    is ListBlock -> addAll(block.itemKeys)
                    is ImageBlock -> block.captionKey?.let(::add)
                    is FactGridBlock -> block.facts.forEach {
                        add(it.labelKey)
                        add(it.valueKey)
                    }
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
    val portraits = blocks.filterIsInstance<ImageBlock>()
    val grids = blocks.filterIsInstance<FactGridBlock>()
    val lists = blocks.filterIsInstance<ListBlock>()
    val heading = blocks.filterIsInstance<HeadingBlock>().firstOrNull()?.let { strings[it.textKey] }.orEmpty()
    val callouts = blocks.filterIsInstance<CalloutBlock>()

    val leftFacts = grids.getOrNull(0)?.facts.orEmpty()
    val rightFacts = grids.getOrNull(1)?.facts.orEmpty()
    val rows = leftFacts.mapIndexed { index, fact ->
        val other = rightFacts.getOrNull(index)
        ComparisonRow(
            label = strings[fact.labelKey].orEmpty(),
            left = strings[fact.valueKey].orEmpty(),
            right = other?.valueKey?.let(strings::get).orEmpty(),
            leftIcon = fact.icon,
            rightIcon = other?.icon,
        )
    }

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 16.dp,
    ) {
        if (portraits.size >= 2) {
            item {
                PortraitFaceOff(
                    left = portraits[0],
                    right = portraits[1],
                    strings = strings,
                )
                Spacer(Modifier.height(20.dp))
            }
        }

        if (intro.isNotBlank()) {
            item {
                ArticleIntroCard(intro)
                Spacer(Modifier.height(16.dp))
                SacredDivider()
                Spacer(Modifier.height(18.dp))
            }
        }

        items(rows.size) { index ->
            ComparisonRowBlock(rows[index])
            Spacer(Modifier.height(16.dp))
        }

        if (lists.size >= 2) {
            if (heading.isNotBlank()) {
                item {
                    Spacer(Modifier.height(4.dp))
                    ArticleSectionLabel(heading)
                }
            }
            item {
                KeyPointsCard(
                    side = strings[portraits.getOrNull(0)?.captionKey]?.sideName().orEmpty(),
                    points = lists[0].itemKeys.map { strings[it].orEmpty() },
                )
                Spacer(Modifier.height(12.dp))
                KeyPointsCard(
                    side = strings[portraits.getOrNull(1)?.captionKey]?.sideName().orEmpty(),
                    points = lists[1].itemKeys.map { strings[it].orEmpty() },
                )
                Spacer(Modifier.height(20.dp))
            }
        }

        items(callouts.size) { index ->
            val callout = callouts[index]
            ArticleCalloutCard(
                title = callout.titleKey?.let(strings::get),
                body = strings[callout.textKey].orEmpty(),
                emphasised = callout.tone == CalloutTone.devotional,
            )
            Spacer(Modifier.height(12.dp))
        }
    
    }
}

/** Captions read "Patriarch — chief bishop of…"; the name is the part before the dash. */
private fun String.sideName(): String = substringBefore(" — ", this).trim()

@Composable
private fun PortraitFaceOff(
    left: ImageBlock,
    right: ImageBlock,
    strings: Map<String, String>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
    ) {
        PortraitCard(
            image = left,
            strings = strings,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(46.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = ArticleSurface,
                border = BorderStroke(1.5.dp, ArticleGold),
            ) {
                Text(
                    text = "VS",
                    color = ArticleGold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.CenterVertically),
                )
            }
        }
        PortraitCard(
            image = right,
            strings = strings,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun PortraitCard(
    image: ImageBlock,
    strings: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    val caption = image.captionKey?.let(strings::get).orEmpty()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Only reserve the portrait's space once the drawable is actually bundled,
            // so a not-yet-generated portrait reads as a name card, not a blank frame.
            hubAssetPainter(image.asset)?.let { painter ->
                Image(
                    painter = painter,
                    contentDescription = caption,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text = caption.sideName().uppercase(),
                color = ArticleGold,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            val role = caption.substringAfter(" — ", "")
            if (role.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = role,
                    color = ArticleMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ComparisonRowBlock(row: ComparisonRow) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ArticleSectionLabel(row.label)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ComparisonCell(
                text = row.left,
                icon = row.leftIcon,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
            ComparisonCell(
                text = row.right,
                icon = row.rightIcon,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun ComparisonCell(
    text: String,
    icon: String?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.28f)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
        ) {
            val symbolPainter = hubAssetPainter(icon)
            if (symbolPainter != null) {
                RightWrappedSymbolText(
                    text = text,
                    symbolPainter = symbolPainter,
                )
            } else {
                Text(
                    text = text,
                    color = ArticleCream.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
            }
        }
    }
}

/** Places a comparison symbol at top-right and continues overflow copy below it. */
@Composable
private fun RightWrappedSymbolText(
    text: String,
    symbolPainter: Painter,
) {
    var width by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { width = it.size.width }
    ) {
        var splitIndex by remember(text, width) { mutableIntStateOf(text.length) }
        val sideText = text.substring(0, splitIndex).trimEnd()
        val remainingText = text.substring(splitIndex).trimStart()

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = sideText,
                    color = ArticleCream.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    maxLines = 3,
                    onTextLayout = { result ->
                        if (splitIndex == text.length && result.didOverflowHeight && result.lineCount > 0) {
                            val visibleEnd = result.getLineEnd(result.lineCount - 1, visibleEnd = true)
                            if (visibleEnd in 1 until text.length) splitIndex = visibleEnd
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = symbolPainter,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(56.dp),
                )
            }
            if (remainingText.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = remainingText,
                    color = ArticleCream.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun KeyPointsCard(side: String, points: List<String>) {
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
                modifier = Modifier.padding(start = 22.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            ) {
                if (side.isNotBlank()) {
                    Text(
                        text = side,
                        color = ArticleGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(10.dp))
                }
                points.forEach { point ->
                    Row(modifier = Modifier.padding(bottom = 7.dp)) {
                        Text(
                            text = "•",
                            color = ArticleGoldSoft,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = point,
                            color = ArticleCream.copy(alpha = 0.88f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }
        }
    }
}

