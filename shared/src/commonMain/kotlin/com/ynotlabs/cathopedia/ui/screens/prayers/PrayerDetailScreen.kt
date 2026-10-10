package com.ynotlabs.cathopedia.ui.screens.prayers

import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import com.ynotlabs.cathopedia.ui.theme.imageFade
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.text.style.TextOverflow
import com.ynotlabs.cathopedia.model.PrayerSummary
import com.ynotlabs.cathopedia.speech.PackState
import com.ynotlabs.cathopedia.speech.PrayerAudioPacks
import com.ynotlabs.cathopedia.speech.ReadAloudController
import com.ynotlabs.cathopedia.speech.SpeechEngine
import com.ynotlabs.cathopedia.speech.SpeechUnit
import com.ynotlabs.cathopedia.speech.prayerSections
import com.ynotlabs.cathopedia.speech.prayerSpeechScript
import com.ynotlabs.cathopedia.speech.rememberSpeechEngine
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.PreferenceKeys
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.AppLanguages
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.PrayerCategory
import com.ynotlabs.cathopedia.model.PrayerDetail
import com.ynotlabs.cathopedia.ui.getCategoryIcon
import com.ynotlabs.cathopedia.ui.CategoryIcon
import com.ynotlabs.cathopedia.ui.PrayerPortraits
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.prayer_category_favorites
import com.ynotlabs.cathopedia.resources.seven_sorrows_01_prophecy
import com.ynotlabs.cathopedia.resources.seven_sorrows_02_flight
import com.ynotlabs.cathopedia.resources.seven_sorrows_03_temple
import com.ynotlabs.cathopedia.resources.seven_sorrows_04_cross
import com.ynotlabs.cathopedia.resources.seven_sorrows_05_calvary
import com.ynotlabs.cathopedia.resources.seven_sorrows_06_pieta
import com.ynotlabs.cathopedia.resources.seven_sorrows_07_burial
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.components.KeepScreenOn
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.PrayerBodyText
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val FONT_SCALE_STEPS = listOf(0.85f, 1.0f, 1.15f, 1.3f, 1.45f)
private const val DEFAULT_FONT_SCALE_INDEX = 1

private val PrayerBg: Color @Composable get() = MaterialTheme.colorScheme.background
private val PrayerGold: Color @Composable get() = MaterialTheme.colorScheme.primary
private val PrayerGoldSoft: Color @Composable get() = MaterialTheme.colorScheme.secondary
private val PrayerCream: Color @Composable get() = MaterialTheme.colorScheme.onBackground
private val PrayerMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

private val SevenSorrowsImages = listOf(
    Res.drawable.seven_sorrows_01_prophecy,
    Res.drawable.seven_sorrows_02_flight,
    Res.drawable.seven_sorrows_03_temple,
    Res.drawable.seven_sorrows_04_cross,
    Res.drawable.seven_sorrows_05_calvary,
    Res.drawable.seven_sorrows_06_pieta,
    Res.drawable.seven_sorrows_07_burial,
)

