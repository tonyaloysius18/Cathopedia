package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "Types of Reverence" — Catechism → What We Believe.
 *
 * Latria, hyperdulia, protodulia and dulia. Unnumbered: these are four kinds of
 * reverence, not steps in a sequence. The `warning` callout carries the point
 * the whole page exists for — veneration differs from worship in kind, not
 * merely in degree.
 */
@Composable
fun ReverenceScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.cat.reverence",
        repository = repository,
        language = language,
        onBack = onBack,
        numbered = false,
        listState = listState,
    )
}
