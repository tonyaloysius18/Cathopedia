package com.ynotlabs.cathopedia.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.saints.saintsFeastSelection
import com.ynotlabs.cathopedia.saints.saintsFeatured
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.SaintsOrnateFrame
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource

internal object SaintsGalleryKeys {
    const val Subtitle = "screen.saints.subtitle"
    const val Today = "screen.saints.today"
    const val Upcoming = "screen.saints.upcoming"
    const val Discover = "screen.saints.discover"
    const val Featured = "screen.saints.featured"
    const val SeeAll = "screen.saints.see_all"
    const val Explore = "screen.saints.explore"
    const val Search = "screen.saints.search"
    const val Empty = "screen.saints.empty"
    const val Date = "screen.saints.date"
    fun month(number: Int) = "screen.saints.month.$number"
    val all = setOf(Subtitle, Today, Upcoming, Discover, Featured, SeeAll, Explore, Search, Empty, Date,
        *(1..12).map(::month).toTypedArray())
}

@Composable
internal fun SaintsGalleryScreen(
    repository: CathopediaRepository,
    language: String,
    saints: List<ContentSummary>?,
    filteredSaints: List<ContentSummary>,
    query: String,
    onQueryChange: (String) -> Unit,
    listState: LazyListState,
    onBack: () -> Unit,
    onFilterClick: () -> Unit,
    onSaintSelected: (ContentSummary) -> Unit,
) {
    val s = LocalStrings.current
    val colors = MaterialTheme.colorScheme
    var labels by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(repository, language) { labels = repository.resolveHubStrings(SaintsGalleryKeys.all, language) }
    // Recheck while visible so an app left open over midnight uses the new local day.
    val today by produceState(LiturgicalCalendar.today()) {
        while (true) { value = LiturgicalCalendar.today(); delay(60_000) }
    }
    val feast = remember(saints, today) { saintsFeastSelection(saints.orEmpty(), today) }
    val featured = remember(saints, feast) { saintsFeatured(saints.orEmpty(), feast?.saints?.map { it.id }?.toSet().orEmpty()) }
    val scope = rememberCoroutineScope()
    var previousQuery by remember { mutableStateOf(query) }
    LaunchedEffect(query) {
        if (query != previousQuery) { listState.scrollToItem(0); previousQuery = query }
    }
    val browse = query.isBlank()
    // Stable item index for the "See all" link; all saint rows remain in the same lazy list.
    val exploreIndex = (if (feast != null) 2 else 0) + (if (featured.isNotEmpty()) 2 else 0)

    Column(Modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxWidth().background(colors.surface).statusBarsPadding()
            .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CathopediaBackButton(onClick = onBack, contentDescription = s.back)
                Column(Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(s.typeSaintsPlural, color = colors.onSurface, fontFamily = FontFamily.Serif,
                        fontSize = 31.sp, lineHeight = 36.sp)
                    Text(labels[SaintsGalleryKeys.Subtitle].orEmpty(), color = colors.onSurfaceVariant,
                        fontSize = 13.sp, lineHeight = 18.sp)
                }
                IconButton(onClick = onFilterClick) {
                    Icon(Icons.Default.FilterList, s.listFilterAndSortDesc, tint = colors.primary)
                }
            }
            OutlinedTextField(
                value = query, onValueChange = onQueryChange, singleLine = true,
                placeholder = { Text(labels[SaintsGalleryKeys.Search].orEmpty(), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.onSurfaceVariant) },
                trailingIcon = if (query.isNotEmpty()) {{
                    IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Close, s.closeSearch) }
                }} else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceContainerHigh,
                    unfocusedContainerColor = colors.surfaceContainerHigh,
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.primary.copy(alpha = 0.5f),
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        if (saints == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colors.primary) }
        } else {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 126.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (browse) {
                    feast?.let { selected ->
                        item(key = "saints-feast-heading") {
                            SaintsSectionHeading(
                                title = labels[if (selected.isToday) SaintsGalleryKeys.Today else SaintsGalleryKeys.Upcoming].orEmpty(),
                                detail = saintsDateLabel(selected.date, labels),
                            )
                        }
                        item(key = "saints-feast-cards") {
                            SaintsFeastCarousel(selected.saints,
                                labels[SaintsGalleryKeys.Discover].orEmpty(), onSaintSelected)
                        }
                    }
                    if (featured.isNotEmpty()) {
                        item(key = "saints-featured-heading") {
                            SaintsSectionHeading(labels[SaintsGalleryKeys.Featured].orEmpty(),
                                labels[SaintsGalleryKeys.SeeAll].orEmpty()) {
                                scope.launch { listState.animateScrollToItem(exploreIndex) }
                            }
                        }
                        item(key = "saints-featured-cards") { SaintsFeaturedRow(featured, onSaintSelected) }
                    }
                }
                item(key = "saints-explore-heading") { SaintsSectionHeading(labels[SaintsGalleryKeys.Explore].orEmpty()) }
                if (filteredSaints.isEmpty()) {
                    item(key = "saints-empty") {
                        Text(labels[SaintsGalleryKeys.Empty].orEmpty(), color = colors.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 18.dp))
                    }
                }
                items(filteredSaints, key = { "saint-${it.id}" }) { saint ->
                    SaintsBrowseCard(saint, labels, { onSaintSelected(saint) })
                }
            }
        }
    }
}

