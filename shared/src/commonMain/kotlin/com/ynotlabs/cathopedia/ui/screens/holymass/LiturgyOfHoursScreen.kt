package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.liturgical.AelfHour
import com.ynotlabs.cathopedia.liturgical.AelfTexts
import com.ynotlabs.cathopedia.liturgical.HOURS
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/** The Hour usually prayed at this time of day. */
private fun officeForNow(): String = when (Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour) {
    in 0..9 -> "laudes"
    in 10..11 -> "tierce"
    in 12..13 -> "sexte"
    in 14..16 -> "none"
    in 17..20 -> "vepres"
    else -> "complies"
}

/**
 * La Liturgie des Heures from AELF. The texts exist only in French, so the screen is reached only
 * in French and its labels are written directly here rather than in the nine-language strings.
 */
@Composable
fun LiturgyOfHoursScreen(onBack: () -> Unit) {
    val s = LocalStrings.current
    val colors = MaterialTheme.colorScheme
    var dateText by rememberSaveable { mutableStateOf(LiturgicalCalendar.today().toString()) }
    val date = LocalDate.parse(dateText)
    var office by rememberSaveable { mutableStateOf(officeForNow()) }
    var hour by remember { mutableStateOf<AelfHour?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var attempt by remember { mutableStateOf(0) }

    LaunchedEffect(date, office, attempt) {
        loaded = false
        hour = AelfTexts.hour(office, date)
        loaded = true
    }

    ArticleScaffold(
        title = "Liturgie des Heures",
        subtitle = hour?.dayName.orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        horizontalPadding = 16.dp,
    ) {
        item(key = "date") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = { dateText = date.plus(-1, DateTimeUnit.DAY).toString() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Jour précédent", tint = colors.primary)
                }
                Text(frenchDate(date), color = colors.onSurface, fontSize = 15.sp, textAlign = TextAlign.Center)
                IconButton(onClick = { dateText = date.plus(1, DateTimeUnit.DAY).toString() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Jour suivant", tint = colors.primary)
                }
            }
        }
        item(key = "offices") {
            // Vêpres and Complies sit past the edge of a phone: bring the selected Hour into view.
            val chips = rememberScrollState()
            LaunchedEffect(office, chips.maxValue) {
                chips.scrollTo(if (HOURS.indexOfFirst { it.first == office } >= 4) chips.maxValue else 0)
            }
            Row(Modifier.fillMaxWidth().horizontalScroll(chips),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HOURS.forEach { (id, name) ->
                    val selected = id == office
                    val shape = RoundedCornerShape(50)
                    Text(
                        name,
                        color = if (selected) colors.onPrimary else colors.onSurface,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.clip(shape)
                            .background(if (selected) colors.primary else colors.surfaceContainerHigh)
                            .border(1.dp, if (selected) colors.primary else CardBorder, shape)
                            .clickable(role = Role.Tab) { office = id }
                            .heightIn(min = 40.dp)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        val current = hour
        when {
            !loaded -> item(key = "loading") { Text(s.loading, color = colors.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp)) }
            current == null -> item(key = "offline") {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(colors.surfaceContainerHigh).padding(16.dp)) {
                    Text("Texte indisponible", color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("La Liturgie des Heures nécessite une connexion à Internet, et l'AELF publie les textes quelques mois à l'avance.",
                        color = colors.onSurfaceVariant, fontSize = 14.sp, lineHeight = 20.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Réessayer", color = colors.primary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(role = Role.Button) { attempt++ }.padding(vertical = 8.dp))
                }
            }
            else -> {
                current.sections.forEachIndexed { index, section ->
                    item(key = "$office-$index") {
                        Text(section.heading.uppercase(), color = colors.primary, fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(top = 18.dp))
                        if (section.reference.isNotBlank()) {
                            Text(section.reference, color = colors.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        if (section.text.isNotBlank()) {
                            Text(section.text, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 18.sp,
                                lineHeight = 27.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                        if (section.credit.isNotBlank()) {
                            Text(section.credit, color = colors.onSurfaceVariant, fontSize = 11.sp, fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
                item(key = "credit") {
                    Text(AelfTexts.CREDIT, color = colors.onSurfaceVariant, fontSize = 11.sp,
                        modifier = Modifier.padding(top = 24.dp, bottom = 32.dp))
                }
            }
        }
    }
}

private val FRENCH_MONTHS = listOf("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre")

private fun frenchDate(date: LocalDate): String = "${date.day} ${FRENCH_MONTHS[date.month.ordinal]} ${date.year}"
