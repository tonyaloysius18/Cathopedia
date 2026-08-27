package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.ContentCategory
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.model.RelatedItem
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.PopeCoatsOfArms
import com.ynotlabs.cathopedia.ui.accentColor
import com.ynotlabs.cathopedia.ui.label
import com.ynotlabs.cathopedia.ui.singularLabel
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.connected_basilica
import com.ynotlabs.cathopedia.resources.connected_person
import com.ynotlabs.cathopedia.resources.liturgical_calendar
import com.ynotlabs.cathopedia.resources.papacy_basilica
import com.ynotlabs.cathopedia.resources.papal_keys
import com.ynotlabs.cathopedia.resources.regnal_crown
import com.ynotlabs.cathopedia.resources.source_globe_book
import com.ynotlabs.cathopedia.ui.theme.ApostleActionBar
import com.ynotlabs.cathopedia.ui.theme.ApostleBg
import com.ynotlabs.cathopedia.ui.theme.ApostleBorder
import com.ynotlabs.cathopedia.ui.theme.ApostleCard
import com.ynotlabs.cathopedia.ui.theme.ApostleCream
import com.ynotlabs.cathopedia.ui.theme.ApostleGold
import com.ynotlabs.cathopedia.ui.theme.ApostleGoldSoft
import com.ynotlabs.cathopedia.ui.theme.ApostleMuted
import com.ynotlabs.cathopedia.ui.theme.ApostleSurfaceElevated
import com.ynotlabs.cathopedia.ui.theme.ChurchActionBar
import com.ynotlabs.cathopedia.ui.theme.ChurchBg
import com.ynotlabs.cathopedia.ui.theme.ChurchBorder
import com.ynotlabs.cathopedia.ui.theme.ChurchCard
import com.ynotlabs.cathopedia.ui.theme.ChurchCream
import com.ynotlabs.cathopedia.ui.theme.ChurchGold
import com.ynotlabs.cathopedia.ui.theme.ChurchGoldSoft
import com.ynotlabs.cathopedia.ui.theme.ChurchMuted
import com.ynotlabs.cathopedia.ui.theme.ChurchSurfaceElevated
import com.ynotlabs.cathopedia.ui.theme.FeastActionBar
import com.ynotlabs.cathopedia.ui.theme.FeastBg
import com.ynotlabs.cathopedia.ui.theme.FeastBorder
import com.ynotlabs.cathopedia.ui.theme.FeastCard
import com.ynotlabs.cathopedia.ui.theme.FeastCream
import com.ynotlabs.cathopedia.ui.theme.FeastGold
import com.ynotlabs.cathopedia.ui.theme.FeastGoldSoft
import com.ynotlabs.cathopedia.ui.theme.FeastMuted
import com.ynotlabs.cathopedia.ui.theme.FeastSurfaceElevated
import com.ynotlabs.cathopedia.ui.theme.LocalApostleTheme
import com.ynotlabs.cathopedia.ui.theme.LocalChurchTheme
import com.ynotlabs.cathopedia.ui.theme.LocalFeastTheme
import com.ynotlabs.cathopedia.ui.theme.LocalMarianTheme
import com.ynotlabs.cathopedia.ui.theme.LocalMiracleTheme
import com.ynotlabs.cathopedia.ui.theme.LocalPopeTheme
import com.ynotlabs.cathopedia.ui.theme.MarianActionBar
import com.ynotlabs.cathopedia.ui.theme.MarianBg
import com.ynotlabs.cathopedia.ui.theme.MarianBorder
import com.ynotlabs.cathopedia.ui.theme.MarianCard
import com.ynotlabs.cathopedia.ui.theme.MarianCream
import com.ynotlabs.cathopedia.ui.theme.MarianGold
import com.ynotlabs.cathopedia.ui.theme.MarianGoldSoft
import com.ynotlabs.cathopedia.ui.theme.MarianMuted
import com.ynotlabs.cathopedia.ui.theme.MarianSurfaceElevated
import com.ynotlabs.cathopedia.ui.theme.MiracleActionBar
import com.ynotlabs.cathopedia.ui.theme.MiracleBg
import com.ynotlabs.cathopedia.ui.theme.MiracleBorder
import com.ynotlabs.cathopedia.ui.theme.MiracleCard
import com.ynotlabs.cathopedia.ui.theme.MiracleCream
import com.ynotlabs.cathopedia.ui.theme.MiracleGold
import com.ynotlabs.cathopedia.ui.theme.MiracleGoldSoft
import com.ynotlabs.cathopedia.ui.theme.MiracleMuted
import com.ynotlabs.cathopedia.ui.theme.MiracleSurfaceElevated
import com.ynotlabs.cathopedia.ui.theme.PopeActionBar
import com.ynotlabs.cathopedia.ui.theme.PopeBg
import com.ynotlabs.cathopedia.ui.theme.PopeBorder
import com.ynotlabs.cathopedia.ui.theme.PopeCard
import com.ynotlabs.cathopedia.ui.theme.PopeCream
import com.ynotlabs.cathopedia.ui.theme.PopeGold
import com.ynotlabs.cathopedia.ui.theme.PopeGoldSoft
import com.ynotlabs.cathopedia.ui.theme.PopeMuted
import com.ynotlabs.cathopedia.ui.theme.PopeSurfaceElevated
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val DetailBg: Color @Composable get() = if (LocalMarianTheme.current) MarianBg else if (LocalFeastTheme.current) FeastBg else if (LocalPopeTheme.current) PopeBg else if (LocalApostleTheme.current) ApostleBg else if (LocalMiracleTheme.current) MiracleBg else if (LocalChurchTheme.current) ChurchBg else MaterialTheme.colorScheme.background
private val DetailSurface: Color @Composable get() = if (LocalMarianTheme.current) MarianCard else if (LocalFeastTheme.current) FeastCard else if (LocalPopeTheme.current) PopeCard else if (LocalApostleTheme.current) ApostleCard else if (LocalMiracleTheme.current) MiracleCard else if (LocalChurchTheme.current) ChurchCard else MaterialTheme.colorScheme.surfaceContainerHigh
private val DetailSurfaceElevated: Color @Composable get() = if (LocalMarianTheme.current) MarianSurfaceElevated else if (LocalFeastTheme.current) FeastSurfaceElevated else if (LocalPopeTheme.current) PopeSurfaceElevated else if (LocalApostleTheme.current) ApostleSurfaceElevated else if (LocalMiracleTheme.current) MiracleSurfaceElevated else if (LocalChurchTheme.current) ChurchSurfaceElevated else MaterialTheme.colorScheme.surfaceContainerHighest
private val DetailBorder: Color @Composable get() = if (LocalMarianTheme.current) MarianBorder else if (LocalFeastTheme.current) FeastBorder else if (LocalPopeTheme.current) PopeBorder else if (LocalApostleTheme.current) ApostleBorder else if (LocalMiracleTheme.current) MiracleBorder else if (LocalChurchTheme.current) ChurchBorder else MaterialTheme.colorScheme.outline
private val DetailGold: Color @Composable get() = if (LocalMarianTheme.current) MarianGold else if (LocalFeastTheme.current) FeastGold else if (LocalPopeTheme.current) PopeGold else if (LocalApostleTheme.current) ApostleGold else if (LocalMiracleTheme.current) MiracleGold else if (LocalChurchTheme.current) ChurchGold else MaterialTheme.colorScheme.primary
private val DetailGoldSoft: Color @Composable get() = if (LocalMarianTheme.current) MarianGoldSoft else if (LocalFeastTheme.current) FeastGoldSoft else if (LocalPopeTheme.current) PopeGoldSoft else if (LocalApostleTheme.current) ApostleGoldSoft else if (LocalMiracleTheme.current) MiracleGoldSoft else if (LocalChurchTheme.current) ChurchGoldSoft else MaterialTheme.colorScheme.secondary
private val DetailCream: Color @Composable get() = if (LocalMarianTheme.current) MarianCream else if (LocalFeastTheme.current) FeastCream else if (LocalPopeTheme.current) PopeCream else if (LocalApostleTheme.current) ApostleCream else if (LocalMiracleTheme.current) MiracleCream else if (LocalChurchTheme.current) ChurchCream else MaterialTheme.colorScheme.onBackground
private val DetailMuted: Color @Composable get() = if (LocalMarianTheme.current) MarianMuted else if (LocalFeastTheme.current) FeastMuted else if (LocalPopeTheme.current) PopeMuted else if (LocalApostleTheme.current) ApostleMuted else if (LocalMiracleTheme.current) MiracleMuted else if (LocalChurchTheme.current) ChurchMuted else MaterialTheme.colorScheme.onSurfaceVariant
private val DetailActionBar: Color @Composable get() = if (LocalMarianTheme.current) MarianActionBar else if (LocalFeastTheme.current) FeastActionBar else if (LocalPopeTheme.current) PopeActionBar else if (LocalApostleTheme.current) ApostleActionBar else if (LocalMiracleTheme.current) MiracleActionBar else if (LocalChurchTheme.current) ChurchActionBar else MaterialTheme.colorScheme.surfaceDim

