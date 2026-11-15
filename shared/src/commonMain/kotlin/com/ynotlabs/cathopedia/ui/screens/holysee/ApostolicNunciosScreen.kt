package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MaterialTheme
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
import com.ynotlabs.cathopedia.data.ApostolicNuncios
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.Envoy
import com.ynotlabs.cathopedia.data.NunciaturePost
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.news.newsDate
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import kotlinx.datetime.LocalDate

private const val K = "art.nuncios."
private val Kinds = listOf("nunciature", "nunciature_nonres", "delegation", "observer", "representative", "pontifical_representative")
private val NunciosStringKeys = (listOf(
    "title", "lead", "intro", "stat_envoys", "stat_posts", "stat_vacant", "filter_all", "filter_nuncios", "filter_other",
    "search_hint", "more", "since", "seat", "vacant_since", "charge", "posts", "born", "age", "nationality", "wikipedia",
    "photo_credit", "no_photo", "none_found", "source_note",
) + Kinds.map { "kind_$it" } + Kinds.map { "mission_$it" }).map { K + it }.toSet()

private enum class Tab { ENVOYS, VACANT }
private enum class EnvoyFilter { ALL, NUNCIOS, OTHER }

private val NCard: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
/** Same emerald as the cardinals' portraits, so both lists look alike. */
private val NPhotoBg = Color(0xFF0E2A20)
private val NAccent: Color @Composable get() = MaterialTheme.colorScheme.primary
private val NOnAccent: Color @Composable get() = MaterialTheme.colorScheme.onPrimary
private val NText: Color @Composable get() = MaterialTheme.colorScheme.onSurface
private val NMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

