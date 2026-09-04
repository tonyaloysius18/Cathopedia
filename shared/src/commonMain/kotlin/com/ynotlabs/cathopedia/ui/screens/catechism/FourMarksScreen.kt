package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.EntityCardsBlock
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.ListBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider

private val MarksBackground = Color(0xFF061A13)
private val MarksSurface = Color(0xFF0A241B)
private val MarksHeader = Color(0xFF081F17)
private val MarksGold = Color(0xFFD6AE3D)
private val MarksCream = Color(0xFFF4ECDD)
private val MarksMuted = Color(0xFFB7B09D)

@Composable
fun FourMarksScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    CatechismCardArticleScreen(
        articleId = "art.cat.four_marks",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
    )
}

@Composable
fun FourLastThingsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    CatechismCardArticleScreen(
        articleId = "art.cat.last_things",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
    )
}

@Composable
fun ConfessionPrayersScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onEntityRefSelected: (EntityRef) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    CatechismCardArticleScreen(
        articleId = "art.cat.confession_prayers",
        repository = repository,
        language = language,
        onBack = onBack,
        onEntityRefSelected = onEntityRefSelected,
        listState = listState,
    )
}

@Composable
private fun CatechismCardArticleScreen(
    articleId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onEntityRefSelected: (EntityRef) -> Unit = {},
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
                    is ParagraphBlock -> add(block.textKey)
                    is ListBlock -> addAll(block.itemKeys)
                    is CalloutBlock -> {
                        block.titleKey?.let(::add)
                        add(block.textKey)
                    }
                    is EntityCardsBlock -> {
                        block.titleKey?.let(::add)
                        block.refs.mapNotNullTo(this) { it.labelKey }
                    }
                    else -> Unit
                }
            }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    val intro = current?.blocks
        ?.filterIsInstance<ParagraphBlock>()
        ?.firstOrNull()
        ?.let { strings[it.textKey] }
        .orEmpty()
    val marks = current?.blocks
        ?.filterIsInstance<ListBlock>()
        ?.flatMap { it.itemKeys }
        ?.mapNotNull(strings::get)
        .orEmpty()
    val callouts = current?.blocks
        ?.filterIsInstance<CalloutBlock>()
        ?.map { block ->
            block.titleKey?.let(strings::get) to strings[block.textKey].orEmpty()
        }
        .orEmpty()
    val entityCards = current?.blocks
        ?.filterIsInstance<EntityCardsBlock>()
        .orEmpty()

    var headerHeightPx by remember(articleId, language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MarksBackground),
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
                    MarksIntroCard(intro)
                    Spacer(Modifier.height(16.dp))
                    SacredDivider()
                    Spacer(Modifier.height(18.dp))
                }
            }

            items(marks) { mark ->
                MarkCard(mark)
                Spacer(Modifier.height(12.dp))
            }

            items(callouts) { (title, body) ->
                CatechismCalloutCard(
                    title = title,
                    body = body,
                )
                Spacer(Modifier.height(12.dp))
            }


            entityCards.forEach { block ->
                block.titleKey?.let(strings::get)?.let { title ->
                    item {
                        Text(
                            text = title.uppercase(),
                            color = MarksGold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                }

                items(block.refs) { ref ->
                    PrayerReferenceCard(
                        label = ref.labelKey?.let(strings::get) ?: ref.id,
                        onClick = { onEntityRefSelected(ref) },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        MarksHeaderPanel(
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
private fun PrayerReferenceCard(
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MarksSurface,
        contentColor = MarksCream,
        border = BorderStroke(1.dp, MarksGold.copy(alpha = 0.35f)),
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    color = MarksCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MarksGold.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CatechismCalloutCard(
    title: String?,
    body: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MarksSurface,
        contentColor = MarksCream,
        border = BorderStroke(1.dp, MarksGold.copy(alpha = 0.45f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            ) {
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        color = MarksGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 19.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Text(
                    text = body,
                    color = MarksCream.copy(alpha = 0.88f),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }
        }
    }
}

@Composable
private fun MarksIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MarksSurface,
        contentColor = MarksCream,
        border = BorderStroke(1.dp, MarksGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Text(
                text = text,
                color = MarksCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun MarkCard(text: String) {
    val separator = " — "
    val title = text.substringBefore(separator, text)
    val body = text.substringAfter(separator, "")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MarksSurface,
        contentColor = MarksCream,
        border = BorderStroke(1.dp, MarksGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            ) {
                Text(
                    text = title,
                    color = MarksGold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 19.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                if (body.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = body,
                        color = MarksCream.copy(alpha = 0.88f),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun MarksHeaderPanel(
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
            .background(MarksHeader)
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
                    color = MarksCream,
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
                        color = MarksGold,
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
