package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "Books Used at Mass" — Holy Mass → the section after The Cycle of Readings.
 *
 * Lectionary, Book of the Gospels, Roman Missal. Numbered, as the source infographic
 * numbers them and the order runs ambo → altar.
 */
@Composable
fun MassBooksScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.mass.books",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
    )
}
