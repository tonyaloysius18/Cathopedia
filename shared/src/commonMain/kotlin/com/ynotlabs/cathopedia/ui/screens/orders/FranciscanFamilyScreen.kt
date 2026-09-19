package com.ynotlabs.cathopedia.ui.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.content.HUB_ENTITY_LINK_TAG
import com.ynotlabs.cathopedia.content.model.CalloutBlock
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.EntityType
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.content.model.QuoteBlock
import com.ynotlabs.cathopedia.content.parseHubMarkup
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubArticleDetail
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources._01
import com.ynotlabs.cathopedia.resources._02
import com.ynotlabs.cathopedia.resources._03
import com.ynotlabs.cathopedia.resources._04
import com.ynotlabs.cathopedia.resources._05
import com.ynotlabs.cathopedia.resources._06
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import com.ynotlabs.cathopedia.ui.hubAssetPainter
import com.ynotlabs.cathopedia.ui.screens.common.ArticleBackground
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCalloutCard
import com.ynotlabs.cathopedia.ui.screens.common.ArticleCream
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleGoldSoft
import com.ynotlabs.cathopedia.ui.screens.common.ArticleMuted
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSectionLabel
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurface
import com.ynotlabs.cathopedia.ui.screens.common.ArticleSurfaceRaised
import com.ynotlabs.cathopedia.ui.screens.common.blockKeys
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private const val FRANCISCAN_FAMILY_ARTICLE_ID = "art.franciscan.family"

private data class FranciscanBranch(
    val number: Int,
    val titleKey: String,
    val imageKey: String,
    val bodyKey: String,
    val asset: String,
)

private val firstOrderBranches = listOf(
    FranciscanBranch(
        number = 1,
        titleKey = "art.franciscan.family.ofm.name",
        imageKey = "art.franciscan.family.ofm.img",
        bodyKey = "art.franciscan.family.ofm.desc",
        asset = "hub/orders/franciscan_ofm.png",
    ),
    FranciscanBranch(
        number = 2,
        titleKey = "art.franciscan.family.conv.name",
        imageKey = "art.franciscan.family.conv.img",
        bodyKey = "art.franciscan.family.conv.desc",
        asset = "hub/orders/franciscan_conventual.png",
    ),
    FranciscanBranch(
        number = 3,
        titleKey = "art.franciscan.family.cap.name",
        imageKey = "art.franciscan.family.cap.img",
        bodyKey = "art.franciscan.family.cap.desc",
        asset = "hub/orders/franciscan_capuchin.png",
    ),
)

private val otherBranches = listOf(
    FranciscanBranch(
        number = 4,
        titleKey = "art.franciscan.family.clares.name",
        imageKey = "art.franciscan.family.clares.img",
        bodyKey = "art.franciscan.family.clares.desc",
        asset = "hub/orders/order_poor_clares_founder.png",
    ),
    FranciscanBranch(
        number = 5,
        titleKey = "art.franciscan.family.tor.name",
        imageKey = "art.franciscan.family.tor.img",
        bodyKey = "art.franciscan.family.tor.desc",
        asset = "hub/orders/franciscan_tor.png",
    ),
    FranciscanBranch(
        number = 6,
        titleKey = "art.franciscan.family.ofs.name",
        imageKey = "art.franciscan.family.ofs.img",
        bodyKey = "art.franciscan.family.ofs.desc",
        asset = "hub/orders/franciscan_secular.png",
    ),
)

@Composable
fun FranciscanFamilyScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
    onEntityRefSelected: (EntityRef) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    val s = LocalStrings.current
    var article by remember(language) { mutableStateOf<HubArticleDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubArticle(FRANCISCAN_FAMILY_ARTICLE_ID) ?: return@LaunchedEffect
        article = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.leadKey?.let(::add)
            loaded.blocks.forEach { addAll(blockKeys(it)) }
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    val current = article
    val intro = current?.blocks?.filterIsInstance<ParagraphBlock>()?.firstOrNull()
        ?.let { strings[it.textKey] }.orEmpty()
    val quote = current?.blocks?.filterIsInstance<QuoteBlock>()?.firstOrNull()
    val callouts = current?.blocks?.filterIsInstance<CalloutBlock>().orEmpty()

    ArticleScaffold(
        title = current?.let { strings[it.titleKey] }.orEmpty(),
        subtitle = current?.leadKey?.let(strings::get).orEmpty(),
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 16.dp,
    ) {
        if (intro.isNotBlank()) {
            item {
                FranciscanHeroCard(
                    intro = intro,
                    contentDescription = current?.let { strings[it.titleKey] }.orEmpty(),
                    onEntityRefSelected = onEntityRefSelected,
                )
                Spacer(Modifier.height(18.dp))
            }
        }

        quote?.let { block ->
            item {
                FranciscanQuote(
                    text = strings[block.textKey].orEmpty(),
                    attribution = block.attributionKey?.let(strings::get).orEmpty(),
                )
                Spacer(Modifier.height(18.dp))
            }
        }

        callouts.getOrNull(0)?.let { block ->
            item {
                ArticleCalloutCard(
                    title = block.titleKey?.let(strings::get),
                    body = strings[block.textKey].orEmpty(),
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        item {
            ArticleSectionLabel(strings["art.franciscan.family.h_first"].orEmpty())
            SectionIntroduction(
                text = strings["art.franciscan.family.p_first"].orEmpty(),
                onEntityRefSelected = onEntityRefSelected,
            )
            Spacer(Modifier.height(14.dp))
        }

        firstOrderBranches.forEach { branch ->
            item {
                FranciscanBranchCard(
                    branch = branch,
                    strings = strings,
                    onEntityRefSelected = onEntityRefSelected,
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            SacredDivider()
            Spacer(Modifier.height(24.dp))
            ArticleSectionLabel(strings["art.franciscan.family.h_others"].orEmpty())
        }

        otherBranches.forEach { branch ->
            item {
                FranciscanBranchCard(
                    branch = branch,
                    strings = strings,
                    onEntityRefSelected = onEntityRefSelected,
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        callouts.getOrNull(1)?.let { block ->
            item {
                Spacer(Modifier.height(4.dp))
                ArticleCalloutCard(
                    title = block.titleKey?.let(strings::get),
                    body = strings[block.textKey].orEmpty(),
                    emphasised = true,
                )
            }
        }
    }
}

@Composable
private fun FranciscanHeroCard(
    intro: String,
    contentDescription: String,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = ArticleSurface,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.45f)),
        shadowElevation = 4.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(224.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ArticleGold.copy(alpha = 0.14f),
                                ArticleSurface,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                hubAssetPainter("hub/symbols/symbol_franciscan_arms.png")?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = contentDescription,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, ArticleGold.copy(alpha = 0.7f), Color.Transparent),
                        ),
                    ),
            )

            Box {
                GoldCardAccent(Modifier.align(Alignment.CenterStart), height = 72.dp)
                FranciscanMarkupText(
                    raw = intro,
                    onEntityRefSelected = onEntityRefSelected,
                    modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 20.dp, bottom = 22.dp),
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                )
            }
        }
    }
}