private data class DetailViewData(
    val name: String,
    val summary: String,
    val body: String,
    val facts: List<Pair<String, String>>,
    val sourceAttribution: String?,
    val related: List<RelatedItem>,
)

private data class ResolvedRelated(
    val item: RelatedItem,
    val name: String,
)

/**
 * Shared premium detail screen for all Cathopedia entity types.
 *
 * The hero portrait is tappable. When a full portrait exists, tapping it opens a
 * full-screen viewer using ContentScale.Fit so the artwork is never cropped.
 */
@Composable
fun EntityDetailScreen(
    type: ContentType,
    id: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onRelatedSelected: (RelatedItem) -> Unit,
) {
    val s = LocalStrings.current
    var data by remember(type, id) { mutableStateOf<DetailViewData?>(null) }
    var resolvedRelated by remember(type, id) { mutableStateOf<List<ResolvedRelated>>(emptyList()) }
    var bookmarked by remember(type, id) { mutableStateOf(false) }
    var lightboxOpen by remember(type, id) { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var relatedY by remember(type, id) { mutableIntStateOf(0) }
    var sourceY by remember(type, id) { mutableIntStateOf(0) }

    LaunchedEffect(type, id, language) {
        // A related entity reuses this detail composable, so rememberScrollState()
        // would otherwise keep the previous entity's scroll position. Always reset
        // to the hero before loading the newly selected entity.
        scrollState.scrollTo(0)
        relatedY = 0
        sourceY = 0
        lightboxOpen = false

        data = when (type) {
            ContentType.SAINT -> repository.saintDetail(id, language)?.let {
                DetailViewData(
                    name = it.name,
                    summary = it.summary,
                    body = it.body,
                    facts = listOfNotNull(
                        it.feastDay?.let { v -> "Feast day" to v },
                        it.canonizationYear?.let { v -> "Canonized" to v.toString() },
                        it.patronage?.let { v -> "Patronage" to v },
                    ),
                    sourceAttribution = it.sourceAttribution,
                    related = it.related,
                )
            }

            ContentType.POPE -> repository.popeDetail(id, language)?.let {
                DetailViewData(
                    name = it.name,
                    summary = it.summary,
                    body = it.body,
                    facts = listOfNotNull(
                        it.regnalNumber?.let { v -> "Regnal number" to v.toString() },
                        it.papacyStart?.let { v -> "Papacy start" to v },
                        it.papacyEnd?.let { v -> "Papacy end" to v },
                    ),
                    sourceAttribution = it.sourceAttribution,
                    related = it.related,
                )
            }

            ContentType.APOSTLE -> repository.apostleDetail(id, language)?.let {
                DetailViewData(
                    name = it.name,
                    summary = it.summary,
                    body = it.body,
                    facts = listOfNotNull(
                        it.originalName?.let { v -> "Original name" to v },
                        it.martyrdom?.let { v -> "Martyrdom" to v },
                    ),
                    sourceAttribution = it.sourceAttribution,
                    related = it.related,
                )
            }

            ContentType.CHURCH -> repository.churchDetail(id, language)?.let {
                DetailViewData(
                    name = it.name,
                    summary = it.summary,
                    body = it.body,
                    facts = listOfNotNull(
                        it.foundedYear?.let { v -> "Founded" to v.toString() },
                        if (it.latitude != null && it.longitude != null) {
                            "Location" to "${it.latitude}, ${it.longitude}"
                        } else {
                            null
                        },
                    ),
                    sourceAttribution = it.sourceAttribution,
                    related = it.related,
                )
            }

            ContentType.APPARITION -> repository.apparitionDetail(id, language)?.let {
                DetailViewData(
                    name = it.name,
                    summary = it.summary,
                    body = it.body,
                    facts = listOfNotNull(
                        it.location?.let { v -> "Location" to v },
                        it.apparitionYear?.let { v -> "Year" to v.toString() },
                        it.approvalStatus?.let { v -> "Status" to v },
                    ),
                    sourceAttribution = it.sourceAttribution,
                    related = it.related,
                )
            }

            ContentType.MIRACLE -> repository.miracleDetail(id, language)?.let {
                DetailViewData(
                    name = it.name,
                    summary = it.summary,
                    body = it.body,
                    facts = listOfNotNull(
                        it.location?.let { v -> "Location" to v },
                        it.miracleYear?.let { v -> "Year" to v.toString() },
                    ),
                    sourceAttribution = it.sourceAttribution,
                    related = it.related,
                )
            }

            ContentType.FEAST -> repository.feastDetail(id, language)?.let {
                DetailViewData(
                    name = it.name,
                    summary = it.summary,
                    body = it.body,
                    facts = listOfNotNull(
                        it.date?.let { v -> "Date" to v },
                    ),
                    sourceAttribution = it.sourceAttribution,
                    related = it.related,
                )
            }
        }

        data?.let { loaded ->
            repository.recordView(type, id, loaded.name, loaded.summary)
            bookmarked = repository.isBookmarked(type, id)
            resolvedRelated = loaded.related.map { related ->
                val name = repository.summaryOf(related.type, related.id, language)?.name ?: related.id
                ResolvedRelated(related, name)
            }
        }
    }

    val current = data
    val portrait = Portraits.forEntity(type, id)
    val fullPortrait = Portraits.fullForEntity(type, id)

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
            .background(DetailBg),
    ) {
        if (current == null) {
            CircularProgressIndicator(
                color = DetailGold,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .padding(bottom = 36.dp),
            ) {
                if (portrait != null) {
                    DetailHero(
                        type = type,
                        id = id,
                        data = current,
                        portrait = fullPortrait ?: portrait,
                        canOpenFullImage = fullPortrait != null,
                        bookmarked = bookmarked,
                        hasRelated = resolvedRelated.isNotEmpty(),
                        onBack = onBack,
                        onBookmark = {
                            scope.launch {
                                bookmarked = repository.toggleBookmark(
                                    type = type,
                                    id = id,
                                    name = current.name,
                                    summary = current.summary,
                                )
                            }
                        },
                        onShare = {
                            // Keep the action visually available. Wire this to your
                            // platform share helper if/when one is added to the project.
                        },
                        onImageClick = {
                            if (fullPortrait != null) lightboxOpen = true
                        },
                        onRelated = {
                            if (relatedY > 0) {
                                scope.launch { scrollState.animateScrollTo(relatedY) }
                            }
                        },
                        onSource = {
                            if (sourceY > 0) {
                                scope.launch { scrollState.animateScrollTo(sourceY) }
                            }
                        },
                    )
                } else {
                    CompactNoPortraitHeader(
                        type = type,
                        name = current.name,
                        bookmarked = bookmarked,
                        onBack = onBack,
                        onBookmark = {
                            scope.launch {
                                bookmarked = repository.toggleBookmark(
                                    type = type,
                                    id = id,
                                    name = current.name,
                                    summary = current.summary,
                                )
                            }
                        },
                    )
                }

                DetailContent(
                    type = type,
                    id = id,
                    data = current,
                    resolvedRelated = resolvedRelated,
                    hasHero = portrait != null,
                    onRelatedSelected = onRelatedSelected,
                    onRelatedPositioned = { relatedY = it },
                    onSourcePositioned = { sourceY = it },
                )
            }
        }

        if (lightboxOpen && fullPortrait != null) {
            PortraitLightbox(
                image = fullPortrait,
                title = current?.name.orEmpty(),
                caption = if (Portraits.isGeneratedArt(type, id)) {
                    s.detailAiGeneratedCaption
                } else {
                    s.detailHistoricalImageCaption
                },
                onDismiss = { lightboxOpen = false },
            )
        }
    }
    }
}

