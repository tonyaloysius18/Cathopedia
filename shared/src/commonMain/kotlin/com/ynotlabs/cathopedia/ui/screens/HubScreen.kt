package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubDetail
import com.ynotlabs.cathopedia.model.HubSectionSummary

/**
 * Hub → section list. The hub is data (see docs/briefs/topic-hubs.md) — nothing about a
 * specific hub is hardcoded here, every visible string is a `*_key` resolved through
 * [CathopediaRepository.resolveHubStrings]. STUB sections render dimmed and are not
 * clickable; only PUBLISHED sections navigate anywhere.
 */
@Composable
fun HubScreen(
    hubId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onSectionSelected: (HubSectionSummary) -> Unit,
) {
    val s = LocalStrings.current
    var detail by remember(hubId) { mutableStateOf<HubDetail?>(null) }
    var strings by remember(hubId) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(hubId, language) {
        val loaded = repository.hubDetail(hubId) ?: return@LaunchedEffect
        detail = loaded
        val keys = buildSet {
            add(loaded.summary.titleKey)
            loaded.summary.subtitleKey?.let(::add)
            loaded.introKey?.let(::add)
            loaded.sections.forEach { section ->
                add(section.titleKey)
                section.summaryKey?.let(::add)
            }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val hub = detail
    val accent = hub?.summary?.accentColor?.toComposeColorOrNull() ?: MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
    ) {
        item {
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
        }

        if (hub != null) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                    Text(
                        text = strings[hub.summary.titleKey].orEmpty(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.Serif,
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    hub.summary.subtitleKey?.let { key ->
                        strings[key]?.let { subtitle ->
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = subtitle,
                                color = accent,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    hub.introKey?.let { key ->
                        strings[key]?.let { intro ->
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = intro,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp,
                                lineHeight = 21.sp,
                            )
                        }
                    }
                }
            }

            items(hub.sections, key = { it.id }) { section ->
                HubSectionRow(
                    section = section,
                    title = strings[section.titleKey].orEmpty(),
                    summary = section.summaryKey?.let { strings[it] },
                    accent = accent,
                    onClick = { onSectionSelected(section) },
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun HubSectionRow(
    section: HubSectionSummary,
    title: String,
    summary: String?,
    accent: Color,
    onClick: () -> Unit,
) {
    val s = LocalStrings.current
    val isStub = section.status == "STUB"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isStub, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(if (isStub) MaterialTheme.colorScheme.outline else accent, CircleShape),
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isStub) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                )
                if (summary != null) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = summary,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (isStub) 0.5f else 1f),
                        fontSize = 12.sp,
                        maxLines = 2,
                    )
                }
                if (isStub) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = s.hubSectionComingSoon,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (!isStub) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

/** `#RRGGBB` or `#AARRGGBB` hex string → [Color]; null (never a crash) for anything else. */
private fun String.toComposeColorOrNull(): Color? {
    val hex = removePrefix("#")
    if (hex.length != 6 && hex.length != 8) return null
    val value = hex.toLongOrNull(16) ?: return null
    return if (hex.length == 6) Color(0xFF000000 or value) else Color(value)
}
