package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.ColorPainter
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
import com.ynotlabs.cathopedia.ui.components.DiagramCircleShape
import com.ynotlabs.cathopedia.ui.components.DiagramHotspot
import com.ynotlabs.cathopedia.ui.components.DiagramPolygonShape
import com.ynotlabs.cathopedia.ui.components.DiagramRectShape
import com.ynotlabs.cathopedia.ui.components.InteractiveDiagram
import com.ynotlabs.cathopedia.model.HubTimelineDetail

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = s.back,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        if (current != null) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Text(
                        text = strings[current.titleKey].orEmpty(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.Serif,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    current.summaryKey?.let { key ->
                        strings[key]?.let { summary ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = summary,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp,
                                lineHeight = 21.sp,
                            )
                        }
                    }
                }
            }

            when (current.layout) {
                "ARTICLES" -> articlesSectionBody(sectionId, repository, language, onArticleSelected)
                "DIAGRAM" -> diagramSectionBody(current.diagramId, repository, language, onEntityRefSelected)
                "FACT_SHEET" -> factSheetSectionBody(current.factSheetId, repository, language)
                "STEPPER" -> stepperSectionBody(current.stepperId, repository, language)
                "TIMELINE" -> timelineSectionBody(current.timelineId, repository, language)
                else -> collectionSectionBody(s.hubSectionComingSoon)
            }
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

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            articles.forEach { article ->
                ArticleRow(
                    title = titles[article.titleKey].orEmpty(),
                    lead = article.leadKey?.let { titles[it] },
                    onClick = { onArticleSelected(article) },
                )
            }
        }
    }
}

@Composable
private fun ArticleRow(title: String, lead: String?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                )
                if (lead != null) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = lead,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 2,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp),
            )
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                st.steps.sortedBy { it.order }.forEach { step ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "${step.order}.",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.width(28.dp),
                        )
                        Column {
                            Text(
                                text = strings[step.titleKey].orEmpty(),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp,
                            )
                            Text(
                                text = strings[step.bodyKey].orEmpty(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tl.events.sortedBy { it.year }.forEach { event ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = (if (event.approximate) "c. " else "") + event.year.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.width(56.dp),
                        )
                        Column {
                            Text(
                                text = strings[event.titleKey].orEmpty(),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp,
                            )
                            event.bodyKey?.let { key ->
                                strings[key]?.let { body ->
                                    Text(text = body, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
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
