package com.ynotlabs.cathopedia.ui.screens.news

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.CatholicNews
import com.ynotlabs.cathopedia.data.NewsItem
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold

private enum class NewsFilter { ALL, NEWS, EVENTS }

/** Every current news item and event, newest first, with a News / Events filter. */
@Composable
fun NewsScreen(
    repository: CathopediaRepository,
    language: String,
    onOpenItem: (String) -> Unit,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val today = remember { LiturgicalCalendar.today() }
    var filter by rememberSaveable { mutableStateOf(NewsFilter.ALL) }
    LaunchedEffect(Unit) { CatholicNews.load(repository) }
    val items = CatholicNews.visibleItems(today).filter {
        when (filter) {
            NewsFilter.ALL -> true
            NewsFilter.NEWS -> !it.isEvent
            NewsFilter.EVENTS -> it.isEvent
        }
    }

    ArticleScaffold(
        title = t["screen.news.title"],
        subtitle = t["screen.news.subtitle"],
        backDescription = s.back,
        onBack = onBack,
    ) {
        item {
            FilterChipsRow(
                options = listOf(
                    t["screen.news.filter_all"] to NewsFilter.ALL,
                    t["screen.news.filter_news"] to NewsFilter.NEWS,
                    t["screen.news.filter_events"] to NewsFilter.EVENTS,
                ),
                selected = filter,
                onSelect = { filter = it },
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
        if (CatholicNews.feed != null && items.isEmpty()) {
            item {
                Text(t["screen.news.empty"], color = ArticleMuted, fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 24.dp))
            }
        }
        items.forEach { news ->
            item(key = news.id) {
                NewsCard(news, language, today, t, { onOpenItem(news.id) }, Modifier.padding(bottom = 14.dp))
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

/** One item in full: its art, dates and place, the story, and a link to the official source. */
@Composable
fun NewsDetailScreen(
    repository: CathopediaRepository,
    id: String,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val today = remember { LiturgicalCalendar.today() }
    val uriHandler = LocalUriHandler.current
    var openFailed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { CatholicNews.load(repository) }
    val item: NewsItem? = CatholicNews.item(id)

    ArticleScaffold(
        title = item?.title(language).orEmpty(),
        subtitle = item?.source.orEmpty(),
        backDescription = s.back,
        onBack = onBack,
    ) {
        if (item == null) return@ArticleScaffold
        item {
            val shape = RoundedCornerShape(20.dp)
            val tagline = item.tagline(language)
            Box(Modifier.fillMaxWidth().height(196.dp).clip(shape)) {
                NewsArt(item, Modifier.matchParentSize(), glyphSize = 128.dp, badgeSize = 148.dp, badgeTop = 40.dp)
                val theme = NewsTheme.of(item)
                if (item.image != null) {
                    Box(Modifier.matchParentSize().background(Brush.horizontalGradient(
                        0f to Color.Black.copy(alpha = 0.55f), 0.5f to Color.Black.copy(alpha = 0.15f), 0.75f to Color.Transparent)))
                }
                val countdown = t.newsCountdown(item, today)
                // The banner's left side carries one line in large type: the theme verse when the
                // item has one, else the countdown for an event, else the date of the news.
                val (headline, detail) = when {
                    tagline.isNotEmpty() -> tagline.split(" — ").let { it[0] to it.getOrNull(1) }
                    countdown != null -> countdown to t.newsDateLine(item)
                    else -> (t.newsDateLine(item) ?: "") to item.location(language).ifEmpty { null }
                }
                Column(Modifier.align(Alignment.BottomStart).fillMaxWidth(0.52f).padding(16.dp)) {
                    Text(headline, color = theme.text, fontFamily = FontFamily.Serif,
                        fontSize = if (tagline.isNotEmpty()) 18.sp else 24.sp, lineHeight = if (tagline.isNotEmpty()) 24.sp else 29.sp)
                    detail?.let {
                        Text(it, color = theme.highlight, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                }
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    NewsPill(t[if (item.isEvent) "screen.news.kind_event" else "screen.news.kind_news"].uppercase(),
                        filled = false, color = theme.highlight, onLight = theme.light)
                    // Shown large below when there is no verse; otherwise as a pill.
                    if (tagline.isNotEmpty()) countdown?.let { NewsPill(it, filled = true, color = theme.accent) }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Skip what the banner already shows in large type.
                val bannerHasDate = item.tagline(language).isEmpty()
                val bannerHasPlace = bannerHasDate && t.newsCountdown(item, today) == null
                if (!bannerHasDate) t.newsDateLine(item)?.let { NewsMetaRow(Icons.Filled.CalendarMonth, it) }
                if (!bannerHasPlace) item.location(language).takeIf { it.isNotEmpty() }?.let { NewsMetaRow(Icons.Filled.Place, it) }
            }
            Spacer(Modifier.height(16.dp))
            Text(item.summary(language), color = ArticleCream, fontFamily = FontFamily.Serif,
                fontSize = 18.sp, lineHeight = 26.sp)
            Spacer(Modifier.height(14.dp))
        }
        item.paragraphs(language).forEachIndexed { index, paragraph ->
            item(key = "p$index") {
                Text(paragraph, color = ArticleCream, fontSize = 15.sp, lineHeight = 23.sp,
                    modifier = Modifier.padding(bottom = 12.dp))
            }
        }
        item.url(language)?.let { url ->
            item {
                val label = if (item.isEvent) t["screen.news.official_site"]
                    else t["screen.news.read_on"].replace("{source}", item.source)
                OutlinedButton(
                    onClick = { openFailed = runCatching { uriHandler.openUri(url) }.isFailure },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    border = BorderStroke(1.dp, ArticleGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ArticleGold),
                ) {
                    Text(label, fontFamily = FontFamily.Serif, fontSize = 15.sp)
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null, Modifier.padding(start = 8.dp).size(18.dp))
                }
                if (openFailed) {
                    Text(t["screen.news.open_error"], color = ArticleMuted, fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp))
                }
                if (item.source.isNotEmpty()) {
                    Text(t["screen.news.source"].replace("{source}", item.source), color = ArticleMuted,
                        fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
                }
                if (item.image != null && item.imageLicense.isNotEmpty()) {
                    Text(
                        t["screen.news.photo_credit"].replace("{author}", item.imageAuthor.ifEmpty { "—" })
                            .replace("{license}", item.imageLicense),
                        color = ArticleMuted, fontSize = 12.sp, lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 4.dp)
                            .clickable(enabled = item.imageSource.isNotEmpty()) { runCatching { uriHandler.openUri(item.imageSource) } },
                    )
                }
                if (item.badgeCredit.isNotEmpty()) {
                    Text(item.badgeCredit, color = ArticleMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun NewsMetaRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = ArticleGold, modifier = Modifier.size(16.dp))
        Text(text, color = ArticleGold, fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp))
    }
}
