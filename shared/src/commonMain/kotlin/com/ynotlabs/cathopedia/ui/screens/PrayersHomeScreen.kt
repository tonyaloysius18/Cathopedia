package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.i18n.LocalStrings

/**
 * Tab root for Prayers. Placeholder body — the category list, favourites, and
 * Rosary entry card land in the prayer library task.
 */
@Composable
fun PrayersHomeScreen(
    onOpenPrayer: (slug: String) -> Unit,
    onOpenRosary: () -> Unit,
) {
    val s = LocalStrings.current
    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = s.prayersComingSoon,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
