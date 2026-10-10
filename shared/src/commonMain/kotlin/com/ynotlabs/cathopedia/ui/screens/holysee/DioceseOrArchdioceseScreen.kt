package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.ComparisonArticle

/**
 * "Diocese and Archdiocese" — Holy See → the two kinds of local church compared.
 *
 * The same side-by-side layout as Patriarch and Pope.
 */
@Composable
fun DioceseOrArchdioceseScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    ComparisonArticle(
        articleId = "art.holy_see.diocese_vs_archdiocese",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
    )
}