@Composable
private fun DetailHero(
    type: ContentType,
    id: String,
    data: DetailViewData,
    portrait: DrawableResource,
    canOpenFullImage: Boolean,
    bookmarked: Boolean,
    hasRelated: Boolean,
    onBack: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit,
    onImageClick: () -> Unit,
    onRelated: () -> Unit,
    onSource: () -> Unit,
) {
    val s = LocalStrings.current
    val isSaintPope = type == ContentType.POPE && data.name.isSaintPopeName()
    val papacyStart = data.fact("Papacy start")?.toPapacyDisplayDate()
    val papacyEnd = data.fact("Papacy end")?.toPapacyDisplayDate()
    val papacyTimeline = when {
        papacyStart != null && papacyEnd != null -> "$papacyStart – $papacyEnd"
        papacyStart != null -> "$papacyStart – ${s.detailPresent}"
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(590.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = canOpenFullImage, onClick = onImageClick),
        ) {
            Image(
                painter = painterResource(portrait),
                contentDescription = data.name,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.38f),
                            0.27f to Color.Transparent,
                            0.58f to Color.Transparent,
                            0.82f to Color.Black.copy(alpha = 0.28f),
                            1f to Color.Black.copy(alpha = 0.92f),
                        ),
                    ),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeroCircleButton(
                onClick = onBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.back,
                    tint = DetailCream,
                    modifier = Modifier.size(24.dp),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroCircleButton(onClick = onBookmark) {
                    Icon(
                        imageVector = if (bookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (bookmarked) s.detailRemoveFromFavorites else s.detailSaveToFavorites,
                        tint = DetailCream,
                        modifier = Modifier.size(22.dp),
                    )
                }

                HeroCircleButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = s.detailShare,
                        tint = DetailCream,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 34.dp, end = 34.dp, bottom = 86.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isSaintPope) {
                    Text(
                        text = "✦",
                        color = DetailGold,
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = s.detailSaintTag,
                        color = DetailGold,
                        fontSize = 11.sp,
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "│",
                        color = DetailCream.copy(alpha = 0.48f),
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.width(10.dp))
                }

                Text(
                    text = type.singularLabel(s).uppercase(),
                    color = DetailGold,
                    fontSize = 11.sp,
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = data.name,
                color = DetailCream,
                fontFamily = FontFamily.Serif,
                fontSize = 37.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            val heroMeta = when (type) {
                ContentType.POPE -> papacyTimeline
                ContentType.SAINT -> data.fact("Feast day")?.let { "${s.detailFactFeastDay} · $it" }
                else -> null
            }

            if (!heroMeta.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = heroMeta,
                    color = DetailGold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        DetailActionBar(
            bookmarked = bookmarked,
            hasRelated = hasRelated,
            onSave = onBookmark,
            onShare = onShare,
            onRelated = onRelated,
            onSource = onSource,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 34.dp)
                .padding(horizontal = 34.dp),
        )
    }
}

