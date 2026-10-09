package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.foundation.BorderStroke
import com.ynotlabs.cathopedia.ui.theme.RosaryMarianCard
import com.ynotlabs.cathopedia.ui.theme.RosaryMarianPanel
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.MysterySummary
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.model.RosarySessionState
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.rosary_landing_single_decade_marian
import com.ynotlabs.cathopedia.resources.rosary_mysteries_glorious
import com.ynotlabs.cathopedia.resources.rosary_mysteries_joyful
import com.ynotlabs.cathopedia.resources.rosary_mysteries_luminous
import com.ynotlabs.cathopedia.resources.rosary_mysteries_sorrowful
import com.ynotlabs.cathopedia.ui.theme.CathopediaTheme
import com.ynotlabs.cathopedia.ui.theme.RosaryTheme
import com.ynotlabs.cathopedia.ui.theme.ThemeMode
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

internal object RosaryStringKeys {
    const val Title = "rosary.title"
    const val Subtitle = "rosary.landing.subtitle"
    const val Heading = "rosary.landing.heading"
    const val Hint = "rosary.landing.hint"
    const val ResumeLabel = "rosary.landing.resume"
    const val DiagramDescription = "rosary.landing.diagram_description"
    const val Start = "rosary.start"
    const val Back = "rosary.back"
    const val ResumeTitle = "rosary.resume.title"
    const val ResumeProgress = "rosary.resume.progress"
    const val ResumeAction = "rosary.resume.action"
    const val MeterTitle = "rosary.meter.title"
    const val MeterTotal = "rosary.meter.total"
    const val MeterMonth = "rosary.meter.this_month"
    const val MeterRecent = "rosary.meter.recent"
    const val MeterBreakdown = "rosary.meter.breakdown"
    const val MeterDayDescription = "rosary.meter.day_description"
    const val MysteriesTitle = "rosary.mysteries.title"
    const val MysteriesIntro = "rosary.mysteries.intro"
    const val MysteryDetails = "rosary.mysteries.details"
    const val HideMysteryDetails = "rosary.mysteries.hide_details"
    const val MysteriesFruit = "rosary.mysteries.fruit"

    val landing = setOf(
        Title,
        Subtitle,
        Heading,
        Hint,
        ResumeLabel,
        DiagramDescription,
        Start,
        Back,
        ResumeTitle,
        ResumeProgress,
        ResumeAction,
        MeterTitle,
        MeterTotal,
        MeterMonth,
        MeterRecent,
        MeterBreakdown,
        MeterDayDescription,
        MysteriesTitle,
        MysteriesIntro,
        MysteriesFruit,
        MysteryDetails,
        HideMysteryDetails,
        MysteryDialogStringKeys.Today,
        *MysterySet.entries.map { "rosary.mysteries.set.${it.tag}" }.toTypedArray(),
        *MysterySet.entries.map { "rosary.mysteries.days.${it.tag}" }.toTypedArray(),
        RosaryPrayingStringKeys.BeadCross,
        RosaryPrayingStringKeys.BeadOurFather,
        RosaryPrayingStringKeys.BeadHailMary,
        RosaryPrayingStringKeys.BeadCenterpiece,
    )
}

internal val RosaryWideLayoutBreakpoint = 720.dp