internal fun saintsDateLabel(date: LocalDate, labels: Map<String, String>): String =
    labels[SaintsGalleryKeys.Date].orEmpty().replace("{day}", date.day.toString())
        .replace("{month}", labels[SaintsGalleryKeys.month((date.month.ordinal + 1))].orEmpty())

@Composable
private fun SaintsSectionHeading(title: String, action: String? = null, detail: String? = null,
    onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontFamily = FontFamily.Serif,
            fontSize = 23.sp, lineHeight = 28.sp, modifier = Modifier.weight(1f))
        if (detail != null) Text(detail, color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Serif, fontSize = 16.sp, lineHeight = 22.sp,
            modifier = Modifier.padding(start = 12.dp))
        if (action != null) TextButton(onClick = onAction, contentPadding = PaddingValues(start = 8.dp)) {
            Text(action, fontSize = 13.sp)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.padding(start = 5.dp).size(17.dp))
        }
    }
}

@Composable
private fun SaintsFeastCarousel(saints: List<ContentSummary>, discover: String,
    onSelected: (ContentSummary) -> Unit) {
    if (saints.size == 1) {
        SaintsFeastCard(saints.first(), discover, { onSelected(saints.first()) })
    } else {
        val state = rememberLazyListState()
        BoxWithConstraints {
            val width = maxWidth
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(state = state, flingBehavior = rememberSnapFlingBehavior(state),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(saints, key = { it.id }) { saint ->
                        SaintsFeastCard(saint, discover, { onSelected(saint) }, Modifier.width(width))
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    saints.forEachIndexed { index, _ ->
                        Box(Modifier.padding(horizontal = 3.dp).size(6.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(
                                alpha = if (index == state.firstVisibleItemIndex) 1f else 0.3f)))
                    }
                }
            }
        }
    }
}

@Composable
private fun SaintsFeastCard(saint: ContentSummary, discover: String,
    onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val bg = colors.surfaceContainerHigh
    val shape = RoundedCornerShape(16.dp)
    SaintsOrnateFrame(modifier.semantics(mergeDescendants = true) {}
        .clickable(role = Role.Button, onClick = onClick)) {
        Box(Modifier.fillMaxWidth().heightIn(min = 200.dp).clip(shape).background(bg)) {
            Box(Modifier.matchParentSize()) {
                Portraits.fullForEntity(saint.type, saint.id)?.let { art ->
                    Image(painterResource(art), null,
                        Modifier.fillMaxWidth(0.68f).fillMaxHeight().align(Alignment.CenterEnd),
                        contentScale = ContentScale.Crop, alignment = Alignment.TopCenter)
                }
                // A long, eased fade from the text panel into the portrait: no visible edge,
                // and it covers the portrait's own left edge so that never shows as a line.
                Box(Modifier.matchParentSize().background(
                    Brush.horizontalGradient(*easedFade(bg, solidUntil = 0.30f, clearFrom = 0.74f))))
            }
            Column(Modifier.fillMaxWidth(0.50f).padding(start = 13.dp, top = 16.dp, end = 2.dp, bottom = 16.dp)) {
                Text(saint.name, color = colors.onSurface, fontFamily = FontFamily.Serif,
                    fontSize = 25.sp, lineHeight = 29.sp)
                Text(saint.summary, color = colors.onSurface.copy(alpha = 0.90f), fontSize = 13.sp, lineHeight = 18.sp,
                    maxLines = 4, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 6.dp))
                Row(Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(discover, color = colors.primary, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f, fill = false))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                        Modifier.padding(start = 6.dp).size(18.dp), tint = colors.primary)
                }
            }
        }
    }
}

