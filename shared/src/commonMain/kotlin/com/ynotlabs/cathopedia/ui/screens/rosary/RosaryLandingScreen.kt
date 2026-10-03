package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.MysterySummary
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.model.RosarySessionState
import com.ynotlabs.cathopedia.rosary.BeadKind
import com.ynotlabs.cathopedia.rosary.rosaryLayout
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.RosaryComposition
import com.ynotlabs.cathopedia.ui.components.RosarySpriteRenderer
import com.ynotlabs.cathopedia.ui.components.RosarySpriteUiModel
import com.ynotlabs.cathopedia.ui.components.beadSprite
import com.ynotlabs.cathopedia.ui.theme.CathopediaTheme
import com.ynotlabs.cathopedia.ui.theme.ThemeMode
import com.ynotlabs.cathopedia.ui.theme.rosaryColors
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.rosary_mysteries_glorious
import com.ynotlabs.cathopedia.resources.rosary_mysteries_joyful
import com.ynotlabs.cathopedia.resources.rosary_mysteries_luminous
import com.ynotlabs.cathopedia.resources.rosary_mysteries_sorrowful
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
    const val MysteriesFruit = "rosary.mysteries.fruit"

    val landing = setOf(
        Title,
        Subtitle,
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
) {
    fun text(key: String): String = strings[key].orEmpty()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = text(RosaryStringKeys.Title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    CathopediaBackButton(
                        onClick = onBack,
                        contentDescription = text(RosaryStringKeys.Back),
                    )
                },
            )
        },
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (maxWidth >= RosaryWideLayoutBreakpoint) {
                WideRosaryLanding(
                    strings = strings,
                    mysteriesBySet = mysteriesBySet,
                    resumeSession = resumeSession,
                    meter = meter,
                    onStart = onStart,
                    onResume = onResume,
                )
            } else {
                CompactRosaryLanding(
                    strings = strings,
                    mysteriesBySet = mysteriesBySet,
                    resumeSession = resumeSession,
                    meter = meter,
                    onStart = onStart,
                    onResume = onResume,
                )
            }
        }
    }
}

@Composable
private fun LandingSubtitle(strings: Map<String, String>, modifier: Modifier = Modifier) {
    Text(
        text = strings[RosaryStringKeys.Subtitle].orEmpty(),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Composable
private fun WideRosaryLanding(
    strings: Map<String, String>,
    mysteriesBySet: Map<MysterySet, List<MysterySummary>>,
    resumeSession: RosarySessionState?,
    meter: RosaryMeter,
    onStart: () -> Unit,
    onResume: (RosarySessionState) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 1200.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LandingSubtitle(strings)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1.08f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        resumeSession?.let { session ->
                            ResumeRosaryCard(
                                session = session,
                                strings = strings,
                                onResume = { onResume(session) },
                            )
                        }
                        RosaryMeterSummary(meter = meter, strings = strings)
                        Button(
                            onClick = onStart,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        ) {
                            Text(strings[RosaryStringKeys.Start].orEmpty())
                        }
                    }

                    RosaryHeroFrame(modifier = Modifier.weight(0.9f)) {
                        RosaryComposition(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = strings[RosaryStringKeys.DiagramDescription].orEmpty()
                                },
                        )
                    }

                    RosaryBeadLegend(
                        strings = strings,
                        modifier = Modifier.weight(0.62f),
                    )
                }
                RosaryMysteriesGallery(
                    strings = strings,
                    mysteriesBySet = mysteriesBySet,
                    wide = true,
                )
            }
        }
    }
}

@Composable
private fun CompactRosaryLanding(
    strings: Map<String, String>,
    mysteriesBySet: Map<MysterySet, List<MysterySummary>>,
    resumeSession: RosarySessionState?,
    meter: RosaryMeter,
    onStart: () -> Unit,
    onResume: (RosarySessionState) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { LandingSubtitle(strings, modifier = Modifier.fillMaxWidth()) }
        item {
            RosaryHeroFrame(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
            ) {
                RosaryComposition(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = strings[RosaryStringKeys.DiagramDescription].orEmpty()
                        },
                )
            }
        }
        item {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            ) {
                Text(strings[RosaryStringKeys.Start].orEmpty())
            }
        }
        resumeSession?.let { session ->
            item {
                ResumeRosaryCard(
                    session = session,
                    strings = strings,
                    onResume = { onResume(session) },
                )
            }
        }
        item {
            RosaryMysteriesGallery(
                strings = strings,
                mysteriesBySet = mysteriesBySet,
                wide = false,
            )
        }
        item { RosaryMeterSummary(meter = meter, strings = strings) }
    }
}

/**
 * Frames the hero ring in a soft gold vignette on a raised card, so the Rosary reads as a
 * treasured object rather than sitting bare on the screen background.
 */
@Composable
private fun RosaryHeroFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = rosaryColors()
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            0f to colors.candle.copy(alpha = 0.18f),
                            1f to colors.candle.copy(alpha = 0f),
                        ),
                        radius = size.maxDimension * 0.62f,
                        center = Offset(size.width / 2f, size.height * 0.4f),
                    )
                }
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

