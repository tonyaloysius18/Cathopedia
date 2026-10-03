package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.ComparisonArticle

/**
 * "Altar and Sacristy" — The Holy Mass → the two places compared.
 *
 * The same side-by-side layout as Patriarch and Pope, the Priesthood's two kinds, and
 * Nun or Sister — here the two "sides" are places rather than people.
 */
@Composable
fun AltarOrSacristyScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    ComparisonArticle(
        articleId = "art.mass.altar_sacristy",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
        unifiedFactCards = true,
    )
}
