package com.ynotlabs.cathopedia.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.back_arrow
import com.ynotlabs.cathopedia.resources.check
import com.ynotlabs.cathopedia.resources.info
import com.ynotlabs.cathopedia.resources.settings_header_bg
import org.jetbrains.compose.resources.painterResource

private data class SettingsLanguageOption(
    val code: String,
    val label: String,
    val subtitle: String,
    val available: Boolean,
)

private fun settingsLanguageOptions(s: Strings) = listOf(
    SettingsLanguageOption("en", "English", s.languageNameEnglish, available = true),
    SettingsLanguageOption("fr", "Français", s.languageNameFrench, available = true),
    SettingsLanguageOption("es", "Español", s.languageNameSpanish, available = false),
    SettingsLanguageOption("it", "Italiano", s.languageNameItalian, available = false),
)

@Composable
fun LanguageScreen(
    currentLanguage: String,
    onSelect: (languageCode: String) -> Unit,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val options = remember(s) { settingsLanguageOptions(s) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
    ) {
        SettingsLanguageHero(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 28.dp),
        ) {
            SettingsLanguageSectionTitle(s.languageAppLanguageSectionTitle)

            options.forEach { option ->
                SettingsLanguageRow(
                    option = option,
                    selected = currentLanguage.equals(option.code, ignoreCase = true),
                    onClick = {
                        if (option.available && option.code != currentLanguage) {
                            onSelect(option.code)
                        }
                    },
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(6.dp))

            LanguageInfoCard()
        }
    }
}

@Composable
private fun SettingsLanguageHero(
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.settings_header_bg),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.62f)
                .height(170.dp)
                .padding(top = 22.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopEnd,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.24f),
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
                            MaterialTheme.colorScheme.background.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 12.dp),
        ) {
            CathopediaBackButton(
                onClick = onBack,
                contentDescription = s.back,
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = s.languageScreenTitle,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = s.languageScreenSubtitle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun SettingsLanguageSectionTitle(
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.5.sp,
            letterSpacing = 1.2.sp,
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
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun SettingsLanguageRow(
    option: SettingsLanguageOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val s = LocalStrings.current
    val shape = RoundedCornerShape(17.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) {
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface,
                        ),
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.76f),
                        ),
                    )
                },
            )
            .border(
                width = 2.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.76f),
                shape = shape,
            )
            .then(
                if (option.available) Modifier.clickable(onClick = onClick) else Modifier,
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(45.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.56f),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.42f),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(23.dp),
                )
            }
        }

        Spacer(Modifier.width(13.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                color = if (option.available) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.66f),
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = option.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (option.available) 0.92f else 0.56f),
                fontSize = 11.5.sp,
            )
        }

        when {
            selected -> {
                Image(
                    painter = painterResource(Res.drawable.check),
                    contentDescription = s.selectedDesc,
                    modifier = Modifier.size(31.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            !option.available -> {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
                    ),
                ) {
                    Text(
                        text = s.soon,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageInfoCard() {
    val s = LocalStrings.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.64f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.info),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit,
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = s.languageInfoText,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}
