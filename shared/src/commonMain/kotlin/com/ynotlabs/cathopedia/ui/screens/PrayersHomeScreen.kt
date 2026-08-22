package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.PrayerCategory
import com.ynotlabs.cathopedia.model.PrayerSummary
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.prayer_category_eucharistic
import com.ynotlabs.cathopedia.resources.prayer_category_everyday
import com.ynotlabs.cathopedia.resources.prayer_category_holy_spirit
import com.ynotlabs.cathopedia.resources.prayer_category_marian
import com.ynotlabs.cathopedia.resources.prayer_category_occasional
import com.ynotlabs.cathopedia.resources.prayer_category_penitential
import com.ynotlabs.cathopedia.resources.prayer_category_saints
import com.ynotlabs.cathopedia.resources.prayer_category_sequences
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private const val SEARCH_DEBOUNCE_MS = 300L
private const val HOLY_ROSARY_ID = "holy-rosary"

private val PrayerBg = Color(0xFF061A13)
private val PrayerSurface = Color(0xFF0C271E)
private val PrayerSurfaceRaised = Color(0xFF123127)
private val PrayerBorder = Color(0xFF315444)
private val PrayerGold = Color(0xFFD8B24C)
private val PrayerGoldSoft = Color(0xFF9D8858)
private val PrayerCream = Color(0xFFF4ECDD)
private val PrayerMuted = Color(0xFFB4AD98)
private val RosaryPurple = Color(0xFF2A1838)
private val RosaryPurpleLight = Color(0xFF5C356F)

