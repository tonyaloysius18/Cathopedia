package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.PreferenceKeys
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.PrayerDetail
import com.ynotlabs.cathopedia.ui.components.KeepScreenOn
import com.ynotlabs.cathopedia.ui.components.PrayerBodyText
import kotlinx.coroutines.launch

private const val HOLY_ROSARY_ID = "holy-rosary"
private val FONT_SCALE_STEPS = listOf(0.85f, 1.0f, 1.15f, 1.3f, 1.45f)
private const val DEFAULT_FONT_SCALE_INDEX = 1

/**
 * Reachable via [com.ynotlabs.cathopedia.ui.navigation.Destination.PrayerDetail]
 * (including the `cathopedia://prayer/{slug}` deep link mapping). [language]
 * seeds the reading language but the in-screen toggle can diverge from it —
 * someone reading the app in English may still want the Latin text.
 */
@Composable
fun PrayerDetailScreen(
    slug: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()

    var readingLanguage by remember(slug) { mutableStateOf(language) }
    var detail by remember(slug) { mutableStateOf<PrayerDetail?>(null) }
    var isFavorite by remember(slug) { mutableStateOf(false) }
    var showSource by remember(slug) { mutableStateOf(false) }
    var keepScreenOn by remember(slug) { mutableStateOf(false) }
    var fontScaleIndex by remember(slug) { mutableStateOf(DEFAULT_FONT_SCALE_INDEX) }

    LaunchedEffect(slug, language) {
        detail = repository.prayerDetail(slug, language)
        readingLanguage = language
        isFavorite = repository.isPrayerFavorite(slug)
        fontScaleIndex = FONT_SCALE_STEPS.indexOf(
            repository.getPreference(PreferenceKeys.PRAYER_FONT_SCALE)?.toFloatOrNull() ?: FONT_SCALE_STEPS[DEFAULT_FONT_SCALE_INDEX],
        ).takeIf { it >= 0 } ?: DEFAULT_FONT_SCALE_INDEX
        repository.recordPrayerRecited(slug)
    }

    LaunchedEffect(slug, readingLanguage) {
        if (readingLanguage != language) {
            detail = repository.prayerDetail(slug, readingLanguage)
        }
    }

    KeepScreenOn(enabled = keepScreenOn)

    val current = detail
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(current?.title ?: s.prayerDetailTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", fontSize = 22.sp) }
                },
                actions = {
                    if (current != null && !(current.isSequence && current.id != HOLY_ROSARY_ID)) {
                        IconButton(
                            onClick = {
                                scope.launch { isFavorite = repository.togglePrayerFavorite(slug) }
                            },
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) s.detailRemoveFromFavorites else s.detailSaveToFavorites,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        when {
            current == null -> Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(s.loading, modifier = Modifier.padding(24.dp))
            }
            current.isSequence && current.id != HOLY_ROSARY_ID -> Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(s.prayerDetailSequenceComingSoon, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> PrayerReadingContent(
                detail = current,
                readingLanguage = readingLanguage,
                onLanguageChange = { readingLanguage = it },
                fontScale = FONT_SCALE_STEPS[fontScaleIndex],
                onFontScaleChange = { newIndex ->
                    fontScaleIndex = newIndex
                    repository.setPreference(PreferenceKeys.PRAYER_FONT_SCALE, FONT_SCALE_STEPS[newIndex].toString())
                },
                keepScreenOn = keepScreenOn,
                onKeepScreenOnChange = { keepScreenOn = it },
                showSource = showSource,
                onShowSourceChange = { showSource = it },
                modifier = Modifier.fillMaxSize().padding(padding),
            )
        }
    }
}

@Composable
private fun PrayerReadingContent(
    detail: PrayerDetail,
    readingLanguage: String,
    onLanguageChange: (String) -> Unit,
    fontScale: Float,
    onFontScaleChange: (Int) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    showSource: Boolean,
    onShowSourceChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val hasLatin = "la" in detail.availableLanguages

    BoxWithConstraints(modifier = modifier) {
        val isWideEnoughForParallel = maxWidth >= 600.dp

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            if (detail.availableLanguages.size > 1) {
                LanguageToggleRow(
                    available = detail.availableLanguages,
                    selected = readingLanguage,
                    onSelect = onLanguageChange,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            ReadingControlsRow(
                fontScale = fontScale,
                onFontScaleChange = onFontScaleChange,
                keepScreenOn = keepScreenOn,
                onKeepScreenOnChange = onKeepScreenOnChange,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            if (detail.subtitle != null) {
                Text(
                    text = detail.subtitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            // Reading comfort is the point of this screen: generous line height
            // and a comfortable measure rather than the app's usual list-row density.
            if (hasLatin && isWideEnoughForParallel) {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    PrayerBodyText(
                        bodyMd = detail.bodyMd,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontScale = fontScale,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                PrayerBodyText(
                    bodyMd = detail.bodyMd,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontScale = fontScale,
                )
            }

            SourceFooter(
                detail = detail,
                expanded = showSource,
                onExpandedChange = onShowSourceChange,
                modifier = Modifier.padding(top = 32.dp),
            )
        }
    }
}

@Composable
private fun LanguageToggleRow(
    available: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        available.forEach { lang ->
            FilterChip(
                selected = lang == selected,
                onClick = { onSelect(lang) },
                label = { Text(languageLabel(lang, s)) },
                colors = FilterChipDefaults.filterChipColors(),
            )
        }
    }
}

@Composable
private fun ReadingControlsRow(
    fontScale: Float,
    onFontScaleChange: (Int) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val currentIndex = FONT_SCALE_STEPS.indexOf(fontScale).takeIf { it >= 0 } ?: DEFAULT_FONT_SCALE_INDEX

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(s.prayerDetailFontSize, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(end = 8.dp))
        IconButton(
            onClick = { if (currentIndex > 0) onFontScaleChange(currentIndex - 1) },
            modifier = Modifier.semantics { contentDescription = s.prayerDetailFontSmaller },
        ) {
            Text("A-")
        }
        IconButton(
            onClick = { if (currentIndex < FONT_SCALE_STEPS.lastIndex) onFontScaleChange(currentIndex + 1) },
            modifier = Modifier.semantics { contentDescription = s.prayerDetailFontLarger },
        ) {
            Text("A+")
        }

        Text(
            s.prayerDetailKeepScreenOn,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f).padding(start = 16.dp),
        )
        Switch(checked = keepScreenOn, onCheckedChange = onKeepScreenOnChange)
    }
}

@Composable
private fun SourceFooter(
    detail: PrayerDetail,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Column(modifier = modifier) {
        TextButton(onClick = { onExpandedChange(!expanded) }) {
            Text(if (expanded) s.prayerDetailHideSource else s.prayerDetailShowSource)
        }
        if (expanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (detail.attribution != null) {
                    Text(
                        text = "${s.prayerDetailAttribution}: ${detail.attribution}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "${s.prayerDetailBySource}: ${detail.source}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun languageLabel(lang: String, s: Strings): String = when (lang) {
    "en" -> s.prayerLanguageEn
    "fr" -> s.prayerLanguageFr
    "la" -> s.prayerLanguageLa
    else -> lang
}
