package com.ynotlabs.cathopedia.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.back_arrow
import com.ynotlabs.cathopedia.resources.info
import com.ynotlabs.cathopedia.resources.settings_header_bg
import org.jetbrains.compose.resources.painterResource

@Composable
fun NotificationsScreen(
    enabled: Boolean,
    permissionDenied: Boolean,
    onToggle: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        NotificationsHeader(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 34.dp),
        ) {
            Spacer(Modifier.height(14.dp))

            FeastToggleCard(
                enabled = enabled,
                onToggle = onToggle,
            )

            Spacer(Modifier.height(14.dp))

            if (permissionDenied) {
                NotificationsInfoCard(text = s.notificationsPermissionDenied)
                Spacer(Modifier.height(10.dp))
            }

            NotificationsInfoCard(text = s.notificationsInfoText)
        }
    }
}

@Composable
private fun NotificationsHeader(
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
                text = s.notificationsTitle,
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = s.notificationsSubtitle,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun FeastToggleCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val s = LocalStrings.current
    val shape = RoundedCornerShape(20.dp)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.58f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = s.notificationsFeastToggleLabel,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = s.notificationsFeastToggleDescription,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp,
                )
            }

            Spacer(Modifier.width(10.dp))

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

@Composable
private fun NotificationsInfoCard(text: String) {
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
                text = text,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}
