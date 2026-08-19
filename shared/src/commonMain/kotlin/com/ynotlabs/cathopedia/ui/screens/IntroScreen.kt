package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class IntroCard(val glyph: String, val title: String, val body: String)

private val INTRO_CARDS = listOf(
    IntroCard(
        "✧",
        "Everything connects",
        "A saint leads to a feast, a feast to a prayer, a prayer to the church that holds his relics. Follow the links wherever they go.",
    ),
    IntroCard(
        "◇",
        "Works without a signal",
        "The whole encyclopedia is on your device. No connection needed inside a basilica, on a pilgrimage, or anywhere else.",
    ),
    IntroCard(
        "⌕",
        "Search across everything",
        "One search box reaches saints, popes, churches, apparitions and miracles at once — instantly, offline.",
    ),
)

/** Three cards, skippable from card one, first run only. */
@Composable
fun IntroScreen(onDone: () -> Unit) {
    var page by remember { mutableStateOf(0) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "Skip",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onDone)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            val card = INTRO_CARDS[page]
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(card.glyph, fontSize = 34.sp, modifier = Modifier.padding(bottom = 16.dp))
                Text(card.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    card.body,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                INTRO_CARDS.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == page) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                            ),
                    )
                }
            }

            Button(
                onClick = { if (page < INTRO_CARDS.lastIndex) page++ else onDone() },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            ) {
                Text(if (page < INTRO_CARDS.lastIndex) "Next" else "Get started")
            }
        }
    }
}
