package com.ynotlabs.cathopedia.ui.screens.orders

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.ComparisonArticle

/**
 * "Nun or Sister?" — Religious Orders → the terms compared.
 *
 * The same side-by-side layout as Patriarch and Pope and the Priesthood's two kinds.
 */
@Composable
fun NunOrSisterScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    ComparisonArticle(
        articleId = "art.orders.nun_vs_sister",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
        unifiedFactCards = true,
    )
}
