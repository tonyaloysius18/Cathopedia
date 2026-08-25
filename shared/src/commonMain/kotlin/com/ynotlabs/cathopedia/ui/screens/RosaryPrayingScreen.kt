package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.RosarySessionRepository
import com.ynotlabs.cathopedia.model.MysteryDetail
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.PrayerDetail
import com.ynotlabs.cathopedia.model.RosarySessionState
import com.ynotlabs.cathopedia.rosary.BeadKind
import com.ynotlabs.cathopedia.rosary.RosaryBead
import com.ynotlabs.cathopedia.rosary.RosarySequence
import com.ynotlabs.cathopedia.rosary.RosaryState
import com.ynotlabs.cathopedia.rosary.rosaryLayout
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.rosary_spacer_gold
import com.ynotlabs.cathopedia.ui.components.PrayerBodyText
import com.ynotlabs.cathopedia.ui.components.RosarySpriteRenderer
import com.ynotlabs.cathopedia.ui.components.RosarySpriteUiModel
import com.ynotlabs.cathopedia.ui.components.beadSprite
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

internal object RosaryPrayingStringKeys {
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

    val all = setOf(
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
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalTime::class)
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
    val sequence = remember(session.mysterySet) { RosarySequence(session.mysterySet) }
    var state by remember(session.id) {
        mutableStateOf(
            RosaryState(
                sessionId = session.id,
                mysterySet = session.mysterySet,
                currentStepIndex = session.currentStepIndex.coerceIn(0, sequence.steps.lastIndex),
            ),
        )
    }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var prayer by remember { mutableStateOf<PrayerDetail?>(null) }
    var mystery by remember { mutableStateOf<MysteryDetail?>(null) }
    val currentDecade = state.currentNode?.decade

    LaunchedEffect(language) {
        strings = repository.resolveHubStrings(
            RosaryPrayingStringKeys.all + MysterySet.entries.map { "rosary.mystery.${it.tag}" },
            language,
        )
    }

