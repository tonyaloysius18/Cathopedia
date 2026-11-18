package com.ynotlabs.cathopedia.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.ScreenText
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.profile.OccasionKind
import com.ynotlabs.cathopedia.profile.ProfileGreetings
import com.ynotlabs.cathopedia.profile.ProfileStore
import com.ynotlabs.cathopedia.profile.StateOfLife
import com.ynotlabs.cathopedia.profile.UserProfile
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import kotlinx.datetime.LocalDate

private const val DAY_MS = 86_400_000L

/** A sacramental date shown on the profile form, in form order. */
private val dateKinds = listOf(
    OccasionKind.BIRTHDAY,
    OccasionKind.BAPTISM,
    OccasionKind.FIRST_COMMUNION,
    OccasionKind.CONFIRMATION,
    OccasionKind.WEDDING,
    OccasionKind.ORDINATION,
    OccasionKind.PROFESSION,
)

/**
 * Settings → My Profile. Every change is saved straight away to this phone's
 * preferences ([ProfileStore]); nothing leaves the device. [onChanged] runs
 * when the screen closes after an edit, so the app can re-schedule greetings.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    repository: CathopediaRepository,
    language: String,
    onChanged: () -> Unit,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val store = remember { ProfileStore(repository) }
    var profile by remember { mutableStateOf(store.load()) }
    var editing by remember { mutableStateOf<OccasionKind?>(null) }
    var pickingSaint by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    var dirty by remember { mutableStateOf(false) }
    fun update(next: UserProfile) {
        profile = next
        store.save(next)
        dirty = true
    }
    // Notifications are re-scheduled once, on leaving, not on every keystroke.
    val latestDirty by rememberUpdatedState(dirty)
    DisposableEffect(Unit) { onDispose { if (latestDirty) onChanged() } }

    ArticleScaffold(
        title = t["screen.profile.title"],
        subtitle = t["screen.profile.subtitle"],
        backDescription = s.back,
        onBack = onBack,
        horizontalPadding = 18.dp,
    ) {
        item(key = "privacy") {
            Row(
                Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 14.dp)
                    .clip(RoundedCornerShape(14.dp)).background(colors.primary.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Lock, null, tint = colors.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text(t["screen.profile.privacy"], color = colors.onSurfaceVariant, fontSize = 12.5.sp, lineHeight = 17.sp)
            }
        }

        item(key = "name") {
            SectionLabel(t["screen.profile.section_name"])
            ProfileField(t["screen.profile.first_name"], profile.firstName) { update(profile.copy(firstName = it)) }
            Spacer(Modifier.height(10.dp))
            ProfileField(t["screen.profile.surname"], profile.surname) { update(profile.copy(surname = it)) }
            Spacer(Modifier.height(18.dp))
        }

        item(key = "state") {
            SectionLabel(t["screen.profile.section_state"])
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StateOfLife.entries.forEach { state ->
                    val selected = profile.state == state
                    Text(
                        t["screen.profile.state.${state.name.lowercase()}"],
                        color = if (selected) colors.onPrimary else colors.onBackground,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                            .background(if (selected) colors.primary else colors.surfaceContainer)
                            .border(1.dp, if (selected) colors.primary else CardBorder, RoundedCornerShape(20.dp))
                            .clickable { update(profile.copy(state = state)) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item(key = "dates") {
            SectionLabel(t["screen.profile.section_dates"])
            Group {
                val visible = dateKinds.filter {
                    when (it) {
                        OccasionKind.WEDDING -> profile.state.hasWedding
                        OccasionKind.ORDINATION -> profile.state.hasOrdination
                        OccasionKind.PROFESSION -> profile.state.hasProfession
                        else -> true
                    }
                }
                visible.forEachIndexed { i, kind ->
                    if (i > 0) RowDivider()
                    val date = ProfileGreetings.parseDate(profile.date(kind))
                    DateRow(
                        label = t["screen.profile.date.${kind.key}"],
                        value = date?.let { formatDate(it, t) },
                        placeholder = t["screen.profile.add_date"],
                        clearDescription = t["screen.profile.clear"],
                        onClick = { editing = kind },
                        onClear = { update(profile.withDate(kind, null)) },
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        item(key = "feast") {
            SectionLabel(t["screen.profile.section_feast"])
            Group {
                DateRow(
                    label = profile.patronName ?: t["screen.profile.patron"],
                    value = profile.feastDay?.let { t.monthDay(it) },
                    placeholder = t["screen.profile.choose_patron"],
                    clearDescription = t["screen.profile.clear"],
                    onClick = { pickingSaint = true },
                    onClear = { update(profile.copy(patronId = null, patronType = null, patronName = null, feastDay = null)) },
                )
                RowDivider()
                Text(
                    t["screen.profile.feast_manual"],
                    color = colors.primary,
                    fontSize = 13.5.sp,
                    modifier = Modifier.fillMaxWidth().clickable { editing = OccasionKind.FEAST_DAY }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                )
            }
            Text(
                t["screen.profile.feast_hint"],
                color = colors.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
            )
            Spacer(Modifier.height(26.dp))
        }

        item(key = "delete") {
            if (!profile.isEmpty) {
                TextButton(onClick = { confirmDelete = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(t["screen.profile.delete"], color = colors.error, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    editing?.let { kind ->
        if (kind == OccasionKind.FEAST_DAY) {
            MonthDayDialog(
                initial = profile.feastDay,
                t = t,
                onDismiss = { editing = null },
                onPick = { mmdd ->
                    // A hand-set day replaces the chosen saint (their feast is a different day).
                    update(profile.copy(feastDay = mmdd, patronId = null, patronType = null, patronName = null))
                    editing = null
                },
            )
        } else {
            SacramentDateDialog(
                initial = ProfileGreetings.parseDate(profile.date(kind)),
                t = t,
                onDismiss = { editing = null },
                onPick = { date ->
                    update(profile.withDate(kind, date.toString()))
                    editing = null
                },
            )
        }
    }

    if (pickingSaint) {
        SaintPickerSheet(repository, language, t, onDismiss = { pickingSaint = false }) { saint ->
            update(profile.copy(patronId = saint.id, patronType = saint.type.name, patronName = saint.name, feastDay = saint.feastDay))
            pickingSaint = false
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(t["screen.profile.delete_title"]) },
            text = { Text(t["screen.profile.delete_body"]) },
            confirmButton = {
                TextButton(onClick = {
                    store.clear()
                    profile = UserProfile()
                    dirty = true
                    confirmDelete = false
                }) { Text(t["screen.profile.delete_confirm"], color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(t["screen.profile.cancel"]) } },
        )
    }
}

/** "21 May 1994" in the app language, using the saints' month names. */
internal fun formatDate(date: LocalDate, t: ScreenText): String {
    val mmdd = "${(date.month.ordinal + 1).toString().padStart(2, '0')}-${date.day.toString().padStart(2, '0')}"
    return "${t.monthDay(mmdd)} ${date.year}"
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 2.dp, bottom = 8.dp),
    )
}

