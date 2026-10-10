package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.border
import com.ynotlabs.cathopedia.ui.theme.RosaryMarianPanel
import com.ynotlabs.cathopedia.ui.theme.RosaryMarianCard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.RosarySessionRepository
import com.ynotlabs.cathopedia.model.MysteryDetail
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.PrayerDetail
import com.ynotlabs.cathopedia.model.RosarySessionState
import com.ynotlabs.cathopedia.rosary.RosarySequence
import com.ynotlabs.cathopedia.rosary.RosaryState
import com.ynotlabs.cathopedia.ui.components.DarkSystemBars
import com.ynotlabs.cathopedia.ui.components.PrayerBodyText
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.launch

internal object RosaryPrayingStringKeys {
    const val Next = "rosary.praying.next"
    const val Previous = "rosary.praying.previous"
    const val Finish = "rosary.praying.finish"
    const val SwipeHint = "rosary.praying.swipe_hint"
    const val Loading = "rosary.praying.loading"
    const val ViewRosary = "rosary.praying.view_rosary"
    const val SharedBead = "rosary.praying.shared_bead"
    const val BeadProgress = "rosary.praying.bead_progress"
    const val Close = "rosary.praying.close"
    const val Progress = "rosary.praying.progress"
    const val Decade = "rosary.praying.decade"
    const val DecadeProgress = "rosary.praying.decade_progress"
    const val TapHint = "rosary.praying.tap_hint"
    const val BackHint = "rosary.praying.back_hint"
    const val TextUnavailable = "rosary.praying.text_unavailable"
    const val MysteryFruit = "rosary.praying.mystery_fruit"
    const val CarouselDescription = "rosary.praying.carousel_description"
    const val BeadCross = "rosary.praying.bead.cross"
    const val BeadOurFather = "rosary.praying.bead.our_father"
    const val BeadHailMary = "rosary.praying.bead.hail_mary"
    const val BeadCenterpiece = "rosary.praying.bead.centerpiece"
    const val OverviewCurrentPrayer = "rosary.overview.current_prayer"
    const val OverviewReturnToPrayer = "rosary.overview.return_to_prayer"

    val all = setOf(
        Next,
        Previous,
        Finish,
        SwipeHint,
        Loading,
        ViewRosary,
        SharedBead,
        BeadProgress,
        RosaryStringKeys.Title,
        Close,
        Progress,
        Decade,
        DecadeProgress,
        TapHint,
        BackHint,
        TextUnavailable,
        MysteryFruit,
        CarouselDescription,
        BeadCross,
        BeadOurFather,
        BeadHailMary,
        BeadCenterpiece,
        OverviewCurrentPrayer,
        OverviewReturnToPrayer,
        "rosary.mysteries.set.joyful",
        "rosary.mysteries.set.sorrowful",
        "rosary.mysteries.set.glorious",
        "rosary.mysteries.set.luminous",
        RosaryStringKeys.DiagramDescription,
    )
}

private data class ResolvedRosaryPrayer(
    val stepIndex: Int,
    val prayer: PrayerDetail?,
    val mystery: MysteryDetail?,
)