@Composable
private fun HeroCircleButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.28f))
            .border(2.dp, Color.White.copy(alpha = 0.08f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun DetailActionBar(
    bookmarked: Boolean,
    hasRelated: Boolean,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onRelated: () -> Unit,
    onSource: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DetailActionBar.copy(alpha = 0.98f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = DetailBorder,
        ),
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ActionItem(
                label = if (bookmarked) s.detailSaved else s.detailSave,
                icon = {
                    Icon(
                        imageVector = if (bookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = DetailCream,
                        modifier = Modifier.size(23.dp),
                    )
                },
                onClick = onSave,
            )

            ActionDivider()

            ActionItem(
                label = s.detailShare,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = DetailCream,
                        modifier = Modifier.size(23.dp),
                    )
                },
                onClick = onShare,
            )

            if (hasRelated) {
                ActionDivider()

                ActionItem(
                    label = s.detailRelated,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = DetailCream,
                            modifier = Modifier.size(23.dp),
                        )
                    },
                    onClick = onRelated,
                )
            }

            ActionDivider()

            ActionItem(
                label = s.detailSourceTitle,
                icon = {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = DetailCream,
                        modifier = Modifier.size(23.dp),
                    )
                },
                onClick = onSource,
            )
        }
    }
}

@Composable
private fun ActionItem(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = DetailCream,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ActionDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(39.dp)
            .background(DetailBorder.copy(alpha = 0.72f)),
    )
}