@Composable
fun PrayerDetailScreen(
    slug: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // "Next prayer" swaps the prayer in place, so Back still returns to where the reader came from.
    var prayerId by remember(slug) { mutableStateOf(slug) }
    var readingLanguage by remember(slug) { mutableStateOf(language) }
    var detail by remember(slug) { mutableStateOf<PrayerDetail?>(null) }
    var isFavorite by remember(prayerId) { mutableStateOf(false) }
    var keepScreenOn by remember(slug) { mutableStateOf(false) }
    var fontScaleIndex by remember(slug) { mutableStateOf(DEFAULT_FONT_SCALE_INDEX) }
    var nextPrayer by remember(prayerId) { mutableStateOf<PrayerSummary?>(null) }
    // Set when "Next prayer" is pressed while reading aloud: the next prayer starts by itself.
    var autoPlayId by remember(slug) { mutableStateOf<String?>(null) }

    var buttonsHeightPx by remember { mutableIntStateOf(0) }
    val buttonsHeight = with(density) { buttonsHeightPx.toDp() }

    LaunchedEffect(prayerId) {
        isFavorite = repository.isPrayerFavorite(prayerId)
        fontScaleIndex = FONT_SCALE_STEPS.indexOf(
            repository.getPreference(PreferenceKeys.PRAYER_FONT_SCALE)?.toFloatOrNull()
                ?: FONT_SCALE_STEPS[DEFAULT_FONT_SCALE_INDEX],
        ).takeIf { it >= 0 } ?: DEFAULT_FONT_SCALE_INDEX
        repository.recordPrayerRecited(prayerId)
    }

    LaunchedEffect(slug, language) { readingLanguage = language }
    LaunchedEffect(prayerId, readingLanguage) {
        val loaded = repository.prayerDetail(prayerId, readingLanguage)
        // The next prayer may not exist in the chosen language (e.g. Latin): fall back to the app's.
        if (loaded == null && readingLanguage != language) readingLanguage = language else detail = loaded
    }
    LaunchedEffect(prayerId, language) { nextPrayer = repository.nextPrayer(prayerId, language) }

    val speechEngine = rememberSpeechEngine()
    val reader = remember(speechEngine) { ReadAloudController(speechEngine) }
    DisposableEffect(reader) { onDispose { reader.release() } }

    // Reading aloud keeps the screen awake too, so the highlighted text stays visible.
    KeepScreenOn(enabled = keepScreenOn || reader.isPlaying)

    val current = detail

    Scaffold(
        containerColor = Color.Transparent,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Fill the entire screen with the prayer artwork
            PrayerPortraits.forPrayer(prayerId)?.let { portrait ->
                Image(
                    painter = painterResource(portrait),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // A single translucent veil keeps text readable without hiding the artwork.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PrayerBg.imageFade(0.78f),
                                PrayerBg.imageFade(0.62f),
                                PrayerBg.imageFade(0.48f),
                                PrayerBg.imageFade(0.66f),
                            ),
                        ),
                    ),
            )

            when {
                current == null -> Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(s.loading, color = PrayerMuted)
                }

                else -> PrayerReadingContent(
                    detail = current,
                    language = language,
                    readingLanguage = readingLanguage,
                    onLanguageChange = { readingLanguage = it },
                    fontScale = FONT_SCALE_STEPS[fontScaleIndex],
                    onFontScaleChange = { newIndex ->
                        fontScaleIndex = newIndex
                        repository.setPreference(
                            PreferenceKeys.PRAYER_FONT_SCALE,
                            FONT_SCALE_STEPS[newIndex].toString(),
                        )
                    },
                    keepScreenOn = keepScreenOn,
                    onKeepScreenOnChange = { keepScreenOn = it },
                    reader = reader,
                    speechEngine = speechEngine,
                    nextPrayer = nextPrayer,
                    onNextPrayer = { next ->
                        autoPlayId = if (reader.isPlaying) next.id else null
                        reader.pause()
                        prayerId = next.id
                    },
                    autoPlayId = autoPlayId,
                    onAutoPlayed = { autoPlayId = null },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = padding.calculateBottomPadding())
                        .padding(top = buttonsHeight),
                )
            }

            // Buttons must be last in the Box Z-order to be clickable above the content.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 18.dp, end = 18.dp, top = 10.dp)
                    .onGloballyPositioned {
                        if (buttonsHeightPx != it.size.height) buttonsHeightPx = it.size.height
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CathopediaBackButton(
                    onClick = onBack,
                    contentDescription = s.back,
                )

                if (current != null) {
                    IconButton(
                        onClick = {
                            scope.launch { isFavorite = repository.togglePrayerFavorite(prayerId) }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.prayer_category_favorites),
                            contentDescription = if (isFavorite) s.detailRemoveFromFavorites else s.detailSaveToFavorites,
                            modifier = Modifier.size(38.dp),
                            alpha = if (isFavorite) 1f else 0.4f
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PrayerReadingContent(
    detail: PrayerDetail,
    language: String,
    readingLanguage: String,
    onLanguageChange: (String) -> Unit,
    fontScale: Float,
    onFontScaleChange: (Int) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    reader: ReadAloudController,
    speechEngine: SpeechEngine,
    nextPrayer: PrayerSummary?,
    onNextPrayer: (PrayerSummary) -> Unit,
    autoPlayId: String?,
    onAutoPlayed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sections = remember(detail.bodyMd) { splitPrayerSections(detail.bodyMd) }
    val script = remember(sections) { prayerSpeechScript(sections.map { it.title to it.body }) }
    LaunchedEffect(script, readingLanguage) {
        reader.load(script, readingLanguage)
        if (autoPlayId == detail.id) {
            onAutoPlayed()
            PrayerAudioPacks.loadInstalled(readingLanguage)
            reader.play()
        }
    }
    // A pack downloaded earlier is opened up front, so the first part is already recorded.
    LaunchedEffect(readingLanguage) { PrayerAudioPacks.loadInstalled(readingLanguage) }

    val listState = remember(detail.id) { LazyListState() }
    val reading = reader.current
    // Follow the voice: bring the section being read into view.
    LaunchedEffect(reading?.section, reader.isPlaying) {
        val section = reading?.section ?: return@LaunchedEffect
        if (reader.isPlaying && section >= 0) listState.animateScrollToItem(section)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(2.dp))

        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            SacredPrayerHeader(
                title = detail.title,
                subtitle = detail.subtitle,
                category = detail.category,
            )
        }

        Spacer(Modifier.height(16.dp))

        // Every language this prayer exists in, as a scrollable row: the reader's own
        // language first, then English and Latin, then the rest in the app's order.
        val readingChoices = orderReadingLanguages(detail.availableLanguages, language)
        if (readingChoices.size > 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
            ) {
                LanguageChipRow(
                    available = readingChoices,
                    selected = readingLanguage,
                    onSelect = onLanguageChange,
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            itemsIndexed(sections) { index, section ->
                val sectionImage = sevenSorrowsImage(detail.id, index)
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    sectionImage?.let { image ->
                        Image(
                            painter = painterResource(image),
                            contentDescription = section.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.18f)
                                .clip(RoundedCornerShape(28.dp))
                                .border(
                                    width = 2.dp,
                                    color = CardBorder,
                                    shape = RoundedCornerShape(28.dp),
                                ),
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    PrayerSectionCard(
                        title = section.title,
                        bodyMd = section.body,
                        fontScale = fontScale,
                        isFirst = index == 0,
                        reading = reading?.takeIf { it.section == index },
                    )
                }
                Spacer(Modifier.height(18.dp))
            }

            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    ReadingActionCard(
                        fontScale = fontScale,
                        onFontScaleChange = onFontScaleChange,
                        keepScreenOn = keepScreenOn,
                        onKeepScreenOnChange = onKeepScreenOnChange,
                    )
                }

                Spacer(Modifier.height(18.dp))

                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    AboutPrayerCard(
                        detail = detail,
                    )
                }
            }
        }

        ReadAloudBar(
            reader = reader,
            engine = speechEngine,
            languageName = nativeLanguageName(readingLanguage, LocalStrings.current),
            nextPrayer = nextPrayer,
            onNextPrayer = onNextPrayer,
        )
    }
}

