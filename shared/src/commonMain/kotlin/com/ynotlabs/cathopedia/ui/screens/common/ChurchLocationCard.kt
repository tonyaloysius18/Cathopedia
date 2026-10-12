package com.ynotlabs.cathopedia.ui.screens.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.maps.ChurchMapLocation
import com.ynotlabs.cathopedia.maps.openAppleChurchMap
import com.ynotlabs.cathopedia.maps.supportsAppleChurchMaps
import com.ynotlabs.cathopedia.ui.theme.ChurchBg
import com.ynotlabs.cathopedia.ui.theme.ChurchBorder
import com.ynotlabs.cathopedia.ui.theme.ChurchCard
import com.ynotlabs.cathopedia.ui.theme.ChurchCream
import com.ynotlabs.cathopedia.ui.theme.ChurchGold
import com.ynotlabs.cathopedia.ui.theme.ChurchMuted
import com.ynotlabs.cathopedia.ui.theme.LocalIsDarkTheme

@Composable
internal fun ChurchLocationCard(location: ChurchMapLocation) {
    val t = LocalScreenText.current
    val uriHandler = LocalUriHandler.current
    val colors = MaterialTheme.colorScheme
    val dark = LocalIsDarkTheme.current
    val gold = if (dark) ChurchGold else colors.primary
    val ink = if (dark) ChurchCream else colors.onSurface
    val muted = if (dark) ChurchMuted else colors.onSurfaceVariant
    var choosingDirections by remember(location) { mutableStateOf<Boolean?>(null) }
    var failed by remember(location) { mutableStateOf(false) }

    fun open(directions: Boolean, apple: Boolean) {
        failed = try {
            if (apple) !openAppleChurchMap(location, directions) else {
                uriHandler.openUri(location.googleMapsUrl(directions))
                false
            }
        } catch (_: Exception) {
            true
        }
        choosingDirections = null
    }

    fun request(directions: Boolean) {
        failed = false
        if (supportsAppleChurchMaps) choosingDirections = directions else open(directions, apple = false)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = if (dark) ChurchCard else colors.surfaceContainerHigh,
        border = BorderStroke(1.dp, if (dark) ChurchBorder.copy(alpha = 0.65f) else colors.outlineVariant),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(44.dp).background(gold.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocationOn, null, tint = gold, modifier = Modifier.size(24.dp))
                }
                Text(t["screen.church.maps.title"], color = gold, fontFamily = FontFamily.Serif,
                    fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f))
            }
            Text(location.name, color = ink, fontSize = 17.sp, lineHeight = 24.sp,
                fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 14.dp))
            location.address?.let { address ->
                Text(address, color = muted, fontSize = 13.sp, lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 4.dp))
            }
            Text(t["screen.church.maps.hint"], color = muted, fontSize = 13.sp, lineHeight = 19.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
            OutlinedButton(
                onClick = { request(false) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, gold.copy(alpha = 0.7f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ink),
            ) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, null, Modifier.padding(end = 8.dp).size(18.dp))
                Text(t["screen.church.maps.view"], lineHeight = 20.sp, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { request(true) }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = gold, contentColor = if (dark) ChurchBg else colors.onPrimary),
            ) {
                Icon(Icons.Default.NearMe, null, Modifier.padding(end = 8.dp).size(18.dp))
                Text(t["screen.church.maps.directions"], lineHeight = 20.sp, modifier = Modifier.weight(1f))
            }
            if (failed) {
                Text(t["screen.church.maps.error"], color = ink, fontSize = 13.sp, lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 12.dp))
            }
        }
    }

    choosingDirections?.let { directions ->
        AlertDialog(
            onDismissRequest = { choosingDirections = null },
            title = { Text(t["screen.church.maps.choose"]) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(location.name, modifier = Modifier.padding(bottom = 4.dp))
                    Text(if (directions) t["screen.church.maps.directions"] else t["screen.church.maps.view"])
                    OutlinedButton(onClick = { open(directions, apple = true) }, modifier = Modifier.fillMaxWidth()) { Text("Apple Maps") }
                    OutlinedButton(onClick = { open(directions, apple = false) }, modifier = Modifier.fillMaxWidth()) { Text("Google Maps") }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { choosingDirections = null }) { Text(t["screen.church.maps.cancel"]) } },
        )
    }
}