@Composable
internal fun RosaryLandingScreen(
    strings: Map<String, String>,
    mysteriesBySet: Map<MysterySet, List<MysterySummary>>,
    resumeSession: RosarySessionState?,
    meter: RosaryMeter,
    onStart: () -> Unit,
    onResume: (RosarySessionState) -> Unit,
    onBack: () -> Unit,
    todaySet: MysterySet = MysterySet.JOYFUL,
    onMysterySelected: (MysterySet) -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        strings[RosaryStringKeys.Title].orEmpty(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings[RosaryStringKeys.Back].orEmpty(),
                        )
                    }
                },
            )
        },
    ) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
            val wide = maxWidth >= RosaryWideLayoutBreakpoint
            val columns = if (maxWidth < 360.dp || LocalDensity.current.fontScale > 1.3f) 1 else 2
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item {
                    if (wide) {
                        Row(
                            modifier = Modifier.widthIn(max = 1200.dp).fillMaxWidth().padding(28.dp),
                            horizontalArrangement = Arrangement.spacedBy(28.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(0.9f),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                RosaryLandingHero(strings, todaySet, onStart)
                                resumeSession?.let { session ->
                                    ResumeRosaryCard(session, strings, onResume = { onResume(session) })
                                }
                                RosaryMeterSummary(meter, strings)
                            }
                            RosaryMysteriesGallery(
                                strings, mysteriesBySet, todaySet, columns, onMysterySelected,
                                modifier = Modifier.weight(1.1f),
                            )
                        }
                    } else {
                        RosaryLandingHero(
                            strings, todaySet, onStart,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp),
                        )
                    }
                }
                if (!wide) {
                    item {
                        RosaryMysteriesGallery(
                            strings, mysteriesBySet, todaySet, columns, onMysterySelected,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }
                    resumeSession?.let { session ->
                        item {
                            ResumeRosaryCard(
                                session, strings, onResume = { onResume(session) },
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                    }
                    item { RosaryMeterSummary(meter, strings, Modifier.padding(horizontal = 20.dp)) }
                }
            }
        }
    }
}