/**
 * "Apostolic nuncios today": every envoy of the Holy See, with a photo where a free one exists,
 * listed by the country he lives in, plus the posts that are vacant today. Tapping an envoy
 * opens him in place with every country he is accredited to.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApostolicNunciosScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(language) { strings = repository.resolveHubStrings(NunciosStringKeys, language) }
    LaunchedEffect(Unit) { ApostolicNuncios.load(repository) }
    fun str(key: String) = strings[K + key].orEmpty()

    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = selectedId?.let { id -> ApostolicNuncios.data?.envoys?.firstOrNull { it.id == id } }
    if (selected != null) {
        BackHandler { selectedId = null }
        EnvoyDetail(selected, language, ::str, onBack = { selectedId = null })
        return
    }

    val s = LocalStrings.current
    val t = LocalScreenText.current
    val today = remember { LiturgicalCalendar.today() }
    val data = ApostolicNuncios.data
    val envoys = remember(data, language) {
        data?.envoys.orEmpty().sortedBy { ApostolicNuncios.place(it.posts.firstOrNull()?.place.orEmpty(), language).lowercase() }
    }
    val vacant = remember(data, language) {
        data?.vacant.orEmpty().sortedBy { ApostolicNuncios.place(it.place, language).lowercase() }
    }
    val postCount = envoys.sumOf { it.posts.size }
    var tab by rememberSaveable { mutableStateOf(Tab.ENVOYS) }
    var filter by rememberSaveable { mutableStateOf(EnvoyFilter.ALL) }
    var query by rememberSaveable { mutableStateOf("") }
    val q = query.trim().lowercase()
    fun placeMatches(p: NunciaturePost) =
        p.place.lowercase().contains(q) || ApostolicNuncios.place(p.place, language).lowercase().contains(q)
    val shownEnvoys = envoys.filter {
        when (filter) {
            EnvoyFilter.ALL -> true
            EnvoyFilter.NUNCIOS -> it.isNuncio
            EnvoyFilter.OTHER -> !it.isNuncio
        }
    }.filter { q.isEmpty() || it.name.lowercase().contains(q) || it.posts.any(::placeMatches) }
    val shownVacant = vacant.filter { q.isEmpty() || placeMatches(it) }

    ArticleScaffold(
        title = str("title"),
        subtitle = str("lead"),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 16.dp,
    ) {
        if (strings.isEmpty() || envoys.isEmpty()) return@ArticleScaffold
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NCard,
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            ) {
                Row {
                    Box(Modifier.padding(vertical = 18.dp).width(4.dp).height(64.dp).clip(RoundedCornerShape(2.dp)).background(NAccent))
                    Text(
                        str("intro").replace("{envoys}", envoys.size.toString()).replace("{posts}", postCount.toString())
                            .replace("{vacant}", vacant.size.toString()),
                        color = NText.copy(alpha = 0.92f), fontSize = 15.sp, lineHeight = 23.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
                    )
                }
            }
            Row(Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NStatTile(envoys.size, str("stat_envoys"), tab == Tab.ENVOYS, Modifier.weight(1f)) { tab = Tab.ENVOYS }
                NStatTile(postCount, str("stat_posts"), false, Modifier.weight(1f)) { tab = Tab.ENVOYS }
                NStatTile(vacant.size, str("stat_vacant"), tab == Tab.VACANT, Modifier.weight(1f)) { tab = Tab.VACANT }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text(str("search_hint"), color = NMuted) },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = NAccent) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NAccent, unfocusedBorderColor = CardBorder,
                    focusedTextColor = NText, unfocusedTextColor = NText, cursorColor = NAccent,
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = if (tab == Tab.VACANT) 12.dp else 0.dp),
            )
            if (tab == Tab.ENVOYS) {
                FilterChipsRow(
                    options = listOf(
                        str("filter_all") to EnvoyFilter.ALL,
                        str("filter_nuncios") to EnvoyFilter.NUNCIOS,
                        str("filter_other") to EnvoyFilter.OTHER,
                    ),
                    selected = filter,
                    onSelect = { filter = it },
                    modifier = Modifier.padding(top = 10.dp, bottom = 12.dp),
                )
            }
        }
        if ((tab == Tab.ENVOYS && shownEnvoys.isEmpty()) || (tab == Tab.VACANT && shownVacant.isEmpty())) {
            item { Text(str("none_found"), color = NMuted, fontSize = 14.sp, modifier = Modifier.padding(vertical = 24.dp)) }
        }
        if (tab == Tab.ENVOYS) {
            shownEnvoys.chunked(2).forEach { pair ->
                item(key = pair.first().id) {
                    Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { envoy -> EnvoyCard(envoy, language, ::str, Modifier.weight(1f)) { selectedId = envoy.id } }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        } else {
            shownVacant.forEach { post ->
                item(key = "vacant_" + post.place + post.kind) { VacantRow(post, language, ::str) }
            }
        }
        item {
            val updated = data?.updated?.take(10)?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            Text(
                str("source_note").replace("{date}", updated?.let { t.newsDate(it) }.orEmpty()),
                color = NMuted, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 8.dp, bottom = 96.dp),
            )
        }
    }
}

@Composable
private fun NStatTile(value: Int, label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier.clip(shape)
            .background(if (selected) NAccent else NCard)
            .border(1.dp, if (selected) NAccent else CardBorder, shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value.toString(), color = if (selected) NOnAccent else NText, fontFamily = FontFamily.Serif,
            fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium)
        Text(label, color = if (selected) NOnAccent else NMuted, fontSize = 11.sp, lineHeight = 14.sp,
            textAlign = TextAlign.Center, maxLines = 2)
    }
}

/** The photo, or a quiet silhouette for envoys with no free photo (or offline before a new one downloads). */
@Composable
private fun EnvoyPhoto(envoy: Envoy, modifier: Modifier) {
    val bytes by produceState<ByteArray?>(null, envoy.id) { value = ApostolicNuncios.photo(envoy) }
    Box(modifier.background(NPhotoBg), contentAlignment = Alignment.Center) {
        val image = bytes
        if (image != null) {
            Image(rememberAsyncImagePainter(image), envoy.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Icon(Icons.Filled.Person, null, tint = Color(0xFFD4AF37).copy(alpha = 0.45f), modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
private fun EnvoyCard(envoy: Envoy, language: String, str: (String) -> String, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val main = envoy.posts.firstOrNull()
    Column(
        modifier.clip(shape).background(NCard).border(1.dp, CardBorder, shape)
            .semantics(mergeDescendants = true) {}.clickable(role = Role.Button, onClick = onClick),
    ) {
        EnvoyPhoto(envoy, Modifier.fillMaxWidth().aspectRatio(4f / 5f))
        Column(Modifier.padding(horizontal = 10.dp, vertical = 9.dp)) {
            Text(main?.let { ApostolicNuncios.place(it.place, language) }.orEmpty(), color = NAccent, fontSize = 12.sp,
                lineHeight = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            // Always two lines, so every card in the grid is the same height.
            Text(envoy.name, color = NText, fontFamily = FontFamily.Serif, fontSize = 15.sp, lineHeight = 19.sp,
                fontWeight = FontWeight.Medium, minLines = 2, maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp))
            Text(
                listOfNotNull(main?.let { str("kind_" + it.kind) },
                    (envoy.posts.size - 1).takeIf { it > 0 }?.let { str("more").replace("{n}", it.toString()) }).joinToString(" · "),
                color = NMuted, fontSize = 11.sp, lineHeight = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun VacantRow(post: NunciaturePost, language: String, str: (String) -> String) {
    val t = LocalScreenText.current
    val shape = RoundedCornerShape(14.dp)
    Column(
        Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(shape).background(NCard).border(1.dp, CardBorder, shape)
            .padding(horizontal = 14.dp, vertical = 11.dp),
    ) {
        Text(ApostolicNuncios.place(post.place, language), color = NText, fontFamily = FontFamily.Serif, fontSize = 16.sp,
            lineHeight = 21.sp, fontWeight = FontWeight.Medium)
        Text(postLine(post, language, str, mission = true), color = NAccent, fontSize = 12.sp, lineHeight = 16.sp,
            modifier = Modifier.padding(top = 2.dp))
        post.sinceDate?.let { date ->
            Text(str(if (post.headedBy == "charge_d_affaires") "charge" else "vacant_since").replace("{date}", t.newsDate(date)),
                color = NMuted, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** "Apostolic Delegate · Resides in Israel", or the mission's name for a vacant post. */
private fun postLine(post: NunciaturePost, language: String, str: (String) -> String, mission: Boolean): String =
    listOfNotNull(
        str((if (mission) "mission_" else "kind_") + post.kind),
        post.seat?.let { str("seat").replace("{place}", ApostolicNuncios.place(it, language)) },
    ).joinToString(" · ")

@Composable
private fun EnvoyDetail(envoy: Envoy, language: String, str: (String) -> String, onBack: () -> Unit) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val uriHandler = LocalUriHandler.current
    val today = remember { LiturgicalCalendar.today() }
    val main = envoy.posts.firstOrNull()
    ArticleScaffold(
        title = envoy.name,
        subtitle = main?.let { str("kind_" + it.kind) + " · " + ApostolicNuncios.place(it.place, language) }.orEmpty(),
        backDescription = s.back,
        onBack = onBack,
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.Top) {
                EnvoyPhoto(envoy, Modifier.width(150.dp).aspectRatio(4f / 5f).clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp)))
                Column(Modifier.padding(start = 16.dp).weight(1f)) {
                    envoy.birthDate?.let { born ->
                        Label(str("born"))
                        Text(t.newsDate(born) + (envoy.age(today)?.let { " · " + str("age").replace("{n}", "$it") } ?: ""),
                            color = NText, fontSize = 14.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 3.dp, bottom = 12.dp))
                    }
                    if (envoy.nationality.isNotEmpty()) {
                        Label(str("nationality"))
                        Text(ApostolicNuncios.place(envoy.nationality, language), color = NText, fontFamily = FontFamily.Serif,
                            fontSize = 16.sp, lineHeight = 21.sp, modifier = Modifier.padding(top = 3.dp))
                    }
                }
            }
        }
        item {
            Label(str("posts") + " · " + envoy.posts.size, Modifier.padding(bottom = 8.dp))
            Surface(shape = RoundedCornerShape(16.dp), color = NCard, border = BorderStroke(1.dp, CardBorder)) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                    envoy.posts.forEachIndexed { i, post ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                            Text(ApostolicNuncios.place(post.place, language), color = NText, fontFamily = FontFamily.Serif,
                                fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium)
                            Text(postLine(post, language, str, mission = false), color = NAccent, fontSize = 12.sp, lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 2.dp))
                            post.sinceDate?.let { date ->
                                Text(str("since").replace("{date}", t.newsDate(date)), color = NMuted, fontSize = 12.sp,
                                    lineHeight = 16.sp, modifier = Modifier.padding(top = 1.dp))
                            }
                        }
                        if (i < envoy.posts.lastIndex) Box(Modifier.fillMaxWidth().height(1.dp).background(CardBorder.copy(alpha = 0.4f)))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            if (envoy.wikipedia.isNotEmpty()) {
                OutlinedButton(
                    onClick = { runCatching { uriHandler.openUri(envoy.wikipedia) } },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, NAccent),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = NCard, contentColor = NText),
                ) {
                    Text(str("wikipedia"), fontFamily = FontFamily.Serif, fontSize = 15.sp)
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null, Modifier.padding(start = 8.dp).size(18.dp))
                }
            }
            val photo = envoy.photo
            Text(
                if (photo != null) str("photo_credit").replace("{author}", photo.author.ifEmpty { "—" }).replace("{license}", photo.license)
                else str("no_photo"),
                color = NMuted, fontSize = 12.sp, lineHeight = 17.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 96.dp)
                    .clickable(enabled = !photo?.source.isNullOrEmpty()) { runCatching { uriHandler.openUri(photo!!.source) } },
            )
        }
    }
}

@Composable
private fun Label(text: String, modifier: Modifier = Modifier) {
    Text(text.uppercase(), color = NAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, modifier = modifier)
}
