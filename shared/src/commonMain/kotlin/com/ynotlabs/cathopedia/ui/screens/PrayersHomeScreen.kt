package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.PrayerCategory
import com.ynotlabs.cathopedia.model.PrayerSummary
import kotlinx.coroutines.delay

private const val SEARCH_DEBOUNCE_MS = 300L
private const val HOLY_ROSARY_ID = "holy-rosary"

/**
 * Tab root for Prayers: category sections, favourites pinned at top when
 * non-empty, a prominent Rosary card, and an inline FTS search that swaps the
 * whole body for results while a query is active. Prayers list even when
 * their text isn't sourced yet — [CathopediaRepository.listPrayers] falls
 * back to a title-cased slug so the catalogue reads as populated; opening one
 * shows [com.ynotlabs.cathopedia.i18n.Strings.prayerTextNotYetAvailable]
 * instead of the real body (see PrayerDetailScreen.kt).
 */
@Composable
fun PrayersHomeScreen(
    repository: CathopediaRepository,
    language: String,
    onOpenPrayer: (slug: String) -> Unit,
    onOpenRosary: () -> Unit,
) {
    val s = LocalStrings.current

    var favorites by remember { mutableStateOf<List<PrayerSummary>>(emptyList()) }
    var byCategory by remember { mutableStateOf<Map<PrayerCategory, List<PrayerSummary>>>(emptyMap()) }

    // Re-entering this branch of the nav `when` disposes and recomposes this
    // composable (same pattern as SavedScreen), so favourites/lists refresh
    // on every visit to the tab without needing an explicit refresh signal.
    LaunchedEffect(language) {
        favorites = repository.listFavoritePrayers(language)
        byCategory = PrayerCategory.entries.associateWith { repository.listPrayers(it, language) }
    }

    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<PrayerSummary>>(emptyList()) }

    LaunchedEffect(query, language) {
        if (query.isBlank()) {
            searchResults = emptyList()
            return@LaunchedEffect
        }
        delay(SEARCH_DEBOUNCE_MS)
        searchResults = repository.searchPrayers(query, language)
    }

    fun openPrayer(prayer: PrayerSummary) {
        if (prayer.isSequence && prayer.id == HOLY_ROSARY_ID) onOpenRosary() else onOpenPrayer(prayer.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text(s.prayersSearchPlaceholder) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = s.searchDesc) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = s.close)
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )

        if (query.isNotBlank()) {
            PrayerSearchResults(
                query = query,
                results = searchResults,
                onSelect = ::openPrayer,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        } else {
            PrayersHomeBody(
                favorites = favorites,
                byCategory = byCategory,
                onSelect = ::openPrayer,
                onOpenRosary = onOpenRosary,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PrayersHomeBody(
    favorites: List<PrayerSummary>,
    byCategory: Map<PrayerCategory, List<PrayerSummary>>,
    onSelect: (PrayerSummary) -> Unit,
    onOpenRosary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val nonEmptyCategories = PrayerCategory.entries.filter { !byCategory[it].isNullOrEmpty() }

    // The Rosary is its own self-contained feature (mysteries, not prayer
    // text) — it stays reachable even while the rest of the catalogue has
    // nothing sourced yet, so this only gates the *list* content below it.
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 116.dp),
    ) {
        item { RosaryCard(onClick = onOpenRosary) }

        if (favorites.isEmpty() && nonEmptyCategories.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)) {
                    Text(s.listContentWillAppear, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (favorites.isNotEmpty()) {
            item { SectionHeader(s.prayersFavoritesSection) }
            items(favorites, key = { "fav-${it.id}" }) { prayer ->
                PrayerRow(prayer = prayer, onClick = { onSelect(prayer) })
            }
        }

        nonEmptyCategories.forEach { category ->
            item { SectionHeader(categoryLabel(category, s)) }
            items(byCategory.getValue(category), key = { "cat-${category.tag}-${it.id}" }) { prayer ->
                PrayerRow(prayer = prayer, onClick = { onSelect(prayer) })
            }
        }
    }
}

@Composable
private fun PrayerSearchResults(
    query: String,
    results: List<PrayerSummary>,
    onSelect: (PrayerSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current

    if (results.isEmpty()) {
        Box(modifier = modifier.padding(32.dp)) {
            Text(s.prayersSearchNoResults, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 116.dp),
    ) {
        items(results, key = { it.id }) { prayer ->
            PrayerRow(prayer = prayer, query = query, onClick = { onSelect(prayer) })
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp),
    )
}

/** The tab's headline feature — a card, not a list row, per the brief. */
@Composable
private fun RosaryCard(onClick: () -> Unit) {
    val s = LocalStrings.current
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column {
                Text(
                    text = s.prayersRosaryCardTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = s.prayersRosaryCardSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun PrayerRow(
    prayer: PrayerSummary,
    onClick: () -> Unit,
    query: String? = null,
) {
    val s = LocalStrings.current
    val isComingSoon = prayer.isSequence && prayer.id != HOLY_ROSARY_ID

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                HighlightedText(
                    text = prayer.title,
                    query = query,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (prayer.subtitle != null) {
                    HighlightedText(
                        text = prayer.subtitle,
                        query = query,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

            if (isComingSoon) {
                Text(
                    text = s.prayersSequenceComingSoon,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HighlightedText(
    text: String,
    query: String?,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
) {
    val highlightColor = MaterialTheme.colorScheme.primary
    val annotated = remember(text, query, highlightColor) { highlight(text, query, highlightColor) }
    Text(text = annotated, color = color, fontSize = fontSize, fontWeight = fontWeight)
}

private fun highlight(text: String, query: String?, highlightColor: Color): AnnotatedString {
    if (query.isNullOrBlank()) return AnnotatedString(text)
    val lowerText = text.lowercase()
    val lowerQuery = query.lowercase()
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val idx = lowerText.indexOf(lowerQuery, i)
            if (idx < 0) {
                append(text.substring(i))
                break
            }
            append(text.substring(i, idx))
            withStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                append(text.substring(idx, idx + lowerQuery.length))
            }
            i = idx + lowerQuery.length
        }
    }
}

private fun categoryLabel(category: PrayerCategory, s: Strings): String = when (category) {
    PrayerCategory.EVERYDAY -> s.prayerCategoryEveryday
    PrayerCategory.MARIAN -> s.prayerCategoryMarian
    PrayerCategory.HOLY_SPIRIT -> s.prayerCategoryHolySpirit
    PrayerCategory.EUCHARISTIC -> s.prayerCategoryEucharistic
    PrayerCategory.SAINTS -> s.prayerCategorySaints
    PrayerCategory.PENITENTIAL -> s.prayerCategoryPenitential
    PrayerCategory.SEQUENCES -> s.prayerCategorySequences
    PrayerCategory.OCCASIONAL -> s.prayerCategoryOccasional
}
