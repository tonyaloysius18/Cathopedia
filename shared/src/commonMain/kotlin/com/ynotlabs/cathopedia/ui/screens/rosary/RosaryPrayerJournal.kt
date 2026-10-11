package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.rosary.RosaryJournalDay
import com.ynotlabs.cathopedia.rosary.rosaryJournalEntries
import com.ynotlabs.cathopedia.rosary.rosaryJournalWeekDays
import com.ynotlabs.cathopedia.rosary.rosaryJournalWeekStart
import com.ynotlabs.cathopedia.ui.theme.RosaryMarianCard
import com.ynotlabs.cathopedia.ui.theme.RosaryMarianPanel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal object RosaryJournalStringKeys {
    const val Title = "rosary.journal.title"
    const val PreviousWeek = "rosary.journal.previous_week"
    const val NextWeek = "rosary.journal.next_week"
    const val EmptyWeek = "rosary.journal.empty_week"
    const val NoOtherCompletions = "rosary.journal.no_other_completions"
    const val RosarySingular = "rosary.journal.rosary_singular"
    const val RosaryPlural = "rosary.journal.rosary_plural"
    const val RosaryFew = "rosary.journal.rosary_few"
    const val Mysteries = "rosary.journal.mysteries"
    const val Completed = "rosary.journal.completed"
    const val Date = "rosary.journal.date"
    fun month(index: Int) = "rosary.journal.month.$index"
    fun weekday(index: Int) = "rosary.journal.weekday.$index"
    fun weekdayShort(index: Int) = "rosary.journal.weekday_short.$index"
    val all = setOf(
        Title, PreviousWeek, NextWeek, EmptyWeek, NoOtherCompletions,
        RosarySingular, RosaryPlural, RosaryFew, Mysteries, Completed, Date,
        *(1..12).map(::month).toTypedArray(),
        *(0..6).map(::weekday).toTypedArray(),
        *(0..6).map(::weekdayShort).toTypedArray(),
    )
}

