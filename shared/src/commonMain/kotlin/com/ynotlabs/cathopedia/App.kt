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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.stringsFor
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.model.BookmarkItem
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.model.RelatedItem
import com.ynotlabs.cathopedia.notifications.FeastNotificationScheduler
import com.ynotlabs.cathopedia.notifications.UpcomingFeastNotification
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
import com.ynotlabs.cathopedia.ui.screens.LanguageScreen
import com.ynotlabs.cathopedia.ui.screens.LanguageScreenStartup
import com.ynotlabs.cathopedia.ui.screens.NotificationsScreen
import com.ynotlabs.cathopedia.ui.screens.PrayerDetailScreen
import com.ynotlabs.cathopedia.ui.screens.PrayersHomeScreen
import com.ynotlabs.cathopedia.ui.screens.RosaryScreen
import com.ynotlabs.cathopedia.ui.screens.SavedScreen
import com.ynotlabs.cathopedia.ui.screens.SearchScreen
import com.ynotlabs.cathopedia.ui.screens.SettingsScreen
import com.ynotlabs.cathopedia.ui.screens.SplashScreen
import com.ynotlabs.cathopedia.ui.theme.CathopediaTheme
import com.ynotlabs.cathopedia.ui.theme.ThemeMode
import com.ynotlabs.cathopedia.ui.theme.toAccentColor
import kotlinx.coroutines.launch

private val TAB_DESTINATIONS = setOf(
    Destination.Home,
    Destination.Explore,
    Destination.PrayersHome,
    Destination.Search,
    Destination.Settings,
)

/** Root composable, shared verbatim between the Android and iOS entry points. */
@Composable
fun App(container: AppContainer, notificationScheduler: FeastNotificationScheduler) {
    val repository = container.repository
    var language by remember {
        mutableStateOf(repository.getPreference(PreferenceKeys.LANGUAGE) ?: "en")
    }
    val onboardingComplete = remember {
        repository.getPreference(PreferenceKeys.ONBOARDING_COMPLETE) == "true"
    }
    var themeMode by remember {
        mutableStateOf(ThemeMode.fromStorageKey(repository.getPreference(PreferenceKeys.THEME_MODE)))
    }
    var notificationsEnabled by remember {
        mutableStateOf(repository.getPreference(PreferenceKeys.NOTIFICATIONS_ENABLED) == "true")
    }
    var notificationsPermissionDenied by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun refreshFeastNotifications(onResult: ((Boolean) -> Unit)? = null) {
        coroutineScope.launch {
            val items = repository.upcomingFeasts(language).map { (date, summary) ->
                UpcomingFeastNotification(
                    feastId = summary.id,
                    date = date,
                    title = summary.name,
                    body = summary.summary,
                )
            }
            notificationScheduler.requestPermissionAndSchedule(items) { granted ->
                notificationsEnabled = granted
                notificationsPermissionDenied = !granted
                repository.setPreference(PreferenceKeys.NOTIFICATIONS_ENABLED, granted.toString())
                onResult?.invoke(granted)
            }
        }
    }

    // Re-schedules on every app open when the feature is already on, so the
    // rolling notification horizon keeps moving forward and picks up any feasts
    // added to the content catalog since the feature was last turned on.
    LaunchedEffect(Unit) {
        if (notificationsEnabled) refreshFeastNotifications()
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

    // Computed once per app session — good enough for a value that only ever
    // changes at most once a day, and avoids recomputing on every recomposition.
    val liturgicalAccent = remember { LiturgicalCalendar.liturgicalDayFor(LiturgicalCalendar.today()).color.toAccentColor() }

    CathopediaTheme(themeMode = themeMode, liturgicalAccent = liturgicalAccent) {
    CompositionLocalProvider(LocalStrings provides stringsFor(language)) {
        val destination = nav.current
        val showBottomBar = destination in TAB_DESTINATIONS

        Box(modifier = Modifier.fillMaxSize().nestedScroll(navBarScrollConnection)) {
            when (val current = destination) {
                is Destination.Splash -> SplashScreen(isFirstRun = !onboardingComplete, repository = repository) { firstRun ->
                    nav.resetTo(if (firstRun) Destination.LanguageStartup else Destination.Home)
                }

                is Destination.LanguageStartup -> LanguageScreenStartup(currentLanguage = language) { code ->
                    language = code
                    repository.setPreference(PreferenceKeys.LANGUAGE, code)
                    nav.navigate(Destination.Intro)
                }

                is Destination.LanguageSettings -> LanguageScreen(
                    currentLanguage = language,
                    onSelect = { code ->
                        language = code
                        repository.setPreference(PreferenceKeys.LANGUAGE, code)
                    },
                    onBack = nav::back,
                )

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

                is Destination.PrayersHome -> PrayersHomeScreen(
                    repository = repository,
                    language = language,
                    onOpenPrayer = { slug -> nav.navigate(Destination.PrayerDetail(slug)) },
                    onOpenRosary = { nav.navigate(Destination.RosaryScreen) },
                )

                is Destination.PrayerDetail -> PrayerDetailScreen(
                    slug = current.slug,
                    repository = repository,
                    language = language,
                    onBack = nav::back,
                )

                is Destination.RosaryScreen -> RosaryScreen(
                    repository = repository,
                    language = language,
                    onBack = nav::back,
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
                    notificationsEnabled = notificationsEnabled,
                    onOpenLanguage = { nav.navigate(Destination.LanguageSettings) },
                    onOpenAppearance = { nav.navigate(Destination.Appearance) },
                    onOpenNotifications = { nav.navigate(Destination.Notifications) },
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

                is Destination.Notifications -> NotificationsScreen(
                    enabled = notificationsEnabled,
                    permissionDenied = notificationsPermissionDenied,
                    onToggle = { enable ->
                        if (enable) {
                            refreshFeastNotifications()
                        } else {
                            notificationScheduler.cancelAll()
                            notificationsEnabled = false
                            notificationsPermissionDenied = false
                            repository.setPreference(PreferenceKeys.NOTIFICATIONS_ENABLED, "false")
                        }
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
}
