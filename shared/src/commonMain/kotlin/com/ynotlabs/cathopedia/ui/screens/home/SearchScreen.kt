package com.ynotlabs.cathopedia.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.ContentCategory
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.settings_header_bg
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow
import com.ynotlabs.cathopedia.ui.label
import com.ynotlabs.cathopedia.ui.singularLabel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private val SearchBg = Color(0xFF061A13)
private val SearchSurface = Color(0xFF0C271E)
private val SearchSurfaceRaised = Color(0xFF123127)
private val SearchBorder = Color(0xFF315444)
private val SearchGold = Color(0xFFD8B24C)
private val SearchGoldSoft = Color(0xFF9D8858)
private val SearchCream = Color(0xFFF4ECDD)
private val SearchMuted = Color(0xFFB4AD98)

/**
 * Premium global search screen.
 *
 * Search history is intentionally kept in-memory here so this file remains compatible
 * with the current repository API. If you later add a preference table/repository method,
 * replace [recentSearches] with persisted history.
 */
@Composable
fun SearchScreen(
    repository: CathopediaRepository,
    language: String,
    onResultSelected: (ContentSummary) -> Unit,
    landingListState: LazyListState = rememberLazyListState(),
    resultsListState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var allResults by remember { mutableStateOf<List<ContentSummary>>(emptyList()) }
    var filter by remember { mutableStateOf<ContentCategory?>(null) }

    var recentSearches by remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(Unit) {
        recentSearches = repository.getSearchHistory()
    }

    fun commitSearch(value: String) {
        val cleaned = value.trim()
        if (cleaned.isBlank()) return

        scope.launch {
            repository.addSearchHistory(cleaned)
            recentSearches = repository.getSearchHistory()
        }
    }

    LaunchedEffect(query, language) {
        allResults = if (query.isBlank()) {
            emptyList()
        } else {
            repository.search(query.trim(), language)
        }
        filter = null
    }

    val counts = ContentCategory.entries.associateWith { category ->
        allResults.count { it.type in category.types }
    }

    val visible = if (filter == null) {
        allResults
    } else {
        allResults.filter { it.type in filter!!.types }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SearchBg),
    ) {
        SearchHeader(
            query = query,
            onQueryChange = { query = it },
            onClear = {
                query = ""
                focusManager.clearFocus()
            },
            onSubmit = {
                focusManager.clearFocus()
            },
        )

        Spacer(Modifier.height(4.dp))

        if (query.isBlank()) {
            SearchLandingContent(
                recentSearches = recentSearches,
                onRecentClick = {
                    query = it
                    commitSearch(it)
                },
                onRemoveRecent = { value ->
                    scope.launch {
                        repository.removeSearchHistory(value)
                        recentSearches = repository.getSearchHistory()
                    }
                },
                onClearAll = {
                    scope.launch {
                        repository.clearSearchHistory()
                        recentSearches = emptyList()
                    }
                },
                listState = landingListState,
            )
        } else {
            SearchResultsContent(
                allResults = allResults,
                visibleResults = visible,
                filter = filter,
                counts = counts,
                onFilterChange = { filter = it },
                onResultSelected = { result ->
                    commitSearch(result.name)
                    onResultSelected(result)
                },
                listState = resultsListState,
            )
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SearchBg),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .statusBarsPadding(),
        ) {
            Image(
                painter = painterResource(Res.drawable.settings_header_bg),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth(0.58f)
                    .height(165.dp)
                    .padding(top = 6.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.TopEnd,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.00f to SearchBg,
                                0.50f to SearchBg.copy(alpha = 0.96f),
                                0.76f to SearchBg.copy(alpha = 0.55f),
                                1.00f to Color.Transparent,
                            ),
                        ),
                    ),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                SearchBg.copy(alpha = 0.08f),
                                SearchBg,
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = s.searchCathopedia,
                    color = SearchCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = s.searchHeroSubtitle,
                    color = SearchMuted,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            placeholder = {
                Text(
                    text = s.searchPlaceholder,
                    color = SearchMuted.copy(alpha = 0.82f),
                    fontSize = 14.sp,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = SearchGold,
                    modifier = Modifier.size(23.dp),
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(onClick = onClear),
                        shape = CircleShape,
                        color = SearchGold.copy(alpha = 0.08f),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = s.searchClear,
                                tint = SearchGold,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSubmit() },
            ),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SearchSurface.copy(alpha = 0.94f),
                unfocusedContainerColor = SearchSurface.copy(alpha = 0.90f),
                focusedBorderColor = SearchGold.copy(alpha = 0.76f),
                unfocusedBorderColor = SearchBorder,
                focusedTextColor = SearchCream,
                unfocusedTextColor = SearchCream,
                cursorColor = SearchGold,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(56.dp),
        )
    }
}

@Composable
private fun SearchLandingContent(
    recentSearches: List<String>,
    onRecentClick: (String) -> Unit,
    onRemoveRecent: (String) -> Unit,
    onClearAll: () -> Unit,
    listState: LazyListState,
) {
    val s = LocalStrings.current
    if (recentSearches.isEmpty()) {
        SearchInitialState()
    } else {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 6.dp,
                bottom = 124.dp,
            ),
        ) {
            item {
                SearchSectionHeader(
                    title = s.searchRecent,
                    actionText = s.searchClearAll,
                    onAction = onClearAll,
                )
            }

            item {
                RecentSearchCard(
                    items = recentSearches,
                    onClick = onRecentClick,
                    onRemove = onRemoveRecent,
                )

                Spacer(Modifier.height(22.dp))
            }

            item {
                SearchTipsCard()
            }
        }
    }
}

