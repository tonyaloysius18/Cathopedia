package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.ComparisonArticle

/**
 * "Cathedra or Presider's Chair?" — The Holy Mass → the two chairs compared.
 *
 * The same side-by-side layout as Altar and Sacristy, Patriarch and Pope, the Priesthood's
 * two kinds, and Nun or Sister.
 */
@Composable
fun CathedraOrPresidersChairScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    ComparisonArticle(
        articleId = "art.mass.cathedra_chair",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
        unifiedFactCards = true,
    )
}
