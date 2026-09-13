package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.DocumentKinds
import com.ynotlabs.cathopedia.model.DocumentPopeGroup
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold

import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface

/**
 * Every papal document of one kind, grouped under the pope who promulgated it.
 *
 * Popes run most recent first, and each pope's documents newest first — the order
 * a reader looking for current teaching expects, and the one that puts the four
 * modern popes above Leo XIII's eighty-six. Grouping happens in the repository,
 * which sorts by each pope's latest document rather than by name, since regnal
 * numerals do not sort chronologically as text.
 */
@Composable
fun DocumentIndexScreen(
    kind: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onDocumentSelected: (ContentSummary) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var groups by remember(kind, language) { mutableStateOf<List<DocumentPopeGroup>>(emptyList()) }

    LaunchedEffect(kind, language) {
        groups = repository.documentsOfKindByPope(kind, language)
    }

    val total = groups.sumOf { it.documents.size }

    ArticleScaffold(
        title = when (kind) {
            DocumentKinds.ENCYCLICAL -> s.encyclicalsIndexTitle
            else -> s.typeDocumentsPlural
        },
        subtitle = if (total > 0) {
            s.countDocuments.replace("{count}", total.toString())
        } else {
            ""
        },
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 20.dp,
    ) {
        groups.forEach { group ->
            item(key = "pope-${group.popeId ?: "unknown"}") {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.popeName.uppercase(),
                        color = ArticleGold,
                        fontSize = 13.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(ArticleGold.copy(alpha = 0.35f)),
                    )
                }
            }

            group.documents.forEach { doc ->
                item(key = doc.id) {
                    DocumentRow(doc = doc, onClick = { onDocumentSelected(doc) })
                    Spacer(Modifier.height(8.dp))
                }
            }

            item(key = "gap-${group.popeId ?: "unknown"}") {
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun DocumentRow(doc: ContentSummary, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.3f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart), height = 36.dp)
            Row(
                modifier = Modifier.padding(start = 20.dp, top = 14.dp, end = 16.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doc.name,
                        color = ArticleCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = doc.summary,
                        color = ArticleMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = ArticleGold.copy(alpha = 0.75f),
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}
