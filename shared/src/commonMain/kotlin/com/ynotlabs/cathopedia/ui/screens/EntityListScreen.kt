package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.displayName
import com.ynotlabs.cathopedia.ui.singularLabel
import com.ynotlabs.cathopedia.ui.theme.ApostleActionBar
import com.ynotlabs.cathopedia.ui.theme.ApostleBg
import com.ynotlabs.cathopedia.ui.theme.ApostleBorder
import com.ynotlabs.cathopedia.ui.theme.ApostleButtonContent
import com.ynotlabs.cathopedia.ui.theme.ApostleCard
import com.ynotlabs.cathopedia.ui.theme.ApostleCardPressed
import com.ynotlabs.cathopedia.ui.theme.ApostleChipForeground
import com.ynotlabs.cathopedia.ui.theme.ApostleCream
import com.ynotlabs.cathopedia.ui.theme.ApostleFieldContainer
import com.ynotlabs.cathopedia.ui.theme.ApostleGold
import com.ynotlabs.cathopedia.ui.theme.ApostleGoldSoft
import com.ynotlabs.cathopedia.ui.theme.ApostleHeader
import com.ynotlabs.cathopedia.ui.theme.ApostleIconCircleBg
import com.ynotlabs.cathopedia.ui.theme.ApostleMuted
import com.ynotlabs.cathopedia.ui.theme.ApostlePlaceholderBg
import com.ynotlabs.cathopedia.ui.theme.ApostleSheet
import com.ynotlabs.cathopedia.ui.theme.ApostleTag
import com.ynotlabs.cathopedia.ui.theme.ChurchActionBar
import com.ynotlabs.cathopedia.ui.theme.ChurchBg
import com.ynotlabs.cathopedia.ui.theme.ChurchBorder
import com.ynotlabs.cathopedia.ui.theme.ChurchButtonContent
import com.ynotlabs.cathopedia.ui.theme.ChurchCard
import com.ynotlabs.cathopedia.ui.theme.ChurchCardPressed
import com.ynotlabs.cathopedia.ui.theme.ChurchChipForeground
import com.ynotlabs.cathopedia.ui.theme.ChurchCream
import com.ynotlabs.cathopedia.ui.theme.ChurchFieldContainer
import com.ynotlabs.cathopedia.ui.theme.ChurchGold
import com.ynotlabs.cathopedia.ui.theme.ChurchGoldSoft
import com.ynotlabs.cathopedia.ui.theme.ChurchHeader
import com.ynotlabs.cathopedia.ui.theme.ChurchIconCircleBg
import com.ynotlabs.cathopedia.ui.theme.ChurchMuted
import com.ynotlabs.cathopedia.ui.theme.ChurchPlaceholderBg
import com.ynotlabs.cathopedia.ui.theme.ChurchSheet
import com.ynotlabs.cathopedia.ui.theme.ChurchTag
import com.ynotlabs.cathopedia.ui.theme.FeastBg
import com.ynotlabs.cathopedia.ui.theme.FeastBorder
import com.ynotlabs.cathopedia.ui.theme.FeastButtonContent
import com.ynotlabs.cathopedia.ui.theme.FeastCard
import com.ynotlabs.cathopedia.ui.theme.FeastCardPressed
import com.ynotlabs.cathopedia.ui.theme.FeastChipForeground
import com.ynotlabs.cathopedia.ui.theme.FeastCream
import com.ynotlabs.cathopedia.ui.theme.FeastFieldContainer
import com.ynotlabs.cathopedia.ui.theme.FeastGold
import com.ynotlabs.cathopedia.ui.theme.FeastGoldSoft
import com.ynotlabs.cathopedia.ui.theme.FeastHeader
import com.ynotlabs.cathopedia.ui.theme.FeastIconCircleBg
import com.ynotlabs.cathopedia.ui.theme.FeastMuted
import com.ynotlabs.cathopedia.ui.theme.FeastPlaceholderBg
import com.ynotlabs.cathopedia.ui.theme.FeastSheet
import com.ynotlabs.cathopedia.ui.theme.FeastTag
import com.ynotlabs.cathopedia.ui.theme.LocalApostleTheme
import com.ynotlabs.cathopedia.ui.theme.LocalChurchTheme
import com.ynotlabs.cathopedia.ui.theme.LocalFeastTheme
import com.ynotlabs.cathopedia.ui.theme.LocalMarianTheme
import com.ynotlabs.cathopedia.ui.theme.LocalMiracleTheme
import com.ynotlabs.cathopedia.ui.theme.LocalPopeTheme
import com.ynotlabs.cathopedia.ui.theme.MarianBg
import com.ynotlabs.cathopedia.ui.theme.MarianBorder
import com.ynotlabs.cathopedia.ui.theme.MarianButtonContent
import com.ynotlabs.cathopedia.ui.theme.MarianCard
import com.ynotlabs.cathopedia.ui.theme.MarianCardPressed
import com.ynotlabs.cathopedia.ui.theme.MarianChipForeground
import com.ynotlabs.cathopedia.ui.theme.MarianCream
import com.ynotlabs.cathopedia.ui.theme.MarianFieldContainer
import com.ynotlabs.cathopedia.ui.theme.MarianGold
import com.ynotlabs.cathopedia.ui.theme.MarianGoldSoft
import com.ynotlabs.cathopedia.ui.theme.MarianHeader
import com.ynotlabs.cathopedia.ui.theme.MarianIconCircleBg
import com.ynotlabs.cathopedia.ui.theme.MarianMuted
import com.ynotlabs.cathopedia.ui.theme.MarianPlaceholderBg
import com.ynotlabs.cathopedia.ui.theme.MarianSheet
import com.ynotlabs.cathopedia.ui.theme.MarianTag
import com.ynotlabs.cathopedia.ui.theme.MiracleBg
import com.ynotlabs.cathopedia.ui.theme.MiracleBorder
import com.ynotlabs.cathopedia.ui.theme.MiracleButtonContent
import com.ynotlabs.cathopedia.ui.theme.MiracleCard
import com.ynotlabs.cathopedia.ui.theme.MiracleCardPressed
import com.ynotlabs.cathopedia.ui.theme.MiracleChipForeground
import com.ynotlabs.cathopedia.ui.theme.MiracleCream
import com.ynotlabs.cathopedia.ui.theme.MiracleFieldContainer
import com.ynotlabs.cathopedia.ui.theme.MiracleGold
import com.ynotlabs.cathopedia.ui.theme.MiracleGoldSoft
import com.ynotlabs.cathopedia.ui.theme.MiracleHeader
import com.ynotlabs.cathopedia.ui.theme.MiracleIconCircleBg
import com.ynotlabs.cathopedia.ui.theme.MiracleMuted
import com.ynotlabs.cathopedia.ui.theme.MiraclePlaceholderBg
import com.ynotlabs.cathopedia.ui.theme.MiracleSheet
import com.ynotlabs.cathopedia.ui.theme.MiracleTag
import com.ynotlabs.cathopedia.ui.theme.PopeActionBar
import com.ynotlabs.cathopedia.ui.theme.PopeBg
import com.ynotlabs.cathopedia.ui.theme.PopeBorder
import com.ynotlabs.cathopedia.ui.theme.PopeButtonContent
import com.ynotlabs.cathopedia.ui.theme.PopeCard
import com.ynotlabs.cathopedia.ui.theme.PopeCardPressed
import com.ynotlabs.cathopedia.ui.theme.PopeChipForeground
import com.ynotlabs.cathopedia.ui.theme.PopeCream
import com.ynotlabs.cathopedia.ui.theme.PopeFieldContainer
import com.ynotlabs.cathopedia.ui.theme.PopeGold
import com.ynotlabs.cathopedia.ui.theme.PopeGoldSoft
import com.ynotlabs.cathopedia.ui.theme.PopeHeader
import com.ynotlabs.cathopedia.ui.theme.PopeIconCircleBg
import com.ynotlabs.cathopedia.ui.theme.PopeMuted
import com.ynotlabs.cathopedia.ui.theme.PopePlaceholderBg
import com.ynotlabs.cathopedia.ui.theme.PopeSheet
import com.ynotlabs.cathopedia.ui.theme.PopeTag
import org.jetbrains.compose.resources.painterResource