@OptIn(ExperimentalTime::class)
@Composable
internal fun RosaryPrayingScreen(
    repository: CathopediaRepository,
    sessionRepository: RosarySessionRepository,
    language: String,
    session: RosarySessionState,
    carouselOnLeft: Boolean,
    onClose: () -> Unit,
    onCompleted: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val sequence = remember(session.mysterySet) { RosarySequence(session.mysterySet) }
    var state by remember(session.id) {
        mutableStateOf(
            RosaryState(
                sessionId = session.id,
                mysterySet = session.mysterySet,
                currentStepIndex = session.currentStepIndex.coerceIn(0, sequence.steps.lastIndex),
                steps = sequence.steps,
            ),
        )
    }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var resolved by remember(session.id, language) { mutableStateOf<ResolvedRosaryPrayer?>(null) }
    var completing by remember(session.id) { mutableStateOf(false) }
    var showOverview by remember(session.id) { mutableStateOf(false) }
    val currentContent = resolved?.takeIf { it.stepIndex == state.currentStepIndex }

    LaunchedEffect(language) {
        strings = repository.resolveHubStrings(
            RosaryPrayingStringKeys.all + MysterySet.entries.map { "rosary.mystery.${it.tag}" },
            language,
        )
    }
    LaunchedEffect(state.currentStepIndex, language) {
        resolved = null
        val current = state
        val slug = current.currentStep.prayerSlug
        val prayer = if (slug == RosarySequence.ANNOUNCE_MYSTERY) null else repository.prayerDetail(slug, language)
        val mystery = (current.currentStep.mysteryId ?: current.currentDecade?.let { "${current.mysterySet.tag}-$it" })
            ?.let { repository.mysteryDetail(it, language) }
        resolved = ResolvedRosaryPrayer(current.currentStepIndex, prayer, mystery)
    }
    LaunchedEffect(state.currentStepIndex) {
        val elapsed = ((Clock.System.now().toEpochMilliseconds() - session.startedAt) / 1000).coerceAtLeast(0)
        sessionRepository.saveProgress(
            id = state.sessionId,
            currentStepIndex = state.currentStepIndex,
            decadesCompleted = state.decadesCompleted,
            durationSeconds = elapsed,
        )
    }

    fun advance() {
        if (completing) return
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        val next = state.advance()
        if (next != null) {
            state = next
        } else {
            completing = true
            scope.launch {
                try {
                    val elapsed = ((Clock.System.now().toEpochMilliseconds() - session.startedAt) / 1000).coerceAtLeast(0)
                    sessionRepository.complete(
                        id = state.sessionId,
                        currentStepIndex = state.steps.size,
                        decadesCompleted = 5,
                        durationSeconds = elapsed,
                    )
                    repository.recordPrayerRecited("holy-rosary")
                    onCompleted()
                } finally {
                    completing = false
                }
            }
        }
    }

    DarkSystemBars()
    // The Marian night palette of the Prayers screen's Rosary card, scoped to this screen.
    // The bead carousel reads background for its edge fades, so it follows automatically.
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = RosaryMarianCard.night,
            onBackground = RosaryMarianCard.cream,
            surface = RosaryMarianCard.night,
            onSurface = RosaryMarianCard.cream,
            onSurfaceVariant = RosaryMarianCard.muted,
            primary = RosaryMarianCard.gold,
            onPrimary = RosaryMarianCard.deep,
        ),
    ) {
    Scaffold(
        containerColor = RosaryMarianCard.night,
        topBar = {
            RosaryPrayingHeader(strings, state, currentContent?.mystery, onClose = { if (!completing) onClose() })
        },
        bottomBar = {
            RosaryPrayerControls(
                strings = strings,
                first = state.currentStepIndex == 0,
                last = state.currentStepIndex == state.steps.lastIndex,
                enabled = !completing,
                onAdvance = ::advance,
                onViewRosary = { showOverview = true },
                onBack = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    state = state.back()
                },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LinearProgressIndicator(
                progress = { (state.currentStepIndex + 1f) / state.steps.size },
                color = RosaryMarianCard.gold,
                trackColor = RosaryMarianCard.goldSoft.copy(alpha = 0.22f),
                gapSize = 0.dp,
                drawStopIndicator = {},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(3.dp).clip(CircleShape),
            )
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 12.dp),
            ) {
                val rail: @Composable () -> Unit = {
                    RosaryBeadCarousel(
                        state = state,
                        strings = strings,
                        enabled = !completing,
                        onStateSelected = { selected ->
                            if (!completing && selected.currentStepIndex != state.currentStepIndex) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                state = selected
                            }
                        },
                        modifier = Modifier.width(92.dp).fillMaxHeight(),
                    )
                }
                if (carouselOnLeft) rail()
                PrayerPane(
                    content = PrayerPaneContent(state, currentContent),
                    strings = strings,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                if (!carouselOnLeft) rail()
            }
        }
    }
    if (showOverview) {
        RosaryOverview(
            state = state,
            strings = strings,
            currentPrayerTitle = currentContent?.prayer?.title ?: strings[RosaryPrayingStringKeys.Loading].orEmpty(),
            onClose = { showOverview = false },
        )
    }
    }
}