@Composable
private fun Group(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, CardBorder, RoundedCornerShape(18.dp)),
    ) { content() }
}

@Composable
private fun RowDivider() {
    Box(Modifier.fillMaxWidth().padding(horizontal = 14.dp).height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)))
}

@Composable
private fun ProfileField(label: String, value: String, onChange: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it.take(60)) },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceContainer,
            unfocusedContainerColor = colors.surfaceContainer,
            focusedBorderColor = colors.primary.copy(alpha = 0.6f),
            unfocusedBorderColor = colors.outline,
            focusedTextColor = colors.onBackground,
            unfocusedTextColor = colors.onBackground,
            focusedLabelColor = colors.primary,
            cursorColor = colors.primary,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DateRow(
    label: String,
    value: String?,
    placeholder: String,
    clearDescription: String,
    onClick: () -> Unit,
    onClear: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().heightIn(min = 58.dp).clickable(onClick = onClick).padding(start = 16.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, color = colors.onBackground, fontFamily = FontFamily.Serif, fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                value ?: placeholder,
                color = if (value != null) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = if (value != null) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
        if (value != null) {
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Close, clearDescription, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SacramentDateDialog(initial: LocalDate?, t: ScreenText, onDismiss: () -> Unit, onPick: (LocalDate) -> Unit) {
    val today = remember { LiturgicalCalendar.today() }
    val todayMs = today.toEpochDays() * DAY_MS
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial?.let { it.toEpochDays() * DAY_MS },
        yearRange = 1900..today.year,
        // No future dates: an anniversary needs a day that has already happened.
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayMs
            override fun isSelectableYear(year: Int) = year <= today.year
        },
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.selectedDateMillis != null,
                onClick = { state.selectedDateMillis?.let { onPick(LocalDate.fromEpochDays((it / DAY_MS).toInt())) } },
            ) { Text(t["screen.profile.ok"]) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(t["screen.profile.cancel"]) } },
    ) { DatePicker(state = state) }
}