private enum class PrayerQuickSection {
    FAVOURITES,
    EVERYDAY,
    MARIAN,
    HOLY_SPIRIT,
    EUCHARISTIC,
    SAINTS,
    PENITENTIAL,
    CHAPLETS_NOVENAS,
    OCCASIONAL,
}

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
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<PrayerSummary>>(emptyList()) }
    var quickSection by remember { mutableStateOf(PrayerQuickSection.EVERYDAY) }

    LaunchedEffect(language) {
        favorites = repository.listFavoritePrayers(language)
        byCategory = PrayerCategory.entries.associateWith { repository.listPrayers(it, language) }
    }

    LaunchedEffect(query, language) {
        if (query.isBlank()) {
            searchResults = emptyList()
            return@LaunchedEffect
        }
        delay(SEARCH_DEBOUNCE_MS)
        searchResults = repository.searchPrayers(query, language)
    }

    fun openPrayer(prayer: PrayerSummary) {
        if (prayer.isSequence && prayer.id == HOLY_ROSARY_ID) {
            onOpenRosary()
        } else {
            onOpenPrayer(prayer.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrayerBg)
            .statusBarsPadding(),
    ) {
        PrayerHeader()

        PrayerSearchBar(
            query = query,
            placeholder = s.prayersSearchPlaceholder,
            searchDescription = s.searchDesc,
            closeDescription = s.close,
            onQueryChange = { query = it },
            onClear = { query = "" },
        )

        if (query.isNotBlank()) {
            PrayerSearchResults(
                query = query,
                results = searchResults,
                onSelect = ::openPrayer,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        } else {
            PrayerHomeBody(
                favorites = favorites,
                byCategory = byCategory,
                selectedSection = quickSection,
                onSectionSelected = { quickSection = it },
                onSelect = ::openPrayer,
                onOpenRosary = onOpenRosary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PrayerHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Prayers",
                color = PrayerCream,
                fontFamily = FontFamily.Serif,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Strengthen your faith with daily prayer",
                color = PrayerMuted,
                fontSize = 13.5.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun PrayerSearchBar(
    query: String,
    placeholder: String,
    searchDescription: String,
    closeDescription: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        placeholder = {
            Text(
                text = placeholder,
                color = PrayerMuted.copy(alpha = 0.82f),
                fontSize = 14.sp,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = searchDescription,
                tint = PrayerGold,
                modifier = Modifier.size(23.dp),
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = closeDescription,
                        tint = PrayerGoldSoft,
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = PrayerSurface,
            unfocusedContainerColor = PrayerSurface.copy(alpha = 0.90f),
            focusedBorderColor = PrayerGold.copy(alpha = 0.70f),
            unfocusedBorderColor = PrayerBorder,
            focusedTextColor = PrayerCream,
            unfocusedTextColor = PrayerCream,
            cursorColor = PrayerGold,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .height(58.dp),
    )
}

@Composable
private fun PrayerHomeBody(
    favorites: List<PrayerSummary>,
    byCategory: Map<PrayerCategory, List<PrayerSummary>>,
    selectedSection: PrayerQuickSection,
    onSectionSelected: (PrayerQuickSection) -> Unit,
    onSelect: (PrayerSummary) -> Unit,
    onOpenRosary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val visiblePrayers = prayersForSection(
        section = selectedSection,
        favorites = favorites,
        byCategory = byCategory,
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 8.dp,
            bottom = 124.dp,
        ),
    ) {
        item {
            RosaryHeroCard(onClick = onOpenRosary)

            Spacer(Modifier.height(12.dp))

            WayOfTheCrossCard()

            Spacer(Modifier.height(18.dp))

            QuickPrayerCategories(
                selected = selectedSection,
                favoritesCount = favorites.size,
                onSelected = onSectionSelected,
            )

            Spacer(Modifier.height(22.dp))

            PrayerSectionHeader(
                title = when (selectedSection) {
                    PrayerQuickSection.FAVOURITES -> "My Favourites"
                    PrayerQuickSection.EVERYDAY -> "Everyday prayers"
                    PrayerQuickSection.MARIAN -> "Marian prayers"
                    PrayerQuickSection.HOLY_SPIRIT -> "Holy Spirit prayers"
                    PrayerQuickSection.EUCHARISTIC -> "Eucharistic prayers"
                    PrayerQuickSection.SAINTS -> "Saint prayers"
                    PrayerQuickSection.PENITENTIAL -> "Penitential prayers"
                    PrayerQuickSection.CHAPLETS_NOVENAS -> "Chaplets & Novenas"
                    PrayerQuickSection.OCCASIONAL -> "Occasional prayers"
                },
                count = visiblePrayers.size,
            )

            Spacer(Modifier.height(10.dp))
        }

        if (visiblePrayers.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = PrayerSurface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = PrayerBorder.copy(alpha = 0.66f),
                    ),
                ) {
                    Text(
                        text = if (selectedSection == PrayerQuickSection.FAVOURITES) {
                            "Your favourite prayers will appear here."
                        } else {
                            s.listContentWillAppear
                        },
                        modifier = Modifier.padding(18.dp),
                        color = PrayerMuted,
                        fontSize = 13.sp,
                    )
                }
            }
        } else {
            items(
                items = visiblePrayers,
                key = { "${selectedSection.name}-${it.id}" },
            ) { prayer ->
                PremiumPrayerRow(
                    prayer = prayer,
                    onClick = { onSelect(prayer) },
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RosaryHeroCard(
    onClick: () -> Unit,
) {
    val s = LocalStrings.current
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        RosaryPurple,
                        Color(0xFF382149),
                        Color(0xFF241631),
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = PrayerGoldSoft.copy(alpha = 0.52f),
                shape = shape,
            )
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(190.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            RosaryPurpleLight.copy(alpha = 0.62f),
                            Color.Transparent,
                        ),
                    ),
                    CircleShape,
                ),
        )

        RosaryBeadDecoration(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
        ) {
            Text(
                text = s.prayersRosaryCardTitle.uppercase(),
                color = PrayerGold,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = s.prayersRosaryCardSubtitle,
                modifier = Modifier.fillMaxWidth(0.53f),
                color = PrayerCream.copy(alpha = 0.90f),
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
            )

            Spacer(Modifier.weight(1f))

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent,
                border = BorderStroke(
                    width = 1.dp,
                    color = PrayerGold.copy(alpha = 0.78f),
                ),
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onClick)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Pray Now",
                        color = PrayerGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = PrayerGold,
                        modifier = Modifier.size(17.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun WayOfTheCrossCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        PrayerSurfaceRaised,
                        PrayerSurface,
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = PrayerGoldSoft.copy(alpha = 0.45f),
                shape = RoundedCornerShape(24.dp),
            )
            .padding(18.dp),
    ) {
        Column {
            Text(
                text = "WAY OF THE CROSS",
                color = PrayerGold,
                fontSize = 12.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Walk with Christ through His Passion",
                color = PrayerCream,
                fontFamily = FontFamily.Serif,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Meditate on the fourteen Stations of the Cross.",
                color = PrayerMuted,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun RosaryBeadDecoration(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(150.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Decorative approximation of rosary beads using Compose circles.
        repeat(12) { index ->
            val angleBucket = index % 4
            val x = when (angleBucket) {
                0 -> 0.dp
                1 -> 42.dp
                2 -> 0.dp
                else -> (-42).dp
            }
            val y = when (angleBucket) {
                0 -> (-52).dp
                1 -> 0.dp
                2 -> 52.dp
                else -> 0.dp
            }

            Box(
                modifier = Modifier
                    .padding(start = x.coerceAtLeast(0.dp), top = y.coerceAtLeast(0.dp))
                    .size(if (index % 3 == 0) 15.dp else 11.dp)
                    .background(
                        if (index % 3 == 0) PrayerGold else Color(0xFF684B78),
                        CircleShape,
                    )
                    .border(
                        width = 0.7.dp,
                        color = PrayerGold.copy(alpha = 0.38f),
                        shape = CircleShape,
                    ),
            )
        }

        Surface(
            modifier = Modifier.size(58.dp),
            shape = CircleShape,
            color = Color(0xFF2A1838),
            border = BorderStroke(
                width = 1.2.dp,
                color = PrayerGold.copy(alpha = 0.62f),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = PrayerGold,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun QuickPrayerCategories(
    selected: PrayerQuickSection,
    favoritesCount: Int,
    onSelected: (PrayerQuickSection) -> Unit,
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(PrayerQuickSection.entries.size) { index ->
            val section = PrayerQuickSection.entries[index]

            QuickPrayerTile(
                title = when (section) {
                    PrayerQuickSection.FAVOURITES -> "Favourites"
                    PrayerQuickSection.EVERYDAY -> "Everyday"
                    PrayerQuickSection.MARIAN -> "Marian"
                    PrayerQuickSection.HOLY_SPIRIT -> "Holy Spirit"
                    PrayerQuickSection.EUCHARISTIC -> "Eucharistic"
                    PrayerQuickSection.SAINTS -> "Saints"
                    PrayerQuickSection.PENITENTIAL -> "Penitential"
                    PrayerQuickSection.CHAPLETS_NOVENAS -> "Chaplets & Novenas"
                    PrayerQuickSection.OCCASIONAL -> "Occasional"
                },
                subtitle = when (section) {
                    PrayerQuickSection.FAVOURITES -> "$favoritesCount saved"
                    PrayerQuickSection.EVERYDAY -> "Daily prayers"
                    else -> "Explore"
                },
                icon = when (section) {
                    PrayerQuickSection.FAVOURITES -> Icons.Filled.Favorite
                    else -> null
                },
                categoryIcon = when (section) {
                    PrayerQuickSection.FAVOURITES -> null
                    PrayerQuickSection.EVERYDAY -> Res.drawable.prayer_category_everyday
                    PrayerQuickSection.MARIAN -> Res.drawable.prayer_category_marian
                    PrayerQuickSection.HOLY_SPIRIT -> Res.drawable.prayer_category_holy_spirit
                    PrayerQuickSection.EUCHARISTIC -> Res.drawable.prayer_category_eucharistic
                    PrayerQuickSection.SAINTS -> Res.drawable.prayer_category_saints
                    PrayerQuickSection.PENITENTIAL -> Res.drawable.prayer_category_penitential
                    PrayerQuickSection.CHAPLETS_NOVENAS -> Res.drawable.prayer_category_sequences
                    PrayerQuickSection.OCCASIONAL -> Res.drawable.prayer_category_occasional
                },
                selected = selected == section,
                modifier = Modifier.width(108.dp),
                onClick = { onSelected(section) },
            )
        }
    }
}

@Composable
private fun QuickPrayerTile(
    title: String,
    subtitle: String,
    icon: ImageVector?,
    categoryIcon: DrawableResource?,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(17.dp)

    Column(
        modifier = modifier
            .height(128.dp)
            .clip(shape)
            .background(
                if (selected) PrayerSurfaceRaised else PrayerSurface,
            )
            .border(
                width = if (selected) 1.4.dp else 1.dp,
                color = if (selected) PrayerGold else PrayerBorder.copy(alpha = 0.70f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (categoryIcon != null) {
            Image(
                painter = painterResource(categoryIcon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(46.dp),
            )
        } else {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = PrayerGold.copy(alpha = if (selected) 0.14f else 0.07f),
                border = BorderStroke(
                    width = 1.dp,
                    color = PrayerGold.copy(alpha = if (selected) 0.52f else 0.24f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon ?: Icons.Filled.Star,
                        contentDescription = null,
                        tint = PrayerGold,
                        modifier = Modifier.size(23.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = title,
            color = PrayerCream,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = subtitle,
            color = PrayerMuted,
            fontSize = 9.5.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun PrayerSectionHeader(
    title: String,
    count: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(22.dp)
                .background(PrayerGold, RoundedCornerShape(2.dp)),
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = title.uppercase(),
            color = PrayerGold,
            fontSize = 10.5.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "• $count",
            color = PrayerGoldSoft,
            fontSize = 10.5.sp,
        )

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun PremiumPrayerRow(
    prayer: PrayerSummary,
    onClick: () -> Unit,
    query: String? = null,
) {
    val s = LocalStrings.current
    val isComingSoon = prayer.isSequence && prayer.id != HOLY_ROSARY_ID
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        PrayerSurfaceRaised,
                        PrayerSurface,
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = PrayerBorder.copy(alpha = 0.64f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(54.dp),
            shape = CircleShape,
            color = PrayerGold.copy(alpha = 0.08f),
            border = BorderStroke(
                width = 1.dp,
                color = PrayerGold.copy(alpha = 0.28f),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = prayerIcon(prayer),
                    contentDescription = null,
                    tint = PrayerGold,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            HighlightedText(
                text = prayer.title,
                query = query,
                color = PrayerCream,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = prayer.subtitle ?: prayerOpeningLine(prayer.title),
                color = PrayerMuted,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (isComingSoon) {
            Text(
                text = s.prayersSequenceComingSoon,
                color = PrayerGoldSoft,
                fontSize = 10.sp,
            )
        } else {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = PrayerGold.copy(alpha = 0.08f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Open prayer",
                        tint = PrayerGold,
                        modifier = Modifier.size(16.dp),
                    )
                }
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
        Column(
            modifier = modifier.padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = PrayerGold.copy(alpha = 0.07f),
                border = BorderStroke(
                    width = 1.dp,
                    color = PrayerGold.copy(alpha = 0.26f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = PrayerGold,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = s.prayersSearchNoResults,
                color = PrayerCream,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Try another prayer name or devotional keyword.",
                color = PrayerMuted,
                fontSize = 12.5.sp,
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 8.dp,
            bottom = 124.dp,
        ),
    ) {
        item {
            PrayerSectionHeader(
                title = "Search results",
                count = results.size,
            )
            Spacer(Modifier.height(10.dp))
        }

        items(results, key = { it.id }) { prayer ->
            PremiumPrayerRow(
                prayer = prayer,
                query = query,
                onClick = { onSelect(prayer) },
            )
            Spacer(Modifier.height(8.dp))
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
    val annotated = remember(text, query) {
        highlight(
            text = text,
            query = query,
            highlightColor = PrayerGold,
        )
    }

    Text(
        text = annotated,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

private fun highlight(
    text: String,
    query: String?,
    highlightColor: Color,
): AnnotatedString {
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

            withStyle(
                SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.Bold,
                ),
            ) {
                append(text.substring(idx, idx + lowerQuery.length))
            }

            i = idx + lowerQuery.length
        }
    }
}

private fun prayersForSection(
    section: PrayerQuickSection,
    favorites: List<PrayerSummary>,
    byCategory: Map<PrayerCategory, List<PrayerSummary>>,
): List<PrayerSummary> = when (section) {
    PrayerQuickSection.FAVOURITES ->
        favorites

    PrayerQuickSection.EVERYDAY ->
        byCategory[PrayerCategory.EVERYDAY].orEmpty()

    PrayerQuickSection.MARIAN ->
        byCategory[PrayerCategory.MARIAN].orEmpty()

    PrayerQuickSection.HOLY_SPIRIT ->
        byCategory[PrayerCategory.HOLY_SPIRIT].orEmpty()

    PrayerQuickSection.EUCHARISTIC ->
        byCategory[PrayerCategory.EUCHARISTIC].orEmpty()

    PrayerQuickSection.SAINTS ->
        byCategory[PrayerCategory.SAINTS].orEmpty()

    PrayerQuickSection.PENITENTIAL ->
        byCategory[PrayerCategory.PENITENTIAL].orEmpty()

    PrayerQuickSection.CHAPLETS_NOVENAS ->
        byCategory[PrayerCategory.SEQUENCES].orEmpty()

    PrayerQuickSection.OCCASIONAL ->
        byCategory[PrayerCategory.OCCASIONAL].orEmpty()
}

private fun prayerIcon(prayer: PrayerSummary): ImageVector {
    val key = prayer.title.lowercase()

    return when {
        "mary" in key || "marian" in key -> Icons.Filled.Favorite
        "creed" in key -> Icons.Filled.AutoStories
        "morning" in key || "evening" in key -> Icons.Filled.Event
        else -> Icons.Filled.Star
    }
}

private fun prayerOpeningLine(title: String): String {
    val key = title.lowercase()

    return when {
        "sign of the cross" in key ->
            "In the name of the Father, and of the Son..."

        "our father" in key ->
            "Our Father, who art in heaven..."

        "hail mary" in key ->
            "Hail Mary, full of grace..."

        "glory be" in key ->
            "Glory be to the Father, and to the Son..."

        "apostles" in key && "creed" in key ->
            "I believe in God, the Father almighty..."

        "nicene" in key && "creed" in key ->
            "I believe in one God, the Father almighty..."

        "morning offering" in key ->
            "Begin your day by offering it to God."

        "evening prayer" in key ->
            "End the day in gratitude and reflection."

        else ->
            "Open this prayer to read and pray."
    }
}

private fun categoryLabel(
    category: PrayerCategory,
    s: Strings,
): String = when (category) {
    PrayerCategory.EVERYDAY -> s.prayerCategoryEveryday
    PrayerCategory.MARIAN -> s.prayerCategoryMarian
    PrayerCategory.HOLY_SPIRIT -> s.prayerCategoryHolySpirit
    PrayerCategory.EUCHARISTIC -> s.prayerCategoryEucharistic
    PrayerCategory.SAINTS -> s.prayerCategorySaints
    PrayerCategory.PENITENTIAL -> s.prayerCategoryPenitential
    PrayerCategory.SEQUENCES -> s.prayerCategorySequences
    PrayerCategory.OCCASIONAL -> s.prayerCategoryOccasional
}