/** The compact split introduction chosen in the Option 1 design preview. */
@Composable
private fun RosaryLandingHero(
    strings: Map<String, String>,
    todaySet: MysterySet,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Same royal blue, gold and cream as the Holy Rosary card on the Prayers screen.
    val shape = RoundedCornerShape(24.dp)
    BoxWithConstraints(
        modifier.fillMaxWidth()
            .clip(shape)
            .background(RosaryMarianCard.gradient)
            .border(2.dp, RosaryMarianCard.goldSoft.copy(alpha = 0.52f), shape)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        val compact = maxWidth < 320.dp || LocalDensity.current.fontScale > 1.3f
        val artWidth = if (compact) 88.dp else 108.dp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "${strings[MysteryDialogStringKeys.Today].orEmpty()} · ${mysterySetName(todaySet, strings).replaceFirstChar { it.titlecase() }}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = RosaryMarianCard.gold,
                )
                Text(
                    text = strings[RosaryStringKeys.Heading].orEmpty(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = if (compact) 26.sp else 28.sp,
                        lineHeight = 32.sp,
                    ),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = RosaryMarianCard.cream,
                )
                Text(
                    text = strings[RosaryStringKeys.Hint].orEmpty(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 22.sp),
                    color = RosaryMarianCard.cream.copy(alpha = 0.9f),
                )
                OutlinedButton(
                    onClick = onStart,
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(2.dp, RosaryMarianCard.gold.copy(alpha = 0.78f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RosaryMarianCard.gold),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                ) {
                    Text(strings[MysteryDialogStringKeys.Begin].orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
            // Crop the empty side margins of the square PNG at display time.
            // The complete, narrow rosary remains inside this portrait frame.
            Image(
                painter = painterResource(Res.drawable.rosary_landing_single_decade_marian),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(artWidth).height(if (compact) 196.dp else 208.dp)
                    .clipToBounds()
                    .clickable(role = Role.Button, onClick = onStart)
                    .semantics { contentDescription = strings[RosaryStringKeys.Start].orEmpty() },
            )
        }
    }
}

private val mysteryDisplayOrder = listOf(
    MysterySet.JOYFUL,
    MysterySet.SORROWFUL,
    MysterySet.GLORIOUS,
    MysterySet.LUMINOUS,
)

@Composable
private fun RosaryMysteriesGallery(
    strings: Map<String, String>,
    mysteriesBySet: Map<MysterySet, List<MysterySummary>>,
    todaySet: MysterySet,
    columns: Int,
    onMysterySelected: (MysterySet) -> Unit,
    modifier: Modifier = Modifier,
) {
    var detailsTag by rememberSaveable { mutableStateOf<String?>(null) }
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val imageHeight = if (columns == 1) 180.dp else (maxWidth / 3.3f).coerceIn(104.dp, 144.dp)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                strings[RosaryStringKeys.MysteriesTitle].orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            mysteryDisplayOrder.chunked(columns).forEach { sets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    sets.forEach { set ->
                        MysterySetCard(
                            set = set, strings = strings, today = set == todaySet,
                            imageHeight = imageHeight,
                            onSelect = { detailsTag = set.tag },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
    MysterySet.entries.firstOrNull { it.tag == detailsTag }?.let { set ->
        MysteryDetailsSheet(
            set = set,
            mysteries = mysteriesBySet[set].orEmpty(),
            strings = strings,
            onStart = {
                detailsTag = null
                onMysterySelected(set)
            },
            onDismiss = { detailsTag = null },
        )
    }
}

@Composable
private fun MysterySetCard(
    set: MysterySet,
    strings: Map<String, String>,
    today: Boolean,
    imageHeight: androidx.compose.ui.unit.Dp,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = strings["rosary.mysteries.set.${set.tag}"].orEmpty()
    val shape = RoundedCornerShape(16.dp)
    Card(
        onClick = onSelect,
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            if (today) 2.dp else 1.dp,
            if (today) RosaryMarianCard.gold.copy(alpha = 0.78f) else RosaryMarianCard.goldSoft.copy(alpha = 0.52f),
        ),
    ) {
      Column(Modifier.background(RosaryMarianCard.verticalGradient)) {
        Box {
            Image(
                painter = painterResource(mysterySetArtwork(set)),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(imageHeight),
            )
            if (today) {
                Surface(
                    color = RosaryMarianCard.gold,
                    contentColor = RosaryMarianCard.deep,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        strings[MysteryDialogStringKeys.Today].orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    mysterySetName(set, strings).replaceFirstChar { it.titlecase() },
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = RosaryMarianCard.gold,
                )
                Text(
                    strings["rosary.mysteries.days.${set.tag}"].orEmpty(),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = RosaryMarianCard.cream.copy(alpha = 0.85f),
                    modifier = Modifier.heightIn(min = 36.dp),
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = RosaryMarianCard.gold,
            )
        }
      }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MysteryDetailsSheet(
    set: MysterySet,
    mysteries: List<MysterySummary>,
    strings: Map<String, String>,
    onStart: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text(
                strings["rosary.mysteries.set.${set.tag}"].orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                strings["rosary.mysteries.days.${set.tag}"].orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(mysteries.sortedBy { it.sortOrder }, key = { _, mystery -> mystery.id }) { index, mystery ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "${index + 1}. ${mystery.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        mystery.scriptureRef?.let { reference ->
                            Text(reference, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            strings[RosaryStringKeys.MysteriesFruit].orEmpty().replace("{fruit}", mystery.fruit),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp).heightIn(min = 52.dp),
            ) {
                Text(strings[RosaryStringKeys.Start].orEmpty(), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

private fun mysterySetArtwork(set: MysterySet): DrawableResource = when (set) {
    MysterySet.JOYFUL -> Res.drawable.rosary_mysteries_joyful
    MysterySet.SORROWFUL -> Res.drawable.rosary_mysteries_sorrowful
    MysterySet.GLORIOUS -> Res.drawable.rosary_mysteries_glorious
    MysterySet.LUMINOUS -> Res.drawable.rosary_mysteries_luminous
}

@Composable
private fun ResumeRosaryCard(
    session: RosarySessionState,
    strings: Map<String, String>,
    onResume: () -> Unit,
    modifier: Modifier = Modifier,
) {
    fun text(key: String): String = strings[key].orEmpty()
    val progress = text(RosaryStringKeys.ResumeProgress)
        .replace("{step}", (session.currentStepIndex + 1).toString())
        .replace("{set}", mysterySetName(session.mysterySet, strings))

    Card(
        onClick = onResume,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, RosaryMarianCard.goldSoft.copy(alpha = 0.52f)),
    ) {
        Row(
            modifier = Modifier.background(RosaryMarianCard.gradient).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = RosaryMarianCard.gold)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = text(RosaryStringKeys.ResumeLabel),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = RosaryMarianCard.cream,
                )
                Text(
                    text = progress,
                    style = MaterialTheme.typography.bodySmall,
                    color = RosaryMarianCard.cream.copy(alpha = 0.8f),
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = RosaryMarianCard.gold,
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun RosaryMeterSummary(
    meter: RosaryMeter,
    strings: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    fun text(key: String): String = strings[key].orEmpty()
    val zone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(zone).date
    val countsByDay = rosaryCompletionCountsByDay(meter.completionTimestamps, zone)
    val recentDays = (34 downTo 0).map { daysAgo ->
        LocalDate.fromEpochDays(today.toEpochDays() - daysAgo)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RosaryMarianPanel {
            Text(
                text(RosaryStringKeys.MeterTitle),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                color = RosaryMarianCard.gold,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MeterValue(
                    value = meter.totalCompleted.toString(),
                    label = text(RosaryStringKeys.MeterTotal),
                    modifier = Modifier.weight(1f),
                )
                MeterValue(
                    value = meter.completedThisMonth.toString(),
                    label = text(RosaryStringKeys.MeterMonth),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        RosaryMarianPanel {
            Text(
                text = text(RosaryStringKeys.MeterRecent),
                style = MaterialTheme.typography.labelLarge,
                color = RosaryMarianCard.muted,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            recentDays.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        val count = countsByDay[day] ?: 0
                        val intensity = when {
                            count <= 0 -> 0f
                            count == 1 -> 0.45f
                            count == 2 -> 0.7f
                            else -> 0.95f
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.07f))
                                .background(RosaryMarianCard.gold.copy(alpha = intensity))
                                .then(
                                    if (day == today) {
                                        Modifier.border(1.5.dp, RosaryMarianCard.gold, RoundedCornerShape(6.dp))
                                    } else {
                                        Modifier
                                    },
                                )
                                .semantics {
                                    contentDescription = text(RosaryStringKeys.MeterDayDescription)
                                        .replace("{date}", day.toString())
                                        .replace("{count}", count.toString())
                                },
                        )
                    }
                }
            }

            Text(
                text = text(RosaryStringKeys.MeterBreakdown),
                style = MaterialTheme.typography.labelLarge,
                color = RosaryMarianCard.muted,
                modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
            )
            MysterySet.entries.forEach { set ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        mysterySetName(set, strings),
                        style = MaterialTheme.typography.bodyMedium,
                        color = RosaryMarianCard.cream,
                    )
                    Text(
                        text = (meter.byMysterySet[set] ?: 0).toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = RosaryMarianCard.gold,
                    )
                }
            }
        }
    }
}

@Composable
private fun MeterValue(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            color = RosaryMarianCard.gold,
        )
        Text(label, style = MaterialTheme.typography.labelMedium, color = RosaryMarianCard.cream.copy(alpha = 0.85f))
    }
}

internal fun mysterySetName(set: MysterySet, strings: Map<String, String>): String =
    strings["rosary.mystery.${set.tag}"].orEmpty()

@OptIn(ExperimentalTime::class)
internal fun rosaryCompletionCountsByDay(
    timestamps: List<Long>,
    timeZone: TimeZone,
): Map<LocalDate, Int> = timestamps
    .map { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date }
    .groupingBy { it }
    .eachCount()

private val rosaryLandingPreviewStrings = mapOf(
    RosaryStringKeys.Title to "Holy Rosary",
    RosaryStringKeys.Subtitle to "Pray the mysteries with a bead-by-bead guide.",
    RosaryStringKeys.Heading to "A moment\nfor prayer.",
    RosaryStringKeys.Hint to "Follow each bead.",
    RosaryStringKeys.ResumeLabel to "Resume my prayer",
    RosaryStringKeys.DiagramDescription to "Complete Rosary",
    RosaryStringKeys.Start to "Start Rosary",
    RosaryStringKeys.Back to "Back",
    MysteryDialogStringKeys.Today to "Today",
    MysteryDialogStringKeys.Begin to "Begin",
    RosaryStringKeys.MysteriesTitle to "Mysteries of the Holy Rosary",
    RosaryStringKeys.MysteriesIntro to "Twenty Gospel moments contemplated with Mary.",
    RosaryStringKeys.MysteryDetails to "View mysteries",
    RosaryStringKeys.HideMysteryDetails to "Hide mysteries",
    RosaryStringKeys.MeterTitle to "Your Rosaries",
    RosaryStringKeys.MeterTotal to "Completed",
    RosaryStringKeys.MeterMonth to "This month",
    RosaryStringKeys.MeterRecent to "Recent days",
    RosaryStringKeys.MeterBreakdown to "By mystery set",
    RosaryStringKeys.MeterDayDescription to "{date}: {count} Rosaries completed",
    RosaryPrayingStringKeys.BeadCross to "Cross",
    RosaryPrayingStringKeys.BeadOurFather to "Our Father bead",
    RosaryPrayingStringKeys.BeadHailMary to "Hail Mary bead",
    RosaryPrayingStringKeys.BeadCenterpiece to "Centerpiece",
    "rosary.mystery.joyful" to "Joyful",
    "rosary.mystery.sorrowful" to "Sorrowful",
    "rosary.mystery.glorious" to "Glorious",
    "rosary.mystery.luminous" to "Luminous",
    "rosary.mysteries.set.joyful" to "The Joyful Mysteries",
    "rosary.mysteries.set.sorrowful" to "The Sorrowful Mysteries",
    "rosary.mysteries.set.glorious" to "The Glorious Mysteries",
    "rosary.mysteries.set.luminous" to "The Luminous Mysteries",
    "rosary.mysteries.days.joyful" to "Monday & Saturday",
    "rosary.mysteries.days.sorrowful" to "Tuesday & Friday",
    "rosary.mysteries.days.glorious" to "Wednesday & Sunday",
    "rosary.mysteries.days.luminous" to "Thursday",
)

private val rosaryLandingPreviewMeter = RosaryMeter(
    totalCompleted = 12,
    completedThisMonth = 4,
    byMysterySet = mapOf(
        MysterySet.JOYFUL to 4,
        MysterySet.SORROWFUL to 2,
        MysterySet.GLORIOUS to 3,
        MysterySet.LUMINOUS to 3,
    ),
    completionTimestamps = emptyList(),
)

@Preview(name = "Rosary landing — phone", widthDp = 390, heightDp = 844, showBackground = true)
@Composable
private fun RosaryLandingPhonePreview() {
    CathopediaTheme(themeMode = ThemeMode.LIGHT) {
        RosaryTheme {
            RosaryLandingScreen(
                strings = rosaryLandingPreviewStrings,
                mysteriesBySet = emptyMap(),
                resumeSession = null,
                meter = rosaryLandingPreviewMeter,
                onStart = {},
                onResume = {},
                onBack = {},
            )
        }
    }
}

@Preview(name = "Rosary landing — tablet", widthDp = 768, heightDp = 1024, showBackground = true)
@Composable
private fun RosaryLandingTabletPreview() {
    CathopediaTheme(themeMode = ThemeMode.DARK) {
        RosaryTheme {
            RosaryLandingScreen(
                strings = rosaryLandingPreviewStrings,
                mysteriesBySet = emptyMap(),
                resumeSession = null,
                meter = rosaryLandingPreviewMeter,
                onStart = {},
                onResume = {},
                onBack = {},
            )
        }
    }
}
