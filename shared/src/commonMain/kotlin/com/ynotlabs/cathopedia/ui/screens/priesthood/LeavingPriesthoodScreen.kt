package com.ynotlabs.cathopedia.ui.screens.priesthood

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "Can a Priest Leave the Priesthood?" — The Priesthood → the third section.
 *
 * Unnumbered: the three canonical routes are a set, not a sequence, and nothing here should read
 * as a series of steps to follow. The pastoral callouts come before them on purpose — a priest in
 * a bad season meets the offer of help first, and the canon law second.
 */
@Composable
fun LeavingPriesthoodScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.priesthood.leaving",
        repository = repository,
        language = language,
        onBack = onBack,
        numbered = false,
        listState = listState,
    )
}