@Composable
private fun SacredPrayerHeader(
    title: String,
    subtitle: String?,
    category: PrayerCategory,
) {
    val icon = getCategoryIcon(category)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (icon) {
            is CategoryIcon.Resource -> {
                Image(
                    painter = painterResource(icon.res),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(70.dp),
                )
            }
            is CategoryIcon.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    tint = PrayerGold,
                    modifier = Modifier.size(48.dp),
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = title,
            color = PrayerCream,
            fontFamily = FontFamily.Serif,
            fontSize = 34.sp,
            lineHeight = 39.sp,
            textAlign = TextAlign.Center,
        )

        subtitle?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it.uppercase(),
                color = PrayerGoldSoft,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** App language, English, Latin, then the others in [AppLanguages.all] order. */
internal fun orderReadingLanguages(available: List<String>, appLanguage: String): List<String> {
    val preferred = listOf(appLanguage, "en", "la")
    val rest = AppLanguages.all.map { it.code }
    return (preferred + rest + available).distinct().filter { it in available }
}

/** A language's own name (Español, தமிழ்), so a reader finds theirs in any app language. */
private fun nativeLanguageName(code: String, s: Strings): String = when {
    code.equals("la", ignoreCase = true) -> "Latina"
    AppLanguages.all.any { it.code.equals(code, ignoreCase = true) } -> AppLanguages.forCode(code).nativeName
    else -> languageLabel(code, s)
}

@Composable
private fun LanguageChipRow(
    available: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    val s = LocalStrings.current
    val listState = rememberLazyListState()
    // Keep the chosen language in view, e.g. when the screen opens on a language far along the row.
    LaunchedEffect(selected, available) {
        val index = available.indexOf(selected)
        if (index >= 0) listState.animateScrollToItem(index)
    }

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, CardBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(available, key = { it }) { lang ->
                val isSelected = lang == selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) PrayerGold else Color.Transparent,
                            shape = RoundedCornerShape(22.dp),
                        )
                        .clickable { onSelect(lang) }
                        .semantics { this.selected = isSelected }
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = nativeLanguageName(lang, s),
                        color = if (isSelected) PrayerCream else PrayerMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerSectionCard(
    title: String?,
    bodyMd: String,
    fontScale: Float,
    isFirst: Boolean,
    reading: SpeechUnit? = null,
) {
    val s = LocalStrings.current

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(2.dp, if (reading != null) PrayerGold else CardBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            if (isFirst) {
                Text(
                    text = "“",
                    color = PrayerGold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 38.sp,
                    lineHeight = 32.sp,
                )
            }

            if (title != null) {
                Text(
                    text = title.uppercase(),
                    color = if (reading?.paragraph == -1) PrayerGold else PrayerGoldSoft,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (bodyMd.isBlank()) {
                Text(
                    text = s.prayerTextNotYetAvailable,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrayerMuted,
                )
            } else {
                PrayerBodyText(
                    bodyMd = bodyMd,
                    color = PrayerCream,
                    fontScale = fontScale,
                    highlightedParagraph = reading?.paragraph?.takeIf { it >= 0 },
                    highlightColor = PrayerGold,
                )
            }
        }
    }
}

@Composable
private fun ReadingActionCard(
    fontScale: Float,
    onFontScaleChange: (Int) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(2.dp, CardBorder),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            SacredDivider(modifier = Modifier.padding(horizontal = 32.dp))

            Spacer(Modifier.height(16.dp))

            ReadingActionRow(
                fontScale = fontScale,
                onFontScaleChange = onFontScaleChange,
                keepScreenOn = keepScreenOn,
                onKeepScreenOnChange = onKeepScreenOnChange,
            )
        }
    }
}

