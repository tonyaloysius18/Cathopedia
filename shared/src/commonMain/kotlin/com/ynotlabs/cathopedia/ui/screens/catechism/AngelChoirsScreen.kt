package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "The Nine Choirs of Angels" — Catechism → What We Believe.
 *
 * Nine choirs in three hierarchies, separated by heading blocks. The closing
 * `warning` callout is the important one and the shared layout sets it apart:
 * the ninefold ranking is a theological tradition, not defined dogma, and the
 * page must not leave a reader thinking otherwise.
 */
@Composable
fun AngelChoirsScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.cat.angels",
        repository = repository,
        language = language,
        onBack = onBack,
        wrapEntryTextBelowImage = true,
        showEntryImageBackground = false,
        listState = listState,
    )
}
