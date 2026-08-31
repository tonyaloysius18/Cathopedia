package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.CircleShape
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.PolygonShape
import com.ynotlabs.cathopedia.content.model.RectShape
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleSummary
import com.ynotlabs.cathopedia.model.HubDiagramDetail
import com.ynotlabs.cathopedia.model.HubFactSheetDetail
import com.ynotlabs.cathopedia.model.HubHotspotDetail
import com.ynotlabs.cathopedia.model.HubSectionSummary
import com.ynotlabs.cathopedia.model.HubStepperDetail
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.SacramentImages
import com.ynotlabs.cathopedia.ui.components.DiagramCircleShape
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.DiagramHotspot
import com.ynotlabs.cathopedia.ui.components.DiagramPolygonShape
import com.ynotlabs.cathopedia.ui.components.DiagramRectShape
import com.ynotlabs.cathopedia.ui.components.InteractiveDiagram
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.model.HubTimelineDetail
import org.jetbrains.compose.resources.painterResource

private val HubSectionHeader = Color(0xFF081F17)
private val HubSectionCard = Color(0xFF0C271E)
private val HubSectionGold = Color(0xFFD6AE3D)
private val HubSectionCream = Color(0xFFF4ECDD)
private val HubSectionMuted = Color(0xFFB7B09D)

/**
 * Section shell: resolves the section's own header, then dispatches to one renderer per
 * [HubSectionSummary.layout] (docs/briefs/topic-hubs.md, T5). ARTICLES and DIAGRAM are the two
 * complete renderers; STEPPER/TIMELINE/FACT_SHEET render their data plainly; COLLECTION is a
 * stub (it self-populates from the pope/document knowledge graph later, not part of this hub).
 */