@Composable
private fun DetailContent(
    type: ContentType,
    id: String,
    data: DetailViewData,
    resolvedRelated: List<ResolvedRelated>,
    hasHero: Boolean,
    onRelatedSelected: (RelatedItem) -> Unit,
    onRelatedPositioned: (Int) -> Unit,
    onSourcePositioned: (Int) -> Unit,
) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (hasHero) 54.dp else 12.dp)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PremiumSectionCard {
            SectionTitle(s.detailOverviewTitle)

            Text(
                text = data.summary,
                color = DetailCream,
                fontSize = 17.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Medium,
            )

            if (data.facts.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                FactGrid(
                    type = type,
                    facts = data.facts,
                )
            }

            if (type == ContentType.POPE && PopeCoatsOfArms.forPope(id) != null) {
                Spacer(Modifier.height(22.dp))
                SectionTitle(s.detailCoatOfArmsTitle)
                CoatOfArmsSection(popeId = id)
            }

            if (data.body.isNotBlank()) {
                Spacer(Modifier.height(22.dp))
                SectionTitle(s.detailLifeAndLegacyTitle)
                Text(
                    text = data.body,
                    color = DetailCream.copy(alpha = 0.88f),
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                )
            }

            if (resolvedRelated.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier.onGloballyPositioned {
                        onRelatedPositioned(it.positionInParent().y.toInt())
                    },
                ) {
                    Column {
                        SectionTitle(s.detailConnectedTitle)
                        ConnectedChips(
                            items = resolvedRelated,
                            onRelatedSelected = onRelatedSelected,
                        )
                    }
                }
            }
        }

        SourceCard(
            sourceAttribution = data.sourceAttribution,
            generatedPortrait = Portraits.forEntity(type, id) != null &&
                    Portraits.isGeneratedArt(type, id),
            modifier = Modifier.onGloballyPositioned {
                onSourcePositioned(it.positionInParent().y.toInt())
            },
        )
    }
}

