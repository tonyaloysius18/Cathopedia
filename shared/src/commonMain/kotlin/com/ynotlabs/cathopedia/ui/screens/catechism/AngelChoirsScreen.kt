package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter

private const val ANGELS_ARTICLE_ID = "art.cat.angels"

private val ChoirBackground = Color(0xFF061A13)
private val ChoirSurface = Color(0xFF0A241B)
private val ChoirSurfaceRaised = Color(0xFF0F2E22)
private val ChoirHeader = Color(0xFF081F17)
private val ChoirGold = Color(0xFFD6AE3D)
private val ChoirGoldSoft = Color(0xFFB08D57)
private val ChoirCream = Color(0xFFF4ECDD)
private val ChoirMuted = Color(0xFFB7B09D)

private data class Choir(
    val number: Int,
    val asset: String,
    val title: String,
    val body: String,
    val reference: String,
)

/**
 * "The Nine Choirs of Angels" — Catechism → What We Believe.
 *
 * Nine choirs in three hierarchies, separated by `heading` blocks; block order
 * carries the structure as in the other illustrated articles, where an `image`
 * followed by a `callout` is one choir and the image's caption is its scripture
 * reference.
 *
 * The closing `warning` callout is the important one and is styled to stand
 * apart: the ninefold ranking is a theological tradition, not defined dogma, and
 * the page must not leave a reader thinking otherwise.
 */
@Composable
fun AngelChoirsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(ANGELS_ARTICLE_ID) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is HeadingBlock -> add(block.textKey)
                    is ParagraphBlock -> add(block.textKey)
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
            .background(ChoirBackground),
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
                    ChoirIntroCard(intro)
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
                    number += 1
                    val choir = Choir(
                        number = number,
                        asset = block.asset,
                        title = next.titleKey?.let(strings::get).orEmpty(),
                        body = strings[next.textKey].orEmpty(),
                        reference = block.captionKey?.let(strings::get).orEmpty(),
                    )
                    item(key = "choir-$number") {
                        ChoirCard(choir)
                        Spacer(Modifier.height(12.dp))
                    }
                    index += 2
                    continue
                }

                when (block) {
                    is HeadingBlock -> item(key = "heading-$index") {
                        Spacer(Modifier.height(8.dp))
                        HierarchyLabel(strings[block.textKey].orEmpty())
                    }

                    is CalloutBlock -> item(key = "callout-$index") {
                        if (block.tone == CalloutTone.warning) {
                            Spacer(Modifier.height(8.dp))
                            SacredDivider()
                            Spacer(Modifier.height(18.dp))
                        }
                        ChoirCalloutCard(
                            title = block.titleKey?.let(strings::get),
                            body = strings[block.textKey].orEmpty(),
                            emphasised = block.tone == CalloutTone.warning,
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    is QuoteBlock -> item(key = "quote-$index") {
                        ChoirQuoteCard(
                            text = strings[block.textKey].orEmpty(),
                            attribution = block.attributionKey?.let(strings::get).orEmpty(),
                        )
                    }

                    else -> Unit
                }
                index += 1
            }
        }

        ChoirHeaderPanel(
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
private fun ChoirCard(choir: Choir) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ChoirSurface,
        contentColor = ChoirCream,
        border = BorderStroke(1.dp, ChoirGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Row(
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ChoirSurfaceRaised),
                    contentAlignment = Alignment.Center,
                ) {
                    hubAssetPainter(choir.asset)?.let { painter ->
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } ?: Text(
                        text = choir.number.toString(),
                        color = ChoirGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = choir.number.toString().padStart(2, '0'),
                            color = ChoirGoldSoft,
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = choir.title,
                            color = ChoirGold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 17.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = choir.body,
                        color = ChoirCream.copy(alpha = 0.88f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                    )
                    if (choir.reference.isNotBlank()) {
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = choir.reference,
                            color = ChoirGoldSoft,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontStyle = FontStyle.Italic,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HierarchyLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = ChoirGold,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp),
    )
}

@Composable
private fun ChoirIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ChoirSurface,
        contentColor = ChoirCream,
        border = BorderStroke(1.dp, ChoirGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Text(
                text = text,
                color = ChoirCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun ChoirCalloutCard(title: String?, body: String, emphasised: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = if (emphasised) ChoirSurfaceRaised else ChoirSurface,
        contentColor = ChoirCream,
        border = BorderStroke(if (emphasised) 1.5.dp else 1.dp, ChoirGold.copy(alpha = if (emphasised) 0.75f else 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            ) {
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        color = ChoirGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 19.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Text(
                    text = body,
                    color = ChoirCream.copy(alpha = 0.88f),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }
        }
    }
}

@Composable
private fun ChoirQuoteCard(text: String, attribution: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SacredDivider()
        Spacer(Modifier.height(16.dp))
        Text(
            text = text,
            color = ChoirCream,
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 25.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
        )
        if (attribution.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = attribution.uppercase(),
                color = ChoirGold,
                fontSize = 10.sp,
                letterSpacing = 1.3.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ChoirHeaderPanel(
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
            .background(ChoirHeader)
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
                    color = ChoirCream,
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
                        color = ChoirGold,
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