/** Day and month only (the year is fixed to a leap year so 29 February can be picked). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthDayDialog(initial: String?, t: ScreenText, onDismiss: () -> Unit, onPick: (String) -> Unit) {
    val start = ProfileGreetings.parseMonthDay(initial)?.let { (m, d) -> LocalDate(2024, m, d) }
        ?: LiturgicalCalendar.today().let { LocalDate(2024, it.month.ordinal + 1, minOf(it.day, 28)) }
    val state = rememberDatePickerState(
        initialSelectedDateMillis = start.toEpochDays() * DAY_MS,
        yearRange = 2024..2024,
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.selectedDateMillis != null,
                onClick = {
                    state.selectedDateMillis?.let {
                        val d = LocalDate.fromEpochDays((it / DAY_MS).toInt())
                        onPick("${(d.month.ordinal + 1).toString().padStart(2, '0')}-${d.day.toString().padStart(2, '0')}")
                    }
                },
            ) { Text(t["screen.profile.ok"]) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(t["screen.profile.cancel"]) } },
    ) {
        DatePicker(
            state = state,
            showModeToggle = false,
            title = { Text(t["screen.profile.feast_manual"], modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)) },
            headline = {
                val ms = state.selectedDateMillis
                Text(
                    ms?.let { val d = LocalDate.fromEpochDays((it / DAY_MS).toInt()); t.monthDay("${d.month.ordinal + 1}-${d.day}") } ?: "",
                    fontSize = 26.sp,
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaintPickerSheet(
    repository: CathopediaRepository,
    language: String,
    t: ScreenText,
    onDismiss: () -> Unit,
    onPick: (ContentSummary) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var saints by remember { mutableStateOf<List<ContentSummary>?>(null) }
    var query by remember { mutableStateOf("") }
    LaunchedEffect(language) {
        saints = repository.listSaints(language).filter { ProfileGreetings.parseMonthDay(it.feastDay) != null }
    }
    val shown = remember(saints, query) {
        val q = query.trim().lowercase()
        saints.orEmpty().filter { q.isEmpty() || it.name.lowercase().contains(q) }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.surfaceContainerLow,
    ) {
        Column(Modifier.padding(horizontal = 18.dp)) {
            Text(t["screen.profile.choose_patron"], color = colors.onBackground, fontFamily = FontFamily.Serif, fontSize = 22.sp)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text(t["screen.profile.search_saints"], fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.primary) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceContainer,
                    unfocusedContainerColor = colors.surfaceContainer,
                    focusedBorderColor = colors.primary.copy(alpha = 0.55f),
                    unfocusedBorderColor = colors.outline,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground,
                    cursorColor = colors.primary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 560.dp)) {
                items(shown, key = { "${it.type}-${it.id}" }) { saint ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onPick(saint) }.padding(vertical = 11.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(colors.primary))
                        Spacer(Modifier.width(12.dp))
                        Text(saint.name, color = colors.onBackground, fontSize = 15.sp, modifier = Modifier.weight(1f))
                        Text(t.monthDay(saint.feastDay.orEmpty()), color = colors.primary, fontSize = 12.5.sp)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
