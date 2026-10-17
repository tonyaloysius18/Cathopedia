package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.liturgical.LectionaryKind
import com.ynotlabs.cathopedia.liturgical.LectionaryReading
import com.ynotlabs.cathopedia.liturgical.LiturgyNote
import com.ynotlabs.cathopedia.liturgical.LiturgyNotes
import com.ynotlabs.cathopedia.liturgical.MassPlan
import com.ynotlabs.cathopedia.liturgical.OrderOfMassText
import com.ynotlabs.cathopedia.liturgical.OrdoCalendar
import com.ynotlabs.cathopedia.liturgical.OrdoProfile
import com.ynotlabs.cathopedia.liturgical.OrdoRank
import com.ynotlabs.cathopedia.liturgical.PlannedPart
import com.ynotlabs.cathopedia.liturgical.IcelTexts
import com.ynotlabs.cathopedia.liturgical.SpokenLine
import com.ynotlabs.cathopedia.liturgical.ReadingText
import com.ynotlabs.cathopedia.liturgical.ReadingTexts
import com.ynotlabs.cathopedia.ui.components.isDebugBuild
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/** One day of the week-ahead list at the foot of the sheet. */
private data class WeekDay(val date: LocalDate, val name: String, val color: String?, val readings: String)

/**
 * The day's Mass as one continuous page, laid out like a parish Mass leaflet: introduction,
 * the parts of the Mass with their readings, the Prayer of the Faithful, a reflection, and the
 * week ahead. Cathopedia's own notes appear only once a priest has approved them (drafts are
 * visible in development builds, marked as drafts).
 */
