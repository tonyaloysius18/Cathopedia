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
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.displayName
import com.ynotlabs.cathopedia.ui.singularLabel
import org.jetbrains.compose.resources.painterResource

private val EntityBg = Color(0xFF061A13)
private val EntityHeader = Color(0xFF0B271D)
private val EntityCard = Color(0xFF102D23)
private val EntityCardPressed = Color(0xFF15382B)
private val EntityBorder = Color(0xFF294C3D)
private val EntityGold = Color(0xFFE0B844)
private val EntityGoldSoft = Color(0xFF9B8651)
private val EntityCream = Color(0xFFF5EEDF)
private val EntityMuted = Color(0xFFAAA58E)
private val EntityTag = Color(0xFF174B35)
private val EntitySheet = Color(0xFF0C261D)

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
    var items by remember(type) { mutableStateOf<List<ContentSummary>?>(null) }
    var query by remember(type) { mutableStateOf("") }
    var searchVisible by remember(type) { mutableStateOf(false) }
    var selectedCentury by remember(type) { mutableStateOf<String?>(null) }
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
                .map { centuryLabel(it.sortYear) }
                .distinct()
        } else {
            emptyList()
        }
    }

    val filteredItems = remember(currentItems, query, selectedCentury, sortOption, type) {
        val source = currentItems.orEmpty()

        val searched = source.filter { item ->
            query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.summary.contains(query, ignoreCase = true)
        }

        val centuryFiltered = if (type == ContentType.POPE && selectedCentury != null) {
            searched.filter { centuryLabel(it.sortYear) == selectedCentury }
        } else {
            searched
        }

        when (sortOption) {
            EntitySortOption.NAME_ASC -> centuryFiltered.sortedBy { it.name.lowercase() }
            EntitySortOption.NAME_DESC -> centuryFiltered.sortedByDescending { it.name.lowercase() }
            EntitySortOption.EARLIEST_FIRST -> centuryFiltered.sortedBy { it.sortYear ?: Long.MAX_VALUE }
            EntitySortOption.LATEST_FIRST -> centuryFiltered.sortedByDescending { it.sortYear ?: Long.MIN_VALUE }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EntityBg),
    ) {
        EntityTopArea(
            title = type.displayName(),
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

        if (currentItems == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = EntityGold)
            }
        } else {
            if (type == ContentType.POPE && allCenturies.isNotEmpty()) {
                CenturyChipStrip(
                    centuries = allCenturies,
                    selectedCentury = selectedCentury,
                    onCenturySelected = { selectedCentury = it },
                )
            }

            if (filteredItems.isEmpty()) {
                EmptyEntityState(
                    query = query,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 4.dp,
                        bottom = 126.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (type == ContentType.POPE) {
                        val grouped = filteredItems.groupBy { centuryLabel(it.sortYear) }
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
        }
    }

    if (showFilterSheet) {
        EntityFilterSheet(
            type = type,
            centuries = allCenturies,
            selectedCentury = selectedCentury,
            selectedSort = sortOption,
            onCenturySelected = { selectedCentury = it },
            onSortSelected = { sortOption = it },
            onDismiss = { showFilterSheet = false },
            onApply = { showFilterSheet = false },
        )
    }
}

@Composable
private fun EntityTopArea(
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(EntityBg)
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
                    contentDescription = "Back",
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
                    text = if (title.equals("Popes", ignoreCase = true)) {
                        "Explore the successors of St. Peter through the centuries"
                    } else {
                        count?.let { "$it entries · Explore through history" }
                            ?: "Explore through history"
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
                        contentDescription = "Filter and sort",
                        tint = EntityCream,
                        modifier = Modifier.size(24.dp),
                    )
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
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
                        text = "Search ${title.lowercase()} by name or description…",
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
                            contentDescription = "Close search",
                            tint = EntityMuted,
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF102C22),
                    unfocusedContainerColor = Color(0xFF102C22),
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EntityBg)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PremiumFilterChip(
            text = "All",
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
private fun PremiumFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) EntityCream else Color.Transparent
    val foreground = if (selected) Color(0xFF173025) else EntityCream
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
    val portrait = Portraits.fullForEntity(type, item.id)
    val isChurchCard = type.displayName().contains("church", ignoreCase = true) ||
            type.displayName().contains("basilica", ignoreCase = true) ||
            type.displayName().contains("shrine", ignoreCase = true)
    val isSaintPope = type == ContentType.POPE && item.isDeclaredSaint()
    val popeTimeline = if (type == ContentType.POPE) item.papacyTimeline() else null
    val saintFeastDay = if (type == ContentType.SAINT) item.feastDayLabel() else null
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
                        .background(Color(0xFF17382C))
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
                            text = "SAINT",
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
                        text = type.singularLabel().uppercase(),
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
                color = Color(0xFF203C2F),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    EntityGoldSoft.copy(alpha = 0.7f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open ${item.name}",
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
    selectedSort: EntitySortOption,
    onCenturySelected: (String?) -> Unit,
    onSortSelected: (EntitySortOption) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
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
                    text = "Filter & Sort",
                    color = EntityCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = EntityCream,
                    )
                }
            }

            if (type == ContentType.POPE && centuries.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                FilterSectionLabel("CENTURY")
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PremiumFilterChip(
                        text = "All",
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

            Spacer(Modifier.height(24.dp))
            FilterSectionLabel("SORT BY")
            Spacer(Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SortChoice(
                        text = "Name (A–Z)",
                        selected = selectedSort == EntitySortOption.NAME_ASC,
                        modifier = Modifier.weight(1f),
                        onClick = { onSortSelected(EntitySortOption.NAME_ASC) },
                    )
                    SortChoice(
                        text = "Name (Z–A)",
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
                        text = "Earliest first",
                        selected = selectedSort == EntitySortOption.EARLIEST_FIRST,
                        modifier = Modifier.weight(1f),
                        onClick = { onSortSelected(EntitySortOption.EARLIEST_FIRST) },
                    )
                    SortChoice(
                        text = "Latest first",
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
                    contentColor = Color(0xFF13261E),
                ),
            ) {
                Text(
                    text = "Apply Filter",
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
            text = if (query.isBlank()) "Nothing to show" else "No matches found",
            color = EntityCream,
            fontFamily = FontFamily.Serif,
            fontSize = 22.sp,
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = if (query.isBlank()) {
                "Content will appear here when available."
            } else {
                "Try another name, keyword, or century."
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

/** "1st century", "11th century", "21st century" from the repository's sort key. */
private fun centuryLabel(sortKey: Long?): String {
    if (sortKey == null) return "Unknown era"
    val year = sortKey / 12
    val century = ((year - 1) / 100 + 1).toInt()
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
    return "$century$suffix century"
}


private fun ContentSummary.papacyTimeline(): String? {
    val start = papacyStart?.trim()?.takeIf { it.isNotEmpty() }
    val end = papacyEnd?.trim()?.takeIf { it.isNotEmpty() }

    return when {
        start != null && end != null -> "$start – $end"
        start != null -> "$start – Present"
        else -> null
    }
}

private fun ContentSummary.feastDayLabel(): String? {
    feastDay?.trim()?.takeIf { it.isNotEmpty() }?.let {
        return "Feast Day · $it"
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
    return "Feast Day · ${match.groupValues[1].trim()}"
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