private val EntityBg: Color @Composable get() = if (LocalMarianTheme.current) MarianBg else if (LocalFeastTheme.current) FeastBg else if (LocalPopeTheme.current) PopeBg else if (LocalApostleTheme.current) ApostleBg else if (LocalMiracleTheme.current) MiracleBg else if (LocalChurchTheme.current) ChurchBg else MaterialTheme.colorScheme.background
private val EntityHeader: Color @Composable get() = if (LocalMarianTheme.current) MarianHeader else if (LocalFeastTheme.current) FeastHeader else if (LocalPopeTheme.current) PopeHeader else if (LocalApostleTheme.current) ApostleHeader else if (LocalMiracleTheme.current) MiracleHeader else if (LocalChurchTheme.current) ChurchHeader else MaterialTheme.colorScheme.surface
private val EntityCard: Color @Composable get() = if (LocalMarianTheme.current) MarianCard else if (LocalFeastTheme.current) FeastCard else if (LocalPopeTheme.current) PopeCard else if (LocalApostleTheme.current) ApostleCard else if (LocalMiracleTheme.current) MiracleCard else if (LocalChurchTheme.current) ChurchCard else MaterialTheme.colorScheme.surfaceContainerHigh
private val EntityCardPressed: Color @Composable get() = if (LocalMarianTheme.current) MarianCardPressed else if (LocalFeastTheme.current) FeastCardPressed else if (LocalPopeTheme.current) PopeCardPressed else if (LocalApostleTheme.current) ApostleCardPressed else if (LocalMiracleTheme.current) MiracleCardPressed else if (LocalChurchTheme.current) ChurchCardPressed else MaterialTheme.colorScheme.surfaceContainerHighest
private val EntityBorder: Color @Composable get() = if (LocalMarianTheme.current) MarianBorder else if (LocalFeastTheme.current) FeastBorder else if (LocalPopeTheme.current) PopeBorder else if (LocalApostleTheme.current) ApostleBorder else if (LocalMiracleTheme.current) MiracleBorder else if (LocalChurchTheme.current) ChurchBorder else MaterialTheme.colorScheme.outline
private val EntityGold: Color @Composable get() = if (LocalMarianTheme.current) MarianGold else if (LocalFeastTheme.current) FeastGold else if (LocalPopeTheme.current) PopeGold else if (LocalApostleTheme.current) ApostleGold else if (LocalMiracleTheme.current) MiracleGold else if (LocalChurchTheme.current) ChurchGold else MaterialTheme.colorScheme.primary
private val EntityGoldSoft: Color @Composable get() = if (LocalMarianTheme.current) MarianGoldSoft else if (LocalFeastTheme.current) FeastGoldSoft else if (LocalPopeTheme.current) PopeGoldSoft else if (LocalApostleTheme.current) ApostleGoldSoft else if (LocalMiracleTheme.current) MiracleGoldSoft else if (LocalChurchTheme.current) ChurchGoldSoft else MaterialTheme.colorScheme.secondary
private val EntityCream: Color @Composable get() = if (LocalMarianTheme.current) MarianCream else if (LocalFeastTheme.current) FeastCream else if (LocalPopeTheme.current) PopeCream else if (LocalApostleTheme.current) ApostleCream else if (LocalMiracleTheme.current) MiracleCream else if (LocalChurchTheme.current) ChurchCream else MaterialTheme.colorScheme.onBackground
private val EntityMuted: Color @Composable get() = if (LocalMarianTheme.current) MarianMuted else if (LocalFeastTheme.current) FeastMuted else if (LocalPopeTheme.current) PopeMuted else if (LocalApostleTheme.current) ApostleMuted else if (LocalMiracleTheme.current) MiracleMuted else if (LocalChurchTheme.current) ChurchMuted else MaterialTheme.colorScheme.onSurfaceVariant
private val EntityTag: Color @Composable get() = if (LocalMarianTheme.current) MarianTag else if (LocalFeastTheme.current) FeastTag else if (LocalPopeTheme.current) PopeTag else if (LocalApostleTheme.current) ApostleTag else if (LocalMiracleTheme.current) MiracleTag else if (LocalChurchTheme.current) ChurchTag else MaterialTheme.colorScheme.secondaryContainer
private val EntitySheet: Color @Composable get() = if (LocalMarianTheme.current) MarianSheet else if (LocalFeastTheme.current) FeastSheet else if (LocalPopeTheme.current) PopeSheet else if (LocalApostleTheme.current) ApostleSheet else if (LocalMiracleTheme.current) MiracleSheet else if (LocalChurchTheme.current) ChurchSheet else MaterialTheme.colorScheme.surfaceContainerLow
private val EntityFieldContainer: Color @Composable get() = if (LocalMarianTheme.current) MarianFieldContainer else if (LocalFeastTheme.current) FeastFieldContainer else if (LocalPopeTheme.current) PopeFieldContainer else if (LocalApostleTheme.current) ApostleFieldContainer else if (LocalMiracleTheme.current) MiracleFieldContainer else if (LocalChurchTheme.current) ChurchFieldContainer else MaterialTheme.colorScheme.surfaceContainerLow
private val EntityChipForeground: Color @Composable get() = if (LocalMarianTheme.current) MarianChipForeground else if (LocalFeastTheme.current) FeastChipForeground else if (LocalPopeTheme.current) PopeChipForeground else if (LocalApostleTheme.current) ApostleChipForeground else if (LocalMiracleTheme.current) MiracleChipForeground else if (LocalChurchTheme.current) ChurchChipForeground else MaterialTheme.colorScheme.background
private val EntityPlaceholderBg: Color @Composable get() = if (LocalMarianTheme.current) MarianPlaceholderBg else if (LocalFeastTheme.current) FeastPlaceholderBg else if (LocalPopeTheme.current) PopePlaceholderBg else if (LocalApostleTheme.current) ApostlePlaceholderBg else if (LocalMiracleTheme.current) MiraclePlaceholderBg else if (LocalChurchTheme.current) ChurchPlaceholderBg else MaterialTheme.colorScheme.surfaceContainerLow
private val EntityIconCircleBg: Color @Composable get() = if (LocalMarianTheme.current) MarianIconCircleBg else if (LocalFeastTheme.current) FeastIconCircleBg else if (LocalPopeTheme.current) PopeIconCircleBg else if (LocalApostleTheme.current) ApostleIconCircleBg else if (LocalMiracleTheme.current) MiracleIconCircleBg else if (LocalChurchTheme.current) ChurchIconCircleBg else MaterialTheme.colorScheme.surfaceContainerHighest
private val EntityButtonContent: Color @Composable get() = if (LocalMarianTheme.current) MarianButtonContent else if (LocalFeastTheme.current) FeastButtonContent else if (LocalPopeTheme.current) PopeButtonContent else if (LocalApostleTheme.current) ApostleButtonContent else if (LocalMiracleTheme.current) MiracleButtonContent else if (LocalChurchTheme.current) ChurchButtonContent else MaterialTheme.colorScheme.onPrimary

