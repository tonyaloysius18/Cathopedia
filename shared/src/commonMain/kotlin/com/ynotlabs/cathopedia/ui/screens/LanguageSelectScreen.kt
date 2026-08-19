package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class LanguageOption(val code: String, val label: String, val available: Boolean)

private val LANGUAGE_OPTIONS = listOf(
    LanguageOption("en", "English", available = true),
    LanguageOption("fr", "Français", available = true),
    LanguageOption("es", "Español", available = false),
    LanguageOption("it", "Italiano", available = false),
)

/** First run, and later re-opened from Settings to change the language. */
@Composable
fun LanguageSelectScreen(currentLanguage: String = "en", onContinue: (languageCode: String) -> Unit) {
    var selected by remember { mutableStateOf(currentLanguage) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "STEP 1 OF 2",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Choose your language",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )
            Text(
                "You can change this later in Settings.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp),
            )

            LANGUAGE_OPTIONS.forEach { option ->
                val isSelected = option.code == selected
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(6.dp),
                        )
                        .then(
                            if (option.available) {
                                Modifier.clickable { selected = option.code }
                            } else {
                                Modifier
                            },
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        option.label,
                        modifier = Modifier.weight(1f),
                        color = if (option.available) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                    Text(
                        when {
                            isSelected -> "✓"
                            !option.available -> "soon"
                            else -> ""
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Button(
                onClick = { onContinue(selected) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Continue")
            }
        }
    }
}
