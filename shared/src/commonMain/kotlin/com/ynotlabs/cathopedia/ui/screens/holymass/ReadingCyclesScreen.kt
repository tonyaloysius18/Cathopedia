package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGoldSoft
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurfaceRaised
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

private const val READING_CYCLES_ARTICLE_ID = "art.mass.reading_cycles"
private const val NOW_LABEL_KEY = "art.cycles.now.label"
private const val NOW_BODY_KEY = "art.cycles.now.body"

/**
 * "Years A, B and C" — Holy Mass → The Cycle of Readings.
 *
 * The four Gospels as a set, so unnumbered. The one thing the article cannot author
 * is which year it is: that comes from [LiturgicalCalendar] and turns over on the
 * First Sunday of Advent, shown in a card above the entries.
 */
@Composable
fun ReadingCyclesScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(language) {
        strings = repository.resolveHubStrings(setOf(NOW_LABEL_KEY, NOW_BODY_KEY), language)
    }

    val today = LiturgicalCalendar.today()
    val sunday = LiturgicalCalendar.sundayCycleFor(today)
    val weekday = if (LiturgicalCalendar.weekdayCycleFor(today) == 1) "I" else "II"

    IllustratedEntriesArticle(
        articleId = READING_CYCLES_ARTICLE_ID,
        repository = repository,
        language = language,
        onBack = onBack,
        numbered = false,
        listState = listState,
        afterIntro = {
            CurrentCycleCard(
                label = strings[NOW_LABEL_KEY].orEmpty(),
                body = strings[NOW_BODY_KEY].orEmpty()
                    .replace("{sunday}", sunday.toString())
                    .replace("{weekday}", weekday),
                current = sunday,
            )
        },
    )
}

/** A · B · C with the year in force ringed in gold, and a line saying so. */
@Composable
private fun CurrentCycleCard(label: String, body: String, current: Char) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurfaceRaised,
        contentColor = ArticleCream,
        border = BorderStroke(1.5.dp, ArticleGold.copy(alpha = 0.6f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart))
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = label.uppercase(),
                    color = ArticleGoldSoft,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    listOf('A', 'B', 'C').forEach { letter ->
                        val active = letter == current
                        Box(
                            modifier = Modifier
                                .size(if (active) 56.dp else 44.dp)
                                .align(Alignment.CenterVertically)
                                .then(
                                    if (active) {
                                        Modifier
                                            .background(ArticleGold.copy(alpha = 0.16f), CircleShape)
                                            .border(1.5.dp, ArticleGold, CircleShape)
                                    } else {
                                        Modifier.border(1.dp, ArticleMuted.copy(alpha = 0.45f), CircleShape)
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = letter.toString(),
                                color = if (active) ArticleGold else ArticleMuted,
                                fontFamily = FontFamily.Serif,
                                fontSize = if (active) 26.sp else 18.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = body,
                    color = ArticleCream.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
