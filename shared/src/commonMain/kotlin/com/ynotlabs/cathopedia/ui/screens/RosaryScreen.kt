package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.PreferenceKeys
import com.ynotlabs.cathopedia.data.RosarySessionRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.model.MysteryDetail
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.PrayerDetail
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.model.RosarySessionState
import com.ynotlabs.cathopedia.rosary.RosarySequence
import com.ynotlabs.cathopedia.rosary.mysterySetForDate
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow
import com.ynotlabs.cathopedia.ui.components.InteractiveDiagram
import com.ynotlabs.cathopedia.ui.components.PrayerBodyText
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.theme.rosaryColors
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.launch

private enum class RosaryMode { GUIDED, MAP }

/** An interrupted Rosary offers to resume only within this window; otherwise it's treated as stale and starts fresh. */
private const val RESUME_WINDOW_MS = 4 * 60 * 60 * 1000L

/**
 * Reachable via [com.ynotlabs.cathopedia.ui.navigation.Destination.RosaryScreen]
 * (including the `cathopedia://rosary` deep link mapping). Guided is the
 * default and primary mode — the canvas is Map mode, opened deliberately
 * rather than being the only way in.
 */
@OptIn(ExperimentalTime::class)
@Composable
fun RosaryScreen(
    repository: CathopediaRepository,
    sessionRepository: RosarySessionRepository,
    language: String,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var showLanding by remember { mutableStateOf(true) }
    var resumeSession by remember { mutableStateOf<RosarySessionState?>(null) }
    var requestedResume by remember { mutableStateOf<RosarySessionState?>(null) }
    val todayMysterySet = remember { mysterySetForDate(LiturgicalCalendar.today()) }
    var selectedMysterySet by remember { mutableStateOf(todayMysterySet) }
    var showMysteryDialog by remember { mutableStateOf(false) }
    var meter by remember {
        mutableStateOf(
            RosaryMeter(
                totalCompleted = 0,
                completedThisMonth = 0,
                byMysterySet = emptyMap(),
                completionTimestamps = emptyList(),
            ),
        )
    }
    var localized by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(showLanding, language) {
        if (showLanding) {
            sessionRepository.abandonStale()
            resumeSession = sessionRepository.resumeInProgress()
            meter = sessionRepository.meter()
            localized = repository.resolveHubStrings(
                RosaryStringKeys.landing + MysteryDialogStringKeys.all +
                    MysterySet.entries.map { "rosary.mystery.${it.tag}" },
                language,
            )
        }
    }

    if (showLanding) {
        RosaryLandingScreen(
            strings = localized,
            resumeSession = resumeSession,
            meter = meter,
            onStart = {
                selectedMysterySet = todayMysterySet
                showMysteryDialog = true
            },
            onResume = { session ->
                requestedResume = session
                showLanding = false
            },
            onBack = onBack,
        )
        if (showMysteryDialog) {
            RosaryMysterySelectionDialog(
                strings = localized,
                todaySet = todayMysterySet,
                selected = selectedMysterySet,
                onSelect = { selectedMysterySet = it },
                onConfirm = {
                    showMysteryDialog = false
                    scope.launch {
                        val id = sessionRepository.startSession(selectedMysterySet)
                        requestedResume = sessionRepository.session(id)
                        showLanding = false
                    }
                },
                onDismiss = { showMysteryDialog = false },
            )
        }
    } else {
        LegacyRosaryScreen(
            repository = repository,
            language = language,
            initialResume = requestedResume,
            onBack = { showLanding = true },
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun LegacyRosaryScreen(
    repository: CathopediaRepository,
    language: String,
    initialResume: RosarySessionState?,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(RosaryMode.GUIDED) }
    var mysterySet by remember(initialResume) {
        mutableStateOf(initialResume?.mysterySet ?: mysterySetForDate(LiturgicalCalendar.today()))
    }
    var currentIndex by remember(initialResume) { mutableStateOf(initialResume?.currentStepIndex ?: 0) }
    var resumePrompt by remember { mutableStateOf<ResumeState?>(null) }
    var sessionReady by remember(initialResume) { mutableStateOf(initialResume != null) }

    val sequence = remember(mysterySet) { RosarySequence(mysterySet) }

    fun persistSession(index: Int, set: MysterySet, startedAt: Long) {
        repository.setPreference(PreferenceKeys.ROSARY_CURRENT_INDEX, index.toString())
        repository.setPreference(PreferenceKeys.ROSARY_MYSTERY_SET, set.tag)
        repository.setPreference(PreferenceKeys.ROSARY_SESSION_STARTED_AT, startedAt.toString())
    }

    // Load any saved session once, deciding whether to offer resume vs. just
    // starting fresh — see the resume-window comment above.
    LaunchedEffect(Unit) {
        if (initialResume != null) return@LaunchedEffect
        val savedSet = repository.getPreference(PreferenceKeys.ROSARY_MYSTERY_SET)?.let { MysterySet.fromTag(it) }
        val savedIndex = repository.getPreference(PreferenceKeys.ROSARY_CURRENT_INDEX)?.toIntOrNull()
        val savedStartedAt = repository.getPreference(PreferenceKeys.ROSARY_SESSION_STARTED_AT)?.toLongOrNull()
        val now = Clock.System.now().toEpochMilliseconds()

        val hasResumableSession = savedSet != null && savedIndex != null && savedStartedAt != null &&
            savedIndex > 0 && savedIndex < RosarySequence(savedSet).steps.size &&
            (now - savedStartedAt) < RESUME_WINDOW_MS

        if (hasResumableSession) {
            resumePrompt = ResumeState(savedSet!!, savedIndex!!)
        } else {
            val freshSet = mysterySetForDate(LiturgicalCalendar.today())
            mysterySet = freshSet
            currentIndex = 0
            persistSession(0, freshSet, now)
            sessionReady = true
        }
    }

    resumePrompt?.let { pending ->
        ResumeDialog(
            onResume = {
                mysterySet = pending.mysterySet
                currentIndex = pending.currentIndex
                resumePrompt = null
                sessionReady = true
            },
            onStartOver = {
                val freshSet = mysterySetForDate(LiturgicalCalendar.today())
                mysterySet = freshSet
                currentIndex = 0
                persistSession(0, freshSet, Clock.System.now().toEpochMilliseconds())
                resumePrompt = null
                sessionReady = true
            },
        )
    }

    fun advance() {
        val next = currentIndex + 1
        currentIndex = next
        persistSession(next, mysterySet, repository.getPreference(PreferenceKeys.ROSARY_SESSION_STARTED_AT)?.toLongOrNull() ?: Clock.System.now().toEpochMilliseconds())
        if (next >= sequence.steps.size) {
            scope.launch { repository.recordPrayerRecited("holy-rosary") }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.rosaryScreenTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", fontSize = 22.sp) }
                },
                actions = {
                    IconButton(onClick = { mode = RosaryMode.GUIDED }) {
                        Icon(
                            Icons.Filled.List,
                            contentDescription = s.rosaryModeGuided,
                            tint = if (mode == RosaryMode.GUIDED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = { mode = RosaryMode.MAP }) {
                        Icon(
                            Icons.Filled.Map,
                            contentDescription = s.rosaryModeMap,
                            tint = if (mode == RosaryMode.MAP) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (!sessionReady) return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MysterySetPicker(
                selected = mysterySet,
                onSelect = { newSet ->
                    mysterySet = newSet
                    currentIndex = 0
                    persistSession(0, newSet, Clock.System.now().toEpochMilliseconds())
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when (mode) {
                RosaryMode.GUIDED -> GuidedRosaryContent(
                    repository = repository,
                    language = language,
                    sequence = sequence,
                    mysterySet = mysterySet,
                    currentIndex = currentIndex,
                    onAdvance = ::advance,
                    modifier = Modifier.weight(1f),
                )

                RosaryMode.MAP -> RosaryMapContent(
                    currentIndex = sequence.steps.getOrNull(currentIndex)?.beadIndex ?: 0,
                    onBeadTap = { beadIndex ->
                        val stepIndex = sequence.steps.indexOfFirst { it.beadIndex == beadIndex }
                        if (stepIndex >= 0) {
                            currentIndex = stepIndex
                            persistSession(stepIndex, mysterySet, repository.getPreference(PreferenceKeys.ROSARY_SESSION_STARTED_AT)?.toLongOrNull() ?: Clock.System.now().toEpochMilliseconds())
                        }
                    },
                )
            }
        }
    }
}

private data class ResumeState(val mysterySet: MysterySet, val currentIndex: Int)

@Composable
private fun ResumeDialog(onResume: () -> Unit, onStartOver: () -> Unit) {
    val s = LocalStrings.current
    AlertDialog(
        onDismissRequest = onStartOver,
        title = { Text(s.rosaryResumeTitle) },
        text = { Text(s.rosaryResumeMessage) },
        confirmButton = { TextButton(onClick = onResume) { Text(s.rosaryResumeButton) } },
        dismissButton = { TextButton(onClick = onStartOver) { Text(s.rosaryStartOverButton) } },
    )
}

@Composable
private fun GuidedRosaryContent(
    repository: CathopediaRepository,
    language: String,
    sequence: RosarySequence,
    mysterySet: MysterySet,
    currentIndex: Int,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val haptics = LocalHapticFeedback.current
    val steps = sequence.steps

    if (currentIndex >= steps.size) {
        FinishedContent(modifier = modifier)
        return
    }

    val step = steps[currentIndex]
    var prayer by remember(step.ordinal, language) { mutableStateOf<PrayerDetail?>(null) }
    var mystery by remember(step.ordinal, language) { mutableStateOf<MysteryDetail?>(null) }
    var isLoading by remember(step.ordinal) { mutableStateOf(true) }

    LaunchedEffect(step.ordinal, language) {
        isLoading = true
        prayer = repository.prayerDetail(step.prayerSlug, language)
        mystery = step.mysteryId?.let { repository.mysteryDetail(it, language) }
        isLoading = false
    }

    val progress = (currentIndex + 1).toFloat() / steps.size

    Column(
        modifier = modifier
            .fillMaxSize()
            // A single tap-anywhere target with a screen-reader hint via
            // onClickLabel ("double tap to <label>" is TalkBack/VoiceOver's
            // own standard phrasing) — children stay individually readable
            // since this isn't cleared/merged into one opaque node.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = s.rosaryTapToAdvanceHint,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAdvance()
                },
            )
            // Announces the new content to TalkBack/VoiceOver whenever the
            // step changes, without requiring an explicit user action first.
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(24.dp),
    ) {
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())

        Text(
            text = s.rosaryProgressLabel.replace("{current}", "${currentIndex + 1}").replace("{total}", "${steps.size}"),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            mystery?.let { m ->
                MysteryAnnouncementCard(
                    mystery = m,
                    decadeOrdinal = step.mysteryId?.let { decadeNumber(it) },
                    mysterySet = mysterySet,
                    modifier = Modifier.padding(bottom = 20.dp),
                )
            }

            if (!isLoading) {
                val body = prayer?.bodyMd
                if (!body.isNullOrBlank()) {
                    PrayerBodyText(bodyMd = body, color = MaterialTheme.colorScheme.onSurface)
                } else {
                    Text(
                        text = s.prayerTextNotYetAvailable,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun MysteryAnnouncementCard(
    mystery: MysteryDetail,
    decadeOrdinal: Int?,
    mysterySet: MysterySet,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (decadeOrdinal != null) {
                Text(
                    text = s.rosaryMysteryAnnouncement
                        .replace("{ordinal}", ordinalLabel(decadeOrdinal, s))
                        .replace("{set}", mysterySetLabel(mysterySet, s)),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                )
            }
            Text(
                text = mystery.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 4.dp),
            )
            if (mystery.scriptureRef != null) {
                Text(
                    text = mystery.scriptureRef,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = "${s.rosaryMysteryFruit}: ${mystery.fruit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp),
            )
            // meditation is absent until it's written separately (content/README.md) —
            // shown only once supplied, per the brief.
            mystery.meditation?.let { meditation ->
                Text(
                    text = meditation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun FinishedContent(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(s.rosaryFinishedTitle, style = MaterialTheme.typography.headlineMedium)
        Text(
            s.rosaryFinishedMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun MysterySetPicker(
    selected: MysterySet,
    onSelect: (MysterySet) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    FilterChipsRow(
        options = MysterySet.entries.map { set -> mysterySetLabel(set, s) to set },
        selected = selected,
        onSelect = onSelect,
        modifier = modifier,
    )
}

@Composable
private fun RosaryMapContent(
    currentIndex: Int,
    onBeadTap: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InteractiveDiagram(
            painter = hubAssetPainter("rosary_marian.png") ?: ColorPainter(rosaryColors().vespers),
            aspectRatio = 600f / 900f,
            hotspots = rosaryBeadHotspots,
            highlightedId = currentIndex.toString(),
            showHotspotHints = false,
            showDetailSheet = false,
            minZoom = 1f,
            maxZoom = 3.5f,
            onHotspotTap = { hotspot -> hotspot.id.toIntOrNull()?.let(onBeadTap) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun decadeNumber(mysteryId: String): Int? = mysteryId.substringAfterLast("-").toIntOrNull()

private fun ordinalLabel(n: Int, s: Strings): String = when (n) {
    1 -> s.rosaryOrdinalFirst
    2 -> s.rosaryOrdinalSecond
    3 -> s.rosaryOrdinalThird
    4 -> s.rosaryOrdinalFourth
    5 -> s.rosaryOrdinalFifth
    else -> n.toString()
}

private fun mysterySetLabel(set: MysterySet, s: Strings): String = when (set) {
    MysterySet.JOYFUL -> s.rosaryMysterySetJoyful
    MysterySet.SORROWFUL -> s.rosaryMysterySetSorrowful
    MysterySet.GLORIOUS -> s.rosaryMysterySetGlorious
    MysterySet.LUMINOUS -> s.rosaryMysterySetLuminous
}
