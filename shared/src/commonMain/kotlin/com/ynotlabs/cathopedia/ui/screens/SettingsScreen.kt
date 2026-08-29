package com.ynotlabs.cathopedia.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.ic_about
import com.ynotlabs.cathopedia.resources.ic_appearance
import com.ynotlabs.cathopedia.resources.ic_feedback
import com.ynotlabs.cathopedia.resources.ic_notification
import com.ynotlabs.cathopedia.ui.theme.ThemeMode
import com.ynotlabs.cathopedia.ui.theme.label
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.ynotlabs.cathopedia.resources.ic_language
import com.ynotlabs.cathopedia.resources.ic_saved
import com.ynotlabs.cathopedia.resources.settings_header_bg
import com.ynotlabs.cathopedia.resources.settings_quote_bg


@Composable
fun SettingsScreen(
    language: String,
    themeMode: ThemeMode,
    notificationsEnabled: Boolean,
    onOpenLanguage: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSaved: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenFeedback: () -> Unit,
) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(bottom = 88.dp),
    ) {
        SettingsHero()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        ) {
            SettingsSectionLabel(s.settingsPreferencesSectionTitle)

            SettingsGroup {
                PremiumSettingsRow(
                    icon = Res.drawable.ic_language,
                    label = s.settingsLanguageRowLabel,
                    value = languageDisplayName(language, s),
                    onClick = onOpenLanguage,
                )

                SettingsDivider()

                PremiumSettingsRow(
                    icon = Res.drawable.ic_appearance,
                    label = s.settingsAppearanceRowLabel,
                    value = themeMode.label(s),
                    onClick = onOpenAppearance,
                )

                SettingsDivider()

                PremiumSettingsRow(
                    icon =  Res.drawable.ic_notification,
                    label = s.settingsNotificationsRowLabel,
                    value = if (notificationsEnabled) s.settingsNotificationsRowValueOn else s.settingsNotificationsRowValueOff,
                    onClick = onOpenNotifications,
                )
            }

            Spacer(Modifier.height(16.dp))

            SettingsSectionLabel(s.settingsLibraryAppSectionTitle)

            SettingsGroup {
                PremiumSettingsRow(
                    icon = Res.drawable.ic_saved,
                    label = s.settingsSavedRowLabel,
                    value = s.settingsSavedRowValue,
                    onClick = onOpenSaved,
                )

                SettingsDivider()

                PremiumSettingsRow(
                    icon = Res.drawable.ic_about,
                    label = s.settingsAboutRowLabel,
                    value = s.settingsAboutRowValue,
                    onClick = onOpenAbout,
                )

                SettingsDivider()

                PremiumSettingsRow(
                    icon = Res.drawable.ic_feedback,
                    label = s.settingsFeedbackRowLabel,
                    value = s.settingsFeedbackRowValue,
                    onClick = onOpenFeedback,
                )
            }

            Spacer(Modifier.height(12.dp))

            InspirationCard()

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "CATHOPEDIA",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.78f),
                    fontSize = 10.sp,
                    letterSpacing = 1.8.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = s.brandTagline,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun SettingsHero() {
    val s = LocalStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.settings_header_bg),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.95f)
                .height(170.dp)
                .padding(top = 40.dp, end = 2.dp),
            contentScale = ContentScale.Fit,
            alignment = Alignment.TopEnd,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.97f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.76f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.16f),
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
                            Color.Black.copy(alpha = 0.20f),
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 18.dp, top = 8.dp, bottom = 16.dp),
        ) {
            Spacer(Modifier.height(18.dp))

            Text(
                text = s.settingsTitle,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 35.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = s.settingsSubtitle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun SettingsSectionLabel(
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
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
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun SettingsGroup(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f),
                shape = RoundedCornerShape(20.dp),
            ),
    ) {
        content()
    }
}

@Composable
private fun PremiumSettingsRow(
    icon: DrawableResource,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.58f))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.74f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(35.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.5.sp,
                lineHeight = 16.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(21.dp),
            )
        }
    }
}

@Composable
private fun PremiumSettingsRowVector(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.58f))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.74f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(Modifier.height(3.dp))

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.5.sp,
                lineHeight = 16.sp,
            )
        }

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(21.dp),
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 76.dp, end = 14.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
    )
}

@Composable
private fun InspirationCard() {
    val s = LocalStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(122.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f),
                shape = RoundedCornerShape(18.dp),
            ),
    ) {
        Image(
            painter = painterResource(Res.drawable.settings_quote_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.86f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.62f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.30f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxWidth(0.68f)
                .padding(end = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "“",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                lineHeight = 20.sp,
            )

            Text(
                text = s.settingsInspirationQuote,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 1.dp),
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = s.settingsInspirationReference,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private fun languageDisplayName(language: String, s: Strings): String =
    when (language.lowercase()) {
        "en", "eng", "english" -> s.languageNameEnglish
        "fr", "fra", "fre", "french", "français" -> s.languageNameFrench
        "es", "spa", "spanish", "español" -> s.languageNameSpanish
        "it", "ita", "italian", "italiano" -> s.languageNameItalian
        "de", "deu", "ger", "german", "deutsch" -> s.languageNameGerman
        else -> language.uppercase()
    }
