package com.ynotlabs.cathopedia.ui.screens.priesthood

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.ComparisonArticle

/**
 * "Religious and Diocesan Priests" — The Priesthood → the two kinds.
 *
 * The same side-by-side layout as Patriarch and Pope, with the religious priest on the left.
 */
@Composable
fun PriestKindsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    ComparisonArticle(
        articleId = "art.priesthood.two_kinds",
        repository = repository,
        language = language,
        onBack = onBack,
        listState = listState,
    )
}
