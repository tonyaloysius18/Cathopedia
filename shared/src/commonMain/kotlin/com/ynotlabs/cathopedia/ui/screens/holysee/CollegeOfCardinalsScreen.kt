package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.ynotlabs.cathopedia.data.Cardinal
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.CollegeOfCardinals
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.news.newsDate
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import kotlinx.datetime.LocalDate

private const val K = "art.cardcollege."
private val CollegeStringKeys = listOf(
    "title", "lead", "intro", "stat_total", "stat_electors", "stat_non_electors", "filter_all", "filter_electors",
    "filter_non_electors", "search_hint", "elector", "non_elector", "renounced", "age", "born", "country", "order",
    "created", "created_value", "office", "office_note", "press_bio", "wikipedia", "photo_credit", "none_found",
    "order_cb", "order_cp", "order_cd", "pope_francis", "pope_benedict_xvi", "pope_john_paul_ii", "source_note",
).map { K + it }.toSet()

private enum class ElectorFilter { ALL, ELECTORS, NON_ELECTORS }

// This page is dressed in cardinal red in both app themes, like the portraits'
// backdrop, so text on its surfaces uses fixed light colours.
// The app's own green and gold, so the list reads like the rest of the Holy See hub in either theme.
private val CollegeCard: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
/** Matches the emerald backdrop the portraits are cut out onto. */
private val CollegePhotoBg = Color(0xFF0E2A20)
private val CollegeAccent: Color @Composable get() = MaterialTheme.colorScheme.primary
private val CollegeOnAccent: Color @Composable get() = MaterialTheme.colorScheme.onPrimary
private val CollegeText: Color @Composable get() = MaterialTheme.colorScheme.onSurface
private val CollegeMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val CollegeBorder: Color @Composable get() = CardBorder

