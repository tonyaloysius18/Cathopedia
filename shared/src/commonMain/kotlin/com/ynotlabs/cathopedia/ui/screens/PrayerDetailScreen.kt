package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
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
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.model.PrayerCategory
import com.ynotlabs.cathopedia.model.PrayerDetail
import com.ynotlabs.cathopedia.ui.getCategoryIcon
import com.ynotlabs.cathopedia.ui.CategoryIcon
import com.ynotlabs.cathopedia.ui.PrayerPortraits
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.prayer_category_favorites
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.components.KeepScreenOn
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.PrayerBodyText
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private val FONT_SCALE_STEPS = listOf(0.85f, 1.0f, 1.15f, 1.3f, 1.45f)
private const val DEFAULT_FONT_SCALE_INDEX = 1

private val PrayerBg = Color(0xFF061A13)
private val PrayerGold = Color(0xFFD6AE3D)
private val PrayerGoldSoft = Color(0xFFAA9158)
private val PrayerCream = Color(0xFFF4ECDD)
private val PrayerMuted = Color(0xFFB7B09D)

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

    var readingLanguage by remember(slug) { mutableStateOf(language) }
    var detail by remember(slug) { mutableStateOf<PrayerDetail?>(null) }
    var isFavorite by remember(slug) { mutableStateOf(false) }
    var keepScreenOn by remember(slug) { mutableStateOf(false) }
    var fontScaleIndex by remember(slug) { mutableStateOf(DEFAULT_FONT_SCALE_INDEX) }

    var buttonsHeightPx by remember { mutableIntStateOf(0) }
    val buttonsHeight = with(density) { buttonsHeightPx.toDp() }

    LaunchedEffect(slug) {
        isFavorite = repository.isPrayerFavorite(slug)
        fontScaleIndex = FONT_SCALE_STEPS.indexOf(
            repository.getPreference(PreferenceKeys.PRAYER_FONT_SCALE)?.toFloatOrNull()
                ?: FONT_SCALE_STEPS[DEFAULT_FONT_SCALE_INDEX],
        ).takeIf { it >= 0 } ?: DEFAULT_FONT_SCALE_INDEX
        repository.recordPrayerRecited(slug)
    }

    LaunchedEffect(slug, language) { readingLanguage = language }
    LaunchedEffect(slug, readingLanguage) { detail = repository.prayerDetail(slug, readingLanguage) }

    KeepScreenOn(enabled = keepScreenOn)

    val current = detail

    Scaffold(
        containerColor = Color.Transparent,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Fill the entire screen with the prayer artwork
            PrayerPortraits.forPrayer(slug)?.let { portrait ->
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
                                PrayerBg.copy(alpha = 0.78f),
                                PrayerBg.copy(alpha = 0.62f),
                                PrayerBg.copy(alpha = 0.48f),
                                PrayerBg.copy(alpha = 0.66f),
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
                            scope.launch { isFavorite = repository.togglePrayerFavorite(slug) }
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
    readingLanguage: String,
    onLanguageChange: (String) -> Unit,
    fontScale: Float,
    onFontScaleChange: (Int) -> Unit,
    keepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sections = remember(detail.bodyMd) { splitPrayerSections(detail.bodyMd) }

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

        if (detail.availableLanguages.size > 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
            ) {
                LanguageSegmentedControl(
                    available = detail.availableLanguages,
                    selected = readingLanguage,
                    onSelect = onLanguageChange,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            items(sections) { section ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    PrayerSectionCard(
                        title = section.title,
                        bodyMd = section.body,
                        fontScale = fontScale,
                        isFirst = section == sections.first(),
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

@Composable
private fun LanguageSegmentedControl(
    available: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    val s = LocalStrings.current

    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, PrayerGold.copy(alpha = 0.75f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            available.forEach { lang ->
                val isSelected = lang == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.Transparent)
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) PrayerGold else Color.Transparent,
                            shape = RoundedCornerShape(22.dp),
                        )
                        .clickable { onSelect(lang) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = languageLabel(lang, s),
                        color = if (isSelected) PrayerCream else PrayerMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
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
) {
    val s = LocalStrings.current

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(2.dp, PrayerGold.copy(alpha = 0.75f)),
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
                    color = PrayerGoldSoft,
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
        border = androidx.compose.foundation.BorderStroke(2.dp, PrayerGold.copy(alpha = 0.75f)),
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
            PrayerGold.copy(alpha = 0.75f),
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

private fun languageLabel(lang: String, s: Strings): String = when (lang) {
    "en" -> s.prayerLanguageEn
    "fr" -> s.prayerLanguageFr
    "la" -> s.prayerLanguageLa
    else -> lang
}

private data class PrayerSection(
    val title: String?,
    val body: String
)

private fun splitPrayerSections(bodyMd: String): List<PrayerSection> {
    if (!bodyMd.contains("#")) {
        return listOf(PrayerSection(null, bodyMd))
    }

    val sections = mutableListOf<PrayerSection>()
    val lines = bodyMd.lines()
    var currentTitle: String? = null
    var currentBody = StringBuilder()

    for (line in lines) {
        if (line.trim().startsWith("#")) {
            if (currentBody.isNotEmpty() || currentTitle != null) {
                sections.add(PrayerSection(currentTitle, currentBody.toString().trim()))
            }
            currentTitle = line.trim().trimStart('#').trim()
            currentBody = StringBuilder()
        } else {
            currentBody.append(line).append("\n")
        }
    }

    if (currentBody.isNotEmpty() || currentTitle != null) {
        sections.add(PrayerSection(currentTitle, currentBody.toString().trim()))
    }

    return sections
}
