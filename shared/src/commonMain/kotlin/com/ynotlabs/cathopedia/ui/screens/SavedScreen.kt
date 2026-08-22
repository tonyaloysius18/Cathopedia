package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.BookmarkItem
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow

private enum class SavedFilter { ALL, NOTES }

/** Stored on this device only — no account, no sync. */
@Composable
fun SavedScreen(
    repository: CathopediaRepository,
    onBack: () -> Unit,
    onItemSelected: (BookmarkItem) -> Unit,
) {
    val s = LocalStrings.current
    var bookmarks by remember { mutableStateOf<List<BookmarkItem>>(emptyList()) }
    var filter by remember { mutableStateOf(SavedFilter.ALL) }

    // Re-entering this branch of the nav `when` (e.g. switching back to the Saved
    // tab after bookmarking something on a detail page) disposes and recomposes
    // this composable, so this refetches on every visit without needing a key.
    LaunchedEffect(Unit) {
        bookmarks = repository.listBookmarks()
    }

    val visible = when (filter) {
        SavedFilter.ALL -> bookmarks
        SavedFilter.NOTES -> bookmarks.filter { it.note != null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.savedTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", fontSize = 22.sp) }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding)) {
            Text(
                s.savedStoredOnDeviceOnly,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            )
            FilterChipsRow(
                options = listOf(s.all to SavedFilter.ALL, s.savedFilterNotes to SavedFilter.NOTES),
                selected = filter,
                onSelect = { filter = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            if (visible.isEmpty()) {
                Text(
                    s.savedNothingSavedYet,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 116.dp)) {
                    items(visible, key = { it.type.tag + it.id }) { bookmark ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { onItemSelected(bookmark) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(bookmark.name)
                                Text(
                                    if (bookmark.note != null) s.savedOneNote else s.savedBookmarked,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