@Composable
fun HubSectionScreen(
    hubId: String,
    sectionId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onArticleSelected: (HubArticleSummary) -> Unit,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    if (sectionId == "cat.gifts") {
        GiftsOfHolySpiritScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
            onArticleSelected = onArticleSelected,
        )
        return
    }

    if (sectionId == "cat.fruits") {
        FruitsOfHolySpiritScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
            onArticleSelected = onArticleSelected,
        )
        return
    }

    if (sectionId == "cat.commandments") {
        TenCommandmentsScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
            onArticleSelected = onArticleSelected,
        )
        return
    }

    if (sectionId == "cat.capital_sins") {
        CapitalSinsScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (sectionId == "cat.beatitudes") {
        BeatitudesScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (sectionId == "cat.works_of_mercy") {
        WorksOfMercyScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (sectionId == "cat.precepts") {
        PreceptsScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (sectionId == "cat.virtues") {
        VirtuesScreen(
            hubId = hubId,
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (sectionId == "holy_see.hierarchy") {
        HierarchyCarouselScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    val s = LocalStrings.current
    var section by remember(sectionId) { mutableStateOf<HubSectionSummary?>(null) }
    var strings by remember(sectionId) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(hubId, sectionId, language) {
        val found = repository.hubDetail(hubId)?.sections?.firstOrNull { it.id == sectionId } ?: return@LaunchedEffect
        section = found
        val keys = buildSet {
            add(found.titleKey)
            found.summaryKey?.let(::add)
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = section
    var headerHeightPx by remember(sectionId, language) { mutableIntStateOf(0) }
    val headerHeightDp = with(LocalDensity.current) { headerHeightPx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeightDp + 18.dp,
                end = 20.dp,
                bottom = 120.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (current != null) {
                val summary = strings[current.summaryKey].orEmpty()
                if (summary.isNotBlank()) {
                    item {
                        HubSectionIntroCard(summary)
                    }
                }

                item {
                    SacredDivider()
                }

                when (current.layout) {
                    "ARTICLES" -> articlesSectionBody(sectionId, repository, language, onArticleSelected)
                    "DIAGRAM" -> diagramSectionBody(current.diagramId, repository, language, onEntityRefSelected)
                    "FACT_SHEET" -> factSheetSectionBody(current.factSheetId, repository, language)
                    "STEPPER" -> stepperSectionBody(current.stepperId, repository, language)
                    "TIMELINE" -> timelineSectionBody(current.timelineId, repository, language)
                    else -> collectionSectionBody(s.hubSectionComingSoon)
                }

                // A section's primary layout doesn't have to be the only content it carries —
                // e.g. the Sistine Chapel is DIAGRAM-primary but also has articles on its history.
                // articlesSectionBody already renders nothing when there are none for this section.
                if (current.layout != "ARTICLES") {
                    articlesSectionBody(sectionId, repository, language, onArticleSelected)
                }
            }
        }

        HubSectionHeaderPanel(
            title = current?.let { strings[it.titleKey] }.orEmpty(),
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                val measuredHeight = it.size.height
                if (measuredHeight != headerHeightPx) headerHeightPx = measuredHeight
            },
        )
    }
}

@Composable
private fun HubSectionIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = HubSectionCard,
        contentColor = HubSectionCream,
        border = BorderStroke(1.dp, HubSectionGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Text(
                text = text,
                color = HubSectionCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun HubSectionHeaderPanel(
    title: String,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(HubSectionHeader)
            .statusBarsPadding()
            .padding(start = 18.dp, top = 10.dp, end = 18.dp, bottom = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            CathopediaBackButton(
                onClick = onBack,
                contentDescription = backDescription,
            )

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = HubSectionCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun HubSectionItemCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = HubSectionCard,
        contentColor = HubSectionCream,
        border = BorderStroke(1.dp, HubSectionGold.copy(alpha = 0.35f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Column(
                modifier = Modifier.padding(start = 18.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
                content = content,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.articlesSectionBody(
    sectionId: String,
    repository: CathopediaRepository,
    language: String,
    onArticleSelected: (HubArticleSummary) -> Unit,
) {
    item {
        var articles by remember(sectionId) { mutableStateOf<List<HubArticleSummary>>(emptyList()) }
        var titles by remember(sectionId) { mutableStateOf<Map<String, String>>(emptyMap()) }

        LaunchedEffect(sectionId, language) {
            val loaded = repository.hubArticlesForSection(sectionId).sortedBy { it.sortOrder }
            articles = loaded
            val keys = loaded.flatMap { listOfNotNull(it.titleKey, it.leadKey) }.toSet()
            titles = repository.resolveHubStrings(keys, language)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (articles.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${articles.size} ARTICLES",
                        color = HubSectionGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.4.sp,
                    )
                    Spacer(
                        Modifier
                            .padding(start = 10.dp)
                            .height(1.dp)
                            .weight(1f)
                            .background(HubSectionGold.copy(alpha = 0.35f)),
                    )
                }
            }

            articles.forEach { article ->
                ArticleRow(
                    title = titles[article.titleKey].orEmpty(),
                    lead = article.leadKey?.let { titles[it] },
                    readingTimeMinutes = article.readingTimeMinutes,
                    onClick = { onArticleSelected(article) },
                )
            }
        }
    }
}

@Composable
private fun ArticleRow(
    title: String,
    lead: String?,
    readingTimeMinutes: Int?,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = HubSectionCard,
        contentColor = HubSectionCream,
        border = BorderStroke(1.dp, HubSectionGold.copy(alpha = 0.48f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 18.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    readingTimeMinutes?.let { minutes ->
                        Text(
                            text = "ARTICLE · $minutes MIN",
                            color = HubSectionGold.copy(alpha = 0.72f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.1.sp,
                        )
                        Spacer(Modifier.height(5.dp))
                    }

                    Text(
                        text = title,
                        color = HubSectionCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    if (lead != null) {
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = lead,
                            color = HubSectionMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            maxLines = 2,
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = HubSectionGold.copy(alpha = 0.86f),
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.diagramSectionBody(
    diagramId: String?,
    repository: CathopediaRepository,
    language: String,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    if (diagramId == null) return
    item {
        val s = LocalStrings.current
        var diagram by remember(diagramId) { mutableStateOf<HubDiagramDetail?>(null) }
        var strings by remember(diagramId) { mutableStateOf<Map<String, String>>(emptyMap()) }
        var hotspotById by remember(diagramId) { mutableStateOf<Map<String, HubHotspotDetail>>(emptyMap()) }

        LaunchedEffect(diagramId, language) {
            val loaded = repository.hubDiagram(diagramId) ?: return@LaunchedEffect
            diagram = loaded
            hotspotById = loaded.hotspots.associateBy { it.id }
            val keys = buildSet {
                loaded.titleKey?.let(::add)
                loaded.captionKey?.let(::add)
                loaded.hotspots.forEach { h -> add(h.labelKey); h.blurbKey?.let(::add) }
            }
            strings = repository.resolveHubStrings(keys, language)
        }

        diagram?.let { d ->
            // hubAssetPainter falls back to null for any asset with no bundled drawable yet;
            // the flat color keeps the viewer (pan/zoom/tap/hotspot sheet) usable regardless.
            val fallback = ColorPainter(MaterialTheme.colorScheme.surfaceContainerLow)
            InteractiveDiagram(
                painter = hubAssetPainter(d.asset) ?: fallback,
                aspectRatio = d.aspectRatio,
                minZoom = d.minZoom,
                maxZoom = d.maxZoom,
                hotspots = d.hotspots.map { it.toDiagramHotspot(strings) },
                readMoreLabel = s.continueLabel,
                onReadMore = { uiHotspot ->
                    hotspotById[uiHotspot.id]?.target?.let(onEntityRefSelected)
                },
            )

            d.captionKey?.let { key ->
                strings[key]?.let { caption ->
                    Spacer(Modifier.height(8.dp))
                    Text(text = caption, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun HubHotspotDetail.toDiagramHotspot(strings: Map<String, String>) = DiagramHotspot(
    id = id,
    label = strings[labelKey].orEmpty(),
    blurb = blurbKey?.let { strings[it] },
    order = order,
    hasTarget = target != null,
    shape = when (val s = shape) {
        is RectShape -> DiagramRectShape(s.x, s.y, s.w, s.h)
        is CircleShape -> DiagramCircleShape(s.cx, s.cy, s.r)
        is PolygonShape -> DiagramPolygonShape(s.points.map { Offset(it.x, it.y) })
    },
)

private fun androidx.compose.foundation.lazy.LazyListScope.factSheetSectionBody(
    factSheetId: String?,
    repository: CathopediaRepository,
    language: String,
) {
    if (factSheetId == null) return
    item {
        var sheet by remember(factSheetId) { mutableStateOf<HubFactSheetDetail?>(null) }
        var strings by remember(factSheetId) { mutableStateOf<Map<String, String>>(emptyMap()) }

        LaunchedEffect(factSheetId, language) {
            val loaded = repository.hubFactSheet(factSheetId) ?: return@LaunchedEffect
            sheet = loaded
            val keys = loaded.facts.flatMap { listOfNotNull(it.labelKey, it.valueKey, it.footnoteKey) }.toSet()
            strings = repository.resolveHubStrings(keys, language)
        }

        sheet?.let { s ->
            if (s.id == "cat.fs.sacraments") {
                SacramentCarousel(s.facts, strings)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    s.facts.forEach { fact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = strings[fact.labelKey].orEmpty(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                            )
                            Text(
                                text = strings[fact.valueKey].orEmpty(),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SacramentCarousel(
    facts: List<com.ynotlabs.cathopedia.content.model.Fact>,
    strings: Map<String, String>,
) {
    val listState = rememberLazyListState()
    val currentIndex by remember {
        derivedStateOf {
            val layout = listState.layoutInfo
            val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
            layout.visibleItemsInfo.minByOrNull { item ->
                kotlin.math.abs(item.offset + item.size / 2 - center)
            }?.index ?: 0
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(horizontal = 34.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(facts, key = { _, fact -> fact.id }) { _, fact ->
                Box(
                    modifier = Modifier
                        .width(236.dp)
                        .height(344.dp)
                        .border(1.dp, HubSectionGold.copy(alpha = 0.42f), RoundedCornerShape(26.dp))
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF15372A), HubSectionCard),
                            ),
                        ),
                ) {
                    SacramentImages.forFact(fact.id)?.let { image ->
                        Image(
                            painter = painterResource(image),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 38.dp)
                                .graphicsLayer { alpha = 0.78f },
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    0.50f to Color.Transparent,
                                    0.76f to HubSectionCard.copy(alpha = 0.78f),
                                    1f to HubSectionCard,
                                ),
                            ),
                    )

                    fact.footnoteKey?.let { key ->
                        Text(
                            text = strings[key].orEmpty().uppercase(),
                            color = HubSectionGold,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(14.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(HubSectionHeader.copy(alpha = 0.82f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp),
                    ) {
                        Text(
                            text = strings[fact.labelKey].orEmpty(),
                            color = HubSectionCream,
                            fontFamily = FontFamily.Serif,
                            fontSize = 21.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = strings[fact.valueKey].orEmpty(),
                            color = HubSectionMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(facts.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (index == currentIndex) 8.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (index == currentIndex) HubSectionGold
                            else HubSectionGold.copy(alpha = 0.24f),
                        ),
                )
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.stepperSectionBody(
    stepperId: String?,
    repository: CathopediaRepository,
    language: String,
) {
    if (stepperId == null) return
    item {
        var stepper by remember(stepperId) { mutableStateOf<HubStepperDetail?>(null) }
        var strings by remember(stepperId) { mutableStateOf<Map<String, String>>(emptyMap()) }

        LaunchedEffect(stepperId, language) {
            val loaded = repository.hubStepper(stepperId) ?: return@LaunchedEffect
            stepper = loaded
            val keys = loaded.steps.flatMap { listOfNotNull(it.titleKey, it.bodyKey, it.latinKey) }.toSet()
            strings = repository.resolveHubStrings(keys, language)
        }

        stepper?.let { st ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            st.steps.sortedBy { it.order }.forEach { step ->
                HubSectionItemCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = strings[step.titleKey].orEmpty(),
                            color = HubSectionCream,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = strings[step.bodyKey].orEmpty(),
                            color = HubSectionMuted,
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                        )
                    }
                }
            }
        }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.timelineSectionBody(
    timelineId: String?,
    repository: CathopediaRepository,
    language: String,
) {
    if (timelineId == null) return
    item {
        var timeline by remember(timelineId) { mutableStateOf<HubTimelineDetail?>(null) }
        var strings by remember(timelineId) { mutableStateOf<Map<String, String>>(emptyMap()) }

        LaunchedEffect(timelineId, language) {
            val loaded = repository.hubTimeline(timelineId) ?: return@LaunchedEffect
            timeline = loaded
            val keys = loaded.events.flatMap { listOfNotNull(it.titleKey, it.bodyKey) }.toSet()
            strings = repository.resolveHubStrings(keys, language)
        }

        timeline?.let { tl ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tl.events.sortedBy { it.year }.forEach { event ->
                HubSectionItemCard {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = (if (event.approximate) "c. " else "") + event.year.toString(),
                            color = HubSectionGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.width(64.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings[event.titleKey].orEmpty(),
                                color = HubSectionCream,
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            event.bodyKey?.let { key ->
                                strings[key]?.let { body ->
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = body,
                                        color = HubSectionMuted,
                                        fontSize = 14.sp,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.collectionSectionBody(comingSoonLabel: String) {
    item {
        Text(
            text = comingSoonLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
        )
    }
}
