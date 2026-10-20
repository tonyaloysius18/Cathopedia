package com.ynotlabs.cathopedia.ui.screens.news

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.ynotlabs.cathopedia.data.CatholicNews
import com.ynotlabs.cathopedia.data.NewsItem
import com.ynotlabs.cathopedia.i18n.ScreenText
import com.ynotlabs.cathopedia.ui.theme.DarkGold
import com.ynotlabs.cathopedia.ui.theme.DarkText
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val WydYouthBlue = Color(0xFF0047BB)
private val WydCrossRed = Color(0xFFF32735)
private val WydGloriousYellow = Color(0xFFFBE126)

/**
 * A card's look until (and beneath) its own art. Its colours are fixed, not
 * taken from the app theme: most cards are dark (text in the Dark* colours);
 * a [light] card carries a logo made for a white page.
 */
internal enum class NewsTheme(
    val top: Color,
    val bottom: Color,
    /** The countdown pill. */
    val accent: Color,
    /** Date line, outlines and border. */
    val highlight: Color = DarkGold,
    val text: Color = DarkText,
    val muted: Color = DarkText.copy(alpha = 0.78f),
    val light: Boolean = false,
) {
    /**
     * World Youth Day Seoul, in the official palette from the WYD Seoul 2027
     * brand guidelines: Youth Blue (Pantone 2728 C), Cross Red (1788 C) and
     * Glorious Yellow (107 C).
     */
    WYD(Color(0xFFEEF3FC), Color(0xFFC9D8F4), WydCrossRed, WydYouthBlue,
        text = Color(0xFF0F172A), muted = Color(0xFF3F4A5E), light = true),
    /** The Holy Father: deep gold and umber, with the keys of Peter. */
    PAPAL(Color(0xFF2A1D0C), Color(0xFF5C4319), DarkGold),
    /** A papal journey: dusk over the hemisphere he is flying to. */
    JOURNEY(Color(0xFF1B1340), Color(0xFF6E3426), Color(0xFFC8553D)),
    /** The College of Cardinals: cardinal red, with the galero. */
    CARDINAL(Color(0xFF3D0710), Color(0xFF861B2B), Color(0xFFB3202F)),
    /** The missions: deep green, with the globe. */
    MISSION(Color(0xFF0A322B), Color(0xFF1C6253), Color(0xFF2E8B6E)),
    /** A papal document: ink and violet, with an open book. */
    DOCUMENT(Color(0xFF17142A), Color(0xFF3A3258), Color(0xFF6A5AA8)),
    DEFAULT(Color(0xFF15202E), Color(0xFF2B3B52), DarkGold);

    companion object {
        fun of(item: NewsItem): NewsTheme = when (item.theme) {
            "wyd" -> WYD
            "papal" -> PAPAL
            "journey" -> JOURNEY
            "cardinal" -> CARDINAL
            "mission" -> MISSION
            "document" -> DOCUMENT
            else -> DEFAULT
        }
    }
}

/** "17 October 2026", or "3–8 August 2027" for an event spanning days of one month. */
internal fun ScreenText.newsDate(date: LocalDate, end: LocalDate? = null): String {
    val pattern = get("screen.saints.date").ifEmpty { "{day} {month}" }
    val withYear = get("screen.news.date_year").ifEmpty { "{date} {year}" }
    fun month(d: LocalDate) = get("screen.saints.month.${d.month.ordinal + 1}")
    fun format(day: String, d: LocalDate) = withYear
        .replace("{date}", pattern.replace("{day}", day).replace("{month}", month(d)))
        .replace("{year}", d.year.toString())
    return when {
        end == null || end == date -> format(date.day.toString(), date)
        end.year == date.year && end.month == date.month -> format("${date.day}–${end.day}", date)
        else -> format(date.day.toString(), date) + " – " + format(end.day.toString(), end)
    }
}

/** The item's date line: the event's dates, or when the news was published. */
internal fun ScreenText.newsDateLine(item: NewsItem): String? {
    val start = item.startDate
    if (item.isEvent && start != null) return newsDate(start, item.endDate)
    return item.publishedDate?.let { newsDate(it) }
}

/** "In 287 days" / "Tomorrow" / "Happening now" for an event; null for news or a past event. */
internal fun ScreenText.newsCountdown(item: NewsItem, today: LocalDate): String? {
    if (!item.isEvent) return null
    val start = item.startDate ?: return null
    val end = item.endDate ?: start
    val days = today.daysUntil(start)
    return when {
        days > 1 -> get("screen.news.in_days").replace("{n}", days.toString())
        days == 1 -> get("screen.news.tomorrow")
        today <= end -> get("screen.news.happening_now")
        else -> null
    }
}

