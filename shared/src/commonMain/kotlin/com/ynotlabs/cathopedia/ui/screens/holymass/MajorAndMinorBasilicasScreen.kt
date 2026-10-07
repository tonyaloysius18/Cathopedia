package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.ComparisonArticle

/**
 * "Major and Minor Basilicas" — The Holy Mass → Basilica or Cathedral, second article.
 *
 * The same side-by-side layout as "Basilica or Cathedral?", reusing its basilica art for the
 * major side.
 */
@Composable
fun MajorAndMinorBasilicasScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    ComparisonArticle(
        articleId = "art.mass.basilica_ranks",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
        unifiedFactCards = true,
    )
}