private val mysteryDisplayOrder = listOf(
    MysterySet.JOYFUL,
    MysterySet.SORROWFUL,
    MysterySet.LUMINOUS,
    MysterySet.GLORIOUS,
)

@Composable
private fun RosaryMysteriesGallery(
    strings: Map<String, String>,
    mysteriesBySet: Map<MysterySet, List<MysterySummary>>,
    wide: Boolean,
    modifier: Modifier = Modifier,
) {
    if (mysteriesBySet.values.all { it.isEmpty() }) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = strings[RosaryStringKeys.MysteriesTitle].orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = strings[RosaryStringKeys.MysteriesIntro].orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        if (wide) {
            mysteryDisplayOrder.chunked(2).forEach { rowSets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    rowSets.forEach { set ->
                        MysterySetCard(
                            set = set,
                            mysteries = mysteriesBySet[set].orEmpty(),
                            strings = strings,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        } else {
            mysteryDisplayOrder.forEach { set ->
                MysterySetCard(
                    set = set,
                    mysteries = mysteriesBySet[set].orEmpty(),
                    strings = strings,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun MysterySetCard(
    set: MysterySet,
    mysteries: List<MysterySummary>,
    strings: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    val title = strings["rosary.mysteries.set.${set.tag}"].orEmpty()
    val days = strings["rosary.mysteries.days.${set.tag}"].orEmpty()

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Image(
            painter = painterResource(mysterySetArtwork(set)),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = days,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            mysteries.sortedBy { it.sortOrder }.forEachIndexed { index, mystery ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "${index + 1}. ${mystery.title}",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    mystery.scriptureRef?.let { reference ->
                        Text(
                            text = reference,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = strings[RosaryStringKeys.MysteriesFruit].orEmpty()
                            .replace("{fruit}", mystery.fruit),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun mysterySetArtwork(set: MysterySet): DrawableResource = when (set) {
    MysterySet.JOYFUL -> Res.drawable.rosary_mysteries_joyful
    MysterySet.SORROWFUL -> Res.drawable.rosary_mysteries_sorrowful
    MysterySet.LUMINOUS -> Res.drawable.rosary_mysteries_luminous
    MysterySet.GLORIOUS -> Res.drawable.rosary_mysteries_glorious
}

@Composable
private fun RosaryBeadLegend(
    strings: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    val colors = rosaryColors()
    val items = listOf(
        BeadKind.CROSS to RosaryPrayingStringKeys.BeadCross,
        BeadKind.OUR_FATHER to RosaryPrayingStringKeys.BeadOurFather,
        BeadKind.HAIL_MARY to RosaryPrayingStringKeys.BeadHailMary,
        BeadKind.CENTERPIECE to RosaryPrayingStringKeys.BeadCenterpiece,
    )

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val railX = 40.dp.toPx()
                    drawLine(
                        color = colors.chain,
                        start = Offset(railX, 34.dp.toPx()),
                        end = Offset(railX, size.height - 34.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                    )
                }
                .padding(vertical = 8.dp),
        ) {
            items.forEach { (kind, labelKey) ->
                val bead = rosaryLayout.first { it.kind == kind }
                val displaySize = when (kind) {
                    BeadKind.CROSS -> 52.dp
                    BeadKind.OUR_FATHER -> 42.dp
                    BeadKind.HAIL_MARY -> 36.dp
                    BeadKind.CENTERPIECE -> 46.dp
                }
                val label = strings[labelKey].orEmpty()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 72.dp)
                        .padding(horizontal = 8.dp)
                        .clearAndSetSemantics { contentDescription = label },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RosarySpriteRenderer(
                        model = RosarySpriteUiModel(
                            sprite = beadSprite(bead),
                            displaySize = displaySize,
                            contentDescription = "",
                        ),
                        distanceFromCenter = 0f,
                        carouselOnLeft = false,
                        modifier = Modifier.size(64.dp),
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
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
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = text(RosaryStringKeys.ResumeTitle),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = progress,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f),
                modifier = Modifier.padding(top = 4.dp),
            )
            Button(onClick = onResume, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(text(RosaryStringKeys.ResumeAction))
            }
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
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text(RosaryStringKeys.MeterTitle), style = MaterialTheme.typography.titleMedium)
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
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = text(RosaryStringKeys.MeterRecent),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                recentDays.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { day ->
                            val count = countsByDay[day] ?: 0
                            val intensity = when {
                                count <= 0 -> 0.08f
                                count == 1 -> 0.24f
                                count == 2 -> 0.4f
                                else -> 0.58f
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(3.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = intensity))
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
                )
                MysterySet.entries.forEach { set ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(mysterySetName(set, strings), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = (meter.byMysterySet[set] ?: 0).toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MeterValue(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    RosaryStringKeys.DiagramDescription to "Complete Rosary",
    RosaryStringKeys.Start to "Start Rosary",
    RosaryStringKeys.Back to "Back",
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

@Preview(name = "Rosary landing — tablet", widthDp = 768, heightDp = 1024, showBackground = true)
@Composable
private fun RosaryLandingTabletPreview() {
    CathopediaTheme(themeMode = ThemeMode.DARK) {
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
