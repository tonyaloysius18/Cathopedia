package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.ContentCategory
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.model.HubSummary
import com.ynotlabs.cathopedia.ui.displayName
import com.ynotlabs.cathopedia.ui.label
import org.jetbrains.compose.resources.painterResource
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.explore_apostles
import com.ynotlabs.cathopedia.resources.explore_churches
import com.ynotlabs.cathopedia.resources.explore_eucharistic
import com.ynotlabs.cathopedia.resources.explore_feasts
import com.ynotlabs.cathopedia.resources.explore_bg
import com.ynotlabs.cathopedia.resources.explore_marian
import com.ynotlabs.cathopedia.resources.explore_popes
import com.ynotlabs.cathopedia.resources.explore_saints
import com.ynotlabs.cathopedia.resources.saints_icon
import com.ynotlabs.cathopedia.resources.popes_icon
import com.ynotlabs.cathopedia.resources.apostles_icon
import com.ynotlabs.cathopedia.resources.churches_shrines_icon
import com.ynotlabs.cathopedia.resources.marian_apparitions_icon
import com.ynotlabs.cathopedia.resources.eucharistic_miracles_icon
import com.ynotlabs.cathopedia.resources.liturgical_feasts_icon

@Composable
fun ExploreScreen(
    repository: CathopediaRepository,
    language: String,
    onCategorySelected: (ContentType) -> Unit,
    onHubSelected: (HubSummary) -> Unit,
) {
    val s = LocalStrings.current
    var counts by remember { mutableStateOf<Map<ContentType, Int>>(emptyMap()) }
    var query by remember { mutableStateOf("") }
    var hubs by remember { mutableStateOf<List<HubSummary>>(emptyList()) }
    var hubStrings by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        counts = ContentType.entries.associateWith { repository.listByType(it, language).size }
        hubs = repository.listHubs()
        val keys = hubs.flatMap { listOfNotNull(it.titleKey, it.subtitleKey) }.toSet()
        hubStrings = repository.resolveHubStrings(keys, language)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            bottom = 20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        item {
            ExploreHero(query) { query = it }
        }

        if (hubs.isNotEmpty() && query.isBlank()) {
            item {
                ExploreSectionHeader(s.exploreHubsHeader.uppercase())
            }
            items(hubs, key = { it.id }) { hub ->
                HubExploreCard(
                    hub = hub,
                    title = hubStrings[hub.titleKey].orEmpty(),
                    subtitle = hub.subtitleKey?.let { hubStrings[it] },
                    onClick = { onHubSelected(hub) },
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        ContentCategory.entries.forEach { category ->
            val visibleTypes = category.types.filter { type ->
                query.isBlank() || type.displayName(s).contains(query, ignoreCase = true)
            }

            if (visibleTypes.isNotEmpty()) {
                item {
                    ExploreSectionHeader(category.label(s).uppercase())
                }

                when (category) {
                    ContentCategory.PEOPLE -> {
                        item {
                            PeopleSection(
                                types = visibleTypes,
                                counts = counts,
                                onCategorySelected = onCategorySelected,
                            )
                        }
                    }

                    ContentCategory.EVENTS -> {
                        item {
                            EventsSection(
                                types = visibleTypes,
                                counts = counts,
                                onCategorySelected = onCategorySelected,
                            )
                        }
                    }

                    else -> {
                        visibleTypes.forEach { type ->
                            item {
                                WideExploreCard(
                                    type = type,
                                    count = counts[type] ?: 0,
                                    onClick = { onCategorySelected(type) },
                                )
                            }
                        }
                    }
                }
            }
        }

        if (query.isNotBlank()) {
            val hasAnyResult = ContentCategory.entries.any { category ->
                category.types.any { it.displayName(s).contains(query, ignoreCase = true) }
            }

            if (!hasAnyResult) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 44.dp, bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = s.exploreNoResultsFound,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = s.exploreTryAnotherCategory,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreHero(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 0.dp),
    ) {
        // Header artwork follows the same treatment as Settings:
        // artwork on the top-right with green fades keeping the text readable.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.explore_bg),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth(0.58f)
                    .height(126.dp)
                    .padding(top = 4.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.TopEnd,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 1f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.42f),
                                Color.Transparent,
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
                                MaterialTheme.colorScheme.background.copy(alpha = 0.18f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.94f),
                            ),
                        ),
                    ),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 14.dp, end = 150.dp),
            ) {
                Text(
                    text = s.exploreTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = s.exploreSubtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            placeholder = {
                Text(
                    text = s.exploreSearchPlaceholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f),
                    fontSize = 14.sp,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
        )
    }
}

@Composable
private fun ExploreSectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.9.sp,
        )

        Spacer(Modifier.size(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.48f)),
        )
    }
}

