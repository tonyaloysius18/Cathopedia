package com.ynotlabs.cathopedia

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
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
import com.ynotlabs.cathopedia.model.PrayerCategory
import com.ynotlabs.cathopedia.model.PrayerSummary
import com.ynotlabs.cathopedia.model.RelatedItem
import com.ynotlabs.cathopedia.notifications.FeastNotificationScheduler
import com.ynotlabs.cathopedia.notifications.UpcomingFeastNotification
import com.ynotlabs.cathopedia.content.model.EntityRef
import com.ynotlabs.cathopedia.content.model.EntityType
import com.ynotlabs.cathopedia.ui.navigation.AppNavController
import com.ynotlabs.cathopedia.ui.navigation.BottomNavBar
import com.ynotlabs.cathopedia.ui.navigation.Destination
import com.ynotlabs.cathopedia.ui.navigation.FloatingSearchButton
import com.ynotlabs.cathopedia.ui.navigation.rememberAppNavController
import com.ynotlabs.cathopedia.ui.screens.settings.AboutScreen
import com.ynotlabs.cathopedia.ui.screens.settings.AppearanceScreen
import com.ynotlabs.cathopedia.ui.screens.common.EntityDetailScreen
import com.ynotlabs.cathopedia.ui.screens.common.EntityListScreen
import com.ynotlabs.cathopedia.ui.screens.home.ExploreScreen
import com.ynotlabs.cathopedia.ui.screens.settings.FeedbackScreen
import com.ynotlabs.cathopedia.ui.screens.home.HomeScreen
import com.ynotlabs.cathopedia.ui.screens.common.HubArticleScreen
import com.ynotlabs.cathopedia.ui.screens.common.HubScreen
import com.ynotlabs.cathopedia.ui.screens.common.HubSectionScreen
import com.ynotlabs.cathopedia.ui.screens.startup.IntroScreen
import com.ynotlabs.cathopedia.ui.screens.settings.LanguageScreen
import com.ynotlabs.cathopedia.ui.screens.startup.LanguageScreenStartup
import com.ynotlabs.cathopedia.ui.screens.settings.NotificationsScreen
import com.ynotlabs.cathopedia.ui.screens.prayers.PrayerDetailScreen
import com.ynotlabs.cathopedia.ui.screens.prayers.PrayerQuickSection
import com.ynotlabs.cathopedia.ui.screens.prayers.PrayersHomeScreen
import com.ynotlabs.cathopedia.ui.screens.rosary.RosaryScreen
import com.ynotlabs.cathopedia.ui.screens.home.SavedScreen
import com.ynotlabs.cathopedia.ui.screens.home.SearchScreen
import com.ynotlabs.cathopedia.ui.screens.settings.SettingsScreen
import com.ynotlabs.cathopedia.ui.screens.startup.SplashScreen
import com.ynotlabs.cathopedia.ui.screens.holymass.ProcessionScreen
import com.ynotlabs.cathopedia.ui.screens.sacraments.SacramentsScreen
import com.ynotlabs.cathopedia.ui.screens.stations.StationsScreen
import com.ynotlabs.cathopedia.ui.screens.vestments.VestmentsScreen
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

    // Tab-level scroll states, hoisted so they persist across tab switches.
    val homeScrollState = rememberScrollState()
    val exploreScrollState = rememberLazyListState()
    val prayersHomeScrollState = rememberLazyListState()
    val prayersQuickSectionScrollState = rememberLazyListState()
    val searchLandingScrollState = rememberLazyListState()
    val searchResultsScrollState = rememberLazyListState()
    val savedScrollState = rememberLazyListState()
    val vestmentsScrollState = rememberLazyListState()
    val sacramentsScrollState = rememberLazyListState()
    val processionScrollState = rememberLazyListState()
    val stationsScrollState = rememberLazyListState()

    // Hub-level scroll states and data caches, indexed by content IDs.
    val hubScrollStates = remember { mutableStateMapOf<String, LazyListState>() }
    val hubDetails = remember { mutableStateMapOf<String, com.ynotlabs.cathopedia.model.HubDetail>() }
    val hubStringsCache = remember { mutableStateMapOf<String, Map<String, String>>() }
    val hubHeaderHeights = remember { mutableStateMapOf<String, Int>() }

    val hubSectionScrollStates = remember { mutableStateMapOf<String, LazyListState>() }
    val hubSectionArticles = remember { mutableStateMapOf<String, List<com.ynotlabs.cathopedia.model.HubArticleSummary>>() }
    val hubSectionStringsCache = remember { mutableStateMapOf<String, Map<String, String>>() }
    val hubSectionHeaderHeights = remember { mutableStateMapOf<String, Int>() }

    val hubArticleScrollStates = remember { mutableStateMapOf<String, LazyListState>() }
    val hubArticleDetails = remember { mutableStateMapOf<String, com.ynotlabs.cathopedia.model.HubArticleDetail>() }
    val hubArticleStringsCache = remember { mutableStateMapOf<String, Map<String, String>>() }
    val hubArticleHeaderHeights = remember { mutableStateMapOf<String, Int>() }

    // EntityListScreen is torn down and recreated by the `when` router below.
    // Retain both its position and the data that determines what that position
    // means, otherwise a filtered Popes list briefly rebuilds as the full list.
    val entityListScrollStates = remember { mutableStateMapOf<Pair<ContentType, String>, LazyListState>() }
    val entityListItemCaches = remember { mutableStateMapOf<Pair<ContentType, String>, List<ContentSummary>>() }
    val entityListSelectedCenturies = remember { mutableStateMapOf<Pair<ContentType, String>, String>() }
    val entityListHeaderHeights = remember { mutableStateMapOf<Pair<ContentType, String>, Int>() }

    // Retain PrayersHome state across detail-page visits.
    var prayersQuickSection by remember { mutableStateOf(PrayerQuickSection.EVERYDAY) }
    // The loaded prayer lists are hoisted here so returning from a prayer detail
    // finds the list already populated — otherwise it re-loads from empty and the
    // scroll position snaps back to the top.
    var prayersFavorites by remember { mutableStateOf<List<PrayerSummary>>(emptyList()) }
    var prayersByCategory by remember { mutableStateOf<Map<PrayerCategory, List<PrayerSummary>>>(emptyMap()) }

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
            AnimatedContent(
                targetState = destination,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    val from = initialState
                    val to = targetState
                    when {
                        from is Destination.Splash -> {
                            fadeIn(tween(durationMillis = 350)).togetherWith(
                                fadeOut(tween(durationMillis = 350)),
                            ) using null
                        }

                        from is Destination.EntityList &&
                                to is Destination.EntityDetail &&
                                from.type == to.type -> {
                            slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = 220,
                                    easing = LinearOutSlowInEasing,
                                ),
                                initialOffsetX = { fullWidth -> fullWidth },
                            ).togetherWith(ExitTransition.KeepUntilTransitionsFinished).also {
                                it.targetContentZIndex = 1f
                            } using null
                        }

                        from is Destination.EntityDetail &&
                                to is Destination.EntityList &&
                                from.type == to.type -> {
                            slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = 260,
                                    easing = FastOutSlowInEasing,
                                ),
                                initialOffsetX = { fullWidth -> -fullWidth / 10 },
                            ).togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(
                                        durationMillis = 260,
                                        easing = FastOutSlowInEasing,
                                    ),
                                    targetOffsetX = { fullWidth -> fullWidth },
                                ),
                            ).also {
                                // Keep the restored list underneath while the detail
                                // screen slides away to the right.
                                it.targetContentZIndex = -1f
                            } using null
                        }

                        else ->
                            (EnterTransition.None togetherWith ExitTransition.None) using null
                    }
                },
                label = "screenNavigation",
            ) { animatedDestination ->
            when (val current = animatedDestination) {
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
                    scrollState = homeScrollState,
                )

                is Destination.Explore -> ExploreScreen(
                    repository = repository,
                    language = language,
                    onCategorySelected = { type -> nav.navigate(Destination.EntityList(type)) },
                    onVestmentsSelected = { nav.navigate(Destination.Vestments) },
                    onHubSelected = { hub -> nav.navigate(Destination.Hub(hub.id)) },
                    listState = exploreScrollState,
                )

                is Destination.Vestments -> VestmentsScreen(
                    onBackClick = nav::back,
                    listState = vestmentsScrollState,
                )

                is Destination.Hub -> {
                    HubScreen(
                        hubId = current.hubId,
                        repository = repository,
                        language = language,
                        onBack = nav::back,
                        onSectionSelected = { section ->
                            when (section.id) {
                                "cat.sacraments" -> nav.navigate(Destination.SacramentsScreen)
                                "mass.procession" -> nav.navigate(Destination.ProcessionScreen)
                                else -> nav.navigate(Destination.HubSection(current.hubId, section.id))
                            }
                        },
                        onArticleSelected = { articleId -> nav.navigate(Destination.HubArticle(current.hubId, articleId)) },
                        listState = hubScrollStates.getOrPut(current.hubId) { LazyListState() },
                        initialDetail = hubDetails[current.hubId],
                        initialStrings = hubStringsCache[current.hubId],
                        initialHeaderHeightPx = hubHeaderHeights[current.hubId] ?: 0,
                        onDetailLoaded = { hubDetails[current.hubId] = it },
                        onStringsLoaded = { hubStringsCache[current.hubId] = it },
                        onHeaderHeightChanged = { hubHeaderHeights[current.hubId] = it },
                    )
                }

                is Destination.HubSection -> {
                    HubSectionScreen(
                        hubId = current.hubId,
                        sectionId = current.sectionId,
                        repository = repository,
                        language = language,
                        onBack = nav::back,
                        onArticleSelected = { article -> nav.navigate(Destination.HubArticle(current.hubId, article.id)) },
                        onEntityRefSelected = { ref -> navigateToHubEntityRef(nav, current.hubId, ref) },
                        listState = hubSectionScrollStates.getOrPut(current.sectionId) { LazyListState() },
                        initialArticles = hubSectionArticles[current.sectionId],
                        initialStrings = hubSectionStringsCache[current.sectionId],
                        initialHeaderHeightPx = hubSectionHeaderHeights[current.sectionId] ?: 0,
                        onArticlesLoaded = { hubSectionArticles[current.sectionId] = it },
                        onStringsLoaded = { hubSectionStringsCache[current.sectionId] = it },
                        onHeaderHeightChanged = { hubSectionHeaderHeights[current.sectionId] = it },
                    )
                }

                is Destination.HubArticle -> {
                    HubArticleScreen(
                        articleId = current.articleId,
                        repository = repository,
                        language = language,
                        onBack = nav::back,
                        onEntityRefSelected = { ref -> navigateToHubEntityRef(nav, current.hubId, ref) },
                        listState = hubArticleScrollStates.getOrPut(current.articleId) { LazyListState() },
                        initialArticle = hubArticleDetails[current.articleId],
                        initialStrings = hubArticleStringsCache[current.articleId],
                        initialHeaderHeightPx = hubArticleHeaderHeights[current.articleId] ?: 0,
                        onArticleLoaded = { hubArticleDetails[current.articleId] = it },
                        onStringsLoaded = { hubArticleStringsCache[current.articleId] = it },
                        onHeaderHeightChanged = { hubArticleHeaderHeights[current.articleId] = it },
                    )
                }

                is Destination.EntityList -> {
                    val listKey = current.type to language
                    EntityListScreen(
                        type = current.type,
                        repository = repository,
                        language = language,
                        onBack = nav::back,
                        onItemSelected = { item: ContentSummary -> nav.navigate(Destination.EntityDetail(item.type, item.id)) },
                        listState = entityListScrollStates.getOrPut(listKey) { LazyListState() },
                        initialItems = entityListItemCaches[listKey],
                        initialSelectedCentury = entityListSelectedCenturies[listKey],
                        initialHeaderHeightPx = entityListHeaderHeights[listKey] ?: 0,
                        onItemsLoaded = { entityListItemCaches[listKey] = it },
                        onCenturySelectionChanged = { century ->
                            if (century == null) {
                                entityListSelectedCenturies.remove(listKey)
                            } else {
                                entityListSelectedCenturies[listKey] = century
                            }
                        },
                        onHeaderHeightChanged = { entityListHeaderHeights[listKey] = it },
                    )
                }

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
                    favorites = prayersFavorites,
                    byCategory = prayersByCategory,
                    onDataLoaded = { favorites, byCategory ->
                        prayersFavorites = favorites
                        prayersByCategory = byCategory
                    },
                    onOpenPrayer = { slug -> nav.navigate(Destination.PrayerDetail(slug)) },
                    onOpenRosary = { nav.navigate(Destination.RosaryScreen) },
                    onOpenStations = { nav.navigate(Destination.StationsScreen) },
                    quickSection = prayersQuickSection,
                    onQuickSectionChange = { prayersQuickSection = it },
                    quickSectionState = prayersQuickSectionScrollState,
                    scrollState = prayersHomeScrollState,
                )

                is Destination.PrayerDetail -> PrayerDetailScreen(
                    slug = current.slug,
                    repository = repository,
                    language = language,
                    onBack = nav::back,
                )

                is Destination.RosaryScreen -> RosaryScreen(
                    repository = repository,
                    sessionRepository = container.rosarySessionRepository,
                    carouselOnLeft = false,
                    language = language,
                    onBack = nav::back,
                )

                is Destination.StationsScreen -> StationsScreen(
                    language = language,
                    onBack = nav::back,
                    listState = stationsScrollState,
                )

                is Destination.SacramentsScreen -> SacramentsScreen(
                    language = language,
                    onBack = nav::back,
                    listState = sacramentsScrollState,
                )

                is Destination.ProcessionScreen -> ProcessionScreen(
                    language = language,
                    onBack = nav::back,
                    listState = processionScrollState,
                )

                is Destination.Search -> SearchScreen(
                    repository = repository,
                    language = language,
                    onResultSelected = { result: ContentSummary -> nav.navigate(Destination.EntityDetail(result.type, result.id)) },
                    landingListState = searchLandingScrollState,
                    resultsListState = searchResultsScrollState,
                )

                is Destination.Saved -> SavedScreen(
                    repository = repository,
                    onBack = nav::back,
                    onItemSelected = { bookmark: BookmarkItem -> nav.navigate(Destination.EntityDetail(bookmark.type, bookmark.id)) },
                    listState = savedScrollState,
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
                    onOpenFeedback = { nav.navigate(Destination.Feedback) },
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

                is Destination.Feedback -> FeedbackScreen(onBack = nav::back)
            }
            }

            // Floating pill nav bar, ported from Itinera's SlidingPillBar — an
            // overlay above the content rather than a docked Scaffold bottomBar,
            // so screens scroll underneath it.
            if (showBottomBar) {
                Row(
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
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isSearch = nav.current == Destination.Search
                    BottomNavBar(
                        selected = if (isSearch) null else nav.selectedTab,
                        onSelect = nav::selectTab,
                        modifier = Modifier.weight(1f)
                    )
                    FloatingSearchButton(
                        onClick = { nav.navigate(Destination.Search) },
                        isSelected = isSearch
                    )
                }
            }
        }
    }
    }
}

/**
 * Routes a hub entity link/hotspot target (docs/briefs/topic-hubs.md, T5) onto an existing
 * screen where one exists. DOCUMENT/COUNCIL/ARTWORK/PLACE have no destination yet — they're the
 * COLLECTION-layout sections' territory (papal_documents etc.), not part of this hub; a tap is a
 * no-op rather than a crash until that's built.
 */
private fun navigateToHubEntityRef(nav: AppNavController, hubId: String, ref: EntityRef) {
    when (ref.type) {
        EntityType.ARTICLE -> nav.navigate(Destination.HubArticle(hubId, ref.id))
        EntityType.POPE -> nav.navigate(Destination.EntityDetail(ContentType.POPE, ref.id))
        EntityType.SAINT -> nav.navigate(Destination.EntityDetail(ContentType.SAINT, ref.id))
        EntityType.CHURCH -> nav.navigate(Destination.EntityDetail(ContentType.CHURCH, ref.id))
        EntityType.PRAYER -> nav.navigate(Destination.PrayerDetail(ref.id))
        EntityType.DOCUMENT, EntityType.COUNCIL, EntityType.ARTWORK, EntityType.PLACE -> Unit
    }
}
