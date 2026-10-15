package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A row of selectable filter chips — the selected one gets a solid fill, matching the mockups. */
@Composable
fun <T> FilterChipsRow(
    options: List<Pair<String, T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (label, value) ->
            val isSelected = value == selected
            AssistChip(
                onClick = { onSelect(value) },
                label = { Text(label, maxLines = 1, softWrap = false) },
                colors = if (isSelected) {
                    AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        labelColor = MaterialTheme.colorScheme.background,
                    )
                } else {
                    AssistChipDefaults.assistChipColors()
                },
            )
        }
    }
}