@Composable
private fun RosaryPrayingHeader(
    strings: Map<String, String>,
    state: RosaryState,
    mystery: MysteryDetail?,
    onClose: () -> Unit,
) {
    val decade = state.currentDecade
    val detail = strings[if (decade != null) RosaryPrayingStringKeys.DecadeProgress else RosaryPrayingStringKeys.Progress]
        .orEmpty()
        .replace("{ordinal}", decade?.toString().orEmpty())
        .replace("{current}", (state.currentStepIndex + 1).toString())
        .replace("{total}", state.steps.size.toString())
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = RosaryMarianCard.night),
        title = {
            Column {
                Text(
                    mystery?.title ?: mysterySetName(state.mysterySet, strings),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    color = RosaryMarianCard.gold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(detail, style = MaterialTheme.typography.labelMedium, color = RosaryMarianCard.muted)
            }
        },
        actions = {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = strings[RosaryPrayingStringKeys.Close].orEmpty(),
                    tint = RosaryMarianCard.cream,
                )
            }
        },
    )
}

private data class PrayerPaneContent(val state: RosaryState, val resolved: ResolvedRosaryPrayer?)

@Composable
private fun PrayerPane(
    content: PrayerPaneContent,
    strings: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = content,
        label = "rosaryPrayer",
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
    ) { page ->
        key(page.state.currentStepIndex) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 16.dp),
            ) {
              RosaryMarianPanel(padding = 20.dp) {
               Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val prayer = page.resolved?.prayer
                val mystery = page.resolved?.mystery
                val sharedSteps = page.state.currentBeadPrayerSteps
                val beadNumber = page.state.currentNode?.indexInDecade
                if (beadNumber != null) {
                    Text(
                        strings[RosaryPrayingStringKeys.BeadProgress].orEmpty()
                            .replace("{current}", beadNumber.toString()).replace("{total}", "10"),
                        style = MaterialTheme.typography.labelLarge,
                        color = RosaryMarianCard.gold,
                    )
                }
                if (sharedSteps.first != sharedSteps.last) {
                    Text(
                        strings[RosaryPrayingStringKeys.SharedBead].orEmpty()
                            .replace("{current}", (page.state.currentStepIndex - sharedSteps.first + 1).toString())
                            .replace("{total}", (sharedSteps.last - sharedSteps.first + 1).toString()),
                        style = MaterialTheme.typography.labelLarge,
                        color = RosaryMarianCard.gold,
                    )
                }
                val announcing = page.state.currentStep.prayerSlug == RosarySequence.ANNOUNCE_MYSTERY
                if (!announcing && page.state.currentStep.mysteryId != null && mystery != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                        border = BorderStroke(1.dp, RosaryMarianCard.gold.copy(alpha = 0.45f)),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                mystery.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Serif,
                                color = RosaryMarianCard.gold,
                            )
                            mystery.scriptureRef?.let {
                                Text(it, style = MaterialTheme.typography.bodyMedium, color = RosaryMarianCard.muted)
                            }
                            Text(
                                strings[RosaryPrayingStringKeys.MysteryFruit].orEmpty().replace("{fruit}", mystery.fruit),
                                style = MaterialTheme.typography.bodyMedium,
                                color = RosaryMarianCard.cream,
                            )
                        }
                    }
                }
                when {
                    announcing && page.resolved != null -> {
                        // The medal at the start of the loop: announce the first mystery.
                        Text(
                            strings[RosaryPrayingStringKeys.Decade].orEmpty()
                                .replace("{ordinal}", page.state.currentDecade?.toString().orEmpty()),
                            style = MaterialTheme.typography.labelLarge,
                            color = RosaryMarianCard.gold,
                        )
                        Text(
                            mystery?.title.orEmpty(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            color = RosaryMarianCard.cream,
                        )
                        Box(
                            Modifier.width(40.dp).height(2.dp).clip(CircleShape)
                                .background(RosaryMarianCard.gold.copy(alpha = 0.7f)),
                        )
                        mystery?.scriptureRef?.let {
                            Text(it, style = MaterialTheme.typography.titleSmall, color = RosaryMarianCard.muted)
                        }
                        mystery?.let {
                            Text(
                                strings[RosaryPrayingStringKeys.MysteryFruit].orEmpty().replace("{fruit}", it.fruit),
                                style = MaterialTheme.typography.bodyLarge,
                                color = RosaryMarianCard.gold,
                            )
                        }
                        mystery?.meditation?.takeIf { it.isNotBlank() }?.let {
                            PrayerBodyText(bodyMd = it, color = RosaryMarianCard.cream)
                        }
                    }
                    page.resolved == null -> {
                        Text(
                            strings[RosaryPrayingStringKeys.Loading].orEmpty(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    else -> {
                        Text(
                            prayer?.title.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            color = RosaryMarianCard.cream,
                        )
                        Box(
                            Modifier.width(40.dp).height(2.dp).clip(CircleShape)
                                .background(RosaryMarianCard.gold.copy(alpha = 0.7f)),
                        )
                        val body = prayer?.bodyMd
                        if (!body.isNullOrBlank()) {
                            PrayerBodyText(bodyMd = body, color = RosaryMarianCard.cream)
                        } else {
                            Text(
                                strings[RosaryPrayingStringKeys.TextUnavailable].orEmpty(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
               }
              }
            }
        }
    }
}

@Composable
private fun RosaryPrayerControls(
    strings: Map<String, String>,
    first: Boolean,
    last: Boolean,
    enabled: Boolean,
    onAdvance: () -> Unit,
    onBack: () -> Unit,
    onViewRosary: () -> Unit,
) {
    Surface(color = RosaryMarianCard.nightRaised) {
        Column(
            Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    strings[RosaryPrayingStringKeys.SwipeHint].orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = RosaryMarianCard.muted,
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = onViewRosary,
                    enabled = enabled,
                    colors = ButtonDefaults.textButtonColors(contentColor = RosaryMarianCard.gold),
                ) {
                    Text(strings[RosaryPrayingStringKeys.ViewRosary].orEmpty())
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onBack,
                    enabled = enabled && !first,
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(
                        1.5.dp,
                        RosaryMarianCard.goldSoft.copy(alpha = if (enabled && !first) 0.8f else 0.3f),
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = RosaryMarianCard.cream,
                        disabledContentColor = RosaryMarianCard.cream.copy(alpha = 0.35f),
                    ),
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                ) {
                    Text(strings[RosaryPrayingStringKeys.Previous].orEmpty())
                }
                val pill = RoundedCornerShape(26.dp)
                Button(
                    onClick = onAdvance,
                    enabled = enabled,
                    shape = pill,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = RosaryMarianCard.deep,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = RosaryMarianCard.deep.copy(alpha = 0.6f),
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier.weight(1.5f)
                        .clip(pill)
                        .background(RosaryMarianCard.goldMetal)
                        .border(1.dp, RosaryMarianCard.goldRim.copy(alpha = 0.7f), pill)
                        .heightIn(min = 52.dp),
                ) {
                    Text(strings[if (last) RosaryPrayingStringKeys.Finish else RosaryPrayingStringKeys.Next].orEmpty())
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