@Composable
private fun FranciscanQuote(text: String, attribution: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "“",
            color = ArticleGold,
            fontFamily = FontFamily.Serif,
            fontSize = 42.sp,
            lineHeight = 34.sp,
        )
        Text(
            text = text,
            color = ArticleCream,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 18.sp,
            lineHeight = 27.sp,
            textAlign = TextAlign.Center,
        )
        if (attribution.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = attribution.uppercase(),
                color = ArticleMuted,
                fontSize = 9.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.1.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SectionIntroduction(
    text: String,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = ArticleSurface.copy(alpha = 0.64f),
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.20f)),
    ) {
        FranciscanMarkupText(
            raw = text,
            onEntityRefSelected = onEntityRefSelected,
            modifier = Modifier.padding(18.dp),
            color = ArticleCream.copy(alpha = 0.84f),
            fontSize = 14.sp,
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun FranciscanBranchCard(
    branch: FranciscanBranch,
    strings: Map<String, String>,
    onEntityRefSelected: (EntityRef) -> Unit,
) {
    val title = strings[branch.titleKey].orEmpty()
    val shape = RoundedCornerShape(24.dp)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = ArticleSurface,
        border = BorderStroke(1.dp, ArticleGold.copy(alpha = 0.38f)),
        shadowElevation = 3.dp,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(ArticleSurfaceRaised, ArticleBackground.copy(alpha = 0.80f)),
                        ),
                    ),
                contentAlignment = Alignment.BottomCenter,
            ) {
                hubAssetPainter(branch.asset)?.let { painter ->
                    Image(
                        painter = painter,
                        contentDescription = strings[branch.imageKey],
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, top = 12.dp, end = 10.dp),
                    )
                }

                Image(
                    painter = painterResource(franciscanNumberDrawable(branch.number)),
                    contentDescription = branch.number.toString(),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(48.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ArticleGold.copy(alpha = 0.55f)),
            )

            Box {
                GoldCardAccent(Modifier.align(Alignment.CenterStart), height = 64.dp)
                Column(
                    modifier = Modifier.padding(start = 24.dp, top = 18.dp, end = 20.dp, bottom = 22.dp),
                ) {
                    Text(
                        text = title,
                        color = ArticleCream,
                        fontFamily = FontFamily.Serif,
                        fontSize = 21.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    val caption = strings[branch.imageKey].orEmpty()
                    if (caption.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(ArticleGold),
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                text = caption,
                                color = ArticleGoldSoft,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    FranciscanMarkupText(
                        raw = strings[branch.bodyKey].orEmpty(),
                        onEntityRefSelected = onEntityRefSelected,
                        color = ArticleCream.copy(alpha = 0.88f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                    )
                }
            }
        }
    }
}

private fun franciscanNumberDrawable(number: Int): DrawableResource = when (number) {
    1 -> Res.drawable._01
    2 -> Res.drawable._02
    3 -> Res.drawable._03
    4 -> Res.drawable._04
    5 -> Res.drawable._05
    else -> Res.drawable._06
}

@Composable
private fun FranciscanMarkupText(
    raw: String,
    onEntityRefSelected: (EntityRef) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = ArticleCream,
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit,
) {
    val annotated = parseHubMarkup(raw)
    ClickableText(
        text = annotated,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = fontSize,
            lineHeight = lineHeight,
        ),
        onClick = { offset ->
            annotated.getStringAnnotations(HUB_ENTITY_LINK_TAG, offset, offset).firstOrNull()?.let { annotation ->
                val parts = annotation.item.split(":", limit = 2)
                val type = parts.firstOrNull().orEmpty()
                val id = parts.getOrNull(1).orEmpty()
                if (id.isNotEmpty()) {
                    runCatching { EntityType.valueOf(type) }.getOrNull()?.let { entityType ->
                        onEntityRefSelected(EntityRef(type = entityType, id = id))
                    }
                }
            }
        },
    )
}
