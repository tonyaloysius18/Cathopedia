package com.ynotlabs.cathopedia.ui.screens.startup

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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.Strings
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.cathopedia_app_logo_transparent
import com.ynotlabs.cathopedia.resources.check
import org.jetbrains.compose.resources.painterResource

private data class StartupLanguageOption(
    val code: String,
    val label: String,
    val subtitle: String,
    val available: Boolean,
)

private fun startupLanguageOptions(s: Strings) = listOf(
    StartupLanguageOption("en", "English", s.languageNameEnglish, available = true),
    StartupLanguageOption("fr", "Français", s.languageNameFrench, available = true),
    StartupLanguageOption("es", "Español", s.languageNameSpanish, available = false),
    StartupLanguageOption("it", "Italiano", s.languageNameItalian, available = false),
)

@Composable
fun LanguageScreenStartup(
    currentLanguage: String = "en",
    onContinue: (languageCode: String) -> Unit,
) {
    val s = LocalStrings.current
    val options = remember(s) { startupLanguageOptions(s) }
    var selected by remember(currentLanguage) { mutableStateOf(currentLanguage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .size(126.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.cathopedia_app_logo_transparent),
                contentDescription = "Cathopedia",
                modifier = Modifier.size(108.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = s.languageWelcomeToCathopedia,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Serif,
            fontSize = 29.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = s.languageChoosePrompt,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        options.forEach { option ->
            StartupLanguageRow(
                option = option,
                selected = selected == option.code,
                onClick = {
                    if (option.available) selected = option.code
                },
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clickable { onContinue(selected) },
            shape = RoundedCornerShape(29.dp),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = s.continueLabel,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)
                        .size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.26f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = s.languageChangeLaterInSettings,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StartupLanguageRow(
    option: StartupLanguageOption,
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
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.78f),
                shape = shape,
            )
            .then(
                if (option.available) Modifier.clickable(onClick = onClick) else Modifier,
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
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
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Spacer(Modifier.width(13.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                color = if (option.available) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.68f),
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = option.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (option.available) 0.92f else 0.58f),
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
