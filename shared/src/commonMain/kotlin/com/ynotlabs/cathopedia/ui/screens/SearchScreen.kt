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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.ynotlabs.cathopedia.model.ContentCategory
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.ui.components.FilterChipsRow

/** One FTS5 query across every entity type; results grouped by category with live counts. */
@Composable
fun SearchScreen(
    repository: CathopediaRepository,
    language: String,
    onResultSelected: (ContentSummary) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var allResults by remember { mutableStateOf<List<ContentSummary>>(emptyList()) }
    var filter by remember { mutableStateOf<ContentCategory?>(null) }

    LaunchedEffect(query) {
        allResults = if (query.isBlank()) emptyList() else repository.search(query, language)
        filter = null
    }

    val counts = ContentCategory.entries.associateWith { category ->
        allResults.count { it.type in category.types }
    }
    val visible = if (filter == null) allResults else allResults.filter { it.type in filter!!.types }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search Cathopedia") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding)) {
            if (allResults.isNotEmpty()) {
                val chipOptions = buildList {
                    add("All ${allResults.size}" to null)
                    ContentCategory.entries.forEach { category ->
                        val count = counts[category] ?: 0
                        if (count > 0) add("${category.label} $count" to category)
                    }
                }
                FilterChipsRow(
                    options = chipOptions,
                    selected = filter,
                    onSelect = { filter = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 116.dp)) {
                items(visible, key = { it.type.tag + it.id }) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { onResultSelected(result) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(result.name)
                            Text(result.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
