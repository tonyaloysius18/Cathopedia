package com.ynotlabs.cathopedia

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.data.PreferenceKeys
import com.ynotlabs.cathopedia.di.AppContainer
import com.ynotlabs.cathopedia.model.BookmarkItem
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.model.RelatedItem
import com.ynotlabs.cathopedia.ui.navigation.BottomNavBar
import com.ynotlabs.cathopedia.ui.navigation.Destination
import com.ynotlabs.cathopedia.ui.navigation.rememberAppNavController
import com.ynotlabs.cathopedia.ui.screens.AboutScreen
import com.ynotlabs.cathopedia.ui.screens.AppearanceScreen
import com.ynotlabs.cathopedia.ui.screens.EntityDetailScreen
import com.ynotlabs.cathopedia.ui.screens.EntityListScreen
import com.ynotlabs.cathopedia.ui.screens.ExploreScreen
import com.ynotlabs.cathopedia.ui.screens.HomeScreen
import com.ynotlabs.cathopedia.ui.screens.IntroScreen
import com.ynotlabs.cathopedia.ui.screens.LanguageSelectScreen
import com.ynotlabs.cathopedia.ui.screens.SavedScreen
import com.ynotlabs.cathopedia.ui.screens.SearchScreen
import com.ynotlabs.cathopedia.ui.screens.SettingsScreen
import com.ynotlabs.cathopedia.ui.screens.SplashScreen
import com.ynotlabs.cathopedia.ui.theme.CathopediaTheme
import com.ynotlabs.cathopedia.ui.theme.LiturgicalOrdinary
import com.ynotlabs.cathopedia.ui.theme.ThemeMode

private val TAB_DESTINATIONS = setOf(Destination.Home, Destination.Explore, Destination.Search, Destination.Settings)

/** Root composable, shared verbatim between the Android and iOS entry points. */
@Composable
fun App(container: AppContainer) {
    val repository = container.repository
    var language by remember {
        mutableStateOf(repository.getPreference(PreferenceKeys.LANGUAGE) ?: "en")
    }
    val onboardingComplete = remember {
        repository.getPreference(PreferenceKeys.ONBOARDING_COMPLETE) == "true"
    }
    var languageEditFromSettings by remember { mutableStateOf(false) }
    var themeMode by remember {
        mutableStateOf(ThemeMode.fromStorageKey(repository.getPreference(PreferenceKeys.THEME_MODE)))
    }

    val nav = rememberAppNavController(Destination.Splash)

    // EntityListScreen is torn down and recreated by the `when` router below every
    // time you navigate away and back (e.g. into a detail screen), so its own
    // rememberLazyListState() can't survive that — hoisted here, one per content
    // type, so scroll position (the Popes list in particular) is preserved.
    val entityListScrollStates = remember { mutableStateMapOf<ContentType, LazyListState>() }

    // Scroll-to-shrink for the bottom pill bar, ported from Itinera's App.kt: the
    // bar shrinks while a screen scrolls down and springs back on scroll up.
    var barScale by remember { mutableStateOf(1f) }
    val animatedBarScale by animateFloatAsState(
        targetValue = barScale,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "barScale",
    )
    val navBarScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -5) {
                    barScale = 0.85f
                } else if (available.y > 5) {
                    barScale = 1f
                }
                return Offset.Zero
            }
        }
    }
    LaunchedEffect(nav.current) {
        barScale = 1f
    }

    // Liturgical accent is a single theme token from day one (per the colour
    // system doc) — hardcoded to Ordinary Time's green until the calendar
    // engine (v1.0) can drive it.
    CathopediaTheme(themeMode = themeMode, liturgicalAccent = LiturgicalOrdinary) {
        val destination = nav.current
        val showBottomBar = destination in TAB_DESTINATIONS ||
            destination is Destination.EntityList

        Box(modifier = Modifier.fillMaxSize().nestedScroll(navBarScrollConnection)) {
            when (val current = destination) {
                is Destination.Splash -> SplashScreen(isFirstRun = !onboardingComplete, repository = repository) { firstRun ->
                    nav.resetTo(if (firstRun) Destination.LanguageSelect else Destination.Home)
                }

                is Destination.LanguageSelect -> LanguageSelectScreen(currentLanguage = language) { code ->
                    language = code
                    repository.setPreference(PreferenceKeys.LANGUAGE, code)
                    if (languageEditFromSettings) {
                        languageEditFromSettings = false
                        nav.back()
                    } else {
                        nav.navigate(Destination.Intro)
                    }
                }

                is Destination.Intro -> IntroScreen {
                    repository.setPreference(PreferenceKeys.ONBOARDING_COMPLETE, "true")
                    nav.resetTo(Destination.Home)
                }

                is Destination.Home -> HomeScreen(
                    repository = repository,
                    language = language,
                    onItemSelected = { item: ContentSummary -> nav.navigate(Destination.EntityDetail(item.type, item.id)) },
                )

                is Destination.Explore -> ExploreScreen(
                    repository = repository,
                    language = language,
                    onCategorySelected = { type -> nav.navigate(Destination.EntityList(type)) },
                )

                is Destination.EntityList -> EntityListScreen(
                    type = current.type,
                    repository = repository,
                    language = language,
                    onBack = nav::back,
                    onItemSelected = { item: ContentSummary -> nav.navigate(Destination.EntityDetail(item.type, item.id)) },
                    listState = entityListScrollStates.getOrPut(current.type) { LazyListState() },
                )

                is Destination.EntityDetail -> EntityDetailScreen(
                    type = current.type,
                    id = current.id,
                    repository = repository,
                    language = language,
                    onBack = nav::back,
                    onRelatedSelected = { related: RelatedItem -> nav.navigate(Destination.EntityDetail(related.type, related.id)) },
                )

                is Destination.Search -> SearchScreen(
                    repository = repository,
                    language = language,
                    onResultSelected = { result: ContentSummary -> nav.navigate(Destination.EntityDetail(result.type, result.id)) },
                )

                is Destination.Saved -> SavedScreen(
                    repository = repository,
                    onBack = nav::back,
                    onItemSelected = { bookmark: BookmarkItem -> nav.navigate(Destination.EntityDetail(bookmark.type, bookmark.id)) },
                )

                is Destination.Settings -> SettingsScreen(
                    language = language,
                    themeMode = themeMode,
                    onOpenLanguage = {
                        languageEditFromSettings = true
                        nav.navigate(Destination.LanguageSelect)
                    },
                    onOpenAppearance = { nav.navigate(Destination.Appearance) },
                    onOpenSaved = { nav.navigate(Destination.Saved) },
                    onOpenAbout = { nav.navigate(Destination.About) },
                )

                is Destination.Appearance -> AppearanceScreen(
                    selected = themeMode,
                    onSelect = { mode ->
                        themeMode = mode
                        repository.setPreference(PreferenceKeys.THEME_MODE, mode.storageKey)
                    },
                    onBack = nav::back,
                )

                is Destination.About -> AboutScreen(onBack = nav::back)
            }

            // Floating pill nav bar, ported from Itinera's SlidingPillBar — an
            // overlay above the content rather than a docked Scaffold bottomBar,
            // so screens scroll underneath it.
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 16.dp)
                        .graphicsLayer {
                            scaleX = animatedBarScale
                            scaleY = animatedBarScale
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    BottomNavBar(selected = nav.selectedTab, onSelect = nav::selectTab)
                }
            }
        }
    }
}
