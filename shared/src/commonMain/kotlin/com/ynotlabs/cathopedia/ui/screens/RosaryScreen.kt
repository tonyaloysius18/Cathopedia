package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.RosarySessionRepository
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.model.RosarySessionState
import com.ynotlabs.cathopedia.rosary.mysterySetForDate
import kotlinx.coroutines.launch

/** Landing, mystery selection, praying mode, and exact-session resume for the Rosary feature. */
@Composable
fun RosaryScreen(
    repository: CathopediaRepository,
    sessionRepository: RosarySessionRepository,
    carouselOnLeft: Boolean,
    language: String,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var showLanding by remember { mutableStateOf(true) }
    var resumeSession by remember { mutableStateOf<RosarySessionState?>(null) }
    var activeSession by remember { mutableStateOf<RosarySessionState?>(null) }
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
                activeSession = session
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
                        activeSession = sessionRepository.session(id)
                        showLanding = false
                    }
                },
                onDismiss = { showMysteryDialog = false },
            )
        }
    } else {
        activeSession?.let { session ->
            RosaryPrayingScreen(
                repository = repository,
                sessionRepository = sessionRepository,
                language = language,
                session = session,
                carouselOnLeft = carouselOnLeft,
                onClose = { showLanding = true },
                onCompleted = {
                    activeSession = null
                    showLanding = true
                },
            )
        }
    }
}
