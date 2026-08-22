package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.rosary.RosarySequence
import com.ynotlabs.cathopedia.rosary.mysterySetForDate
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow

private enum class RosaryMode { GUIDED, MAP }

/**
 * Reachable via [com.ynotlabs.cathopedia.ui.navigation.Destination.RosaryScreen]
 * (including the `cathopedia://rosary` deep link mapping). Guided is the
 * default and primary mode — the canvas is Map mode, opened deliberately
 * rather than being the only way in.
 */
@Composable
fun RosaryScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current

    var mode by remember { mutableStateOf(RosaryMode.GUIDED) }
    var mysterySet by remember { mutableStateOf(mysterySetForDate(LiturgicalCalendar.today())) }
    var currentIndex by remember { mutableStateOf(0) }
    var organicShape by remember { mutableStateOf(true) }

    val sequence = remember(mysterySet) { RosarySequence(mysterySet) }

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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            MysterySetPicker(
                selected = mysterySet,
                onSelect = { newSet ->
                    mysterySet = newSet
                    currentIndex = 0
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when (mode) {
                RosaryMode.GUIDED -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(s.prayersComingSoon, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                RosaryMode.MAP -> RosaryMapContent(
                    currentIndex = sequence.steps.getOrNull(currentIndex)?.beadIndex ?: 0,
                    organicShape = organicShape,
                    onOrganicShapeChange = { organicShape = it },
                    onBeadTap = { bead ->
                        val stepIndex = sequence.steps.indexOfFirst { it.beadIndex == bead.index }
                        if (stepIndex >= 0) currentIndex = stepIndex
                    },
                )
            }
        }
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
    organicShape: Boolean,
    onOrganicShapeChange: (Boolean) -> Unit,
    onBeadTap: (RosaryBead) -> Unit,
) {
    val s = LocalStrings.current
    val seed = remember { LiturgicalCalendar.today().toEpochDays().toInt() }

    Column(modifier = Modifier.fillMaxSize()) {
        RosaryCanvas(
            currentIndex = currentIndex,
            onBeadTap = onBeadTap,
            seed = if (organicShape) seed else 0,
            organic = if (organicShape) 1f else 0f,
            modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(s.rosaryOrganicShape, modifier = Modifier.padding(end = 12.dp))
            Switch(checked = organicShape, onCheckedChange = onOrganicShapeChange)
        }
    }
}

private fun mysterySetLabel(set: MysterySet, s: Strings): String = when (set) {
    MysterySet.JOYFUL -> s.rosaryMysterySetJoyful
    MysterySet.SORROWFUL -> s.rosaryMysterySetSorrowful
    MysterySet.GLORIOUS -> s.rosaryMysterySetGlorious
    MysterySet.LUMINOUS -> s.rosaryMysterySetLuminous
}
