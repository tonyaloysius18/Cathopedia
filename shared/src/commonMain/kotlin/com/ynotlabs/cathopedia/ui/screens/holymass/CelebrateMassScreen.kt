package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.liturgical.LectionaryReading
import com.ynotlabs.cathopedia.liturgical.ReadingTexts
import com.ynotlabs.cathopedia.liturgical.ReadingText
import com.ynotlabs.cathopedia.liturgical.LectionaryKind
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.liturgical.MassPlan
import com.ynotlabs.cathopedia.liturgical.OrderOfMass
import com.ynotlabs.cathopedia.liturgical.OrderOfMassText
import com.ynotlabs.cathopedia.liturgical.OrdoCalendar
import com.ynotlabs.cathopedia.liturgical.OrdoCelebration
import com.ynotlabs.cathopedia.liturgical.OrdoProfile
import com.ynotlabs.cathopedia.liturgical.OrdoRank
import com.ynotlabs.cathopedia.liturgical.PlannedPart
import com.ynotlabs.cathopedia.liturgical.IcelTexts
import com.ynotlabs.cathopedia.ui.components.isDebugBuild
import com.ynotlabs.cathopedia.liturgical.SpokenLine
import com.ynotlabs.cathopedia.liturgical.fullBookNameCitation
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.KeepScreenOn
import com.ynotlabs.cathopedia.ui.theme.LiturgicalAdventLent
import com.ynotlabs.cathopedia.ui.theme.LiturgicalGaudeteLaetare
import com.ynotlabs.cathopedia.ui.theme.LiturgicalOrdinary
import com.ynotlabs.cathopedia.ui.theme.LiturgicalPentecostMartyrs
import com.ynotlabs.cathopedia.ui.theme.LocalIsDarkTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private const val PROFILE_PREF = "ordo_profile"
private const val TEXT_SCALE_PREF = "mass_text_scale"
private const val VIEW_PREF = "mass_view"

/** Width at which the parts list sits beside the text (tablets, and phones in landscape). */
private val TwoPaneWidth = 720.dp

