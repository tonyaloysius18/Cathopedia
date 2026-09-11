package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources._01
import com.ynotlabs.cathopedia.resources._02
import com.ynotlabs.cathopedia.resources._03
import com.ynotlabs.cathopedia.resources._04
import com.ynotlabs.cathopedia.resources.cat_four_marks_apostolic
import com.ynotlabs.cathopedia.resources.cat_four_marks_catholic
import com.ynotlabs.cathopedia.resources.cat_four_marks_holy
import com.ynotlabs.cathopedia.resources.cat_four_marks_one
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val MarksBackground = Color(0xFF061A13)
private val MarksSurface = Color(0xFF0A241B)
private val MarksHeader = Color(0xFF081F17)
private val MarksGold = Color(0xFFD6AE3D)
private val MarksCream = Color(0xFFF4ECDD)
private val MarksMuted = Color(0xFFB7B09D)

private const val FOUR_MARKS_ARTICLE_ID = "art.cat.four_marks"

/** The four marks, in creed order, as the pillar illustration draws them top to bottom. */
private data class Mark(
    val ordinal: Int,
    val name: String,
    val body: String,
    val reference: String,
)

/**
 * The Four Marks of the Church, presented as four paired illustration and copy cards.
 * Each generated square illustration is fitted without cropping or stretching.
 */
@Composable
fun FourMarksScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(FOUR_MARKS_ARTICLE_ID) ?: return@LaunchedEffect
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

    // The article carries two lists: the marks themselves, then their scripture refs.
    val lists = current?.blocks?.filterIsInstance<ListBlock>().orEmpty()
    val markItems = lists.getOrNull(0)?.itemKeys?.mapNotNull(strings::get).orEmpty()
    val references = lists.getOrNull(1)?.itemKeys?.map { strings[it].orEmpty() }.orEmpty()
    val marks = markItems.mapIndexed { index, item ->
        val separator = " — "
        Mark(
            ordinal = index + 1,
            name = item.substringBefore(separator, item),
            body = item.substringAfter(separator, ""),
            reference = references.getOrNull(index).orEmpty(),
        )
    }

    val callouts = current?.blocks
        ?.filterIsInstance<CalloutBlock>()
        ?.map { block -> block.titleKey?.let(strings::get) to strings[block.textKey].orEmpty() }
        .orEmpty()

    var selected by remember(language) { mutableIntStateOf(-1) }
    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
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
                start = 16.dp,
                top = headerHeight + 20.dp,
                end = 16.dp,
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

            if (marks.isNotEmpty()) {
                item {
                    MarksPillar(
                        marks = marks,
                        selected = selected,
                        contentDescription = strings[current?.titleKey].orEmpty(),
                        onSelect = { index -> selected = if (selected == index) -1 else index },
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }

            items(callouts) { (title, body) ->
                CatechismCalloutCard(
                    title = title,
                    body = body,
                )
                Spacer(Modifier.height(12.dp))
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
private fun MarksPillar(
    marks: List<Mark>,
    selected: Int,
    contentDescription: String,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        marks.forEachIndexed { index, mark ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MarkIllustrationCard(
                    illustration = markIllustration(mark.ordinal),
                    contentDescription = "$contentDescription: ${mark.name}",
                    selected = index == selected,
                    onClick = { onSelect(index) },
                    modifier = Modifier
                        .width(116.dp)
                        .fillMaxHeight(),
                )
                MarkTierCard(
                    mark = mark,
                    selected = index == selected,
                    onClick = { onSelect(index) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun MarkIllustrationCard(
    illustration: DrawableResource,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MarksSurface.copy(alpha = 0.55f),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = MarksGold.copy(alpha = if (selected) 0.75f else 0.18f),
        ),
    ) {
        Box(
            modifier = Modifier.padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(illustration),
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(104.dp),
            )
        }
    }
}

private fun markIllustration(ordinal: Int): DrawableResource = when (ordinal) {
    1 -> Res.drawable.cat_four_marks_one
    2 -> Res.drawable.cat_four_marks_holy
    3 -> Res.drawable.cat_four_marks_catholic
    else -> Res.drawable.cat_four_marks_apostolic
}

private fun markNumber(ordinal: Int): DrawableResource = when (ordinal) {
    1 -> Res.drawable._01
    2 -> Res.drawable._02
    3 -> Res.drawable._03
    else -> Res.drawable._04
}

@Composable
private fun MarkTierCard(
    mark: Mark,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MarksSurface,
        contentColor = MarksCream,
        border = BorderStroke(1.dp, MarksGold.copy(alpha = if (selected) 0.85f else 0.35f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(markNumber(mark.ordinal)),
                        contentDescription = mark.ordinal.toString(),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = mark.name.uppercase(),
                        color = MarksGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (mark.body.isNotBlank()) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = mark.body,
                        color = MarksCream.copy(alpha = 0.88f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                    )
                }
                if (mark.reference.isNotBlank()) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = mark.reference,
                        color = MarksMuted,
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
        }
    }
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
