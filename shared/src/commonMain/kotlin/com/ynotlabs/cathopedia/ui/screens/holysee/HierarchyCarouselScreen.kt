package com.ynotlabs.cathopedia.ui.screens.holysee

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.model.HubStepperDetail
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton

@Composable
fun HierarchyCarouselScreen(
    repository: CathopediaRepository,
    language: String,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    var stepper by remember { mutableStateOf<HubStepperDetail?>(null) }
    var strings by remember(language) { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(language) {
        val loaded = repository.hubStepper("step.hierarchy") ?: return@LaunchedEffect
        stepper = loaded
        val keys = buildSet {
            add(loaded.titleKey)
            loaded.introKey?.let(::add)
            loaded.steps.forEach { step ->
                add(step.titleKey)
                add(step.bodyKey)
            }
            add("hierarchy.carousel.label")
        }
        strings = repository.resolveHubStrings(keys, language)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(HolySeeBackground),
        contentPadding = PaddingValues(bottom = 48.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 8.dp, top = 8.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CathopediaBackButton(onClick = onBack, contentDescription = s.back)
                Text(
                    text = strings[stepper?.titleKey].orEmpty(),
                    color = HolySeeCream,
                    fontFamily = FontFamily.Serif,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        val current = stepper
        if (current == null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = HolySeeGold)
                }
            }
        } else {
            item {
                current.introKey?.let { introKey ->
                    Text(
                        text = strings[introKey].orEmpty(),
                        color = HolySeeMuted,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    )
                }

                HolySeePortraitCarousel(
                    label = strings["hierarchy.carousel.label"].orEmpty(),
                    items = current.steps.sortedBy { it.order }.map { step ->
                        HolySeeCarouselItem(
                            id = step.id,
                            title = strings[step.titleKey].orEmpty(),
                            body = strings[step.bodyKey].orEmpty(),
                            imageAsset = step.asset.orEmpty(),
                        )
                    },
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