/**
 * Celebrate Mass: the day's Mass from the offline Ordo, laid out part by part for the celebrant.
 * Phones page through one part at a time; wide screens show the parts list beside the text.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CelebrateMassScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    KeepScreenOn(enabled = true)

    var date by remember { mutableStateOf(LiturgicalCalendar.today()) }
    var profile by remember {
        mutableStateOf(OrdoProfile.entries.firstOrNull { it.name == repository.getPreference(PROFILE_PREF) } ?: OrdoProfile.GENERAL)
    }
    var textScale by remember { mutableFloatStateOf(repository.getPreference(TEXT_SCALE_PREF)?.toFloatOrNull() ?: 1f) }
    var celebrationKey by remember(date, profile) { mutableStateOf<String?>(null) }
    var sheetView by remember { mutableStateOf(repository.getPreference(VIEW_PREF) == "sheet") }
    var plan by remember { mutableStateOf<MassPlan?>(null) }
    var text by remember { mutableStateOf<OrderOfMassText?>(null) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(date, profile, celebrationKey, language) {
        loaded = false
        text = OrderOfMass.text(language)
        plan = OrdoCalendar.day(date, profile, language)?.let { OrderOfMass.plan(it, celebrationKey, language) }
        loaded = true
    }

    Column(Modifier.fillMaxSize().background(colors.background)) {
        MassHeader(
            title = t["screen.mass.title"],
            date = date,
            profile = profile,
            onBack = onBack,
            onDateChange = { date = it },
            onProfileChange = {
                profile = it
                repository.setPreference(PROFILE_PREF, it.name)
            },
            onTextScale = { delta ->
                textScale = (textScale + delta).coerceIn(0.85f, 1.9f)
                repository.setPreference(TEXT_SCALE_PREF, textScale.toString())
            },
            backLabel = s.back,
        )
        ViewToggle(sheetView) {
            sheetView = it
            repository.setPreference(VIEW_PREF, if (it) "sheet" else "guide")
        }
        val current = plan
        val omt = text
        when {
            !loaded -> Unit
            current == null || omt == null -> Text(
                t["screen.mass.out_of_range"],
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(24.dp),
            )
            sheetView -> MassSheet(
                plan = current,
                text = omt,
                language = language,
                textScale = textScale,
                profile = profile,
                onCelebrationChosen = { celebrationKey = it },
            )
            else -> BoxWithConstraints(Modifier.fillMaxSize()) {
                val wide = maxWidth >= TwoPaneWidth
                var cardOpen by remember(current.celebration.key, current.day.date) { mutableStateOf(false) }
                Column(Modifier.fillMaxSize()) {
                    if (wide || cardOpen) {
                        CelebrationCard(
                            plan = current,
                            language = language,
                            onCelebrationChosen = { celebrationKey = it; cardOpen = false },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                .then(if (wide) Modifier else Modifier.clickable { cardOpen = false }),
                        )
                    } else {
                        // On phones the reading needs the room; one line, tap for the full card.
                        CelebrationStrip(current, onClick = { cardOpen = true })
                    }
                    if (wide) {
                        TwoPaneMass(current, omt, language, textScale)
                    } else {
                        PagedMass(current, omt, language, textScale)
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewToggle(sheet: Boolean, onChange: (Boolean) -> Unit) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().background(colors.surface).padding(bottom = 8.dp),
    ) {
        listOf(false to t["screen.mass.view_guide"], true to t["screen.mass.view_sheet"]).forEach { (isSheet, label) ->
            val selected = isSheet == sheet
            Text(
                label,
                color = if (selected) colors.primary else colors.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp).clip(RoundedCornerShape(50))
                    .background(if (selected) colors.primary.copy(alpha = 0.16f) else Color.Transparent)
                    .border(1.dp, colors.primary.copy(alpha = if (selected) 0.6f else 0.25f), RoundedCornerShape(50))
                    .clickable { onChange(isSheet) }
                    .padding(horizontal = 18.dp, vertical = 7.dp),
            )
        }
    }
}

@Composable
private fun MassHeader(
    title: String,
    date: LocalDate,
    profile: OrdoProfile,
    onBack: () -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onProfileChange: (OrdoProfile) -> Unit,
    onTextScale: (Float) -> Unit,
    backLabel: String,
) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    var calendarMenu by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth().background(colors.surface).statusBarsPadding()
            .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CathopediaBackButton(onClick = onBack, contentDescription = backLabel)
            Text(
                title,
                color = colors.onSurface,
                fontFamily = FontFamily.Serif,
                // Some languages' titles ("Ordinaire de la messe") are long beside the three icons.
                fontSize = if (title.length > 14) 19.sp else 24.sp,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = { onTextScale(-0.1f) }) {
                Icon(Icons.Default.TextDecrease, t["screen.mass.text_size"], tint = colors.primary)
            }
            IconButton(onClick = { onTextScale(0.1f) }) {
                Icon(Icons.Default.TextIncrease, t["screen.mass.text_size"], tint = colors.primary)
            }
            Box {
                IconButton(onClick = { calendarMenu = true }) {
                    Icon(Icons.Default.DateRange, t["screen.mass.calendar"], tint = colors.primary)
                }
                DropdownMenu(expanded = calendarMenu, onDismissRequest = { calendarMenu = false }) {
                    OrdoProfile.entries.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    t["screen.mass.calendar.${option.name.lowercase()}"],
                                    fontWeight = if (option == profile) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            onClick = {
                                calendarMenu = false
                                onProfileChange(option)
                            },
                        )
                    }
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = { onDateChange(date.plus(-1, DateTimeUnit.DAY)) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, t["screen.mass.previous"], tint = colors.primary)
            }
            Text(
                text = dateLabel(date),
                color = colors.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .clickable { onDateChange(LiturgicalCalendar.today()) }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
            IconButton(onClick = { onDateChange(date.plus(1, DateTimeUnit.DAY)) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, t["screen.mass.next"], tint = colors.primary)
            }
            if (date != LiturgicalCalendar.today()) {
                TextButton(onClick = { onDateChange(LiturgicalCalendar.today()) }) { Text(t["screen.mass.today"]) }
            }
        }
    }
}

@Composable
internal fun dateLabel(date: LocalDate): String {
    val t = LocalScreenText.current
    val month = t["screen.saints.month.${date.month.ordinal + 1}"]
    val day = t["screen.saints.date"].replace("{day}", date.day.toString()).replace("{month}", month)
    return "$day ${date.year}"
}

@Composable
private fun CelebrationCard(
    plan: MassPlan,
    language: String,
    onCelebrationChosen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val celebration = plan.celebration
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(colors.surfaceContainerHigh)
            .border(2.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(14.dp).clip(CircleShape).background(vestmentColor(celebration.colors.firstOrNull()))
                    .border(1.dp, colors.outline, CircleShape),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                rankLabel(celebration, plan.day.date).uppercase(),
                color = colors.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(celebration.name, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 20.sp, lineHeight = 25.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            t["screen.mass.cycle"].replace("{sunday}", plan.day.sundayCycle.toString())
                .replace("{weekday}", if (plan.day.weekdayCycle == 1) "I" else "II"),
            color = colors.onSurfaceVariant,
            fontSize = 12.sp,
        )
        if (plan.readings?.weekdayReadings == true && celebration.rank != OrdoRank.WEEKDAY) {
            Text(t["screen.mass.weekday_readings"], color = colors.onSurfaceVariant, fontSize = 12.sp)
        }
        val alternatives = plan.day.celebrations.filter {
            it.key != celebration.key && (it.rank <= OrdoRank.OPTIONAL_MEMORIAL || plan.day.celebrations.size > 1 && it.rank == OrdoRank.WEEKDAY)
        }.filter { it.rank != OrdoRank.COMMEMORATION }
        if (alternatives.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(t["screen.mass.choose_celebration"], color = colors.onSurfaceVariant, fontSize = 11.sp)
            alternatives.forEach { alt ->
                Text(
                    "› ${alt.name}",
                    color = colors.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .clickable { onCelebrationChosen(alt.key) }.padding(vertical = 6.dp),
                )
            }
        }
        // The guide and celebration names are translated; the reading texts are still English.
        if (language != "en" && t["screen.mass.english_only"].isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(t["screen.mass.english_only"], color = colors.onSurfaceVariant, fontSize = 11.sp, fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
private fun CelebrationStrip(plan: MassPlan, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).background(colors.surfaceContainerHigh)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Box(
            Modifier.size(12.dp).clip(CircleShape).background(vestmentColor(plan.celebration.colors.firstOrNull()))
                .border(1.dp, colors.outline, CircleShape),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            plan.celebration.name,
            color = colors.onSurface,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            rankLabel(plan.celebration, plan.day.date),
            color = colors.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun rankLabel(celebration: OrdoCelebration, date: LocalDate): String {
    val t = LocalScreenText.current
    val key = when {
        date.dayOfWeek == DayOfWeek.SUNDAY && celebration.rank < OrdoRank.SOLEMNITY -> "sunday"
        celebration.rank >= OrdoRank.SOLEMNITY -> if (celebration.key.contains("Weekday") || celebration.key.contains("HolyWeek") ||
            celebration.key.contains("AshWednesday")) "weekday" else "solemnity"
        celebration.rank >= OrdoRank.FEAST -> "feast"
        celebration.rank == OrdoRank.MEMORIAL -> "memorial"
        celebration.rank == OrdoRank.OPTIONAL_MEMORIAL -> "optional_memorial"
        else -> "weekday"
    }
    return t["screen.mass.rank.$key"]
}

internal fun vestmentColor(name: String?): Color = when (name) {
    "purple" -> LiturgicalAdventLent
    "red" -> LiturgicalPentecostMartyrs
    "rose" -> LiturgicalGaudeteLaetare
    "green" -> LiturgicalOrdinary
    "black" -> Color(0xFF1B1B1B)
    else -> Color(0xFFF4EFE3) // white / gold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PagedMass(plan: MassPlan, text: OrderOfMassText, language: String, textScale: Float) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val pager = rememberPagerState(pageCount = { plan.parts.size })
    val scope = rememberCoroutineScope()
    var showParts by remember { mutableStateOf(false) }
    LaunchedEffect(plan.celebration.key, plan.day.date) { pager.scrollToPage(0) }

    Column(Modifier.fillMaxSize()) {
        plan.noMassNote?.let { NoMassNote(it, textScale) }
        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
            PartContent(plan.parts[page], text, language, textScale)
        }
        HorizontalDivider(color = colors.outlineVariant)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().background(colors.surface).navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            TextButton(
                onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } },
                enabled = pager.currentPage > 0,
            ) { Text(t["screen.mass.previous"]) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).clickable { showParts = true }
                    .padding(vertical = 8.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.List, t["screen.mass.parts"], tint = colors.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "${pager.currentPage + 1} / ${plan.parts.size}",
                    color = colors.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }
            TextButton(
                onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                enabled = pager.currentPage < plan.parts.lastIndex,
            ) { Text(t["screen.mass.next"]) }
        }
    }
    if (showParts) {
        ModalBottomSheet(onDismissRequest = { showParts = false }) {
            PartsIndex(plan, pager.currentPage, Modifier.fillMaxWidth().padding(bottom = 24.dp)) { index ->
                showParts = false
                scope.launch { pager.scrollToPage(index) }
            }
        }
    }
}

@Composable
private fun TwoPaneMass(plan: MassPlan, text: OrderOfMassText, language: String, textScale: Float) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    var selected by remember(plan.celebration.key, plan.day.date) { mutableStateOf(0) }
    Row(Modifier.fillMaxSize().navigationBarsPadding()) {
        PartsIndex(
            plan,
            selected,
            Modifier.widthIn(min = 240.dp, max = 320.dp).fillMaxHeight().background(colors.surface),
        ) { selected = it }
        Box(Modifier.width(1.dp).fillMaxHeight().background(colors.outlineVariant))
        Column(Modifier.weight(1f).fillMaxHeight()) {
            plan.noMassNote?.let { NoMassNote(it, textScale) }
            Box(Modifier.weight(1f)) {
                plan.parts.getOrNull(selected)?.let { PartContent(it, text, language, textScale, wide = true) }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                TextButton(onClick = { selected-- }, enabled = selected > 0) { Text(t["screen.mass.previous"]) }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { selected++ }, enabled = selected < plan.parts.lastIndex) { Text(t["screen.mass.next"]) }
            }
        }
    }
}

@Composable
private fun PartsIndex(plan: MassPlan, selected: Int, modifier: Modifier, onSelect: (Int) -> Unit) {
    val colors = MaterialTheme.colorScheme
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) { listState.scrollToItem((selected - 2).coerceAtLeast(0)) }
    LazyColumn(state = listState, modifier = modifier) {
        itemsIndexed(plan.parts) { index, planned ->
            if (index == 0 || plan.parts[index - 1].section.id != planned.section.id) {
                Text(
                    planned.section.title.uppercase(),
                    color = colors.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                )
            }
            val isSelected = index == selected
            Text(
                planned.part.title,
                color = if (isSelected) colors.primary else colors.onSurface,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) colors.primary.copy(alpha = 0.16f) else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 10.dp, vertical = 9.dp),
            )
        }
    }
}

@Composable
private fun NoMassNote(note: String, textScale: Float) {
    val colors = MaterialTheme.colorScheme
    Text(
        note,
        color = colors.onSurface,
        fontSize = (15 * textScale).sp,
        lineHeight = (21 * textScale).sp,
        modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceContainerHigh).padding(14.dp),
    )
}

/** Rubrics are printed in red in liturgical books; kept readable on both themes. */
@Composable
private fun rubricColor(): Color = if (LocalIsDarkTheme.current) Color(0xFFE59A8E) else Color(0xFFA3271F)