/**
 * "The College of Cardinals today": every living cardinal, with a photo, in
 * order of precedence (bishops, priests, deacons, each by date of creation).
 * Electors are worked out from today's date, so the counts stay right as
 * cardinals turn eighty. Tapping a card opens that cardinal in place.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CollegeOfCardinalsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(language) { strings = repository.resolveHubStrings(CollegeStringKeys, language) }
    LaunchedEffect(Unit) { CollegeOfCardinals.load(repository) }
    fun str(key: String) = strings[K + key].orEmpty()

    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = selectedId?.let { id -> CollegeOfCardinals.data?.cardinals?.firstOrNull { it.id == id } }
    if (selected != null) {
        BackHandler { selectedId = null }
        CardinalDetail(selected, language, ::str, onBack = { selectedId = null })
        return
    }

    val s = LocalStrings.current
    val today = remember { LiturgicalCalendar.today() }
    val all = CollegeOfCardinals.data?.cardinals.orEmpty()
    val electors = all.count { it.isElector(today) }
    var filter by rememberSaveable { mutableStateOf(ElectorFilter.ALL) }
    var query by rememberSaveable { mutableStateOf("") }
    val shown = all.filter {
        when (filter) {
            ElectorFilter.ALL -> true
            ElectorFilter.ELECTORS -> it.isElector(today)
            ElectorFilter.NON_ELECTORS -> !it.isElector(today)
        }
    }.filter { query.isBlank() || it.matches(query, language) }

    ArticleScaffold(
        title = str("title"),
        subtitle = str("lead"),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 16.dp,
    ) {
        if (strings.isEmpty() || all.isEmpty()) return@ArticleScaffold
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CollegeCard,
                border = BorderStroke(1.dp, CollegeBorder),
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            ) {
                Row {
                    Box(Modifier.padding(vertical = 18.dp).width(4.dp).height(64.dp).clip(RoundedCornerShape(2.dp))
                        .background(CollegeAccent))
                    Text(
                        str("intro").replace("{total}", all.size.toString()).replace("{electors}", electors.toString()),
                        color = CollegeText.copy(alpha = 0.92f), fontSize = 15.sp, lineHeight = 23.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
                    )
                }
            }
            Row(Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(all.size, str("stat_total"), filter == ElectorFilter.ALL, Modifier.weight(1f)) { filter = ElectorFilter.ALL }
                StatTile(electors, str("stat_electors"), filter == ElectorFilter.ELECTORS, Modifier.weight(1f)) {
                    filter = ElectorFilter.ELECTORS
                }
                StatTile(all.size - electors, str("stat_non_electors"), filter == ElectorFilter.NON_ELECTORS, Modifier.weight(1f)) {
                    filter = ElectorFilter.NON_ELECTORS
                }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text(str("search_hint"), color = HolySeeMuted) },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = CollegeAccent) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CollegeAccent, unfocusedBorderColor = CollegeBorder,
                    focusedTextColor = CollegeText, unfocusedTextColor = CollegeText, cursorColor = CollegeAccent,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            FilterChipsRow(
                options = listOf(
                    str("filter_all") to ElectorFilter.ALL,
                    str("filter_electors") to ElectorFilter.ELECTORS,
                    str("filter_non_electors") to ElectorFilter.NON_ELECTORS,
                ),
                selected = filter,
                onSelect = { filter = it },
                modifier = Modifier.padding(top = 10.dp, bottom = 12.dp),
            )
        }
        if (shown.isEmpty()) {
            item {
                Text(str("none_found"), color = HolySeeMuted, fontSize = 14.sp, modifier = Modifier.padding(vertical = 24.dp))
            }
        }
        shown.chunked(2).forEach { pair ->
            item(key = pair.first().id) {
                Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEach { cardinal ->
                        CardinalCard(cardinal, language, today, ::str, Modifier.weight(1f)) { selectedId = cardinal.id }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        item {
            val updated = CollegeOfCardinals.data?.updated?.take(10)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            val t = LocalScreenText.current
            Text(
                str("source_note").replace("{date}", updated?.let { t.newsDate(it) }.orEmpty()) + " " + str("office_note"),
                color = HolySeeMuted, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 8.dp, bottom = 96.dp),
            )
        }
    }
}

private fun Cardinal.matches(query: String, language: String): Boolean {
    val q = query.trim().lowercase()
    return name.lowercase().contains(q) || country.lowercase().contains(q) ||
        CollegeOfCardinals.country(this, language).lowercase().contains(q)
}

@Composable
private fun StatTile(value: Int, label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier.clip(shape)
            .background(if (selected) CollegeAccent else CollegeCard)
            .border(1.dp, if (selected) CollegeAccent else CollegeBorder, shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value.toString(), color = if (selected) CollegeOnAccent else CollegeText, fontFamily = FontFamily.Serif,
            fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium)
        Text(label, color = if (selected) CollegeOnAccent else CollegeMuted, fontSize = 11.sp, lineHeight = 14.sp,
            textAlign = TextAlign.Center, maxLines = 2)
    }
}

/** The photo, or a quiet silhouette while it downloads or when offline before its first view. */
@Composable
private fun CardinalPhoto(cardinal: Cardinal, modifier: Modifier) {
    val bytes by produceState<ByteArray?>(null, cardinal.id) { value = CollegeOfCardinals.photo(cardinal) }
    Box(modifier.background(CollegePhotoBg), contentAlignment = Alignment.Center) {
        val image = bytes
        if (image != null) {
            Image(rememberAsyncImagePainter(image), cardinal.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Icon(Icons.Filled.Person, null, tint = CollegeAccent.copy(alpha = 0.45f), modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun CardinalCard(
    cardinal: Cardinal,
    language: String,
    today: LocalDate,
    str: (String) -> String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val elector = cardinal.isElector(today)
    Column(
        modifier.clip(shape).background(CollegeCard).border(1.dp, CollegeBorder, shape)
            .semantics(mergeDescendants = true) {}.clickable(role = Role.Button, onClick = onClick),
    ) {
        Box {
            CardinalPhoto(cardinal, Modifier.fillMaxWidth().aspectRatio(4f / 5f))
            if (elector) {
                Text(str("elector").uppercase(), color = CollegeOnAccent, fontSize = 9.sp, lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp,
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).clip(RoundedCornerShape(50))
                        .background(CollegeAccent).padding(horizontal = 8.dp, vertical = 3.dp))
            }
        }
        Column(Modifier.padding(horizontal = 10.dp, vertical = 9.dp)) {
            // Always two lines, so every card in the grid is the same height.
            Text(cardinal.name, color = CollegeText, fontFamily = FontFamily.Serif, fontSize = 15.sp, lineHeight = 19.sp,
                fontWeight = FontWeight.Medium, minLines = 2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                listOfNotNull(CollegeOfCardinals.country(cardinal, language),
                    cardinal.age(today)?.let { str("age").replace("{n}", it.toString()) }).joinToString(" · "),
                color = CollegeAccent, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp),
            )
            Text(str(orderKey(cardinal.order)), color = CollegeMuted, fontSize = 11.sp, lineHeight = 15.sp, maxLines = 1,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

private fun orderKey(order: String) = when (order) {
    "CB" -> "order_cb"
    "CD" -> "order_cd"
    else -> "order_cp"
}

@Composable
private fun CardinalDetail(cardinal: Cardinal, language: String, str: (String) -> String, onBack: () -> Unit) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val uriHandler = LocalUriHandler.current
    val today = remember { LiturgicalCalendar.today() }
    val elector = cardinal.isElector(today)
    ArticleScaffold(
        title = cardinal.name,
        subtitle = CollegeOfCardinals.country(cardinal, language),
        backDescription = s.back,
        onBack = onBack,
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.Top) {
                CardinalPhoto(cardinal, Modifier.width(150.dp).aspectRatio(4f / 5f).clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CollegeBorder, RoundedCornerShape(16.dp)))
                Column(Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(
                        (if (elector) str("elector") else str("non_elector")).uppercase(),
                        color = if (elector) CollegeOnAccent else CollegeAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.clip(RoundedCornerShape(50))
                            .background(if (elector) CollegeAccent else Color.Transparent)
                            .border(1.dp, if (elector) CollegeAccent else CollegeBorder, RoundedCornerShape(50))
                            .padding(horizontal = 9.dp, vertical = 4.dp),
                    )
                    if (cardinal.nonElector == "renounced") {
                        Text(str("renounced"), color = HolySeeMuted, fontSize = 12.sp, lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 6.dp))
                    }
                    Text(str("office").uppercase(), color = CollegeAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp, modifier = Modifier.padding(top = 14.dp))
                    Text(cardinal.office, color = CollegeText, fontFamily = FontFamily.Serif, fontSize = 16.sp,
                        lineHeight = 22.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item {
            Surface(shape = RoundedCornerShape(16.dp), color = CollegeCard, border = BorderStroke(1.dp, CollegeBorder)) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                    cardinal.birthDate?.let { born ->
                        FactRow(str("born"), t.newsDate(born) + (cardinal.age(today)?.let { " · " + str("age").replace("{n}", "$it") } ?: ""))
                    }
                    FactRow(str("country"), CollegeOfCardinals.country(cardinal, language))
                    FactRow(str("order"), str(orderKey(cardinal.order)))
                    cardinal.consistoryDate?.let { date ->
                        FactRow(str("created"), str("created_value").replace("{date}", t.newsDate(date))
                            .replace("{pope}", str("pope_" + cardinal.createdBy)), last = true)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (cardinal.pressBio.isNotEmpty()) LinkButton(str("press_bio")) { runCatching { uriHandler.openUri(cardinal.pressBio) } }
                if (cardinal.wikipedia.isNotEmpty()) LinkButton(str("wikipedia")) { runCatching { uriHandler.openUri(cardinal.wikipedia) } }
            }
            cardinal.photo?.let { photo ->
                Text(
                    str("photo_credit").replace("{author}", photo.author.ifEmpty { "—" }).replace("{license}", photo.license),
                    color = HolySeeMuted, fontSize = 12.sp, lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 96.dp)
                        .clickable(enabled = photo.source.isNotEmpty()) { runCatching { uriHandler.openUri(photo.source) } },
                )
            }
        }
    }
}

@Composable
private fun FactRow(label: String, value: String, last: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(label, color = CollegeMuted, fontSize = 13.sp, modifier = Modifier.width(118.dp))
        Text(value, color = CollegeText, fontSize = 14.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f))
    }
    if (!last) Box(Modifier.fillMaxWidth().height(1.dp).background(CollegeBorder.copy(alpha = 0.4f)))
}

@Composable
private fun LinkButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, CollegeAccent),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = CollegeCard, contentColor = CollegeText),
    ) {
        Text(label, fontFamily = FontFamily.Serif, fontSize = 15.sp)
        Icon(Icons.AutoMirrored.Filled.OpenInNew, null, Modifier.padding(start = 8.dp).size(18.dp))
    }
}
