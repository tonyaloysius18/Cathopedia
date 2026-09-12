package com.ynotlabs.cathopedia.ui.screens.priesthood

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.content.model.QuoteBlock
import com.ynotlabs.cathopedia.content.model.Step
import com.ynotlabs.cathopedia.content.model.StepperRefBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.model.HubStepperDetail
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter
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

private const val FORMATION_ARTICLE_ID = "art.priesthood.formation"

/**
 * "How a Man Becomes a Priest" — The Priesthood → the formation path.
 *
 * The two routes are the same length and largely parallel, so showing them one under the other
 * would make a very long page and a poor comparison. Instead the article's two `stepperRef`
 * blocks become the two sides of a toggle — the first is the diocesan path, the second the
 * religious one — and only the chosen path is drawn, as a numbered timeline. Everything after
 * the steppers (the callouts and the closing quote) belongs to both and stays put below.
 */
@Composable
fun PriestFormationScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var steppers by remember(language) { mutableStateOf<List<HubStepperDetail>>(emptyList()) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showReligious by remember { mutableStateOf(false) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(FORMATION_ARTICLE_ID) ?: return@LaunchedEffect
        val paths = loaded.blocks.filterIsInstance<StepperRefBlock>()
            .mapNotNull { repository.hubStepper(it.stepperId) }
        article = loaded
        steppers = paths
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is ParagraphBlock -> add(block.textKey)
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
            paths.forEach { path ->
                add(path.titleKey)
                path.introKey?.let(::add)
                path.steps.forEach {
                    add(it.titleKey)
                    add(it.bodyKey)
                }
            }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    val blocks = current?.blocks.orEmpty()
    val intro = blocks.filterIsInstance<ParagraphBlock>().firstOrNull()?.let { strings[it.textKey] }.orEmpty()
    val callouts = blocks.filterIsInstance<CalloutBlock>()
    val quote = blocks.filterIsInstance<QuoteBlock>().firstOrNull()
    val path = steppers.getOrNull(if (showReligious) 1 else 0)

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
                Spacer(Modifier.height(18.dp))
            }
        }

        if (steppers.size >= 2) {
            item(key = "toggle") {
                PathToggle(
                    left = strings[steppers[0].titleKey].orEmpty(),
                    right = strings[steppers[1].titleKey].orEmpty(),
                    rightSelected = showReligious,
                    onSelect = { showReligious = it },
                )
                Spacer(Modifier.height(6.dp))
            }
        }

        path?.introKey?.let(strings::get)?.takeIf { it.isNotBlank() }?.let { line ->
            item(key = "path-intro-${path.id}") {
                Text(
                    text = line,
                    color = ArticleGoldSoft,
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp),
                )
            }
        }

        if (path != null) {
            itemsIndexed(path.steps) { index, step ->
                StepRow(
                    step = step,
                    number = index + 1,
                    title = strings[step.titleKey].orEmpty(),
                    body = strings[step.bodyKey].orEmpty(),
                    isLast = index == path.steps.lastIndex,
                )
            }
        }

        if (callouts.isNotEmpty()) {
            item(key = "after-steps") {
                Spacer(Modifier.height(10.dp))
                SacredDivider()
                Spacer(Modifier.height(18.dp))
            }
        }

        items(callouts.size) { index ->
            val callout = callouts[index]
            ArticleCalloutCard(
                title = callout.titleKey?.let(strings::get),
                body = strings[callout.textKey].orEmpty(),
            )
            Spacer(Modifier.height(12.dp))
        }

        quote?.let {
            item(key = "quote") {
                Spacer(Modifier.height(6.dp))
                ArticleQuoteCard(
                    text = strings[it.textKey].orEmpty(),
                    attribution = it.attributionKey?.let(strings::get).orEmpty(),
                )
            }
        }
    }
}

/** Diocesan | Religious. Two halves of one pill; the chosen side is filled gold. */
@Composable
private fun PathToggle(
    left: String,
    right: String,
    rightSelected: Boolean,
    onSelect: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = ArticleSurface,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.35f)),
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            PathToggleHalf(left, selected = !rightSelected, modifier = Modifier.weight(1f)) { onSelect(false) }
            Spacer(Modifier.width(5.dp))
            PathToggleHalf(right, selected = rightSelected, modifier = Modifier.weight(1f)) { onSelect(true) }
        }
    }
}

@Composable
private fun PathToggleHalf(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val background by animateColorAsState(
        if (selected) ArticleGold.copy(alpha = 0.18f) else ArticleSurface,
        label = "pathToggleBackground",
    )
    val content by animateColorAsState(
        if (selected) ArticleGold else ArticleMuted,
        label = "pathToggleContent",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = content,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

/**
 * One stage: a numbered medallion in a thread running down the page, with the stage's card beside
 * it. The thread must be [Modifier.fillMaxHeight] inside a `Row` of `IntrinsicSize.Min`, or it
 * only wraps the medallion and renders as a stub.
 */
@Composable
private fun StepRow(
    step: Step,
    number: Int,
    title: String,
    body: String,
    isLast: Boolean,
) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier.width(46.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ArticleSurfaceRaised),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = number.toString().padStart(2, '0'),
                    color = ArticleGold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (!isLast) {
                Canvas(modifier = Modifier.width(2.dp).fillMaxHeight()) {
                    drawLine(
                        color = ArticleGold.copy(alpha = 0.35f),
                        start = Offset(size.width / 2f, 0f),
                        end = Offset(size.width / 2f, size.height),
                        strokeWidth = size.width,
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Surface(
            modifier = Modifier.weight(1f).padding(bottom = 12.dp),
            shape = RoundedCornerShape(18.dp),
            color = ArticleSurface,
            contentColor = ArticleCream,
            border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.3f)),
        ) {
            Box {
                GoldCardAccent(Modifier.align(Alignment.CenterStart))
                Column(modifier = Modifier.padding(start = 18.dp, top = 14.dp, end = 14.dp, bottom = 14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        hubAssetPainter(step.asset)?.let { painter ->
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(30.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                        }
                        Text(
                            text = title,
                            color = ArticleGold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = body,
                        color = ArticleCream.copy(alpha = 0.88f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                    )
                }
            }
        }
    }
}
