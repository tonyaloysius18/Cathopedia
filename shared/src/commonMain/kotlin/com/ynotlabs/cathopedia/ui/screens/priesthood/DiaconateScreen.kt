package com.ynotlabs.cathopedia.ui.screens.priesthood

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.ui.screens.common.IllustratedEntriesArticle

/**
 * "The Diaconate" — The Priesthood → the third section.
 *
 * Unnumbered: transitional and permanent are two kinds of deacon, not two steps.
 */
@Composable
fun DiaconateScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    IllustratedEntriesArticle(
        articleId = "art.priesthood.diaconate",
        repository = repository,
        language = language,
        onBack = onBack,
        numbered = false,
        listState = listState,
    )
}