private enum class EntitySortOption {
    NAME_ASC,
    NAME_DESC,
    EARLIEST_FIRST,
    LATEST_FIRST,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityListScreen(
    type: ContentType,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onItemSelected: (ContentSummary) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var items by remember(type) { mutableStateOf<List<ContentSummary>?>(null) }
    var query by remember(type) { mutableStateOf("") }
    var searchVisible by remember(type) { mutableStateOf(false) }
    var selectedCentury by remember(type) { mutableStateOf<String?>(null) }
    var selectedRank by remember(type) { mutableStateOf<String?>(null) }
    var sortOption by remember(type) { mutableStateOf(EntitySortOption.EARLIEST_FIRST) }
    var showFilterSheet by remember(type) { mutableStateOf(false) }

    LaunchedEffect(type, language) {
        items = repository.listByType(type, language)
    }

    LaunchedEffect(selectedCentury) {
        if (type == ContentType.POPE) {
            listState.scrollToItem(0)
        }
    }

    val currentItems = items
    val allCenturies = remember(currentItems, type) {
        if (type == ContentType.POPE && currentItems != null) {
            currentItems
                .map { centuryLabel(it.sortYear, s) }
                .distinct()
        } else {
            emptyList()
        }
    }

    val allRanks = remember(currentItems, type) {
        if (type == ContentType.FEAST && currentItems != null) {
            currentItems
                .mapNotNull { it.rank?.takeIf { r -> r.isNotBlank() } }
                .distinct()
                .sortedBy { rankSortOrder(it) }
        } else {
            emptyList()
        }
    }

    val filteredItems = remember(currentItems, query, selectedCentury, selectedRank, sortOption, type) {
        val source = currentItems.orEmpty()

        val searched = source.filter { item ->
            query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.summary.contains(query, ignoreCase = true)
        }

        val centuryFiltered = if (type == ContentType.POPE && selectedCentury != null) {
            searched.filter { centuryLabel(it.sortYear, s) == selectedCentury }
        } else {
            searched
        }

        val rankFiltered = if (type == ContentType.FEAST && selectedRank != null) {
            centuryFiltered.filter { it.rank == selectedRank }
        } else {
            centuryFiltered
        }

        when (sortOption) {
            EntitySortOption.NAME_ASC -> rankFiltered.sortedBy { it.name.lowercase() }
            EntitySortOption.NAME_DESC -> rankFiltered.sortedByDescending { it.name.lowercase() }
            EntitySortOption.EARLIEST_FIRST -> rankFiltered.sortedBy { it.sortYear ?: Long.MAX_VALUE }
            EntitySortOption.LATEST_FIRST -> rankFiltered.sortedByDescending { it.sortYear ?: Long.MIN_VALUE }
        }
    }

    var headerHeightPx by remember(type) { mutableIntStateOf(0) }
    val headerHeightDp = with(LocalDensity.current) { headerHeightPx.toDp() }

    CompositionLocalProvider(
        LocalMarianTheme provides (type == ContentType.APPARITION),
        LocalFeastTheme provides (type == ContentType.FEAST),
        LocalPopeTheme provides (type == ContentType.POPE),
        LocalApostleTheme provides (type == ContentType.APOSTLE),
        LocalMiracleTheme provides (type == ContentType.MIRACLE),
        LocalChurchTheme provides (type == ContentType.CHURCH),
    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EntityBg),
    ) {
        // Content layer — scrolls beneath the elevated header below.
        if (currentItems == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = headerHeightDp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = EntityGold)
            }
        } else if (filteredItems.isEmpty()) {
            EmptyEntityState(
                query = query,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = headerHeightDp),
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = headerHeightDp + 12.dp,
                    bottom = 126.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (type == ContentType.POPE) {
                    val grouped = filteredItems.groupBy { centuryLabel(it.sortYear, s) }
                    grouped.forEach { (century, popes) ->
                        stickyHeader(key = "century-$century") {
                            PremiumCenturyHeader(century)
                        }

                        items(popes, key = { it.id }) { item ->
                            PremiumEntityCard(
                                type = type,
                                item = item,
                                century = null,
                                onClick = { onItemSelected(item) },
                            )
                        }
                    }
                } else {
                    items(filteredItems, key = { it.id }) { item ->
                        PremiumEntityCard(
                            type = type,
                            item = item,
                            century = null,
                            onClick = { onItemSelected(item) },
                        )
                    }
                }
            }
        }

        // Header layer — elevated rounded panel; content above scrolls in behind it.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { headerHeightPx = it.size.height }
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    clip = false,
                )
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(EntityHeader),
        ) {
            EntityTopArea(
                type = type,
                title = type.displayName(s),
                count = currentItems?.size,
                searchVisible = searchVisible,
                query = query,
                onQueryChange = { query = it },
                onBack = onBack,
                onSearchClick = { searchVisible = !searchVisible },
                onCloseSearch = {
                    query = ""
                    searchVisible = false
                },
                onFilterClick = { showFilterSheet = true },
            )

            if (type == ContentType.POPE && allCenturies.isNotEmpty()) {
                CenturyChipStrip(
                    centuries = allCenturies,
                    selectedCentury = selectedCentury,
                    onCenturySelected = { selectedCentury = it },
                )
            }

            if (type == ContentType.FEAST && allRanks.isNotEmpty()) {
                RankChipStrip(
                    ranks = allRanks,
                    selectedRank = selectedRank,
                    onRankSelected = { selectedRank = it },
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }

    if (showFilterSheet) {
        EntityFilterSheet(
            type = type,
            centuries = allCenturies,
            selectedCentury = selectedCentury,
            ranks = allRanks,
            selectedRank = selectedRank,
            selectedSort = sortOption,
            onCenturySelected = { selectedCentury = it },
            onRankSelected = { selectedRank = it },
            onSortSelected = { sortOption = it },
            onDismiss = { showFilterSheet = false },
            onApply = { showFilterSheet = false },
        )
    }
    }
}

