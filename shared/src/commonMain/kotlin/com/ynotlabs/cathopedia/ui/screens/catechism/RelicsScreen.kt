package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "Types of Relics" — Catechism → What We Believe, just after Types of Reverence.
 *
 * First, second and third class. Numbered, since the classes are ranked. The
 * `warning` callout carries the point the page must not lose — a relic has no
 * power of its own, and venerating it is dulia, never worship.
 */
@Composable
fun RelicsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.cat.relics",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
    )
}