/**
 * Colour stops fading [color] from solid at [solidUntil] to clear at [clearFrom]
 * (either way round), along a smootherstep curve sampled finely, so the fade has
 * no band or edge where it starts or stops.
 */
private fun easedFade(color: Color, solidUntil: Float, clearFrom: Float): Array<Pair<Float, Color>> {
    val steps = 16
    val stops = (0..steps).map { i ->
        val t = i / steps.toFloat()
        val eased = t * t * t * (t * (t * 6f - 15f) + 10f)
        val position = solidUntil + (clearFrom - solidUntil) * t
        position to color.copy(alpha = color.alpha * (1f - eased))
    }
    val ends = listOf(0f to (if (solidUntil < clearFrom) color else Color.Transparent),
        1f to (if (solidUntil < clearFrom) Color.Transparent else color))
    return (stops + ends).sortedBy { it.first }.toTypedArray()
}

@Composable
private fun SaintsFeaturedRow(saints: List<ContentSummary>, onSelected: (ContentSummary) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val cardWidth = (maxWidth - 12.dp) / 2
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(saints, key = { it.id }) { saint ->
                SaintsFeaturedCard(saint, { onSelected(saint) }, Modifier.width(cardWidth))
            }
        }
    }
}

@Composable
private fun SaintsFeaturedCard(saint: ContentSummary, onClick: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val bg = colors.surfaceContainerHigh
    val shape = RoundedCornerShape(18.dp)
    Box(modifier.heightIn(min = 224.dp).clip(shape).background(bg)
        .border(BorderStroke(1.dp, colors.primary.copy(alpha = 0.8f)), shape)
        .clickable(role = Role.Button, onClick = onClick)) {
        Portraits.fullForEntity(saint.type, saint.id)?.let { art ->
            Image(painterResource(art), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter)
        }
        Box(Modifier.matchParentSize().background(
            Brush.verticalGradient(*easedFade(bg, solidUntil = 0.94f, clearFrom = 0.30f))))
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth()
            .padding(start = 13.dp, end = 13.dp, top = 134.dp, bottom = 12.dp)) {
            Text(saint.name, color = colors.onSurface, fontFamily = FontFamily.Serif,
                fontSize = 18.sp, lineHeight = 22.sp)
            Text(saint.summary, color = colors.onSurfaceVariant, fontSize = 11.sp, lineHeight = 15.sp,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun SaintsBrowseCard(saint: ContentSummary, labels: Map<String, String>, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val s = LocalStrings.current
    val feast = saint.feastDay?.let { runCatching { LocalDate.parse("2000-$it") }.getOrNull() }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick),
        color = colors.surfaceContainerHigh,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.32f)),
    ) {
        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(width = 66.dp, height = 80.dp).clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceContainerHighest), contentAlignment = Alignment.Center) {
                val portrait = Portraits.forEntity(saint.type, saint.id)
                if (portrait != null) Image(painterResource(portrait), null, Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop)
                else Text(saint.name.take(1), fontFamily = FontFamily.Serif, fontSize = 28.sp, color = colors.primary)
            }
            Column(Modifier.weight(1f)) {
                Text(saint.name, fontFamily = FontFamily.Serif, fontSize = 18.sp, lineHeight = 23.sp,
                    color = colors.onSurface)
                Text(if (feast != null) s.detailFeastDayHeroMeta.replace("{value}", saintsDateLabel(feast, labels)) else saint.summary,
                    color = colors.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = colors.onSurfaceVariant,
                modifier = Modifier.size(24.dp))
        }
    }
}
