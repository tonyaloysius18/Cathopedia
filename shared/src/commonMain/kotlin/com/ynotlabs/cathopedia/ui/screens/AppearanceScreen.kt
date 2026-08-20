package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.ui.theme.ThemeMode
import com.ynotlabs.cathopedia.ui.theme.MetallicGold
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.cathopedia_app_logo_transparent
import com.ynotlabs.cathopedia.resources.back_arrow
import com.ynotlabs.cathopedia.resources.check
import com.ynotlabs.cathopedia.resources.dark
import com.ynotlabs.cathopedia.resources.info
import com.ynotlabs.cathopedia.resources.light
import com.ynotlabs.cathopedia.resources.settings_header_bg
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppearanceScreen(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        AppearanceHeader(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 34.dp),
        ) {
            Spacer(Modifier.height(14.dp))

            SectionTitle("Theme mode")

            ThemeSelector(
                selected = selected,
                onSelect = onSelect,
            )

            Spacer(Modifier.height(14.dp))

            ThemeInfo(selected = selected)

            Spacer(Modifier.height(22.dp))

            SectionTitle("Preview")

            ThemePreview(selected = selected)
        }
    }
}

@Composable
private fun AppearanceHeader(
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier
                .size(42.dp)
                .clickable(onClick = onBack),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            ),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(Res.drawable.back_arrow),
                    contentDescription = "Back",
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = "Appearance",
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 35.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Customize the look and feel",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun SectionTitle(
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
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun ThemeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ThemeChoiceCard(
            mode = ThemeMode.SYSTEM,
            subtitle = "Match your\ndevice setting",
            selected = selected == ThemeMode.SYSTEM,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(ThemeMode.SYSTEM) },
        )

        Spacer(Modifier.width(8.dp))

        ThemeChoiceCard(
            mode = ThemeMode.LIGHT,
            subtitle = "Always use the\nlight theme",
            selected = selected == ThemeMode.LIGHT,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(ThemeMode.LIGHT) },
        )

        Spacer(Modifier.width(8.dp))

        ThemeChoiceCard(
            mode = ThemeMode.DARK,
            subtitle = "Always use the\ndark theme",
            selected = selected == ThemeMode.DARK,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(ThemeMode.DARK) },
        )
    }
}

@Composable
private fun ThemeChoiceCard(
    mode: ThemeMode,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    val isItemLight = mode == ThemeMode.LIGHT
    
    // The background of the card should be based on its type (Light/Dark/System)
    // but the border and selection should respect the current theme.
    val cardBackground = if (isItemLight) {
        Brush.verticalGradient(listOf(Color(0xFFFFFBF2), Color(0xFFF1E8D5)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF0D3328), Color(0xFF08251D)))
    }

    val titleColor = if (isItemLight) Color(0xFF161A18) else Color(0xFFF4ECDD)
    val subtitleColor = if (isItemLight) Color(0xFF333A36) else Color(0xFFF4ECDD).copy(alpha = 0.90f)
    val idleBorder = if (isItemLight) Color(0xFFD8CDB9) else Color(0xFF315444).copy(alpha = 0.90f)

    Box(
        modifier = modifier
            .height(190.dp)
            .clip(shape)
            .background(cardBackground)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else idleBorder,
                shape = shape,
            )
            .clickable(onClick = onClick),
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.23f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                Color.Transparent,
                            ),
                            radius = 250f,
                        ),
                    ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))

                if (selected) {
                    Image(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = "Selected",
                        modifier = Modifier.size(30.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(27.dp)
                            .border(
                                width = 1.2.dp,
                                color = if (isItemLight) Color(0xFF858782) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
                                shape = CircleShape,
                            ),
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            Box(
                modifier = Modifier.size(68.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (mode) {
                    ThemeMode.SYSTEM -> {
                        Box(modifier = Modifier.size(68.dp)) {
                            Image(
                                painter = painterResource(Res.drawable.light),
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .size(42.dp),
                                contentScale = ContentScale.Fit,
                            )
                            Canvas(modifier = Modifier.matchParentSize()) {
                                drawLine(
                                    color = MetallicGold.copy(alpha = 0.55f),
                                    start = androidx.compose.ui.geometry.Offset(size.width * 0.72f, size.height * 0.28f),
                                    end = androidx.compose.ui.geometry.Offset(size.width * 0.28f, size.height * 0.72f),
                                    strokeWidth = 2.dp.toPx(),
                                )
                            }
                            Image(
                                painter = painterResource(Res.drawable.dark),
                                contentDescription = "System theme",
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(40.dp),
                                contentScale = ContentScale.Fit,
                            )
                        }
                    }
                    ThemeMode.LIGHT -> {
                        Image(
                            painter = painterResource(Res.drawable.light),
                            contentDescription = "Light theme",
                            modifier = Modifier.size(58.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    ThemeMode.DARK -> {
                        Image(
                            painter = painterResource(Res.drawable.dark),
                            contentDescription = "Dark theme",
                            modifier = Modifier.size(58.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }

            Spacer(Modifier.height(7.dp))

            Text(
                text = mode.label,
                color = titleColor,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = subtitle,
                color = subtitleColor,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Composable
private fun ThemeInfo(
    selected: ThemeMode,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.64f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.09f),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.26f),
                ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.info),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = when (selected) {
                    ThemeMode.SYSTEM -> "System follows the appearance setting of your device."
                    ThemeMode.LIGHT -> "Light keeps Cathopedia in its brighter theme regardless of your device setting."
                    ThemeMode.DARK -> "Dark keeps Cathopedia in its deep forest-green theme regardless of your device setting."
                },
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun ThemePreview(
    selected: ThemeMode,
) {
    val isLightPreview = selected == ThemeMode.LIGHT

    val cardBackground = if (isLightPreview) Color(0xFFF3EBDD) else Color(0xFF082219)
    val primaryText = if (isLightPreview) Color(0xFF173128) else Color(0xFFF4ECDD)
    val secondaryText = if (isLightPreview) Color(0xFF514C43) else Color(0xFFF2EEE5)
    val cardShape = RoundedCornerShape(22.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp)
            .clip(cardShape)
            .background(cardBackground)
            .border(
                width = 1.25.dp,
                color = if (isLightPreview) Color(0xFF9D8858).copy(alpha = 0.48f) else Color(0xFF2D715D).copy(alpha = 0.78f),
                shape = cardShape,
            ),
    ) {
        Image(
            painter = painterResource(Res.drawable.settings_header_bg),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxWidth(0.55f)
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.00f to cardBackground,
                            0.48f to cardBackground.copy(alpha = 0.98f),
                            0.68f to cardBackground.copy(alpha = 0.72f),
                            1.00f to cardBackground.copy(alpha = 0.16f),
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
                            Color.White.copy(alpha = if (isLightPreview) 0.04f else 0.025f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.12f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, top = 13.dp, end = 14.dp, bottom = 13.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(Res.drawable.cathopedia_app_logo_transparent),
                    contentDescription = "Cathopedia",
                    modifier = Modifier.size(54.dp),
                    contentScale = ContentScale.Fit,
                )

                Spacer(Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cathopedia",
                        color = primaryText,
                        fontFamily = FontFamily.Serif,
                        fontSize = 23.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "Faith • Knowledge • Tradition",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.5.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "Explore the richness of our Catholic faith\nwith a beautiful reading experience.",
                    modifier = Modifier.weight(1f),
                    color = secondaryText,
                    fontSize = 12.3.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}