/** The card picture: the item's own art when there is any, else its theme drawn in code. */
@Composable
internal fun NewsArt(
    item: NewsItem,
    modifier: Modifier = Modifier,
    glyphSize: Dp = 80.dp,
    badgeSize: Dp = 92.dp,
    badgeTop: Dp = 34.dp,
) {
    if (item.badge != null) {
        Box(modifier) {
            NewsArtBackground(item, Modifier.matchParentSize(), showGlyph = false)
            NewsBadge(item.badge, Modifier.align(Alignment.TopEnd).padding(top = badgeTop, end = 4.dp)
                .size(badgeSize * 0.92f, badgeSize))
        }
        return
    }
    NewsArtBackground(item, modifier, glyphSize)
}

/** A logo with a transparent background, on its card's own colour; empty until its bytes load. */
@Composable
private fun NewsBadge(path: String, modifier: Modifier) {
    val bytes by produceState<ByteArray?>(null, path) { value = CatholicNews.image(path) }
    val image = bytes ?: return
    Image(rememberAsyncImagePainter(image), null, modifier.padding(6.dp), contentScale = ContentScale.Fit)
}

@Composable
private fun NewsArtBackground(item: NewsItem, modifier: Modifier, glyphSize: Dp = 80.dp, showGlyph: Boolean = true) {
    val theme = NewsTheme.of(item)
    val path = item.image
    val bytes by produceState<ByteArray?>(null, path) { value = path?.let { CatholicNews.image(it) } }
    Box(modifier.background(Brush.verticalGradient(listOf(theme.top, theme.bottom)))) {
        val image = bytes
        if (image != null) {
            Image(rememberAsyncImagePainter(image), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else if (showGlyph) {
            NewsGlyph(item, theme, Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 18.dp).size(glyphSize))
        }
    }
}

@Composable
private fun NewsGlyph(item: NewsItem, theme: NewsTheme, modifier: Modifier) {
    when (theme) {
        NewsTheme.WYD -> Canvas(modifier) { drawBrushCross() }
        NewsTheme.PAPAL -> Canvas(modifier) { drawCrossedKeys(DarkGold) }
        NewsTheme.CARDINAL -> Canvas(modifier) { drawGalero(DarkGold) }
        NewsTheme.JOURNEY -> Icon(Icons.Filled.FlightTakeoff, null, tint = DarkGold.copy(alpha = 0.85f), modifier = modifier)
        NewsTheme.MISSION -> Icon(Icons.Filled.Public, null, tint = DarkGold.copy(alpha = 0.85f), modifier = modifier)
        NewsTheme.DOCUMENT -> Icon(Icons.Filled.AutoStories, null, tint = DarkGold.copy(alpha = 0.85f), modifier = modifier)
        NewsTheme.DEFAULT -> Icon(Icons.Filled.Newspaper, null, tint = DarkGold.copy(alpha = 0.8f), modifier = modifier)
    }
}

/**
 * WYD Seoul's palette as a picture: a red and a white brush stroke crossing
 * in front of the yellow light of Christ, with a ring at the centre for the
 * young pilgrim. Our own drawing in the event's colours, not the logo.
 */
private fun DrawScope.drawBrushCross() {
    val w = size.width
    val center = Offset(w * 0.5f, w * 0.42f)
    drawCircle(Brush.radialGradient(listOf(WydGloriousYellow, WydGloriousYellow.copy(alpha = 0.3f), Color.Transparent),
        center = center, radius = w * 0.5f), radius = w * 0.5f, center = center)
    for (index in 0 until 12) {
        val angle = index * PI / 6
        drawLine(WydGloriousYellow.copy(alpha = 0.6f),
            Offset(center.x + cos(angle).toFloat() * w * 0.34f, center.y + sin(angle).toFloat() * w * 0.34f),
            Offset(center.x + cos(angle).toFloat() * w * 0.48f, center.y + sin(angle).toFloat() * w * 0.48f),
            strokeWidth = w * 0.02f)
    }
    // Brush strokes: thick in the middle, tapering to the ends.
    fun stroke(color: Color, from: Offset, to: Offset, width: Float) {
        for (step in 0..24) {
            val f = step / 24f
            val taper = sin(f * PI).toFloat().coerceAtLeast(0.55f)
            drawCircle(color, radius = width * taper / 2,
                center = Offset(from.x + (to.x - from.x) * f, from.y + (to.y - from.y) * f))
        }
    }
    stroke(WydYouthBlue, Offset(w * 0.12f, w * 0.40f), Offset(w * 0.88f, w * 0.36f), w * 0.14f)
    stroke(WydCrossRed, Offset(w * 0.52f, w * 0.02f), Offset(w * 0.47f, w * 0.98f), w * 0.17f)
    drawCircle(WydYouthBlue, radius = w * 0.075f, center = center, style = Stroke(w * 0.035f))
}

/** The galero, the broad red hat of a cardinal, with its cords and tassels falling either side. */
private fun DrawScope.drawGalero(gold: Color) {
    val w = size.width
    // Crown, then the wide flat brim across it.
    drawArc(gold, 180f, 180f, useCenter = true, topLeft = Offset(w * 0.28f, w * 0.04f), size = Size(w * 0.44f, w * 0.36f))
    drawOval(gold, topLeft = Offset(w * 0.02f, w * 0.18f), size = Size(w * 0.96f, w * 0.10f))
    for (side in listOf(-1f, 1f)) {
        val x = w * 0.5f + side * w * 0.30f
        drawLine(gold, Offset(w * 0.5f + side * w * 0.12f, w * 0.27f), Offset(x, w * 0.44f), strokeWidth = w * 0.02f)
        // Tassels in rows of one, two and three.
        for (row in 1..3) {
            val y = w * (0.47f + (row - 1) * 0.11f)
            for (index in 0 until row) {
                drawCircle(gold, radius = w * 0.035f, center = Offset(x + (index - (row - 1) / 2f) * w * 0.085f, y))
            }
        }
    }
}

/** The keys of Saint Peter, crossed: the sign of the Pope's office. */
private fun DrawScope.drawCrossedKeys(gold: Color) {
    val w = size.width
    val stroke = w * 0.045f
    for (degrees in listOf(-40f, 40f)) {
        rotate(degrees, Offset(w / 2, w / 2)) {
            val x = w / 2
            drawCircle(gold, radius = w * 0.11f, center = Offset(x, w * 0.83f), style = Stroke(stroke))
            drawLine(gold, Offset(x, w * 0.72f), Offset(x, w * 0.10f), strokeWidth = stroke)
            drawLine(gold, Offset(x, w * 0.14f), Offset(x + w * 0.12f, w * 0.14f), strokeWidth = stroke)
            drawLine(gold, Offset(x, w * 0.24f), Offset(x + w * 0.09f, w * 0.24f), strokeWidth = stroke)
        }
    }
}

/**
 * One news or event card: the art fills it, text sits on a dark scrim at the
 * bottom. Used by the Home carousel and the full News screen.
 */
@Composable
internal fun NewsCard(
    item: NewsItem,
    language: String,
    today: LocalDate,
    text: ScreenText,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 188.dp,
) {
    val shape = RoundedCornerShape(18.dp)
    val theme = NewsTheme.of(item)
    val countdown = text.newsCountdown(item, today)
    val meta = listOfNotNull(text.newsDateLine(item), item.location(language).ifEmpty { null }).joinToString(" · ")
    Box(modifier.fillMaxWidth().height(height).clip(shape)
        .border(1.dp, theme.highlight.copy(alpha = 0.65f), shape)
        .semantics(mergeDescendants = true) {}.clickable(role = Role.Button, onClick = onClick)) {
        NewsArt(item, Modifier.matchParentSize())
        val scrim = if (theme.light) {
            arrayOf(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to Color.White.copy(alpha = 0.6f))
        } else {
            arrayOf(0f to Color.Black.copy(alpha = 0.10f), 0.40f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.78f))
        }
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(*scrim)))
        if (item.image != null) {
            // Photos fill the card: shade the left side too, so the title stays readable over a face.
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(
                0f to Color.Black.copy(alpha = 0.55f), 0.55f to Color.Black.copy(alpha = 0.15f), 0.8f to Color.Transparent)))
        }
        NewsCardBadges(item, countdown, text)
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth(if (item.badge != null) 0.70f else 0.80f).padding(14.dp)) {
            Text(item.title(language), color = theme.text, fontFamily = FontFamily.Serif, fontSize = 19.sp,
                lineHeight = 23.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (meta.isNotEmpty()) {
                Text(meta, color = theme.highlight, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 1,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
            }
            Text(item.summary(language), color = theme.muted,
                fontSize = 12.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun BoxScope.NewsCardBadges(item: NewsItem, countdown: String?, text: ScreenText) {
    Row(Modifier.align(Alignment.TopStart).fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        val theme = NewsTheme.of(item)
        NewsPill(text[if (item.isEvent) "screen.news.kind_event" else "screen.news.kind_news"].uppercase(),
            filled = false, color = theme.highlight, onLight = theme.light)
        Spacer(Modifier.weight(1f))
        countdown?.let { NewsPill(it, filled = true, color = theme.accent) }
    }
}

@Composable
internal fun NewsPill(label: String, filled: Boolean, color: Color = DarkGold, onLight: Boolean = false) {
    val shape = RoundedCornerShape(50)
    Text(label, color = if (filled) Color.White else color, fontSize = 10.sp, lineHeight = 13.sp,
        fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, maxLines = 1,
        modifier = Modifier.clip(shape)
            .background(when {
                filled -> color.copy(alpha = 0.92f)
                onLight -> Color.White.copy(alpha = 0.7f)
                else -> Color.Black.copy(alpha = 0.35f)
            })
            .border(1.dp, if (filled) Color.Transparent else color.copy(alpha = 0.6f), shape)
            .padding(horizontal = 9.dp, vertical = 4.dp))
}