@Composable
private fun PremiumSectionCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DetailSurface)
            .border(
                width = 2.dp,
                color = DetailBorder.copy(alpha = 0.78f),
                shape = RoundedCornerShape(22.dp),
            )
            .padding(18.dp),
        content = content,
    )
}

@Composable
private fun SectionTitle(
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(1.dp)
                .background(DetailGold),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            color = DetailGold,
            fontFamily = FontFamily.Serif,
            fontSize = 12.sp,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * Only called when [PopeCoatsOfArms.forPope] is non-null for this pope — the caller
 * omits the whole "Coat of Arms" section (title included) rather than show an empty
 * state for popes before personal papal heraldry existed. See [PopeCoatsOfArms]'s doc.
 */
@Composable
private fun CoatOfArmsSection(popeId: String) {
    val s = LocalStrings.current
    val coatOfArms = PopeCoatsOfArms.forPope(popeId) ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(coatOfArms),
            contentDescription = s.detailCoatOfArmsTitle,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxHeight(),
        )
    }
}

@Composable
private fun FactGrid(
    type: ContentType,
    facts: List<Pair<String, String>>,
) {
    val s = LocalStrings.current
    val displayFacts = if (type == ContentType.POPE) {
        val start = facts.firstOrNull { it.first.equals("Papacy start", true) }?.second
            ?.toPapacyDisplayDate()
        val end = facts.firstOrNull { it.first.equals("Papacy end", true) }?.second
            ?.toPapacyDisplayDate()
        val regnal = facts.firstOrNull { it.first.equals("Regnal number", true) }?.second

        buildList {
            if (start != null || end != null) {
                add(
                    "Papacy" to when {
                        start != null && end != null -> "$start – $end"
                        start != null -> "$start – ${s.detailPresent}"
                        else -> end.orEmpty()
                    },
                )
            }
            if (!regnal.isNullOrBlank()) add("Regnal number" to regnal)
            facts
                .filterNot {
                    it.first.equals("Papacy start", true) ||
                            it.first.equals("Papacy end", true) ||
                            it.first.equals("Regnal number", true)
                }
                .forEach(::add)
        }
    } else {
        facts
    }

    // A fact whose value is too long to read comfortably in a half-width card
    // gets its own full-width row instead of being crammed in and truncated;
    // short facts still pair up two-per-row as before.
    val rows = buildFactRows(displayFacts)

    rows.forEachIndexed { index, row ->
        if (index > 0) Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            row.facts.forEach { (label, value) ->
                FactCard(
                    label = factLabel(label, s),
                    value = value,
                    icon = factIcon(type = type, label = label),
                    modifier = if (row.fillWidth) Modifier.fillMaxWidth() else Modifier.weight(1f),
                )
            }
            if (row.facts.size == 1 && !row.fillWidth) {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

private class FactRow(val facts: List<Pair<String, String>>, val fillWidth: Boolean)

/** Values longer than this don't fit comfortably in a half-width card. */
private const val LONG_FACT_VALUE_LENGTH = 28

private fun buildFactRows(facts: List<Pair<String, String>>): List<FactRow> {
    val rows = mutableListOf<FactRow>()
    var i = 0
    while (i < facts.size) {
        val current = facts[i]
        if (current.second.length > LONG_FACT_VALUE_LENGTH) {
            rows += FactRow(listOf(current), fillWidth = true)
            i += 1
            continue
        }
        val next = facts.getOrNull(i + 1)
        if (next != null && next.second.length <= LONG_FACT_VALUE_LENGTH) {
            rows += FactRow(listOf(current, next), fillWidth = false)
            i += 2
        } else {
            rows += FactRow(listOf(current), fillWidth = false)
            i += 1
        }
    }
    return rows
}

@Composable
private fun FactCard(
    label: String,
    value: String,
    icon: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DetailSurfaceElevated)
            .border(
                width = 2.dp,
                color = DetailGoldSoft.copy(alpha = 0.55f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DetailGold.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(29.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label.uppercase(),
                color = DetailGold,
                fontSize = 9.sp,
                letterSpacing = 0.7.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = value,
                color = DetailCream,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ConnectedChips(
    items: List<ResolvedRelated>,
    onRelatedSelected: (RelatedItem) -> Unit,
) {
    val s = LocalStrings.current
    items
        .groupBy { ContentCategory.of(it.item.type) }
        .forEach { (category, group) ->
            Text(
                text = category.label(s).uppercase(),
                color = DetailMuted,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 7.dp),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                group.forEach { resolved ->
                    AssistChip(
                        onClick = { onRelatedSelected(resolved.item) },
                        label = {
                            Text(
                                text = resolved.name,
                                fontSize = 11.sp,
                                maxLines = 1,
                            )
                        },
                        leadingIcon = {
                            Image(
                                painter = painterResource(connectedIcon(resolved.item.type)),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                contentScale = ContentScale.Fit,
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = DetailSurfaceElevated,
                            labelColor = DetailCream,
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = DetailBorder,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
}

@Composable
private fun SourceCard(
    sourceAttribution: String?,
    generatedPortrait: Boolean,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(DetailSurface)
            .border(
                width = 2.dp,
                color = DetailBorder.copy(alpha = 0.78f),
                shape = RoundedCornerShape(22.dp),
            )
            .padding(18.dp),
    ) {
        SectionTitle(s.detailSourceTitle)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(DetailSurfaceElevated)
                .border(
                    width = 2.dp,
                    color = DetailGoldSoft.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(15.dp),
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DetailGold.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(Res.drawable.source_globe_book),
                    contentDescription = s.detailSourceTitle,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sourceAttribution?.takeIf { it.isNotBlank() }
                        ?: s.detailSourceFallback,
                    color = DetailCream,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (generatedPortrait) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = s.detailPortraitIllustrationNote,
                        color = DetailMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactNoPortraitHeader(
    type: ContentType,
    name: String,
    bookmarked: Boolean,
    onBack: () -> Unit,
    onBookmark: () -> Unit,
) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeroCircleButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.back,
                    tint = DetailCream,
                    modifier = Modifier.size(23.dp),
                )
            }

            Spacer(Modifier.weight(1f))

            HeroCircleButton(onClick = onBookmark) {
                Icon(
                    imageVector = if (bookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (bookmarked) s.detailRemoveFromFavorites else s.detailSaveToFavorites,
                    tint = DetailCream,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = type.singularLabel(s).uppercase(),
            color = DetailGold,
            fontSize = 10.sp,
            letterSpacing = 1.1.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = name,
            color = DetailCream,
            fontFamily = FontFamily.Serif,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PortraitLightbox(
    image: DrawableResource,
    title: String,
    caption: String,
    onDismiss: () -> Unit,
) {
    val s = LocalStrings.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF030605)),
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 72.dp),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(2.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.closeFullImage,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = caption,
                    color = Color.White.copy(alpha = 0.58f),
                    fontSize = 9.sp,
                    letterSpacing = 0.45.sp,
                )
            }
        }
    }
}

/** [label] is the internal English key the fact was stored under — translates it for display. */
private fun factLabel(label: String, s: Strings): String = when (label.lowercase()) {
    "feast day" -> s.detailFactFeastDay
    "canonized" -> s.detailFactCanonized
    "patronage" -> s.detailFactPatronage
    "regnal number" -> s.detailFactRegnalNumber
    "papacy start" -> s.detailFactPapacyStart
    "papacy end" -> s.detailFactPapacyEnd
    "papacy" -> s.detailFactPapacy
    "original name" -> s.detailFactOriginalName
    "martyrdom" -> s.detailFactMartyrdom
    "founded" -> s.detailFactFounded
    "location" -> s.detailFactLocation
    "year" -> s.detailFactYear
    "status" -> s.detailFactStatus
    "date" -> s.detailFactDate
    else -> label
}

private fun factIcon(
    type: ContentType,
    label: String,
): DrawableResource {
    val key = label.lowercase()

    return when {
        key == "papacy" ||
                key == "papacy start" ||
                key == "papacy end" ->
            Res.drawable.papacy_basilica

        key == "regnal number" ->
            Res.drawable.regnal_crown

        key == "feast day" ||
                key == "date" ||
                key == "year" ||
                key == "canonized" ->
            Res.drawable.liturgical_calendar

        type == ContentType.CHURCH ||
                key == "founded" ||
                key == "location" ->
            Res.drawable.connected_basilica

        type == ContentType.APOSTLE ||
                key == "original name" ||
                key == "martyrdom" ->
            Res.drawable.papal_keys

        key == "patronage" ||
                key == "status" ->
            Res.drawable.connected_person

        else ->
            Res.drawable.papal_keys
    }
}

private fun connectedIcon(type: ContentType): DrawableResource =
    when (type) {
        ContentType.CHURCH -> Res.drawable.connected_basilica
        ContentType.FEAST -> Res.drawable.liturgical_calendar
        ContentType.POPE,
        ContentType.SAINT,
        ContentType.APOSTLE -> Res.drawable.connected_person
        ContentType.APPARITION,
        ContentType.MIRACLE -> Res.drawable.papal_keys
    }

private fun DetailViewData.fact(label: String): String? =
    facts.firstOrNull { it.first.equals(label, ignoreCase = true) }?.second

private fun String.toPapacyDisplayDate(): String? {
    val value = trim()
    if (value.isBlank()) return null

    Regex("""(\d{4})-(\d{2})-(\d{2})""").matchEntire(value)?.let {
        val (year, month, day) = it.destructured
        return "$day-$month-$year"
    }

    Regex("""(\d{2})/(\d{2})/(\d{4})""").matchEntire(value)?.let {
        val (day, month, year) = it.destructured
        return "$day-$month-$year"
    }

    Regex("""\d{1,2}-\d{1,2}-\d{4}""").matchEntire(value)?.let {
        val parts = value.split("-")
        return "${parts[0].padStart(2, '0')}-${parts[1].padStart(2, '0')}-${parts[2]}"
    }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )

    Regex(
        """(?i)(\d{1,2})\s+(January|February|March|April|May|June|July|August|September|October|November|December)\s+(\d{4})""",
    ).matchEntire(value)?.let {
        val day = it.groupValues[1].padStart(2, '0')
        val month = (months.indexOfFirst { m -> m.equals(it.groupValues[2], true) } + 1)
            .toString()
            .padStart(2, '0')
        return "$day-$month-${it.groupValues[3]}"
    }

    Regex(
        """(?i)(January|February|March|April|May|June|July|August|September|October|November|December)\s+(\d{1,2}),?\s+(\d{4})""",
    ).matchEntire(value)?.let {
        val day = it.groupValues[2].padStart(2, '0')
        val month = (months.indexOfFirst { m -> m.equals(it.groupValues[1], true) } + 1)
            .toString()
            .padStart(2, '0')
        return "$day-$month-${it.groupValues[3]}"
    }

    Regex(
        """(?i)(January|February|March|April|May|June|July|August|September|October|November|December)\s+(\d{4})""",
    ).matchEntire(value)?.let {
        val month = months.firstOrNull { m -> m.equals(it.groupValues[1], true) }
            ?: it.groupValues[1]
        return "$month - ${it.groupValues[2]}"
    }

    Regex("""(\d{4})-(\d{2})""").matchEntire(value)?.let {
        val year = it.groupValues[1]
        val monthIndex = it.groupValues[2].toIntOrNull()?.minus(1)
        val month = monthIndex?.takeIf { index -> index in months.indices }?.let(months::get)
            ?: return value
        return "$month - $year"
    }

    return value
}

private val saintPopeNames = setOf(
    "peter",
    "linus",
    "cletus",
    "anacletus",
    "clement i",
    "evaristus",
    "alexander i",
    "sixtus i",
    "telesphorus",
    "hyginus",
    "pius i",
    "anicetus",
    "soter",
    "eleutherius",
    "victor i",
    "zephyrinus",
    "callixtus i",
    "urban i",
    "pontian",
    "anterus",
    "fabian",
    "cornelius",
    "lucius i",
    "stephen i",
    "sixtus ii",
    "dionysius",
    "felix i",
    "eutychian",
    "caius",
    "marcellinus",
    "marcellus i",
    "eusebius",
    "miltiades",
    "sylvester i",
    "mark",
    "julius i",
    "damasus i",
    "siricius",
    "anastasius i",
    "innocent i",
    "zosimus",
    "boniface i",
    "celestine i",
    "sixtus iii",
    "leo i",
    "leo the great",
    "hilary",
    "simplicius",
    "felix iii",
    "gelasius i",
    "symmachus",
    "hormisdas",
    "john i",
    "felix iv",
    "agapetus i",
    "silverius",
    "gregory i",
    "gregory the great",
    "boniface iv",
    "adeodatus i",
    "martin i",
    "eugene i",
    "vitalian",
    "agatho",
    "leo ii",
    "benedict ii",
    "sergius i",
    "gregory ii",
    "gregory iii",
    "zachary",
    "paul i",
    "leo iii",
    "paschal i",
    "leo iv",
    "nicholas i",
    "nicholas the great",
    "adrian iii",
    "leo ix",
    "gregory vii",
    "celestine v",
    "pius v",
    "pius x",
    "john xxiii",
    "paul vi",
    "john paul ii",
)

private fun String.isSaintPopeName(): Boolean {
    val normalized = lowercase()
        .removePrefix("pope ")
        .removePrefix("saint ")
        .removePrefix("st. ")
        .trim()
        .replace(Regex("\\s+"), " ")
    return normalized in saintPopeNames
}