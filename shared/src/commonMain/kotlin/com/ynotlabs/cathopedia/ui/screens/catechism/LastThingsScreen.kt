package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter

private const val LAST_THINGS_ARTICLE_ID = "art.cat.last_things"

private val LastBackground = Color(0xFF061A13)
private val LastSurface = Color(0xFF0A241B)
private val LastSurfaceRaised = Color(0xFF0F2E22)
private val LastHeader = Color(0xFF081F17)
private val LastGold = Color(0xFFD6AE3D)
private val LastGoldSoft = Color(0xFFB08D57)
private val LastCream = Color(0xFFF4ECDD)
private val LastMuted = Color(0xFFB7B09D)

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

    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LastBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeight + 20.dp,
                end = 20.dp,
                bottom = 120.dp,
            ),
        ) {
            if (intro.isNotBlank()) {
                item {
                    LastIntroCard(intro)
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
                        accent = StateAccents.getOrElse(position) { LastGold },
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
                        SequenceLabel(strings[block.textKey].orEmpty())
                    }

                    is ListBlock -> item(key = "sequence-$index") {
                        SequenceCard(block.itemKeys.map { strings[it].orEmpty() })
                        Spacer(Modifier.height(16.dp))
                    }

                    is CalloutBlock -> item(key = "callout-$index") {
                        LastCalloutCard(
                            title = block.titleKey?.let(strings::get),
                            body = strings[block.textKey].orEmpty(),
                            devotional = block.tone == CalloutTone.devotional,
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    else -> Unit
                }
                index += 1
            }
        }

        LastHeaderPanel(
            title = current?.let { strings[it.titleKey] }.orEmpty(),
            subtitle = current?.leadKey?.let(strings::get).orEmpty(),
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun LastThingCard(thing: LastThing) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LastSurface,
        contentColor = LastCream,
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
                Row(verticalAlignment = Alignment.Top) {
                    hubAssetPainter(thing.asset)?.let { painter ->
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(LastSurfaceRaised),
                        )
                        Spacer(Modifier.width(14.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = thing.title,
                            color = thing.accent,
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = thing.body,
                            color = LastCream.copy(alpha = 0.88f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }

                if (thing.reference.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = thing.reference,
                        color = LastGoldSoft,
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
        }
    }
}

/** Earth, death, judgement and what follows — as one thread running down the page. */
@Composable
private fun SequenceCard(steps: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LastSurface,
        contentColor = LastCream,
        border = BorderStroke(1.dp, LastGold.copy(alpha = 0.35f)),
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
                                        color = LastGold.copy(alpha = 0.3f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, dotY - gap),
                                        strokeWidth = 1.5.dp.toPx(),
                                    )
                                }
                                if (!isLast) {
                                    drawLine(
                                        color = LastGold.copy(alpha = 0.3f),
                                        start = Offset(x, dotY + gap),
                                        end = Offset(x, size.height),
                                        strokeWidth = 1.5.dp.toPx(),
                                    )
                                }
                                drawCircle(
                                    color = LastGold,
                                    radius = 3.5.dp.toPx(),
                                    center = Offset(x, dotY),
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = step,
                            color = LastCream.copy(alpha = 0.9f),
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

@Composable
private fun SequenceLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = LastGold,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun LastIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LastSurface,
        contentColor = LastCream,
        border = BorderStroke(1.dp, LastGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Text(
                text = text,
                color = LastCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun LastCalloutCard(title: String?, body: String, devotional: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = if (devotional) LastSurfaceRaised else LastSurface,
        contentColor = LastCream,
        border = BorderStroke(1.dp, LastGold.copy(alpha = if (devotional) 0.65f else 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            ) {
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        color = LastGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 19.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Text(
                    text = body,
                    color = LastCream.copy(alpha = 0.88f),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }
        }
    }
}

@Composable
private fun LastHeaderPanel(
    title: String,
    subtitle: String,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var titleLineCount by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(LastHeader)
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
                    color = LastCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                    onTextLayout = { titleLineCount = it.lineCount },
                )
                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(if (titleLineCount <= 1) 4.dp else 10.dp))
                    Text(
                        text = subtitle.uppercase(),
                        color = LastGold,
                        fontSize = 10.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.3.sp,
                    )
                }
            }
        }
    }
}
