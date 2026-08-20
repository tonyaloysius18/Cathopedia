package com.ynotlabs.cathopedia.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.app_logo
import com.ynotlabs.cathopedia.resources.cathopedia_app_logo_transparent
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

private val CREDITS = listOf(
    Credit("Compose Multiplatform", "Cross-platform user interface"),
    Credit("SQLDelight", "Local structured data and persistence"),
    Credit("Kotlinx Coroutines", "Asynchronous application logic"),
    Credit("Kotlinx Serialization", "Content and data serialization"),
)

@Composable
fun AboutScreen(
    onBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

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

            SectionTitle("About Cathopedia")
            AboutCard {
                Text(
                    text = "Cathopedia is an offline-first Catholic encyclopedia and spiritual reference " +
                            "designed to bring saints, popes, apostles, churches, Marian apparitions, " +
                            "Eucharistic miracles and liturgical feasts together in one carefully organized experience.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle("App & data")
            AboutCard {
                InfoRow(
                    icon = Icons.Filled.Storage,
                    title = "Offline-first",
                    subtitle = "Core encyclopedia content is stored on your device.",
                )

                ThinDivider()

                InfoRow(
                    icon = Icons.Filled.Lock,
                    title = "Privacy",
                    subtitle = "No account is required for the core app experience.",
                )

                ThinDivider()

                InfoRow(
                    icon = Icons.Filled.AutoStories,
                    title = "Saved entries",
                    subtitle = "Bookmarks and preferences are kept locally on this device.",
                )
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle("Developer")
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
                            text = "$STUDIO_NAME · Android & iOS",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                }

                ThinDivider()

                LinkRow(
                    icon = Icons.Filled.Code,
                    title = "GitHub",
                    subtitle = "View projects and source",
                ) {
                    uriHandler.openUri(GITHUB_URL)
                }

                ThinDivider()

                LinkRow(
                    icon = Icons.Filled.Link,
                    title = "LinkedIn",
                    subtitle = "Connect with the developer",
                ) {
                    uriHandler.openUri(LINKEDIN_URL)
                }

                ThinDivider()

                LinkRow(
                    icon = Icons.Filled.Email,
                    title = "Contact",
                    subtitle = CONTACT_EMAIL,
                ) {
                    uriHandler.openUri("mailto:$CONTACT_EMAIL")
                }
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle("Acknowledgements")
            AboutCard {
                CREDITS.forEachIndexed { index, credit ->
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

                    if (index != CREDITS.lastIndex) {
                        ThinDivider(startPadding = 35.dp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle("Content note")
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
                        text = "Cathopedia is an independent educational reference app. " +
                                "It is not an official publication of the Holy See or any diocese. " +
                                "Historical and devotional material should be read alongside authoritative Church sources.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "© 2026 $STUDIO_NAME",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.62f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Knowledge · Faith · Tradition",
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable(onClick = onBack),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(21.dp),
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = "About",
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 35.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "App info, storage & attributions",
            modifier = Modifier.padding(start = 58.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
            fontSize = 14.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun AppIdentity() {
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
            text = "Version $APP_VERSION",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "A Catholic knowledge companion for learning, discovery and reflection.",
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
        border = androidx.compose.foundation.BorderStroke(
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
            border = androidx.compose.foundation.BorderStroke(
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
