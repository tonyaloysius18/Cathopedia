package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.HUB_ENTITY_LINK_TAG
import com.ynotlabs.cathopedia.content.model.Block
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.DiagramRefBlock
import com.ynotlabs.cathopedia.content.model.EntityCardsBlock
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.ExternalLinkBlock
import com.ynotlabs.cathopedia.content.model.FactGridBlock
import com.ynotlabs.cathopedia.content.model.HeadingBlock
import com.ynotlabs.cathopedia.content.model.ImageBlock
import com.ynotlabs.cathopedia.content.model.ListBlock
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.content.model.QuoteBlock
import com.ynotlabs.cathopedia.content.model.StepperRefBlock
import com.ynotlabs.cathopedia.content.model.TimelineRefBlock
import com.ynotlabs.cathopedia.content.parseHubMarkup
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail

/**
 * Renders every [Block] type (docs/briefs/topic-hubs.md, T5). `DiagramRefBlock`/`StepperRefBlock`/
 * `TimelineRefBlock` are cross-references into other hub structures — with no in-article renderer
 * of their own by design (the referenced diagram/stepper/timeline is the section's own body, this
 * is just a pointer to it) they, along with anything this app version doesn't recognize, are
 * skipped silently rather than rendered as an error or a gap.
 */
@Composable
fun HubArticleScreen(
    articleId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    val s = LocalStrings.current
    var article by remember(articleId) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(articleId) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(articleId, language) {
        val loaded = repository.hubArticle(articleId) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { addAll(blockKeys(it)) }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = s.back,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        val current = article ?: return@LazyColumn

        item {
            Text(
                text = strings[current.titleKey].orEmpty(),
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Medium,
            )
            current.leadKey?.let { key ->
                strings[key]?.let { lead ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = lead,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        current.blocks.forEach { block ->
            item {
                BlockView(block, strings, onEntityRefSelected)
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

private fun blockKeys(block: Block): Set<String> = when (block) {
    is HeadingBlock -> setOf(block.textKey)
    is ParagraphBlock -> setOf(block.textKey)
    is ListBlock -> block.itemKeys.toSet()
    is QuoteBlock -> setOfNotNull(block.textKey, block.attributionKey)
    is CalloutBlock -> setOfNotNull(block.titleKey, block.textKey)
    is ImageBlock -> setOfNotNull(block.captionKey)
    is FactGridBlock -> block.facts.flatMap { listOf(it.labelKey, it.valueKey) }.toSet()
    is EntityCardsBlock -> setOfNotNull(block.titleKey) + block.refs.mapNotNull { it.labelKey }
    is ExternalLinkBlock -> setOf(block.labelKey)
    is DiagramRefBlock, is StepperRefBlock, is TimelineRefBlock -> emptySet()
    else -> emptySet()
}

@Composable
private fun BlockView(block: Block, strings: Map<String, String>, onEntityRefSelected: (EntityRef) -> Unit) {
    when (block) {
        is HeadingBlock -> Text(
            text = strings[block.textKey].orEmpty(),
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = FontFamily.Serif,
            fontSize = if (block.level <= 2) 20.sp else 17.sp,
            fontWeight = FontWeight.SemiBold,
        )

        is ParagraphBlock -> MarkupText(strings[block.textKey].orEmpty(), onEntityRefSelected)

        is ListBlock -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            block.itemKeys.forEach { key ->
                Row {
                    Text(
                        text = if (block.style.name == "numbered") "•" else "—",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    MarkupText(strings[key].orEmpty(), onEntityRefSelected)
                }
            }
        }

        is QuoteBlock -> Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(Modifier.padding(14.dp)) {
                MarkupText(strings[block.textKey].orEmpty(), onEntityRefSelected)
                block.attributionKey?.let { key ->
                    strings[key]?.let { attribution ->
                        Spacer(Modifier.height(6.dp))
                        Text(text = "— $attribution", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        }

        is CalloutBlock -> Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Column(Modifier.padding(14.dp)) {
                block.titleKey?.let { key ->
                    strings[key]?.let { title ->
                        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                    }
                }
                MarkupText(strings[block.textKey].orEmpty(), onEntityRefSelected)
            }
        }

        is ImageBlock -> block.captionKey?.let { key ->
            strings[key]?.let { caption ->
                Text(text = caption, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        } ?: Unit

        is FactGridBlock -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            block.facts.forEach { fact ->
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = strings[fact.labelKey].orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Text(text = strings[fact.valueKey].orEmpty(), color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
                }
            }
        }

        is EntityCardsBlock -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            block.titleKey?.let { key -> strings[key]?.let { Text(it, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) } }
            block.refs.forEach { ref ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onEntityRefSelected(ref) },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Text(
                        text = ref.labelKey?.let { strings[it] } ?: ref.id,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 13.sp,
                    )
                }
            }
        }

        is ExternalLinkBlock -> Text(
            text = strings[block.labelKey].orEmpty(),
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            fontSize = 14.sp,
        )

        // DiagramRefBlock / StepperRefBlock / TimelineRefBlock point at structures the section
        // itself already renders — nothing to draw here. Any future block type this app version
        // doesn't know about falls into the same branch: skipped silently, never a crash.
        else -> Unit
    }
}

@Composable
private fun MarkupText(raw: String, onEntityRefSelected: (EntityRef) -> Unit) {
    val annotated = parseHubMarkup(raw)
    ClickableText(
        text = annotated,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        ),
        onClick = { offset ->
            annotated.getStringAnnotations(HUB_ENTITY_LINK_TAG, offset, offset).firstOrNull()?.let { annotation ->
                val (type, id) = annotation.item.split(":", limit = 2).let { it[0] to it.getOrElse(1) { "" } }
                if (id.isNotEmpty()) {
                    runCatching { com.ynotlabs.cathopedia.content.model.EntityType.valueOf(type) }
                        .getOrNull()
                        ?.let { onEntityRefSelected(EntityRef(type = it, id = id)) }
                }
            }
        },
    )
}
