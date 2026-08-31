package com.ynotlabs.cathopedia.ui.screens.catechism

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubFactSheetDetail
import com.ynotlabs.cathopedia.model.HubSectionSummary
import com.ynotlabs.cathopedia.resources.*
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.components.GoldCardAccent
import com.ynotlabs.cathopedia.ui.components.SacredDivider
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val BeatitudesBackground = Color(0xFF061A13)
private val BeatitudesHeader = Color(0xFF081F17)
private val BeatitudesSurface = Color(0xFF0A241B)
private val BeatitudesSurfaceRaised = Color(0xFF0C271E)
private val BeatitudesGold = Color(0xFFD6AE3D)
private val BeatitudesCream = Color(0xFFF4ECDD)
private val BeatitudesMuted = Color(0xFFB7B09D)

@Composable
fun BeatitudesScreen(
    hubId: String,
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    var section by remember(language) { mutableStateOf<HubSectionSummary?>(null) }
    var sheet by remember(language) { mutableStateOf<HubFactSheetDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(hubId, language) {
        val loadedSection = repository.hubDetail(hubId)
            ?.sections
            ?.firstOrNull { it.id == "cat.beatitudes" }
            ?: return@LaunchedEffect
        val loadedSheet = loadedSection.factSheetId
            ?.let { repository.hubFactSheet(it) }
            ?: return@LaunchedEffect
        
        val keys = buildSet {
            add(loadedSection.titleKey)
            loadedSection.summaryKey?.let(::add)
            add(loadedSheet.titleKey)
            loadedSheet.facts.forEach { fact ->
                add(fact.labelKey)
                add(fact.valueKey)
            }
        }

        section = loadedSection
        sheet = loadedSheet
        strings = repository.resolveHubStrings(keys, language)
    }

    var headerHeightPx by remember(language) { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }
    val facts = sheet?.facts.orEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeatitudesBackground),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = headerHeight + 20.dp,
                end = 20.dp,
                bottom = 120.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                BeatitudesHeroCard()
            }

            section?.summaryKey?.let(strings::get)?.let { summary ->
                item {
                    BeatitudesIntroCard(summary)
                }
            }

            item {
                SacredDivider()
                Spacer(Modifier.height(4.dp))
            }

            if (facts.isNotEmpty()) {
                itemsIndexed(facts, key = { _, fact -> fact.id }) { index, fact ->
                    BeatitudeCard(
                        number = index + 1,
                        condition = strings[fact.labelKey].orEmpty(),
                        promise = strings[fact.valueKey].orEmpty(),
                    )
                }
            }
        }

        BeatitudesHeaderPanel(
            title = section?.let { strings[it.titleKey] }.orEmpty(),
            backDescription = s.back,
            onBack = onBack,
            modifier = Modifier.onGloballyPositioned {
                if (headerHeightPx != it.size.height) headerHeightPx = it.size.height
            },
        )
    }
}

@Composable
private fun BeatitudesHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F3024), Color(0xFF061A13))
                )
            )
            .border(1.dp, BeatitudesGold.copy(alpha = 0.35f), RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.symbol_rock),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
private fun BeatitudesIntroCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = BeatitudesSurface,
        contentColor = BeatitudesCream,
        border = BorderStroke(1.dp, BeatitudesGold.copy(alpha = 0.42f)),
    ) {
        Box {
            GoldCardAccent(modifier = Modifier.align(Alignment.CenterStart))
            Text(
                text = text,
                color = BeatitudesCream,
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun BeatitudeCard(
    number: Int,
    condition: String,
    promise: String,
) {
    val icon = beatitudeNumberIcon(number)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp),
        shape = RoundedCornerShape(20.dp),
        color = BeatitudesSurfaceRaised,
        contentColor = BeatitudesCream,
        border = BorderStroke(1.dp, BeatitudesGold.copy(alpha = 0.22f)),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .border(1.dp, BeatitudesGold, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = number.toString().padStart(2, '0'),
                        color = BeatitudesGold,
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = condition,
                    color = BeatitudesCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = promise,
                    color = BeatitudesMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

private fun beatitudeNumberIcon(number: Int): DrawableResource? = when (number) {
    1 -> Res.drawable._01
    2 -> Res.drawable._02
    3 -> Res.drawable._03
    4 -> Res.drawable._04
    5 -> Res.drawable._05
    6 -> Res.drawable._06
    7 -> Res.drawable._07
    8 -> Res.drawable._08
    else -> null
}

@Composable
private fun BeatitudesHeaderPanel(
    title: String,
    backDescription: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                clip = false,
            )
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(BeatitudesHeader)
            .statusBarsPadding()
            .padding(start = 18.dp, top = 6.dp, end = 18.dp, bottom = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            CathopediaBackButton(
                onClick = onBack,
                contentDescription = backDescription,
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = BeatitudesCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 29.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