@OptIn(ExperimentalTime::class)
@Composable
internal fun RosaryPrayerJournal(meter: RosaryMeter, strings: Map<String, String>) {
    val zone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(zone).date
    var weekOffset by rememberSaveable { mutableIntStateOf(0) }
    val currentWeek = rosaryJournalWeekStart(today)
    val start = LocalDate.fromEpochDays(currentWeek.toEpochDays() + weekOffset * 7)
    val days = rosaryJournalWeekDays(start)
    val entries = remember(meter, start, zone) { rosaryJournalEntries(meter, start, zone) }
    val counts = entries.associate { it.date to it.count }

    RosaryMarianPanel {
        JournalHeading(strings[RosaryJournalStringKeys.Title].orEmpty())
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { weekOffset-- }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = strings[RosaryJournalStringKeys.PreviousWeek], tint = RosaryMarianCard.muted)
            }
            Text(
                journalWeekLabel(start, days.last(), today, strings),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = RosaryMarianCard.cream,
            )
            IconButton(onClick = { weekOffset++ }, enabled = weekOffset < 0) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = strings[RosaryJournalStringKeys.NextWeek],
                    tint = RosaryMarianCard.muted.copy(alpha = if (weekOffset < 0) 1f else 0.35f))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            days.forEach { day ->
                JournalDayMarker(day, counts[day] ?: 0, today, strings, Modifier.weight(1f))
            }
        }
        JournalDivider(Modifier.padding(top = 20.dp, bottom = 12.dp))
        entries.forEachIndexed { index, entry ->
            if (index > 0) JournalDivider(Modifier.padding(vertical = 12.dp))
            JournalEntry(entry, today, strings)
        }
        if (entries.isNotEmpty()) JournalDivider(Modifier.padding(top = 16.dp, bottom = 12.dp))
        Text(
            strings[if (entries.isEmpty()) RosaryJournalStringKeys.EmptyWeek else RosaryJournalStringKeys.NoOtherCompletions].orEmpty(),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = RosaryMarianCard.muted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun JournalDayMarker(day: LocalDate, count: Int, today: LocalDate, strings: Map<String, String>, modifier: Modifier) {
    val prayed = count > 0
    val description = strings[RosaryStringKeys.MeterDayDescription].orEmpty()
        .replace("{date}", journalDateLabel(day, today, strings)).replace("{count}", count.toString()) +
        if (day == today) ", ${strings[MysteryDialogStringKeys.Today].orEmpty()}" else ""
    Column(
        modifier.semantics(mergeDescendants = true) { contentDescription = description }
            .alpha(if (day > today) 0.45f else 1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            strings[RosaryJournalStringKeys.weekdayShort(day.dayOfWeek.ordinal)].orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            color = RosaryMarianCard.muted,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
        Box(
            Modifier.widthIn(max = 48.dp).fillMaxWidth().aspectRatio(1f)
                .background(if (prayed) RosaryMarianCard.gold else RosaryMarianCard.muted.copy(alpha = 0.12f), CircleShape)
                .border(if (day == today) 2.dp else 1.dp,
                    if (day == today) RosaryMarianCard.gold else RosaryMarianCard.muted.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(day.day.toString(), style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                fontFamily = FontFamily.Serif, color = if (prayed) RosaryMarianCard.deep else RosaryMarianCard.cream)
        }
    }
}

@Composable
private fun JournalEntry(entry: RosaryJournalDay, today: LocalDate, strings: Map<String, String>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        JournalRosaryMark()
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(journalDateLabel(entry.date, today, strings), style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, color = RosaryMarianCard.cream)
            val sets = MysterySet.entries.filter { (entry.byMysterySet[it] ?: 0) > 0 }
            val detail = when (sets.size) {
                0 -> strings[RosaryJournalStringKeys.Completed].orEmpty()
                1 -> strings[RosaryJournalStringKeys.Mysteries].orEmpty().replace("{set}", mysterySetName(sets.first(), strings))
                else -> sets.joinToString(" · ") { "${mysterySetName(it, strings)} ${entry.byMysterySet[it]}" }
            }
            Text(detail, style = MaterialTheme.typography.bodySmall, color = RosaryMarianCard.muted)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(entry.count.toString(), style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Serif, color = RosaryMarianCard.gold)
            val countLabel = when {
                entry.count == 1 -> RosaryJournalStringKeys.RosarySingular
                entry.count % 10 in 2..4 && entry.count % 100 !in 12..14 -> RosaryJournalStringKeys.RosaryFew
                else -> RosaryJournalStringKeys.RosaryPlural
            }
            Text(strings[countLabel].orEmpty(),
                style = MaterialTheme.typography.labelSmall, color = RosaryMarianCard.cream)
        }
    }
}

@Composable
internal fun RosaryMysteryTotals(meter: RosaryMeter, strings: Map<String, String>) {
    RosaryMarianPanel {
        JournalHeading(strings[RosaryStringKeys.MeterBreakdown].orEmpty())
        Column(Modifier.padding(top = 12.dp)) {
            MysterySet.entries.chunked(2).forEachIndexed { index, sets ->
                if (index > 0) JournalDivider(Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    sets.forEachIndexed { column, set ->
                        if (column > 0) Box(Modifier.padding(horizontal = 12.dp).width(1.dp).height(40.dp)
                            .background(RosaryMarianCard.muted.copy(alpha = 0.3f)))
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(mysterySetName(set, strings), modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium, color = RosaryMarianCard.cream)
                            Text((meter.byMysterySet[set] ?: 0).toString(), style = MaterialTheme.typography.headlineMedium,
                                fontFamily = FontFamily.Serif, color = RosaryMarianCard.gold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun JournalHeading(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold, color = RosaryMarianCard.gold)
}

@Composable
private fun JournalDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier, color = RosaryMarianCard.muted.copy(alpha = 0.3f))
}

@Composable
private fun JournalRosaryMark() {
    val gold = RosaryMarianCard.gold
    Canvas(Modifier.size(width = 26.dp, height = 40.dp)) {
        val center = Offset(size.width / 2, size.height * 0.34f)
        repeat(12) { bead ->
            val angle = bead * 2 * kotlin.math.PI / 12
            drawCircle(gold, size.width * 0.065f,
                Offset(center.x + cos(angle).toFloat() * size.width * 0.39f,
                    center.y + sin(angle).toFloat() * size.height * 0.27f))
        }
        drawLine(gold, Offset(center.x, size.height * 0.63f), Offset(center.x, size.height * 0.95f), 2.dp.toPx())
        drawLine(gold, Offset(center.x - size.width * 0.15f, size.height * 0.79f),
            Offset(center.x + size.width * 0.15f, size.height * 0.79f), 2.dp.toPx())
    }
}

internal fun journalWeekLabel(start: LocalDate, end: LocalDate, today: LocalDate, strings: Map<String, String>): String {
    fun month(date: LocalDate) = strings[RosaryJournalStringKeys.month(date.month.ordinal + 1)].orEmpty()
    return when {
        start.year != end.year -> "${start.day} ${month(start)} ${start.year} – ${end.day} ${month(end)} ${end.year}"
        start.month != end.month -> "${start.day} ${month(start)} – ${end.day} ${month(end)}" + if (start.year != today.year) " ${start.year}" else ""
        else -> "${start.day}–${end.day} ${month(end)}" + if (start.year != today.year) " ${start.year}" else ""
    }
}

internal fun journalDateLabel(date: LocalDate, today: LocalDate, strings: Map<String, String>): String =
    strings[RosaryJournalStringKeys.Date].orEmpty()
        .replace("{weekday}", strings[RosaryJournalStringKeys.weekday(date.dayOfWeek.ordinal)].orEmpty())
        .replace("{day}", date.day.toString())
        .replace("{month}", strings[RosaryJournalStringKeys.month(date.month.ordinal + 1)].orEmpty()) +
        if (date.year != today.year) " ${date.year}" else ""