@Composable
private fun ReadingActionRow(
    fontScale: Float,
    onFontScaleChange: (Int) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
) {
    val s = LocalStrings.current
    val currentIndex = FONT_SCALE_STEPS.indexOf(fontScale).takeIf { it >= 0 } ?: DEFAULT_FONT_SCALE_INDEX

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
    ) {
        RoundAction(
            label = s.prayerDetailFontSmaller,
            icon = { Text("A−", color = PrayerCream, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            onClick = { if (currentIndex > 0) onFontScaleChange(currentIndex - 1) },
        )
        RoundAction(
            label = s.prayerDetailFontLarger,
            icon = { Text("A+", color = PrayerCream, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            onClick = { if (currentIndex < FONT_SCALE_STEPS.lastIndex) onFontScaleChange(currentIndex + 1) },
        )
        RoundAction(
            label = s.prayerDetailKeepScreenOn,
            active = keepScreenOn,
            icon = { Text("☀", color = if (keepScreenOn) PrayerGold else PrayerCream, fontSize = 20.sp) },
            onClick = { onKeepScreenOnChange(!keepScreenOn) },
        )
    }
}

@Composable
private fun RoundAction(
    label: String,
    active: Boolean = false,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(88.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(2.dp, if (active) PrayerGold else PrayerGold.copy(alpha = 0.75f), CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Spacer(Modifier.height(7.dp))
        Text(
            text = label,
            color = if (active) PrayerGold else PrayerMuted,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

/**
 * The read-aloud player docked under the prayer: previous / play-pause / next and
 * the reading speed, in the language of the selected chip. Built for screen-reader
 * users too: every control has a spoken label and a 48dp target.
 */
@Composable
private fun ReadAloudBar(
    reader: ReadAloudController,
    engine: SpeechEngine,
    languageName: String,
    nextPrayer: PrayerSummary?,
    onNextPrayer: (PrayerSummary) -> Unit,
) {
    val s = LocalStrings.current
    val scope = rememberCoroutineScope()
    if (reader.units.isEmpty()) return
    val pack = PrayerAudioPacks.states[reader.language]
    val hasVoice = pack == PackState.Ready || engine.hasVoice(reader.language)
    val downloading = pack as? PackState.Downloading

    Surface(
        shape = RoundedCornerShape(30.dp),
        color = PrayerBg.copy(alpha = 0.94f),
        border = androidx.compose.foundation.BorderStroke(2.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Column {
            if (!hasVoice) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Text(
                        text = s.readAloudNoVoice.replace("{language}", languageName),
                        color = PrayerCream,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(6.dp))
                    if (engine.canInstallVoices) {
                        Text(
                            text = s.readAloudInstallVoice,
                            color = PrayerGold,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(role = Role.Button) { engine.openVoiceInstall() }
                                .padding(vertical = 8.dp),
                        )
                    } else {
                        Text(s.readAloudIosVoiceHint, color = PrayerMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.padding(start = 20.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.readAloudListen.uppercase(),
                            color = PrayerGold,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = when {
                                downloading != null ->
                                    s.readAloudDownloading.replace("{percent}", (downloading.progress * 100).toInt().toString())
                                pack == PackState.Failed && !reader.hasStarted -> s.readAloudDownloadFailed
                                reader.hasStarted -> "$languageName · ${reader.index + 1}/${reader.units.size}"
                                else -> languageName
                            },
                            color = PrayerMuted,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                        )
                    }

                    BarIconButton(Icons.Filled.SkipPrevious, s.readAloudPrevious, enabled = reader.hasStarted) { reader.previous() }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(PrayerGold)
                            .clickable(role = Role.Button) {
                                when {
                                    reader.isPlaying -> reader.pause()
                                    downloading != null -> Unit
                                    else -> scope.launch {
                                        // The first play in a language fetches its recorded voice; if that
                                        // fails (offline), the device voice reads instead.
                                        if (PrayerAudioPacks.needsDownload(reader.language)) {
                                            PrayerAudioPacks.ensure(reader.language)
                                        }
                                        reader.play()
                                    }
                                }
                            }
                            .semantics { contentDescription = if (reader.isPlaying) s.readAloudPause else s.readAloudPlay },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (reader.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = PrayerBg,
                            modifier = Modifier.size(34.dp),
                        )
                    }

                    BarIconButton(Icons.Filled.SkipNext, s.readAloudNext, enabled = reader.hasStarted) { reader.next() }

                    val speed = ReadAloudController.speedLabel(reader.speedIndex)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button) { reader.cycleSpeed() }
                            .semantics { contentDescription = "${s.readAloudSpeed}: $speed" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(speed, color = PrayerCream, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            nextPrayer?.let { next ->
                NextPrayerRow(title = next.title, onClick = { onNextPrayer(next) })
            }
        }
    }
}

/** "Next prayer · Hail Mary ›": moves on through the library without going back to the list. */
@Composable
private fun NextPrayerRow(title: String, onClick: () -> Unit) {
    val s = LocalStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(CardBorder),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { contentDescription = "${s.readAloudNextPrayer}: $title" }
            .heightIn(min = 48.dp)
            .padding(start = 20.dp, end = 14.dp, top = 8.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = s.readAloudNextPrayer.uppercase(),
            color = PrayerGold,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            color = PrayerCream,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = PrayerGold,
            modifier = Modifier.size(26.dp),
        )
    }
}

@Composable
private fun BarIconButton(icon: ImageVector, label: String, enabled: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, enabled = enabled, modifier = Modifier.size(48.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) PrayerCream else PrayerMuted.copy(alpha = 0.5f),
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun AboutPrayerCard(
    detail: PrayerDetail,
) {
    val s = LocalStrings.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            CardBorder,
        ),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = PrayerGold,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    text = s.prayerDetailAboutTitle.uppercase(),
                    color = PrayerGold,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = detail.about.ifBlank { s.prayerDetailAboutFallback },
                color = PrayerCream,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
            )
        }
    }
}

private fun languageLabel(lang: String, s: Strings): String = AppLanguages.labelFor(lang, s)

private data class PrayerSection(
    val title: String?,
    val body: String
)

private fun sevenSorrowsImage(prayerId: String, sectionIndex: Int): DrawableResource? {
    if (prayerId != "seven-sorrows") return null
    return SevenSorrowsImages.getOrNull(sectionIndex - 1)
}

private fun splitPrayerSections(bodyMd: String): List<PrayerSection> =
    prayerSections(bodyMd).map { (title, body) -> PrayerSection(title, body) }