@Composable
private fun SearchInitialState() {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = SearchGold.copy(alpha = 0.05f),
            border = BorderStroke(
                width = 2.dp,
                color = SearchGold.copy(alpha = 0.15f),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = SearchGold.copy(alpha = 0.6f),
                    modifier = Modifier.size(36.dp),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = s.searchReadyTitle,
            color = SearchCream.copy(alpha = 0.9f),
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = s.searchReadyBody,
            color = SearchMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SearchSectionHeader(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            color = SearchGold,
            fontSize = 10.5.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            SearchGoldSoft.copy(alpha = 0.45f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        if (actionText != null && onAction != null) {
            Spacer(Modifier.width(10.dp))

            Text(
                text = actionText,
                color = SearchGold,
                fontSize = 11.sp,
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}

@Composable
private fun RecentSearchCard(
    items: List<String>,
    onClick: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    val s = LocalStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SearchSurface.copy(alpha = 0.92f),
        border = BorderStroke(
            width = 2.dp,
            color = SearchBorder.copy(alpha = 0.72f),
        ),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(item) }
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = SearchGold.copy(alpha = 0.07f),
                        border = BorderStroke(
                            width = 2.dp,
                            color = SearchGold.copy(alpha = 0.22f),
                        ),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                tint = SearchGold,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = item,
                        modifier = Modifier.weight(1f),
                        color = SearchCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Surface(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onRemove(item) },
                        shape = CircleShape,
                        color = Color.Transparent,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = s.searchRemoveRecent,
                                tint = SearchGoldSoft,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    }
                }

                if (index != items.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 60.dp, end = 14.dp)
                            .height(1.dp)
                            .background(SearchBorder.copy(alpha = 0.42f)),
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTipsCard() {
    val s = LocalStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SearchSurface.copy(alpha = 0.92f),
        border = BorderStroke(
            width = 2.dp,
            color = SearchGoldSoft.copy(alpha = 0.35f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = SearchGold.copy(alpha = 0.08f),
                border = BorderStroke(
                    width = 2.dp,
                    color = SearchGold.copy(alpha = 0.26f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = SearchGold,
                        modifier = Modifier.size(21.dp),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = s.searchTipsTitle.uppercase(),
                    color = SearchGold,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = s.searchTipsBody,
                    color = SearchMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                )
            }
        }
    }
}

@Composable
private fun SearchResultsContent(
    allResults: List<ContentSummary>,
    visibleResults: List<ContentSummary>,
    filter: ContentCategory?,
    counts: Map<ContentCategory, Int>,
    onFilterChange: (ContentCategory?) -> Unit,
    onResultSelected: (ContentSummary) -> Unit,
    listState: LazyListState,
) {
    val s = LocalStrings.current

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (allResults.isNotEmpty()) {
            val chipOptions = buildList {
                add("${s.all} ${allResults.size}" to null)
                ContentCategory.entries.forEach { category ->
                    val count = counts[category] ?: 0
                    if (count > 0) {
                        add("${category.label(s)} $count" to category)
                    }
                }
            }

            FilterChipsRow(
                options = chipOptions,
                selected = filter,
                onSelect = onFilterChange,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }

        if (allResults.isEmpty()) {
            SearchEmptyState()
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 4.dp,
                    bottom = 124.dp,
                ),
            ) {
                item {
                    Text(
                        text = (if (visibleResults.size == 1) s.searchOneResult else s.searchManyResults)
                            .replace("{count}", visibleResults.size.toString()),
                        color = SearchMuted,
                        fontSize = 11.5.sp,
                        modifier = Modifier.padding(bottom = 9.dp),
                    )
                }

                items(
                    items = visibleResults,
                    key = { it.type.tag + it.id },
                ) { result ->
                    SearchResultCard(
                        result = result,
                        onClick = { onResultSelected(result) },
                    )

                    Spacer(Modifier.height(9.dp))
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    result: ContentSummary,
    onClick: () -> Unit,
) {
    val s = LocalStrings.current
    val portrait = Portraits.fullForEntity(result.type, result.id)
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        SearchSurfaceRaised,
                        SearchSurface,
                    ),
                ),
            )
            .border(
                width = 2.dp,
                color = SearchBorder.copy(alpha = 0.76f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (portrait != null) {
            Image(
                painter = painterResource(portrait),
                contentDescription = result.name,
                modifier = Modifier
                    .width(78.dp)
                    .height(106.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = 2.dp,
                        color = SearchGoldSoft.copy(alpha = 0.42f),
                        shape = RoundedCornerShape(14.dp),
                    ),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
            )

            Spacer(Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = result.type.singularLabel(s).uppercase(),
                color = SearchGold,
                fontSize = 9.5.sp,
                letterSpacing = 0.85.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = result.name,
                color = SearchCream,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = result.summary,
                color = SearchMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SearchEmptyState() {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            color = SearchGold.copy(alpha = 0.07f),
            border = BorderStroke(
                width = 2.dp,
                color = SearchGold.copy(alpha = 0.25f),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = SearchGold,
                    modifier = Modifier.size(34.dp),
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = s.exploreNoResultsFound,
            color = SearchCream,
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(7.dp))

        Text(
            text = s.searchNoResultsDetail,
            color = SearchMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}