@Composable
private fun PartContent(
    planned: PlannedPart,
    text: OrderOfMassText,
    language: String,
    textScale: Float,
    wide: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val part = planned.part
    val base = if (wide) 1.1f else 1f
    val scale = textScale * base
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = if (wide) 32.dp else 20.dp, vertical = 16.dp),
    ) {
        Text(
            planned.section.title.uppercase(),
            color = colors.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            part.title,
            color = colors.onSurface,
            fontFamily = FontFamily.Serif,
            fontSize = (28 * scale).sp,
            lineHeight = (33 * scale).sp,
        )
        if (part.who.isNotBlank()) {
            Text(part.who, color = colors.onSurfaceVariant, fontSize = (13 * scale).sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            planned.rubric,
            color = rubricColor(),
            fontStyle = FontStyle.Italic,
            fontSize = (15 * scale).sp,
            lineHeight = (22 * scale).sp,
        )
        part.text?.let { spoken ->
            Spacer(Modifier.height(16.dp))
            Text(
                spoken,
                color = colors.onSurface,
                fontFamily = FontFamily.Serif,
                fontSize = (21 * scale).sp,
                lineHeight = (31 * scale).sp,
            )
            part.textNote?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, color = colors.onSurfaceVariant, fontSize = (12 * scale).sp)
            }
            part.textCredit?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, color = colors.onSurfaceVariant, fontSize = (10 * scale).sp, lineHeight = (14 * scale).sp)
            }
        }
        val showIcel = planned.responses.isNotEmpty() && (IcelTexts.PERMISSION_GRANTED || isDebugBuild())
        val (beforeReading, afterReading) =
            if (showIcel) IcelTexts.aroundReading(planned.responses) else emptyList<SpokenLine>() to emptyList()
        if (beforeReading.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            SpokenLines(beforeReading, scale)
        }
        planned.reading?.let { reading ->
            Spacer(Modifier.height(16.dp))
            ReadingBlock(reading, language, scale, text)
        }
        if (afterReading.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            SpokenLines(afterReading, scale)
        }
        if (showIcel) {
            Spacer(Modifier.height(6.dp))
            Text(
                (if (IcelTexts.PERMISSION_GRANTED) "" else "DRAFT · awaiting ICEL permission · ") +
                    "Excerpts from the English translation of The Roman Missal © 2010, International Commission on English in the Liturgy Corporation. All rights reserved.",
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = (10 * scale).sp, lineHeight = (14 * scale).sp,
            )
        }
        if (part.reading != null && planned.reading == null) {
            Spacer(Modifier.height(16.dp))
            PendingSlot(null, text.readingPending, scale)
        }
        if (part.homily) {
            Spacer(Modifier.height(16.dp))
            PendingSlot(null, text.homilyPending, scale)
        }
        part.missal?.takeUnless { showIcel && part.id in IcelTexts.completeParts }?.let { label ->
            Spacer(Modifier.height(16.dp))
            PendingSlot(label, text.missalPending, scale)
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ReadingBlock(reading: LectionaryReading, language: String, scale: Float, text: OrderOfMassText) {
    val colors = MaterialTheme.colorScheme
    val t = LocalScreenText.current
    var chosen by remember(reading) { mutableStateOf(0) }
    reading.options.forEachIndexed { index, option ->
        if (index > 0) {
            Text(
                "— " + (if (option.short) t["screen.mass.short_form"] else t["screen.mass.or"]),
                color = colors.onSurfaceVariant,
                fontSize = (12 * scale).sp,
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
        val selectable = reading.options.size > 1
        Text(
            displayCitation(option.citation, language),
            color = if (selectable && index != chosen) colors.onSurfaceVariant else colors.onSurface,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * scale).sp,
            lineHeight = (27 * scale).sp,
            modifier = if (selectable) {
                Modifier.clip(RoundedCornerShape(8.dp))
                    .background(if (index == chosen) colors.primary.copy(alpha = 0.14f) else Color.Transparent)
                    .clickable { chosen = index }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            } else Modifier,
        )
    }
    if (reading.kind == LectionaryKind.SEQUENCE) return
    val citation = reading.options.getOrNull(chosen)?.citation ?: return
    var passage by remember(citation) { mutableStateOf<ReadingText?>(null) }
    var loaded by remember(citation) { mutableStateOf(false) }
    LaunchedEffect(citation) {
        passage = ReadingTexts.textFor(citation)
        loaded = true
    }
    Spacer(Modifier.height(12.dp))
    val current = passage
    when {
        !loaded -> Unit
        current == null -> PendingSlot(null, text.readingPending, scale)
        else -> {
            Text(
                passageText(current, colors.primary, scale, stanzas = reading.kind == LectionaryKind.PSALM),
                color = colors.onSurface,
                fontFamily = FontFamily.Serif,
                fontSize = (19 * scale).sp,
                lineHeight = (29 * scale).sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text.readingStudyNote,
                color = colors.onSurfaceVariant,
                fontSize = (11.5f * scale).sp,
                lineHeight = (16 * scale).sp,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

/** Verse numbers small and raised, as in a printed Bible; a new chapter shows "chapter:verse". */
internal fun passageText(passage: ReadingText, numberColor: Color, scale: Float, stanzas: Boolean): AnnotatedString = buildAnnotatedString {
    var lastChapter = -1
    passage.verses.forEachIndexed { index, verse ->
        if (index > 0) append(if (stanzas && verse.stanza != passage.verses[index - 1].stanza) "\n\n" else " ")
        val label = if (verse.chapter != lastChapter && index > 0) "${verse.chapter}:${verse.verse}" else "${verse.verse}"
        withStyle(
            SpanStyle(
                color = numberColor,
                fontSize = (11 * scale).sp,
                fontFamily = FontFamily.SansSerif,
                baselineShift = BaselineShift.Superscript,
            ),
        ) { append(label) }
        append("\u2009")
        append(verse.text)
        lastChapter = verse.chapter
    }
}

/** Expands the book name, keeping a leading "Cf." (an adapted acclamation verse). */
internal fun displayCitation(citation: String, language: String): String =
    if (citation.startsWith("Cf. ")) "Cf. " + fullBookNameCitation(citation.removePrefix("Cf. "), language)
    else fullBookNameCitation(citation, language)

@Composable
private fun PendingSlot(label: String?, message: String, scale: Float) {
    val colors = MaterialTheme.colorScheme
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, colors.primary.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .background(colors.surfaceContainerLow)
            .padding(14.dp),
    ) {
        label?.let {
            Text(it, color = colors.primary, fontWeight = FontWeight.SemiBold, fontSize = (14 * scale).sp)
            Spacer(Modifier.height(4.dp))
        }
        Text(message, color = colors.onSurfaceVariant, fontSize = (13 * scale).sp, lineHeight = (18 * scale).sp)
    }
}

/** A liturgical dialogue: who speaks in small caps, the people's words in bold. */
@Composable
private fun SpokenLines(lines: List<SpokenLine>, scale: Float) {
    val colors = MaterialTheme.colorScheme
    lines.forEachIndexed { i, line ->
        if (i > 0) Spacer(Modifier.height(10.dp))
        Text(line.who.uppercase(), color = colors.primary, fontSize = (11 * scale).sp, letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold)
        Text(
            line.text,
            color = colors.onSurface,
            fontFamily = FontFamily.Serif,
            fontWeight = if (line.isPeople) FontWeight.Bold else FontWeight.Normal,
            fontSize = (20 * scale).sp,
            lineHeight = (29 * scale).sp,
        )
        line.note?.let {
            Text(it, color = colors.onSurfaceVariant, fontSize = (12 * scale).sp, lineHeight = (17 * scale).sp,
                modifier = Modifier.padding(top = 4.dp))
        }
    }
}
