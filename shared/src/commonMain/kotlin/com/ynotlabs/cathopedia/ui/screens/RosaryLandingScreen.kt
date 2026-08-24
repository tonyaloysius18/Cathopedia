package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.model.RosarySessionState
import com.ynotlabs.cathopedia.ui.components.RosaryComposition

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
    )
}

@Composable
internal fun RosaryLandingScreen(
    strings: Map<String, String>,
    resumeSession: RosarySessionState?,
    meter: RosaryMeter,
    onStart: () -> Unit,
    onResume: (RosarySessionState) -> Unit,
    onBack: () -> Unit,
) {
    fun text(key: String): String = strings[key].orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text(RosaryStringKeys.Title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = text(RosaryStringKeys.Back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = text(RosaryStringKeys.Subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    RosaryComposition(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .semantics {
                                contentDescription = text(RosaryStringKeys.DiagramDescription)
                            },
                    )
                    Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    ) {
                        Text(text(RosaryStringKeys.Start))
                    }
                }
            }

            resumeSession?.let { session ->
                item {
                    ResumeRosaryCard(
                        session = session,
                        strings = strings,
                        onResume = { onResume(session) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            item {
                RosaryMeterSummary(
                    meter = meter,
                    strings = strings,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item { androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 12.dp)) }
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

@Composable
private fun RosaryMeterSummary(
    meter: RosaryMeter,
    strings: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    fun text(key: String): String = strings[key].orEmpty()
    Card(modifier = modifier.fillMaxWidth()) {
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
