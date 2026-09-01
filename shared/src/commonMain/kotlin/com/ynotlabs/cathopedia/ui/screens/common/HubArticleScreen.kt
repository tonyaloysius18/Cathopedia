package com.ynotlabs.cathopedia.ui.screens.common

import com.ynotlabs.cathopedia.ui.screens.catechism.ConfessionPrayersScreen
import com.ynotlabs.cathopedia.ui.screens.catechism.CreedsScreen
import com.ynotlabs.cathopedia.ui.screens.catechism.FourLastThingsScreen
import com.ynotlabs.cathopedia.ui.screens.catechism.FourMarksScreen
import com.ynotlabs.cathopedia.ui.screens.catechism.HolyTrinityScreen
import com.ynotlabs.cathopedia.ui.screens.holymass.AltarScreen
import com.ynotlabs.cathopedia.ui.screens.holymass.MonstranceScreen
import com.ynotlabs.cathopedia.ui.screens.holymass.PosturesScreen
import com.ynotlabs.cathopedia.ui.screens.holymass.ThuribleScreen
import com.ynotlabs.cathopedia.ui.screens.holymass.VesselsScreen
import com.ynotlabs.cathopedia.ui.screens.holysee.CardinalsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.HUB_ENTITY_LINK_TAG
import com.ynotlabs.cathopedia.content.model.Block
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.DiagramRefBlock
import com.ynotlabs.cathopedia.content.model.EntityCardsBlock
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.ExternalLinkBlock
import com.ynotlabs.cathopedia.content.model.FactGridBlock
import com.ynotlabs.cathopedia.content.model.HeadingBlock
import com.ynotlabs.cathopedia.content.model.ImageBlock
import com.ynotlabs.cathopedia.content.model.ListBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.content.model.QuoteBlock
import com.ynotlabs.cathopedia.content.model.StepperRefBlock
import com.ynotlabs.cathopedia.content.model.TimelineRefBlock
import com.ynotlabs.cathopedia.content.parseHubMarkup
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import org.jetbrains.compose.resources.painterResource
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.nav_search

private val SymbolCardSurface = Color(0xFF0C271E)
private val SymbolCardGold = Color(0xFFD8B24C)
private val SymbolCardCream = Color(0xFFF4ECDD)
private val SymbolCardMuted = Color(0xFFB4AD98)

private data class SymbolCardContent(
    val heading: HeadingBlock,
    val image: ImageBlock,
    val paragraph: ParagraphBlock,
)

private data class BiblicalCharacterContent(
    val heading: HeadingBlock,
    val image: ImageBlock,
    val paragraph: ParagraphBlock,
)

private data class OrderCardContent(
    val heading: HeadingBlock,
    val image: ImageBlock,        // main image (founder portrait, or the sole image)
    val emblem: ImageBlock?,      // emblem badge overlaid top-right, when present
    val paragraph: ParagraphBlock,
)

/**
 * Renders every [Block] type (docs/briefs/topic-hubs.md, T5). `DiagramRefBlock`/`StepperRefBlock`/
 * `TimelineRefBlock` are cross-references into other hub structures — with no in-article renderer
 * of their own by design (the referenced diagram/stepper/timeline is the section's own body, this
 * is just a pointer to it) they, along with anything this app version doesn't recognize, are
 * skipped silently rather than rendered as an error or a gap.
 */
