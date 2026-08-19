package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.ui.Portraits
import com.ynotlabs.cathopedia.ui.components.PortraitHeroCard
import com.ynotlabs.cathopedia.ui.singularLabel

/**
 * POC scope per the app-flow doc: Discover + Continue reading only. The
 * feast-of-the-day header needs the liturgical calendar engine (v1.0).
 */
@Composable
fun HomeScreen(
    repository: CathopediaRepository,
    language: String,
    onItemSelected: (ContentSummary) -> Unit,
) {
    var continueReading by remember { mutableStateOf<ContentSummary?>(null) }
    var discover by remember { mutableStateOf<ContentSummary?>(null) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(language) {
        continueReading = repository.mostRecentlyViewed()
        discover = repository.discoverPick(language)
        loaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cathopedia") },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 116.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (continueReading != null) {
                SectionLabel("Continue reading")
                HomeCard(continueReading!!, onClick = { onItemSelected(continueReading!!) })
            }

            if (discover != null) {
                SectionLabel("Discover")
                HomeCard(discover!!, onClick = { onItemSelected(discover!!) })
            }

            if (loaded && continueReading == null && discover == null) {
                Text(
                    "Nothing to show yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
    )
}

@Composable
private fun HomeCard(item: ContentSummary, onClick: () -> Unit) {
    // Full (uncropped, 3:4) art — the card image box is 3:4, and the pre-cropped
    // "portrait" variant is narrower than that, which crops into the subject's head.
    val portrait = Portraits.fullForEntity(item.type, item.id)
    if (portrait != null) {
        PortraitHeroCard(
            portrait = portrait,
            typeLabel = item.type.singularLabel(),
            name = item.name,
            subtitle = item.summary,
            onClick = onClick,
        )
    } else {
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(item.name)
                Text(item.summary, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}
