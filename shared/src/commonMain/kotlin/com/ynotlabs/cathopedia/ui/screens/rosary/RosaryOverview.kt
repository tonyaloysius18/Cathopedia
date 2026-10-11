package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ynotlabs.cathopedia.rosary.ROSARY_OVERVIEW_ASPECT
import com.ynotlabs.cathopedia.rosary.RosaryState
import com.ynotlabs.cathopedia.rosary.rosaryOverviewLayout
import com.ynotlabs.cathopedia.ui.components.RosaryComposition
import com.ynotlabs.cathopedia.ui.screens.common.fullScreenDialogProperties

@Composable
internal fun RosaryOverview(
    state: RosaryState,
    strings: Map<String, String>,
    currentPrayerTitle: String,
    onClose: () -> Unit,
) {
    val progress = strings[RosaryPrayingStringKeys.Progress].orEmpty()
        .replace("{current}", (state.currentStepIndex + 1).toString())
        .replace("{total}", state.steps.size.toString())
    Dialog(onDismissRequest = onClose, properties = fullScreenDialogProperties()) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().navigationBarsPadding()) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                    title = {
                        Text(
                            strings[RosaryStringKeys.Title].orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    actions = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Close, strings[RosaryPrayingStringKeys.Close].orEmpty())
                        }
                    },
                )
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        strings["rosary.mysteries.set.${state.mysterySet.tag}"].orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(progress, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        LinearProgressIndicator(
                            progress = { (state.currentStepIndex + 1).toFloat() / state.steps.size },
                            modifier = Modifier.weight(1f),
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        )
                    }
                }
                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                    val width = minOf(maxWidth, maxHeight * ROSARY_OVERVIEW_ASPECT, 420.dp)
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        RosaryComposition(
                            modifier = Modifier.width(width).semantics {
                                contentDescription = strings[RosaryStringKeys.DiagramDescription].orEmpty()
                            },
                            beads = rosaryOverviewLayout,
                            selectedBeadIndex = state.currentStep.beadIndex,
                            aspectRatio = ROSARY_OVERVIEW_ASPECT,
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        strings[RosaryPrayingStringKeys.OverviewCurrentPrayer].orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        currentPrayerTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Button(
                        onClick = onClose,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).heightIn(min = 52.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            strings[RosaryPrayingStringKeys.OverviewReturnToPrayer].orEmpty(),
                            modifier = Modifier.padding(start = 10.dp),
                        )
                    }
                }
            }
        }
    }
}
