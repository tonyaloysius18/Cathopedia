package com.ynotlabs.cathopedia.ui.screens.rosary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.model.MysterySet

internal object MysteryDialogStringKeys {
    const val Title = "rosary.mystery_dialog.title"
    const val Message = "rosary.mystery_dialog.message"
    const val Today = "rosary.mystery_dialog.today"
    const val Cancel = "rosary.mystery_dialog.cancel"
    const val Begin = "rosary.mystery_dialog.begin"

    val all = setOf(Title, Message, Today, Cancel, Begin)
}

@Composable
internal fun RosaryMysterySelectionDialog(
    strings: Map<String, String>,
    todaySet: MysterySet,
    selected: MysterySet,
    onSelect: (MysterySet) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    fun text(key: String): String = strings[key].orEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text(MysteryDialogStringKeys.Title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text(MysteryDialogStringKeys.Message), modifier = Modifier.padding(bottom = 8.dp))
                Column(Modifier.selectableGroup()) {
                    MysterySet.entries.forEach { set ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selected == set,
                                    onClick = { onSelect(set) },
                                    role = Role.RadioButton,
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = selected == set, onClick = null)
                            Text(
                                text = mysterySetName(set, strings),
                                modifier = Modifier.weight(1f).padding(start = 12.dp),
                            )
                            if (set == todaySet) {
                                Text(text(MysteryDialogStringKeys.Today))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(text(MysteryDialogStringKeys.Begin)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text(MysteryDialogStringKeys.Cancel)) }
        },
    )
}
