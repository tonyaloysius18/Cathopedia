package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.ComparisonArticle

/**
 * "Basilica or Cathedral?" — The Holy Mass → the two kinds of great church compared.
 *
 * The same side-by-side layout as Cathedra and Presider's Chair, and Altar and Sacristy.
 */
@Composable
fun BasilicaOrCathedralScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    ComparisonArticle(
        articleId = "art.mass.basilica_cathedral",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
        unifiedFactCards = true,
    )
}
