package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "Idols and Sacred Images" — Catechism → What We Believe, beside Types of Reverence.
 *
 * Two entries set against each other rather than a sequence, so unnumbered. The
 * `warning` callout carries the point: the honour shown to an image passes to the
 * person represented and never stops at the object.
 */
@Composable
fun SacredImagesScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.cat.images",
        repository = repository,
        language = language,
        onBack = onBack,
        numbered = false,
        listState = listState,
    )
}
