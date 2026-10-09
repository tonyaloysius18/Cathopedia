package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ynotlabs.cathopedia.rosary.RosaryState
import com.ynotlabs.cathopedia.ui.components.RosaryComposition
import com.ynotlabs.cathopedia.ui.screens.common.fullScreenDialogProperties

@Composable
internal fun RosaryOverview(state: RosaryState, strings: Map<String, String>, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose, properties = fullScreenDialogProperties()) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().navigationBarsPadding()) {
                TopAppBar(
                    title = { Text(strings[RosaryStringKeys.Title].orEmpty()) },
                    actions = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Filled.Close, strings[RosaryPrayingStringKeys.Close].orEmpty())
                        }
                    },
                )
                Text(
                    strings[RosaryPrayingStringKeys.Progress].orEmpty()
                        .replace("{current}", (state.currentStepIndex + 1).toString())
                        .replace("{total}", state.steps.size.toString()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(20.dp)) {
                    val width = minOf(maxWidth, maxHeight * (2f / 3f), 460.dp)
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        RosaryComposition(
                            modifier = Modifier.width(width),
                            selectedBeadIndex = state.currentStep.beadIndex,
                        )
                    }
                }
            }
        }
    }
}