    LaunchedEffect(state.currentStepIndex, language) {
        prayer = null
        mystery = null
        prayer = repository.prayerDetail(state.currentStep.prayerSlug, language)
        mystery = currentDecade?.let { repository.mysteryDetail("${state.mysterySet.tag}-$it", language) }
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

    fun completeRosary() {
        scope.launch {
            val elapsed = ((Clock.System.now().toEpochMilliseconds() - session.startedAt) / 1000).coerceAtLeast(0)
            sessionRepository.complete(
                id = state.sessionId,
                currentStepIndex = state.steps.size,
                decadesCompleted = 5,
                durationSeconds = elapsed,
            )
            repository.recordPrayerRecited("holy-rosary")
            onCompleted()
        }
    }

    fun advance() {
        val next = state.advance()
        if (next == null) completeRosary() else state = next
    }

    Scaffold(
        topBar = {
            RosaryPrayingHeader(
                strings = strings,
                state = state,
                mystery = mystery,
                onClose = onClose,
            )
        },
    ) { padding ->
        Row(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (carouselOnLeft) {
                RosaryCarousel(
                    state = state,
                    strings = strings,
                    carouselOnLeft = true,
                    onNodeSelected = { state = state.jumpToNode(it) },
                )
            }
            PrayerPane(
                state = state,
                strings = strings,
                prayer = prayer,
                mystery = mystery,
                onAdvance = ::advance,
                onBack = { state = state.back() },
                modifier = Modifier.weight(1f),
            )
            if (!carouselOnLeft) {
                RosaryCarousel(
                    state = state,
                    strings = strings,
                    carouselOnLeft = false,
                    onNodeSelected = { state = state.jumpToNode(it) },
                )
            }
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
    fun text(key: String): String = strings[key].orEmpty()
    val decade = state.currentNode?.decade
    val title = mystery?.title ?: mysterySetName(state.mysterySet, strings)
    val detail = if (decade != null) {
        text(RosaryPrayingStringKeys.DecadeProgress)
            .replace("{ordinal}", decade.toString())
            .replace("{current}", (state.currentStepIndex + 1).toString())
            .replace("{total}", state.steps.size.toString())
    } else {
        text(RosaryPrayingStringKeys.Progress)
            .replace("{current}", (state.currentStepIndex + 1).toString())
            .replace("{total}", state.steps.size.toString())
    }

    TopAppBar(
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(detail, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        actions = {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = text(RosaryPrayingStringKeys.Close))
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PrayerPane(
    state: RosaryState,
    strings: Map<String, String>,
    prayer: PrayerDetail?,
    mystery: MysteryDetail?,
    onAdvance: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    fun text(key: String): String = strings[key].orEmpty()
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxHeight()
            .combinedClickable(
                onClickLabel = text(RosaryPrayingStringKeys.TapHint),
                onLongClickLabel = text(RosaryPrayingStringKeys.BackHint),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAdvance()
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBack()
                },
            )
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Crossfade(targetState = state.currentStepIndex, label = "rosaryPrayer") {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LinearProgressIndicator(
                    progress = { (state.currentStepIndex + 1f) / state.steps.size },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = text(RosaryPrayingStringKeys.Progress)
                        .replace("{current}", (state.currentStepIndex + 1).toString())
                        .replace("{total}", state.steps.size.toString()),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (state.currentStep.mysteryId != null && mystery != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(mystery.title, style = MaterialTheme.typography.titleMedium)
                            mystery.scriptureRef?.let {
                                Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                            }
                            Text(
                                text = text(RosaryPrayingStringKeys.MysteryFruit).replace("{fruit}", mystery.fruit),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                }

                Text(prayer?.title.orEmpty(), style = MaterialTheme.typography.headlineSmall)
                val body = prayer?.bodyMd
                if (!body.isNullOrBlank()) {
                    PrayerBodyText(bodyMd = body, color = MaterialTheme.colorScheme.onSurface)
                } else if (prayer != null) {
                    Text(
                        text = text(RosaryPrayingStringKeys.TextUnavailable),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(96.dp))
            }
        }
    }
}

@Composable
private fun RosaryCarousel(
    state: RosaryState,
    strings: Map<String, String>,
    carouselOnLeft: Boolean,
    onNodeSelected: (Int) -> Unit,
) {
    val currentNodeIndex = state.currentNode?.index ?: 0
    val listState = rememberLazyListState()

    LaunchedEffect(currentNodeIndex) {
        listState.animateScrollToItem(currentNodeIndex)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling ->
                if (!scrolling) {
                    val layout = listState.layoutInfo
                    val center = (layout.viewportStartOffset + layout.viewportEndOffset) / 2
                    layout.visibleItemsInfo
                        .minByOrNull { abs((it.offset + it.size / 2) - center) }
                        ?.index
                        ?.let { index -> rosaryLayout.getOrNull(index)?.index }
                        ?.let(onNodeSelected)
                }
            }
    }

    BoxWithConstraints(
        modifier = Modifier
            .width(112.dp)
            .fillMaxHeight()
            .semantics {
                contentDescription = strings[RosaryPrayingStringKeys.CarouselDescription].orEmpty()
            },
    ) {
        val verticalPadding = ((maxHeight - 68.dp) / 2).coerceAtLeast(0.dp)
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = verticalPadding),
            flingBehavior = rememberSnapFlingBehavior(listState),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(rosaryLayout, key = { _, bead -> bead.index }) { index, bead ->
                CarouselSlot(
                    listState = listState,
                    index = index,
                    bead = bead,
                    current = bead.index == currentNodeIndex,
                    prayed = index < currentNodeIndex,
                    carouselOnLeft = carouselOnLeft,
                    contentDescription = beadDescription(bead, strings),
                    showSpacer = index < rosaryLayout.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun CarouselSlot(
    listState: LazyListState,
    index: Int,
    bead: RosaryBead,
    current: Boolean,
    prayed: Boolean,
    carouselOnLeft: Boolean,
    contentDescription: String,
    showSpacer: Boolean,
) {
    val distance by remember(listState, index) {
        derivedStateOf {
            val layout = listState.layoutInfo
            val info = layout.visibleItemsInfo.firstOrNull { it.index == index } ?: return@derivedStateOf 4f
            val viewportCenter = (layout.viewportStartOffset + layout.viewportEndOffset) / 2f
            ((info.offset + info.size / 2f) - viewportCenter) / info.size.coerceAtLeast(1)
        }
    }
    val beadSize = when (bead.kind) {
        BeadKind.HAIL_MARY -> 44.dp
        BeadKind.OUR_FATHER -> 52.dp
        BeadKind.CENTERPIECE -> 60.dp
        BeadKind.CROSS -> 72.dp
    }

    Box(
        modifier = Modifier.height(68.dp).fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        RosarySpriteRenderer(
            model = RosarySpriteUiModel(
                sprite = beadSprite(bead),
                displaySize = beadSize,
                contentDescription = contentDescription,
                isCurrent = current,
                isPrayed = prayed,
            ),
            distanceFromCenter = distance,
            carouselOnLeft = carouselOnLeft,
            modifier = Modifier.fillMaxSize(),
        )

        if (showSpacer) {
            Image(
                painter = painterResource(Res.drawable.rosary_spacer_gold),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomCenter).size(14.dp),
            )
        }
    }
}

private fun beadDescription(bead: RosaryBead, strings: Map<String, String>): String = strings[
    when (bead.kind) {
        BeadKind.CROSS -> RosaryPrayingStringKeys.BeadCross
        BeadKind.OUR_FATHER -> RosaryPrayingStringKeys.BeadOurFather
        BeadKind.HAIL_MARY -> RosaryPrayingStringKeys.BeadHailMary
        BeadKind.CENTERPIECE -> RosaryPrayingStringKeys.BeadCenterpiece
    },
].orEmpty()