@Composable
internal fun MassSheet(
    plan: MassPlan,
    text: OrderOfMassText,
    language: String,
    textScale: Float,
    profile: OrdoProfile,
    onCelebrationChosen: (String) -> Unit,
) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val debug = isDebugBuild()
    var note by remember(plan.day.date, plan.celebration.key) { mutableStateOf<LiturgyNote?>(null) }
    var week by remember(plan.day.date, profile) { mutableStateOf<List<WeekDay>>(emptyList()) }
    LaunchedEffect(plan.day.date, plan.celebration.key) {
        note = LiturgyNotes.noteFor(plan.day, plan.celebration.key)?.takeIf { it.approved || debug }
    }
    LaunchedEffect(plan.day.date, profile) {
        week = (1..7).mapNotNull { offset ->
            val date = plan.day.date.plus(offset, DateTimeUnit.DAY)
            val day = OrdoCalendar.day(date, profile, language) ?: return@mapNotNull null
            val principal = day.principal ?: return@mapNotNull null
            val readings = OrdoCalendar.readings(day)?.readings.orEmpty()
                .filter { it.kind in setOf(LectionaryKind.FIRST_READING, LectionaryKind.PSALM, LectionaryKind.SECOND_READING, LectionaryKind.GOSPEL) }
                .mapNotNull { r -> (r.options.firstOrNull { !it.short } ?: r.options.firstOrNull())?.citation }
                .map { displayCitation(it, language) }
            WeekDay(date, principal.name, principal.colors.firstOrNull(), readings.joinToString(" / "))
        }
    }

    val scale = textScale
    val body = (17 * scale).sp
    val bodyLine = (26 * scale).sp
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max = 760.dp).fillMaxSize().verticalScroll(rememberScrollState())
                .navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // Masthead
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(12.dp).clip(CircleShape).background(vestmentColor(plan.celebration.colors.firstOrNull()))
                        .border(1.dp, colors.outline, CircleShape),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${rankLabel(plan.celebration, plan.day.date).uppercase()}  ·  ${dateLabel(plan.day.date)}",
                    color = colors.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(plan.celebration.name, color = colors.onSurface, fontFamily = FontFamily.Serif,
                fontSize = (26 * scale).sp, lineHeight = (32 * scale).sp)
            Text(
                t["screen.mass.cycle"].replace("{sunday}", plan.day.sundayCycle.toString())
                    .replace("{weekday}", if (plan.day.weekdayCycle == 1) "I" else "II"),
                color = colors.onSurfaceVariant, fontSize = 12.sp,
            )
            plan.day.celebrations.filter { it.key != plan.celebration.key && it.rank <= OrdoRank.OPTIONAL_MEMORIAL && it.rank != OrdoRank.COMMEMORATION }
                .forEach { alt ->
                    Text("› ${alt.name}", color = colors.primary, fontSize = 13.sp,
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable { onCelebrationChosen(alt.key) }.padding(vertical = 4.dp))
                }
            plan.noMassNote?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, color = colors.onSurface, fontSize = body, lineHeight = bodyLine)
            }

            note?.let { n ->
                if (!n.approved) DraftBanner(t["screen.mass.draft_note"])
                SheetHeading(t["screen.mass.introduction"])
                Text(n.introduction, color = colors.onSurface, fontSize = body, lineHeight = bodyLine, fontStyle = FontStyle.Italic)
            }

            var studyNoteShown = false
            plan.parts.forEachIndexed { index, planned ->
                if (index == 0 || plan.parts[index - 1].section.id != planned.section.id) {
                    Spacer(Modifier.height(22.dp))
                    Text(planned.section.title.uppercase(), color = colors.primary, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    HorizontalDivider(color = colors.primary.copy(alpha = 0.35f), modifier = Modifier.padding(top = 4.dp))
                }
                when {
                    planned.part.homily -> Unit // the reflection below stands in for homily notes
                    planned.part.intentions -> {
                        SheetHeading(t["screen.mass.prayer_faithful"])
                        val pof = note?.prayerOfTheFaithful
                        if (pof == null) {
                            MissalLine(planned.rubric) // no approved intentions yet: describe the part instead
                        } else {
                            Speaker(t["screen.mass.celebrant_short"], pof.invitation, scale)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("${t["screen.mass.response"]}: ") }
                                    append(pof.response)
                                },
                                color = colors.onSurface, fontSize = body, lineHeight = bodyLine,
                            )
                            pof.intentions.forEachIndexed { i, intention ->
                                Spacer(Modifier.height(6.dp))
                                Text("${i + 1}. $intention", color = colors.onSurface, fontSize = body, lineHeight = bodyLine)
                            }
                            Spacer(Modifier.height(6.dp))
                            Speaker(t["screen.mass.celebrant_short"], pof.conclusion, scale)
                        }
                    }
                    else -> {
                        SheetHeading(sheetTitle(planned, language))
                        planned.part.text?.let { Text(it, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = body, lineHeight = bodyLine) }
                        planned.part.textCredit?.let {
                            Text(it, color = colors.onSurfaceVariant, fontSize = 10.sp, lineHeight = 14.sp,
                                modifier = Modifier.padding(top = 4.dp))
                        }
                        val showIcel = planned.responses.isNotEmpty() && (IcelTexts.PERMISSION_GRANTED || isDebugBuild())
                        val (beforeReading, afterReading) =
                            if (showIcel) IcelTexts.aroundReading(planned.responses) else emptyList<SpokenLine>() to emptyList()
                        beforeReading.forEach { SheetLine(it, scale) }
                        planned.reading?.let { reading ->
                            if (reading.kind == LectionaryKind.PSALM) MissalLine(t["screen.mass.refrain_line"])
                            SheetReading(reading, scale)
                            if (!studyNoteShown && reading.kind != LectionaryKind.SEQUENCE) {
                                studyNoteShown = true
                                Text(text.readingStudyNote, color = colors.onSurfaceVariant, fontSize = 11.sp,
                                    lineHeight = 15.sp, fontStyle = FontStyle.Italic, modifier = Modifier.padding(top = 6.dp))
                            }
                        }
                        // A part can have both: said text (e.g. Lamb of God) and Missal text still pending (the priest's prayer).
                        afterReading.forEach { SheetLine(it, scale) }
                        if (planned.part.missal != null && !(showIcel && planned.part.id in IcelTexts.completeParts)) {
                            MissalLine(t["screen.mass.missal_line"])
                        }
                    }
                }
            }

            if ((IcelTexts.PERMISSION_GRANTED || isDebugBuild()) && plan.parts.any { it.responses.isNotEmpty() }) {
                Text(
                    (if (IcelTexts.PERMISSION_GRANTED) "" else "DRAFT · awaiting ICEL permission · ") +
                        "Excerpts from the English translation of The Roman Missal © 2010, International Commission on English in the Liturgy Corporation. All rights reserved.",
                    color = colors.onSurfaceVariant, fontSize = 10.sp, lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            note?.let { n ->
                Spacer(Modifier.height(26.dp))
                Text(t["screen.mass.reflection"].uppercase(), color = colors.primary, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                HorizontalDivider(color = colors.primary.copy(alpha = 0.35f), modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
                Text(n.reflection.title, color = colors.onSurface, fontFamily = FontFamily.Serif,
                    fontSize = (21 * scale).sp, lineHeight = (27 * scale).sp)
                Spacer(Modifier.height(8.dp))
                n.reflection.body.split("\n\n").forEach { paragraph ->
                    Text(paragraph, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = body, lineHeight = bodyLine)
                    Spacer(Modifier.height(10.dp))
                }
                Text("— ${n.reflection.author}", color = colors.onSurfaceVariant, fontSize = 12.sp, fontStyle = FontStyle.Italic)
            }

            if (week.isNotEmpty()) {
                Spacer(Modifier.height(26.dp))
                Text(t["screen.mass.week_ahead"].uppercase(), color = colors.primary, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                HorizontalDivider(color = colors.primary.copy(alpha = 0.35f), modifier = Modifier.padding(top = 4.dp, bottom = 6.dp))
                week.forEach { d ->
                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 5.dp)) {
                        Box(
                            Modifier.padding(top = 5.dp).size(9.dp).clip(CircleShape).background(vestmentColor(d.color))
                                .border(1.dp, colors.outline, CircleShape),
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("${dateLabel(d.date)} · ${d.name}", color = colors.onSurface, fontSize = (13.5f * scale).sp,
                                fontWeight = FontWeight.SemiBold)
                            Text(d.readings, color = colors.onSurfaceVariant, fontSize = (12.5f * scale).sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

/** "FIRST READING  ·  Isaiah 45:1, 4-6" for readings; the part's own title otherwise. */
@Composable
private fun sheetTitle(planned: PlannedPart, language: String): String {
    val reading = planned.reading ?: return planned.part.title
    val option = reading.options.firstOrNull { !it.short } ?: reading.options.firstOrNull() ?: return planned.part.title
    return if (reading.kind == LectionaryKind.SEQUENCE) "${planned.part.title}  ·  ${option.citation}"
    else "${planned.part.title}  ·  ${displayCitation(option.citation, language)}"
}

@Composable
private fun SheetHeading(title: String) {
    Spacer(Modifier.height(14.dp))
    Text(title.uppercase(), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp,
        fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun Speaker(who: String, line: String, scale: Float) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("$who: ") }
            append(line)
        },
        color = MaterialTheme.colorScheme.onSurface, fontSize = (17 * scale).sp, lineHeight = (26 * scale).sp,
    )
}

/** One dialogue line on the sheet: "People: And with your spirit." with the people's words in bold. */
@Composable
private fun SheetLine(line: SpokenLine, scale: Float) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)) { append("${line.who}: ") }
            if (line.isPeople) withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(line.text) } else append(line.text)
        },
        color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Serif,
        fontSize = (17 * scale).sp, lineHeight = (26 * scale).sp,
        modifier = Modifier.padding(vertical = 3.dp),
    )
    line.note?.let {
        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = (12 * scale).sp, lineHeight = (17 * scale).sp,
            fontStyle = FontStyle.Italic)
    }
}

@Composable
private fun MissalLine(label: String) {
    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.5.sp, fontStyle = FontStyle.Italic,
        modifier = Modifier.padding(vertical = 2.dp))
}

@Composable
private fun DraftBanner(message: String) {
    val colors = MaterialTheme.colorScheme
    Text(
        message,
        color = colors.onSurface, fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(10.dp))
            .background(colors.error.copy(alpha = 0.18f)).padding(10.dp),
    )
}

@Composable
private fun SheetReading(reading: LectionaryReading, scale: Float) {
    if (reading.kind == LectionaryKind.SEQUENCE) return
    val colors = MaterialTheme.colorScheme
    val citation = (reading.options.firstOrNull { !it.short } ?: reading.options.firstOrNull())?.citation ?: return
    var passage by remember(citation) { mutableStateOf<ReadingText?>(null) }
    LaunchedEffect(citation) { passage = ReadingTexts.textFor(citation) }
    passage?.let {
        Text(
            passageText(it, colors.primary, scale, stanzas = reading.kind == LectionaryKind.PSALM),
            color = colors.onSurface, fontFamily = FontFamily.Serif,
            fontSize = (17 * scale).sp, lineHeight = (26 * scale).sp,
        )
    }
}