@Composable
private fun PeopleSection(
    types: List<ContentType>,
    counts: Map<ContentType, Int>,
    onCategorySelected: (ContentType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val featured = types.take(2)

        if (featured.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                featured.forEach { type ->
                    PortraitExploreCard(
                        type = type,
                        count = counts[type] ?: 0,
                        onClick = { onCategorySelected(type) },
                        modifier = Modifier.weight(1f),
                    )
                }

                if (featured.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        types.drop(2).forEach { type ->
            WideExploreCard(
                type = type,
                count = counts[type] ?: 0,
                onClick = { onCategorySelected(type) },
            )
        }
    }
}

@Composable
private fun EventsSection(
    types: List<ContentType>,
    counts: Map<ContentType, Int>,
    onCategorySelected: (ContentType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        types.chunked(2).forEach { rowTypes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowTypes.forEach { type ->
                    EventExploreCard(
                        type = type,
                        count = counts[type] ?: 0,
                        onClick = { onCategorySelected(type) },
                        modifier = Modifier.weight(1f),
                    )
                }

                if (rowTypes.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PortraitExploreCard(
    type: ContentType,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val title = type.displayName(s)

    Card(
        modifier = modifier
            .height(156.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            CategoryArtwork(
                type = type,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, start = 6.dp, end = 2.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterEnd,
            )

            ArtworkFadeOverlay(
                includeBottomFade = true,
            )

            CategoryGlyph(
                type = type,
                title = title,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                size = 30.dp,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = humanCountLabel(type, count, s),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun EventExploreCard(
    type: ContentType,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val title = type.displayName(s)

    Card(
        modifier = modifier
            .aspectRatio(1.3f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            CategoryArtwork(
                type = type,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, start = 6.dp, end = 2.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.CenterEnd,
            )

            ArtworkFadeOverlay(
                includeBottomFade = true,
            )

            CategoryGlyph(
                type = type,
                title = title,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                size = 30.dp,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = humanCountLabel(type, count, s),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun WideExploreCard(
    type: ContentType,
    count: Int,
    onClick: () -> Unit,
) {
    val s = LocalStrings.current
    val title = type.displayName(s)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(122.dp)
            .padding(bottom = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            CategoryArtwork(
                type = type,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 0.dp, start = 110.dp, end = 0.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.BottomEnd,
            )

            val needsStrongerWideFade = type == ContentType.CHURCH || type == ContentType.FEAST

            ArtworkFadeOverlay(
                wideCardFade = needsStrongerWideFade,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                CategoryGlyph(type = type, title = title, size = 30.dp)

                Column {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = humanCountLabel(type, count, s),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun HubExploreCard(
    hub: HubSummary,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
) {
    val accent = hub.accentColor?.let { hex ->
        val cleaned = hex.removePrefix("#")
        cleaned.toLongOrNull(16)?.let { value ->
            if (cleaned.length == 6) Color(0xFF000000 or value) else Color(value)
        }
    } ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.2.dp, accent.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accent, androidx.compose.foundation.shape.CircleShape),
            )
            Spacer(Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtworkFadeOverlay(
    includeBottomFade: Boolean = false,
    wideCardFade: Boolean = false,
) {
    val bg = MaterialTheme.colorScheme.background

    val horizontalFade = if (wideCardFade) {
        Brush.horizontalGradient(
            0f to bg.copy(alpha = 1f),
            0.34f to bg.copy(alpha = 0.94f),
            0.58f to bg.copy(alpha = 0.70f),
            0.80f to bg.copy(alpha = 0.24f),
            1f to Color.Transparent,
        )
    } else {
        Brush.horizontalGradient(
            0f to bg.copy(alpha = 0.98f),
            0.38f to bg.copy(alpha = 0.78f),
            0.68f to bg.copy(alpha = 0.26f),
            1f to Color.Transparent,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(horizontalFade),
    )

    if (includeBottomFade) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.58f to Color.Transparent,
                        1f to bg.copy(alpha = 0.95f),
                    )
                )
        )
    }
}

@Composable
private fun CategoryArtwork(
    type: ContentType,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
) {
    val painter = when (type) {
        ContentType.SAINT -> painterResource(Res.drawable.explore_saints)
        ContentType.POPE -> painterResource(Res.drawable.explore_popes)
        ContentType.APOSTLE -> painterResource(Res.drawable.explore_apostles)
        ContentType.CHURCH -> painterResource(Res.drawable.explore_churches)
        ContentType.APPARITION -> painterResource(Res.drawable.explore_marian)
        ContentType.MIRACLE -> painterResource(Res.drawable.explore_eucharistic)
        ContentType.FEAST -> painterResource(Res.drawable.explore_feasts)
    }

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = contentScale,
        alignment = alignment,
        modifier = modifier,
    )
}

@Composable
private fun CategoryGlyph(
    type: ContentType,
    title: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp,
) {
    val iconPainter = when (type) {
        ContentType.SAINT -> painterResource(Res.drawable.saints_icon)
        ContentType.POPE -> painterResource(Res.drawable.popes_icon)
        ContentType.APOSTLE -> painterResource(Res.drawable.apostles_icon)
        ContentType.CHURCH -> painterResource(Res.drawable.churches_shrines_icon)
        ContentType.APPARITION -> painterResource(Res.drawable.marian_apparitions_icon)
        ContentType.MIRACLE -> painterResource(Res.drawable.eucharistic_miracles_icon)
        ContentType.FEAST -> painterResource(Res.drawable.liturgical_feasts_icon)
    }

    Image(
        painter = iconPainter,
        contentDescription = "$title icon",
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size),
    )
}

private fun humanCountLabel(type: ContentType, count: Int, s: Strings): String {
    val template = when (type) {
        ContentType.SAINT -> s.countHolyLives
        ContentType.POPE -> s.countPontiffs
        ContentType.APOSTLE -> s.countWitnesses
        ContentType.CHURCH -> s.countSacredPlaces
        ContentType.APPARITION -> s.countApparitions
        ContentType.MIRACLE -> s.countMiracles
        ContentType.FEAST -> return if (count > 0) {
            s.countFeastCelebrations.replace("{count}", count.toString())
        } else {
            s.countFeastFallback
        }
    }
    return template.replace("{count}", count.toString())
}