package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.back_arrow
import com.ynotlabs.cathopedia.resources.cathopedia_app_logo_transparent
import com.ynotlabs.cathopedia.resources.settings_header_bg
import org.jetbrains.compose.resources.painterResource

private const val APP_VERSION = "1.0.0"
private const val GITHUB_URL = "https://github.com/tonyaloysius18"
private const val LINKEDIN_URL = "https://www.linkedin.com/in/tony-ajay-aloysius-70195a1a4"
private const val CONTACT_EMAIL = "ynotlabs.dev@gmail.com"
private const val AUTHOR_NAME = "Tony Aloysius"
private const val STUDIO_NAME = "YNOT Labs"

private data class Credit(
    val name: String,
    val purpose: String,
)

private fun credits(s: Strings) = listOf(
    Credit("Compose Multiplatform", s.aboutCreditComposePurpose),
    Credit("SQLDelight", s.aboutCreditSqlDelightPurpose),
    Credit("Kotlinx Coroutines", s.aboutCreditCoroutinesPurpose),
    Credit("Kotlinx Serialization", s.aboutCreditSerializationPurpose),
)

@Composable
fun AboutScreen(
    onBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val s = LocalStrings.current
    val credits = remember(s) { credits(s) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        AboutHero(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
        ) {
            AppIdentity()

            Spacer(Modifier.height(24.dp))

            SectionTitle(s.aboutSectionTitle)
            AboutCard {
                Text(
                    text = s.aboutBody,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle(s.aboutAppDataSectionTitle)
            AboutCard {
                InfoRow(
                    icon = Icons.Filled.Storage,
                    title = s.aboutOfflineFirstTitle,
                    subtitle = s.aboutOfflineFirstSubtitle,
                )

                ThinDivider()

                InfoRow(
                    icon = Icons.Filled.Lock,
                    title = s.aboutPrivacyTitle,
                    subtitle = s.aboutPrivacySubtitle,
                )

                ThinDivider()

                InfoRow(
                    icon = Icons.Filled.AutoStories,
                    title = s.aboutSavedEntriesTitle,
                    subtitle = s.aboutSavedEntriesSubtitle,
                )
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle(s.aboutDeveloperSectionTitle)
            AboutCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AUTHOR_NAME,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "$STUDIO_NAME ${s.aboutStudioSuffix}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                }

                ThinDivider()

                LinkRow(
                    icon = Icons.Filled.Code,
                    title = s.aboutGithubTitle,
                    subtitle = s.aboutGithubSubtitle,
                ) {
                    uriHandler.openUri(GITHUB_URL)
                }

                ThinDivider()

                LinkRow(
                    icon = Icons.Filled.Link,
                    title = s.aboutLinkedinTitle,
                    subtitle = s.aboutLinkedinSubtitle,
                ) {
                    uriHandler.openUri(LINKEDIN_URL)
                }

                ThinDivider()

                LinkRow(
                    icon = Icons.Filled.Email,
                    title = s.aboutContactTitle,
                    subtitle = CONTACT_EMAIL,
                ) {
                    uriHandler.openUri("mailto:$CONTACT_EMAIL")
                }
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle(s.aboutAcknowledgementsSectionTitle)
            AboutCard {
                credits.forEachIndexed { index, credit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = credit.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = credit.purpose,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp,
                            )
                        }
                    }

                    if (index != credits.lastIndex) {
                        ThinDivider(startPadding = 35.dp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle(s.aboutContentNoteSectionTitle)
            AboutCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(21.dp),
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = s.aboutContentNoteBody,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "${s.aboutCopyrightPrefix} $STUDIO_NAME",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = s.brandTagline,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f),
                fontSize = 10.5.sp,
                letterSpacing = 0.6.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun AboutHero(
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
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.back_arrow),
                        contentDescription = s.back,
                        modifier = Modifier.size(34.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = s.aboutTitle,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = s.aboutSubtitle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun AppIdentity() {
    val s = LocalStrings.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier = Modifier.size(92.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            shadowElevation = 6.dp,
        ) {
            Image(
                painter = painterResource(Res.drawable.cathopedia_app_logo_transparent),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Cathopedia",
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Serif,
            fontSize = 27.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = "${s.aboutVersionPrefix} $APP_VERSION",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = s.aboutAppTagline,
            modifier = Modifier.padding(horizontal = 36.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 3.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.sp,
            letterSpacing = 1.25.sp,
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
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.50f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun AboutCard(
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f),
        ),
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(19.dp),
                )
            }
        }

        Spacer(Modifier.width(13.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
            )
        }
    }
}

@Composable
private fun LinkRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(21.dp),
        )

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 14.sp,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.62f),
            modifier = Modifier.size(17.dp),
        )
    }
}

@Composable
private fun ThinDivider(
    startPadding: androidx.compose.ui.unit.Dp = 66.dp,
) {
    HorizontalDivider(
        modifier = Modifier.padding(start = startPadding, end = 14.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.48f),
        thickness = 1.dp,
    )
}
