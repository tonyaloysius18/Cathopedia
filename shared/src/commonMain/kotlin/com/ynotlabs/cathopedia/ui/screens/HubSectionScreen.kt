package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubSectionSummary

/**
 * Section shell: resolves and shows the section's own header (title/summary) while the
 * per-[layout][HubSectionSummary.layout] body renderers land in T5 (docs/briefs/topic-hubs.md).
 */
@Composable
fun HubSectionScreen(
    hubId: String,
    sectionId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    var section by remember(sectionId) { mutableStateOf<HubSectionSummary?>(null) }
    var strings by remember(sectionId) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(hubId, sectionId, language) {
        val found = repository.hubDetail(hubId)?.sections?.firstOrNull { it.id == sectionId } ?: return@LaunchedEffect
        section = found
        val keys = buildSet {
            add(found.titleKey)
            found.summaryKey?.let(::add)
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.back,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        section?.let { current ->
            Text(
                text = strings[current.titleKey].orEmpty(),
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )

            current.summaryKey?.let { key ->
                strings[key]?.let { summary ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = summary,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                    )
                }
            }
        }
    }
}