@Composable
private fun EntityTopArea(
    type: ContentType,
    title: String,
    count: Int?,
    searchVisible: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onCloseSearch: () -> Unit,
    onFilterClick: () -> Unit,
) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 18.dp, end = 14.dp, top = 6.dp, bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.back,
                    tint = EntityCream,
                    modifier = Modifier.size(25.dp),
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    color = EntityCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = if (type == ContentType.POPE) {
                        s.listPopesSubtitle
                    } else {
                        s.listExploreThroughHistory
                    },
                    color = EntityMuted,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = s.listFilterAndSortDesc,
                        tint = EntityCream,
                        modifier = Modifier.size(24.dp),
                    )
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = s.searchDesc,
                        tint = EntityCream,
                        modifier = Modifier.size(25.dp),
                    )
                }
            }
        }

        AnimatedVisibility(visible = searchVisible) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = {
                    Text(
                        text = s.listSearchByNameOrDescription.replace("{type}", title.lowercase()),
                        color = EntityMuted,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = EntityGold,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onCloseSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = s.closeSearch,
                            tint = EntityMuted,
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = EntityFieldContainer,
                    unfocusedContainerColor = EntityFieldContainer,
                    focusedBorderColor = EntityGoldSoft,
                    unfocusedBorderColor = EntityBorder,
                    focusedTextColor = EntityCream,
                    unfocusedTextColor = EntityCream,
                    cursorColor = EntityGold,
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun CenturyChipStrip(
    centuries: List<String>,
    selectedCentury: String?,
    onCenturySelected: (String?) -> Unit,
) {
    val s = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PremiumFilterChip(
            text = s.all,
            selected = selectedCentury == null,
            onClick = { onCenturySelected(null) },
        )
        centuries.forEach { century ->
            PremiumFilterChip(
                text = century.titleCaseCentury(),
                selected = selectedCentury == century,
                onClick = { onCenturySelected(century) },
            )
        }
    }
}

@Composable
private fun RankChipStrip(
    ranks: List<String>,
    selectedRank: String?,
    onRankSelected: (String?) -> Unit,
) {
    val s = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PremiumFilterChip(
            text = s.all,
            selected = selectedRank == null,
            onClick = { onRankSelected(null) },
        )
        ranks.forEach { rank ->
            PremiumFilterChip(
                text = rank,
                selected = selectedRank == rank,
                onClick = { onRankSelected(rank) },
            )
        }
    }
}

