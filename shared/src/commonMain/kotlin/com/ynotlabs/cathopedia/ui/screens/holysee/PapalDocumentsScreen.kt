package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.model.FactGridBlock
import com.ynotlabs.cathopedia.content.model.ImageBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.DocumentKinds
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleIntroCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

/**
 * The label the content stores each document form under, mapped to the [DocumentKinds]
 * tag used by `content/documents`. A row whose kind we carry documents for becomes a
 * link into that kind's index; the rest stay plain description rows.
 */
private val KIND_BY_LABEL_KEY = mapOf(
    "art.papal_documents.grid.encyclical.label" to DocumentKinds.ENCYCLICAL,
    "art.papal_documents.grid.constitution.label" to DocumentKinds.APOSTOLIC_CONSTITUTION,
    "art.papal_documents.grid.exhortation.label" to DocumentKinds.APOSTOLIC_EXHORTATION,
    "art.papal_documents.grid.motu_proprio.label" to DocumentKinds.MOTU_PROPRIO,
)

/**
 * "Kinds of Papal Documents" — Holy See → Papal Documents.
 *
 * Renders the article's own blocks, but turns each document-kind row into a
 * tappable card when the app actually carries documents of that kind. Counting
 * first means a kind with nothing behind it stays a plain row rather than opening
 * an empty list.
 */
@Composable
fun PapalDocumentsScreen(
    articleId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onKindSelected: (String) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(articleId, language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(articleId, language) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var counts by remember(language) { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(articleId, language) {
        val loaded = repository.hubArticle(articleId) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { block ->
                when (block) {
                    is ParagraphBlock -> add(block.textKey)
                    is FactGridBlock -> block.facts.forEach {
                        add(it.labelKey)
                        add(it.valueKey)
                    }
                    is ImageBlock -> block.captionKey?.let(::add)
                    else -> Unit
                }
            }
        }
        strings = repository.resolveHubStrings(keys, language)
        counts = KIND_BY_LABEL_KEY.values.distinct().associateWith {
            repository.countDocumentsOfKind(it, language)
        }
    }

    val current = article
    val blocks = current?.blocks.orEmpty()

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 20.dp,
    ) {
        current?.heroAsset?.let { asset ->
            item(key = "hero") {
                hubAssetPainter(asset)?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(18.dp)),
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        blocks.forEachIndexed { index, block ->
            when (block) {
                is ParagraphBlock -> item(key = "p-$index") {
                    ArticleIntroCard(strings[block.textKey].orEmpty())
                    Spacer(Modifier.height(14.dp))
                }

                is FactGridBlock -> block.facts.forEachIndexed { factIndex, fact ->
                    item(key = "fact-$index-$factIndex") {
                        val kind = KIND_BY_LABEL_KEY[fact.labelKey]
                        val count = kind?.let { counts[it] } ?: 0
                        DocumentKindRow(
                            label = strings[fact.labelKey].orEmpty(),
                            value = strings[fact.valueKey].orEmpty(),
                            count = if (count > 0) count else null,
                            onClick = if (kind != null && count > 0) {
                                { onKindSelected(kind) }
                            } else {
                                null
                            },
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun DocumentKindRow(
    label: String,
    value: String,
    count: Int?,
    onClick: (() -> Unit)?,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(20.dp),
        color = ArticleSurface,
        contentColor = ArticleCream,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = if (onClick != null) 0.55f else 0.34f)),
    ) {
        Box {
            GoldCardAccent(Modifier.align(Alignment.CenterStart), height = 42.dp)
            Row(
                modifier = Modifier.padding(start = 22.dp, top = 16.dp, end = 18.dp, bottom = 17.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = label.uppercase(),
                            color = ArticleGold,
                            fontSize = 10.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                        )
                        if (count != null) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "· $count",
                                color = ArticleMuted,
                                fontSize = 10.sp,
                                letterSpacing = 0.6.sp,
                            )
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = value,
                        color = ArticleCream,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )
                }
                if (onClick != null) {
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = ArticleGold,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}