@Composable
fun HubArticleScreen(
    articleId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    if (articleId == "art.cat.trinity") {
        HolyTrinityScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.cat.creeds") {
        CreedsScreen(
            repository = repository,
            language = language,
            onBack = onBack,
            onEntityRefSelected = onEntityRefSelected,
        )
        return
    }

    if (articleId == "art.cat.four_marks") {
        FourMarksScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.cat.last_things") {
        FourLastThingsScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.cat.confession_prayers") {
        ConfessionPrayersScreen(
            repository = repository,
            language = language,
            onBack = onBack,
            onEntityRefSelected = onEntityRefSelected,
        )
        return
    }

    if (articleId == "art.cardinals.overview") {
        CardinalsScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.monstrance.overview") {
        MonstranceScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.thurible.overview") {
        ThuribleScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.vessels.overview") {
        VesselsScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.postures.overview") {
        PosturesScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    if (articleId == "art.altar.overview") {
        AltarScreen(
            repository = repository,
            language = language,
            onBack = onBack,
        )
        return
    }

    val s = LocalStrings.current
    val isSymbolsArticle = articleId.startsWith("art.symbols.")
    val isBiblicalArticle = articleId.startsWith("art.biblical.")
    val isOrdersArticle = articleId.startsWith("art.orders.")
    var article by remember(articleId) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(articleId) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(articleId, language) {
        val loaded = repository.hubArticle(articleId) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { addAll(blockKeys(it)) }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    var headerHeightPx by remember(articleId, language) { mutableIntStateOf(0) }
    val headerHeightDp = with(LocalDensity.current) { headerHeightPx.toDp() }
    var orderQuery by remember(articleId) { mutableStateOf("") }
    var searchExpanded by remember(articleId) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isSymbolsArticle) Color(0xFF061A13)
                else MaterialTheme.colorScheme.background
            ),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = if (isSymbolsArticle || isBiblicalArticle || isOrdersArticle) headerHeightDp + 20.dp else 0.dp,
                end = 20.dp,
                bottom = 120.dp
            ),
        ) {
            if (current == null) return@LazyColumn

            if (!isSymbolsArticle && !isBiblicalArticle && !isOrdersArticle) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 8.dp, bottom = 12.dp),
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            CathopediaBackButton(
                                onClick = onBack,
                                contentDescription = s.back,
                            )
                        }
                    }

                    Text(
                        text = strings[current.titleKey].orEmpty(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = FontFamily.Serif,
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    current.leadKey?.let { key ->
                        strings[key]?.let { lead ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = lead,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            val symbolCards = if (isSymbolsArticle) current.blocks.asSymbolCards() else null
            val biblicalCards = if (isBiblicalArticle) current.blocks.asBiblicalCharacterCards() else null
            val orderCards = if (isOrdersArticle) current.blocks.asOrderCards() else null
            if (symbolCards != null) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when {
                                articleId == "art.symbols.crosses" && language == "fr" -> "FORMES SACRÉES · ${symbolCards.size}"
                                articleId == "art.symbols.crosses" -> "SACRED FORMS · ${symbolCards.size}"
                                language == "fr" -> "SYMBOLES · ${symbolCards.size}"
                                else -> "SYMBOLS · ${symbolCards.size}"
                            },
                            color = SymbolCardGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.5.sp,
                        )
                        Spacer(
                            Modifier
                                .padding(start = 10.dp)
                                .height(1.dp)
                                .weight(1f)
                                .background(SymbolCardGold.copy(alpha = 0.35f)),
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                }

                symbolCards.forEach { card ->
                    item {
                        SymbolCard(
                            card = card,
                            strings = strings,
                            onEntityRefSelected = onEntityRefSelected,
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }
            } else if (biblicalCards != null) {
                item {
                    BiblicalFiguresLabel(
                        count = biblicalCards.size,
                        language = language,
                    )
                    Spacer(Modifier.height(16.dp))
                }

                biblicalCards.forEach { card ->
                    item {
                        BiblicalCharacterCard(
                            card = card,
                            strings = strings,
                            onEntityRefSelected = onEntityRefSelected,
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            } else if (orderCards != null) {
                val q = orderQuery.trim()
                val filtered = if (q.isBlank()) orderCards else orderCards.filter { card ->
                    val name = strings[card.heading.textKey].orEmpty()
                    val desc = strings[card.paragraph.textKey].orEmpty()
                    name.contains(q, ignoreCase = true) || desc.contains(q, ignoreCase = true)
                }

                item {
                    OrdersHeaderRow(
                        count = filtered.size,
                        language = language,
                        searchExpanded = searchExpanded,
                        onToggleSearch = {
                            searchExpanded = !searchExpanded
                            if (!searchExpanded) orderQuery = ""
                        },
                    )
                    AnimatedVisibility(
                        visible = searchExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        Column {
                            Spacer(Modifier.height(12.dp))
                            OrderSearchField(
                                query = orderQuery,
                                onQueryChange = { orderQuery = it },
                                onClear = { orderQuery = "" },
                                language = language,
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                if (filtered.isEmpty()) {
                    item {
                        Text(
                            text = if (language == "fr") "Aucun ordre trouvé." else "No orders found.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp,
                        )
                    }
                }

                filtered.forEach { card ->
                    item {
                        OrderCard(
                            card = card,
                            strings = strings,
                            onEntityRefSelected = onEntityRefSelected,
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            } else {
                current.blocks.forEach { block ->
                    item {
                        BlockView(block, strings, onEntityRefSelected)
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }

        if ((isSymbolsArticle || isBiblicalArticle || isOrdersArticle) && current != null) {
            HubArticleHeaderCard(
                title = strings[current.titleKey].orEmpty(),
                lead = current.leadKey?.let { strings[it] },
                backDescription = s.back,
                onBack = onBack,
                modifier = Modifier.onGloballyPositioned {
                    if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
                }
            )
        }
    }
}

@Composable
private fun HubArticleHeaderCard(
    title: String,
    lead: String?,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var titleLineCount by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Color(0xFF081F17)) // HubCardDeep
            .statusBarsPadding()
            .padding(start = 18.dp, top = 6.dp, end = 18.dp, bottom = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            CathopediaBackButton(
                onClick = onBack,
                contentDescription = backDescription,
            )

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = SymbolCardCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                    onTextLayout = { titleLineCount = it.lineCount }
                )

                if (!lead.isNullOrBlank()) {
                    Spacer(Modifier.height(if (titleLineCount <= 1) 4.dp else 10.dp))
                    Text(
                        text = lead,
                        color = SymbolCardMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}

private fun List<Block>.asSymbolCards(): List<SymbolCardContent>? {
    if (isEmpty() || size % 3 != 0) return null
    val cards = mutableListOf<SymbolCardContent>()
    for (index in indices step 3) {
        val heading = getOrNull(index) as? HeadingBlock ?: return null
        val image = getOrNull(index + 1) as? ImageBlock ?: return null
        val paragraph = getOrNull(index + 2) as? ParagraphBlock ?: return null
        cards += SymbolCardContent(heading, image, paragraph)
    }
    return cards
}

private fun List<Block>.asBiblicalCharacterCards(): List<BiblicalCharacterContent>? {
    if (isEmpty() || size % 3 != 0) return null
    val cards = mutableListOf<BiblicalCharacterContent>()
    for (index in indices step 3) {
        val heading = getOrNull(index) as? HeadingBlock ?: return null
        val image = getOrNull(index + 1) as? ImageBlock ?: return null
        val paragraph = getOrNull(index + 2) as? ParagraphBlock ?: return null
        cards += BiblicalCharacterContent(heading, image, paragraph)
    }
    return cards
}

// Each order = heading, one or two images (founder and/or emblem), then a paragraph.
// Founder is the main image; the emblem (when present alongside a founder) is the top-right badge.
private fun List<Block>.asOrderCards(): List<OrderCardContent>? {
    if (isEmpty()) return null
    val cards = mutableListOf<OrderCardContent>()
    var i = 0
    while (i < size) {
        val heading = getOrNull(i) as? HeadingBlock ?: return null
        i++
        val images = mutableListOf<ImageBlock>()
        while (getOrNull(i) is ImageBlock) {
            images += getOrNull(i) as ImageBlock
            i++
        }
        val paragraph = getOrNull(i) as? ParagraphBlock ?: return null
        i++
        if (images.isEmpty()) return null
        val founder = images.firstOrNull { it.asset.contains("_founder") }
        val emblem = images.firstOrNull { it.asset.contains("_emblem") }
        val main = founder ?: images.first()
        val badge = if (images.size >= 2) emblem?.takeIf { it != main } else null
        cards += OrderCardContent(heading, main, badge, paragraph)
    }
    return cards
}

@Composable
private fun OrdersHeaderRow(
    count: Int,
    language: String,
    searchExpanded: Boolean,
    onToggleSearch: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (language == "fr") "ORDRES · $count" else "ORDERS · $count",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.4.sp,
        )
        Spacer(
            Modifier
                .padding(start = 12.dp)
                .height(1.dp)
                .weight(1f)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                            Color.Transparent,
                        )
                    )
                ),
        )
        IconButton(
            onClick = onToggleSearch,
            modifier = Modifier.size(38.dp),
        ) {
            val tint = if (searchExpanded) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
            }
            if (searchExpanded) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = if (language == "fr") "Fermer la recherche" else "Close search",
                    tint = tint,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.nav_search),
                    contentDescription = if (language == "fr") "Rechercher" else "Search",
                    modifier = Modifier.size(22.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun OrderSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    language: String,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        leadingIcon = {
            Image(
                painter = painterResource(Res.drawable.nav_search),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = if (language == "fr") "Effacer" else "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        },
        placeholder = {
            Text(
                text = if (language == "fr") "Rechercher un ordre…" else "Search orders…",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun OrderCard(
    card: OrderCardContent,
    strings: Map<String, String>,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    val title = strings[card.heading.textKey].orEmpty()
    val shape = RoundedCornerShape(22.dp)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)),
        shadowElevation = 3.dp,
    ) {
        Box {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.surface,
                                )
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    hubAssetPainter(card.image.asset)?.let { painter ->
                        Image(
                            painter = painter,
                            contentDescription = title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 20.dp, top = 10.dp, end = 20.dp),
                        )
                    }

                    GoldCardAccent(
                        modifier = Modifier.align(Alignment.CenterStart),
                        color = MaterialTheme.colorScheme.primary,
                        height = 68.dp,
                    )

                    // Emblem badge, top-right corner of the image card
                    card.emblem?.let { emblem ->
                        hubAssetPainter(emblem.asset)?.let { painter ->
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF081F17).copy(alpha = 0.55f))
                                    .border(
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                                        RoundedCornerShape(50),
                                    )
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.58f))
                )

                Column(
                    modifier = Modifier.padding(start = 24.dp, top = 18.dp, end = 20.dp, bottom = 22.dp),
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    BiblicalMarkupText(
                        raw = strings[card.paragraph.textKey].orEmpty(),
                        onEntityRefSelected = onEntityRefSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun BiblicalFiguresLabel(
    count: Int,
    language: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (language == "fr") "FIGURES BIBLIQUES · $count" else "BIBLICAL FIGURES · $count",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.4.sp,
        )
        Spacer(
            Modifier
                .padding(start = 12.dp)
                .height(1.dp)
                .weight(1f)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                            Color.Transparent,
                        )
                    )
                ),
        )
    }
}

@Composable
private fun BiblicalCharacterCard(
    card: BiblicalCharacterContent,
    strings: Map<String, String>,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    val title = strings[card.heading.textKey].orEmpty()
    val shape = RoundedCornerShape(22.dp)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)),
        shadowElevation = 3.dp,
    ) {
        Box {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.surface,
                                )
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    hubAssetPainter(card.image.asset)?.let { painter ->
                        Image(
                            painter = painter,
                            contentDescription = title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 20.dp, top = 10.dp, end = 20.dp),
                        )
                    }

                    GoldCardAccent(
                        modifier = Modifier.align(Alignment.CenterStart),
                        color = MaterialTheme.colorScheme.primary,
                        height = 68.dp,
                    )
                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.58f))
                )

                Column(
                    modifier = Modifier.padding(start = 24.dp, top = 18.dp, end = 20.dp, bottom = 22.dp),
                ) {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        lineHeight = 27.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(8.dp))
                    BiblicalMarkupText(
                        raw = strings[card.paragraph.textKey].orEmpty(),
                        onEntityRefSelected = onEntityRefSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun BiblicalMarkupText(
    raw: String,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    val annotated = parseHubMarkup(raw)
    ClickableText(
        text = annotated,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            fontSize = 15.sp,
            lineHeight = 23.sp,
        ),
        onClick = { offset ->
            annotated.getStringAnnotations(HUB_ENTITY_LINK_TAG, offset, offset).firstOrNull()?.let { annotation ->
                val (type, id) = annotation.item.split(":", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
                if (id.isNotEmpty()) {
                    runCatching { com.ynotlabs.cathopedia.content.model.EntityType.valueOf(type) }
                        .getOrNull()
                        ?.let { onEntityRefSelected(EntityRef(type = it, id = id)) }
                }
            }
        },
    )
}

@Composable
private fun SymbolCard(
    card: SymbolCardContent,
    strings: Map<String, String>,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    val title = strings[card.heading.textKey].orEmpty()
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 132.dp)
            .clip(shape)
            .background(SymbolCardSurface)
            .border(1.dp, SymbolCardGold.copy(alpha = 0.35f), shape),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(3.dp)
                .height(54.dp)
                .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
                .background(SymbolCardGold),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = title,
                    color = SymbolCardCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                )

                Spacer(Modifier.height(7.dp))
                SymbolMarkupText(
                    raw = strings[card.paragraph.textKey].orEmpty(),
                    onEntityRefSelected = onEntityRefSelected,
                )
            }

            hubAssetPainter(card.image.asset)?.let { painter ->
                Spacer(Modifier.width(12.dp))
                Image(
                    painter = painter,
                    contentDescription = title,
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun SymbolMarkupText(
    raw: String,
    onEntityRefSelected: (EntityRef) -> Unit,
    modifier: Modifier = Modifier,
) {
    val annotated = parseHubMarkup(raw)
    ClickableText(
        text = annotated,
        modifier = modifier,
        style = TextStyle(
            color = SymbolCardMuted,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        ),
        onClick = { offset ->
            annotated.getStringAnnotations(HUB_ENTITY_LINK_TAG, offset, offset).firstOrNull()?.let { annotation ->
                val (type, id) = annotation.item.split(":", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
                if (id.isNotEmpty()) {
                    runCatching { com.ynotlabs.cathopedia.content.model.EntityType.valueOf(type) }
                        .getOrNull()
                        ?.let { onEntityRefSelected(EntityRef(type = it, id = id)) }
                }
            }
        },
    )
}

private fun blockKeys(block: Block): Set<String> = when (block) {
    is HeadingBlock -> setOf(block.textKey)
    is ParagraphBlock -> setOf(block.textKey)
    is ListBlock -> block.itemKeys.toSet()
    is QuoteBlock -> setOfNotNull(block.textKey, block.attributionKey)
    is CalloutBlock -> setOfNotNull(block.titleKey, block.textKey)
    is ImageBlock -> setOfNotNull(block.captionKey)
    is FactGridBlock -> block.facts.flatMap { listOf(it.labelKey, it.valueKey) }.toSet()
    is EntityCardsBlock -> setOfNotNull(block.titleKey) + block.refs.mapNotNull { it.labelKey }
    is ExternalLinkBlock -> setOf(block.labelKey)
    is DiagramRefBlock, is StepperRefBlock, is TimelineRefBlock -> emptySet()
    else -> emptySet()
}

@Composable
private fun BlockView(block: Block, strings: Map<String, String>, onEntityRefSelected: (EntityRef) -> Unit) {
    when (block) {
        is HeadingBlock -> Text(
            text = strings[block.textKey].orEmpty(),
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Serif,
            fontSize = if (block.level <= 2) 20.sp else 17.sp,
            fontWeight = FontWeight.SemiBold,
        )

        is ParagraphBlock -> MarkupText(strings[block.textKey].orEmpty(), onEntityRefSelected)

        is ListBlock -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            block.itemKeys.forEach { key ->
                Row {
                    Text(
                        text = if (block.style.name == "numbered") "•" else "—",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    MarkupText(strings[key].orEmpty(), onEntityRefSelected)
                }
            }
        }

        is QuoteBlock -> Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(1.dp, SymbolCardGold.copy(alpha = 0.35f)),
        ) {
            Box {
                GoldCardAccent(Modifier.align(Alignment.CenterStart))
                Column(Modifier.padding(start = 22.dp, top = 14.dp, end = 14.dp, bottom = 14.dp)) {
                    MarkupText(strings[block.textKey].orEmpty(), onEntityRefSelected)
                    block.attributionKey?.let { key ->
                        strings[key]?.let { attribution ->
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "— $attribution",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        is CalloutBlock -> Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            border = BorderStroke(1.dp, SymbolCardGold.copy(alpha = 0.45f)),
        ) {
            Box {
                GoldCardAccent(Modifier.align(Alignment.CenterStart))
                Column(Modifier.padding(start = 22.dp, top = 14.dp, end = 14.dp, bottom = 14.dp)) {
                    block.titleKey?.let { key ->
                        strings[key]?.let { title ->
                            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    MarkupText(strings[block.textKey].orEmpty(), onEntityRefSelected)
                }
            }
        }

        is ImageBlock -> hubAssetPainter(block.asset)?.let { painter ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painter,
                    contentDescription = block.captionKey?.let { strings[it] },
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp)),
                )
                block.captionKey?.let { key ->
                    strings[key]?.let { caption ->
                        Spacer(Modifier.height(6.dp))
                        Text(text = caption, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        } ?: Unit

        is FactGridBlock -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            block.facts.forEach { fact ->
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = strings[fact.labelKey].orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Text(text = strings[fact.valueKey].orEmpty(), color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
                }
            }
        }

        is EntityCardsBlock -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            block.titleKey?.let { key -> strings[key]?.let { Text(it, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) } }
            block.refs.forEach { ref ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onEntityRefSelected(ref) },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, SymbolCardGold.copy(alpha = 0.25f)),
                ) {
                    Box {
                        GoldCardAccent(Modifier.align(Alignment.CenterStart), height = 32.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 18.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = ref.labelKey?.let { strings[it] } ?: ref.id,
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp,
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = SymbolCardGold.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        is ExternalLinkBlock -> Text(
            text = strings[block.labelKey].orEmpty(),
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            fontSize = 14.sp,
        )

        // DiagramRefBlock / StepperRefBlock / TimelineRefBlock point at structures the section
        // itself already renders — nothing to draw here. Any future block type this app version
        // doesn't know about falls into the same branch: skipped silently, never a crash.
        else -> Unit
    }
}

@Composable
private fun MarkupText(raw: String, onEntityRefSelected: (EntityRef) -> Unit) {
    val annotated = parseHubMarkup(raw)
    ClickableText(
        text = annotated,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        ),
        onClick = { offset ->
            annotated.getStringAnnotations(HUB_ENTITY_LINK_TAG, offset, offset).firstOrNull()?.let { annotation ->
                val (type, id) = annotation.item.split(":", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
                if (id.isNotEmpty()) {
                    runCatching { com.ynotlabs.cathopedia.content.model.EntityType.valueOf(type) }
                        .getOrNull()
                        ?.let { onEntityRefSelected(EntityRef(type = it, id = id)) }
                }
            }
        },
    )
}
