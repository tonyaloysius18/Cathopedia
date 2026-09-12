package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "The Three Pillars of the Faith" — Catechism → What We Believe, the opening article.
 *
 * Scripture, Tradition and the Magisterium: a set rather than a sequence, so unnumbered.
 * The `warning` callout carries the correction the page exists to make — Scripture and
 * Tradition are one deposit, and the Magisterium serves that Word rather than standing
 * over it as a third source.
 */
@Composable
fun ThreePillarsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.cat.three_pillars",
        repository = repository,
        language = language,
        onBack = onBack,
        numbered = false,
        listState = listState,
    )
}