/** Solemnity outranks Feast liturgically, so it sorts first regardless of alphabetical order. */
private fun rankSortOrder(rank: String): Int = when (rank) {
    "Solemnity" -> 0
    "Feast" -> 1
    else -> 2
}

@Composable
private fun PremiumFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) EntityCream else Color.Transparent
    val foreground = if (selected) EntityChipForeground else EntityCream
    val border = if (selected) EntityCream else EntityBorder

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = background,
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, border),
    ) {
        Text(
            text = text,
            color = foreground,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun PremiumCenturyHeader(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EntityBg)
            .padding(top = 8.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "♔",
            color = EntityGold,
            fontSize = 12.sp,
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = label.uppercase(),
            color = EntityGold,
            fontSize = 12.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(EntityGoldSoft.copy(alpha = 0.7f), Color.Transparent),
                    ),
                ),
        )
    }
}

@Composable
private fun PremiumEntityCard(
    type: ContentType,
    item: ContentSummary,
    century: String?,
    onClick: () -> Unit,
) {
    val s = LocalStrings.current
    val portrait = Portraits.fullForEntity(type, item.id)
    val isChurchCard = type == ContentType.CHURCH
    val isSaintPope = type == ContentType.POPE && item.isDeclaredSaint()
    val popeTimeline = if (type == ContentType.POPE) item.papacyTimeline(s) else null
    val saintFeastDay = if (type == ContentType.SAINT) item.feastDayLabel(s) else null
    val titleSize = when {
        item.name.length <= 18 -> 18.sp
        item.name.length <= 26 -> 16.sp
        item.name.length <= 36 -> 15.sp
        item.name.length <= 46 -> 14.sp
        else -> 13.sp
    }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        label = "entity-card-scale",
    )
    val cardColor = if (pressed) EntityCardPressed else EntityCard

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        color = cardColor,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (pressed) EntityGoldSoft.copy(alpha = 0.75f) else EntityBorder,
        ),
        shadowElevation = if (pressed) 7.dp else 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (portrait != null) {
                Box(
                    modifier = Modifier
                        .size(
                            width = 94.dp,
                            height = 128.dp,
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = EntityGoldSoft.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(16.dp),
                        ),
                ) {
                    Image(
                        painter = painterResource(portrait),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                                ),
                            ),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(
                            width = 94.dp,
                            height = 128.dp,
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(EntityPlaceholderBg)
                        .border(1.dp, EntityBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.name.take(1).uppercase(),
                        color = EntityGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 32.sp,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSaintPope) {
                        Text(
                            text = "✦",
                            color = EntityGold,
                            fontSize = 10.sp,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = s.detailSaintTag,
                            color = EntityGold,
                            fontSize = 10.sp,
                            letterSpacing = 0.9.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(9.dp))
                    }

                    Text(
                        text = "♛",
                        color = EntityGold,
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (type == ContentType.FEAST) {
                            (item.rank?.takeIf { it.isNotBlank() } ?: type.singularLabel(s)).uppercase()
                        } else {
                            type.singularLabel(s).uppercase()
                        },
                        color = EntityGold,
                        fontSize = 10.sp,
                        letterSpacing = 0.9.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.name,
                    color = EntityCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = if (isChurchCard) 16.sp else titleSize,
                    lineHeight = if (isChurchCard) 19.sp else titleSize * 1.15f,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                )

                if (popeTimeline != null || saintFeastDay != null) {
                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = popeTimeline ?: saintFeastDay.orEmpty(),
                        color = EntityGoldSoft,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.summary,
                    color = EntityMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

            }

            Spacer(Modifier.width(10.dp))

            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = EntityIconCircleBg,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    EntityGoldSoft.copy(alpha = 0.7f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = s.listOpenEntity.replace("{name}", item.name),
                        tint = EntityGold,
                        modifier = Modifier.size(17.dp),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntityFilterSheet(
    type: ContentType,
    centuries: List<String>,
    selectedCentury: String?,
    ranks: List<String>,
    selectedRank: String?,
    selectedSort: EntitySortOption,
    onCenturySelected: (String?) -> Unit,
    onRankSelected: (String?) -> Unit,
    onSortSelected: (EntitySortOption) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    val s = LocalStrings.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EntitySheet,
        contentColor = EntityCream,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .background(EntityBorder, RoundedCornerShape(50)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, bottom = 30.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = s.listFilterAndSortSheetTitle,
                    color = EntityCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = s.close,
                        tint = EntityCream,
                    )
                }
            }

            if (type == ContentType.POPE && centuries.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                FilterSectionLabel(s.listFilterCentury)
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PremiumFilterChip(
                        text = s.all,
                        selected = selectedCentury == null,
                        onClick = { onCenturySelected(null) },
                    )
                    centuries.forEach { century ->
                        PremiumFilterChip(
                            text = century.titleCaseCentury(),
                            selected = selectedCentury == century,
                            onClick = { onCenturySelected(century) },
                        )
                    }
                }
            }

            if (type == ContentType.FEAST && ranks.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                FilterSectionLabel(s.listFilterRank)
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PremiumFilterChip(
                        text = s.all,
                        selected = selectedRank == null,
                        onClick = { onRankSelected(null) },
                    )
                    ranks.forEach { rank ->
                        PremiumFilterChip(
                            text = rank,
                            selected = selectedRank == rank,
                            onClick = { onRankSelected(rank) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            FilterSectionLabel(s.listSortBy)
            Spacer(Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SortChoice(
                        text = s.listSortNameAsc,
                        selected = selectedSort == EntitySortOption.NAME_ASC,
                        modifier = Modifier.weight(1f),
                        onClick = { onSortSelected(EntitySortOption.NAME_ASC) },
                    )
                    SortChoice(
                        text = s.listSortNameDesc,
                        selected = selectedSort == EntitySortOption.NAME_DESC,
                        modifier = Modifier.weight(1f),
                        onClick = { onSortSelected(EntitySortOption.NAME_DESC) },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SortChoice(
                        text = s.listSortEarliestFirst,
                        selected = selectedSort == EntitySortOption.EARLIEST_FIRST,
                        modifier = Modifier.weight(1f),
                        onClick = { onSortSelected(EntitySortOption.EARLIEST_FIRST) },
                    )
                    SortChoice(
                        text = s.listSortLatestFirst,
                        selected = selectedSort == EntitySortOption.LATEST_FIRST,
                        modifier = Modifier.weight(1f),
                        onClick = { onSortSelected(EntitySortOption.LATEST_FIRST) },
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            Button(
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EntityGold,
                    contentColor = EntityButtonContent,
                ),
            ) {
                Text(
                    text = s.listApplyFilter,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun FilterSectionLabel(text: String) {
    Text(
        text = text,
        color = EntityGold,
        fontSize = 11.sp,
        letterSpacing = 1.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun SortChoice(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = if (selected) EntityGold.copy(alpha = 0.22f) else Color.Transparent
    val border = if (selected) EntityGold else EntityBorder
    val textColor = if (selected) EntityCream else EntityMuted

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun EmptyEntityState(
    query: String,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "✦",
            color = EntityGold,
            fontSize = 38.sp,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (query.isBlank()) s.listNothingToShow else s.listNoMatchesFound,
            color = EntityCream,
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = if (query.isBlank()) {
                s.listContentWillAppear
            } else {
                s.listTryAnotherNameKeyword
            },
            color = EntityMuted,
            fontSize = 13.sp,
        )
    }
}

private fun String.titleCaseCentury(): String {
    if (isBlank()) return this
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

/** "1st century", "11th century", "21st century" (or the French equivalent) from the repository's sort key. */
private fun centuryLabel(sortKey: Long?, s: Strings): String {
    if (sortKey == null) return s.listUnknownEra
    val year = sortKey / 12
    val century = ((year - 1) / 100 + 1).toInt()
    val ordinal = if (s.listUseFrenchOrdinals) {
        if (century == 1) "1er" else "${century}e"
    } else {
        val suffix = if (century % 100 in 11..13) {
            "th"
        } else {
            when (century % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
        "$century$suffix"
    }
    return "$ordinal ${s.listCenturyWord}"
}


private fun ContentSummary.papacyTimeline(s: Strings): String? {
    val start = papacyStart?.trim()?.takeIf { it.isNotEmpty() }
    val end = papacyEnd?.trim()?.takeIf { it.isNotEmpty() }

    return when {
        start != null && end != null -> "$start – $end"
        start != null -> "$start – ${s.detailPresent}"
        else -> null
    }
}

private fun ContentSummary.feastDayLabel(s: Strings): String? {
    feastDay?.trim()?.takeIf { it.isNotEmpty() }?.let {
        return "${s.detailFactFeastDay} · $it"
    }

    val source = summary.replace('\n', ' ')

    val patterns = listOf(
        Regex(
            """(?i)\bfeast\s*day\s*:?\s*(?:is\s+|on\s+)?((?:January|February|March|April|May|June|July|August|September|October|November|December)\s+\d{1,2})"""
        ),
        Regex(
            """(?i)\bfeast\s*:?\s*(?:is\s+|on\s+)?((?:January|February|March|April|May|June|July|August|September|October|November|December)\s+\d{1,2})"""
        ),
        Regex(
            """(?i)\bfeast\s*day\s*:?\s*(?:is\s+|on\s+)?(\d{1,2}\s+(?:January|February|March|April|May|June|July|August|September|October|November|December))"""
        )
    )

    val match = patterns.firstNotNullOfOrNull { it.find(source) } ?: return null
    return "${s.detailFactFeastDay} · ${match.groupValues[1].trim()}"
}

private val canonizedPopeNames = setOf(
    "adeodatus i",
    "adrian iii",
    "agapetus i",
    "agatho",
    "alexander i",
    "anacletus",
    "cletus",
    "anastasius i",
    "anicetus",
    "anterus",
    "benedict ii",
    "boniface i",
    "boniface iv",
    "caius",
    "callixtus i",
    "celestine i",
    "celestine v",
    "clement i",
    "cornelius",
    "damasus i",
    "dionysius",
    "eleuterus",
    "eleutherius",
    "eugene i",
    "eusebius",
    "eutychian",
    "evaristus",
    "fabian",
    "felix i",
    "felix iii",
    "felix iv",
    "gelasius i",
    "gregory i",
    "gregory i the great",
    "gregory the great",
    "gregory ii",
    "gregory iii",
    "gregory vii",
    "hilarius",
    "hilary",
    "hormisdas",
    "hyginus",
    "innocent i",
    "john i",
    "john xxiii",
    "john paul ii",
    "julius i",
    "leo i",
    "leo i the great",
    "leo the great",
    "leo ii",
    "leo iii",
    "leo iv",
    "leo ix",
    "linus",
    "lucius i",
    "marcellinus",
    "marcellus i",
    "mark",
    "martin i",
    "miltiades",
    "nicholas i",
    "nicholas i the great",
    "nicholas the great",
    "paschal i",
    "paul i",
    "paul vi",
    "peter",
    "pius i",
    "pius v",
    "pius x",
    "pontian",
    "sergius i",
    "silverius",
    "simplicius",
    "siricius",
    "sixtus i",
    "sixtus ii",
    "sixtus iii",
    "soter",
    "stephen i",
    "sylvester i",
    "symmachus",
    "telesphorus",
    "urban i",
    "victor i",
    "vitalian",
    "zachary",
    "zephyrinus",
    "zosimus",
)

private fun ContentSummary.isDeclaredSaint(): Boolean {
    val normalizedName = name
        .trim()
        .lowercase()
        .removePrefix("pope ")
        .removePrefix("saint ")
        .removePrefix("st. ")
        .replace(Regex("\\s+"), " ")
        .trim()

    val summaryText = summary.lowercase()

    return normalizedName in canonizedPopeNames ||
            name.trim().lowercase().startsWith("saint ") ||
            name.trim().lowercase().startsWith("st. ") ||
            " saint " in " $summaryText " ||
            "canonized" in summaryText ||
            "canonised" in summaryText ||
            "recognized as a saint" in summaryText ||
            "recognised as a saint" in summaryText ||
            "venerated as a saint" in summaryText
}