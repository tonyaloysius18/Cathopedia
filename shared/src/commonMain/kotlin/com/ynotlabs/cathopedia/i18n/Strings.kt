package com.ynotlabs.cathopedia.i18n

import androidx.compose.runtime.compositionLocalOf

/**
 * Every piece of UI chrome text the app owns — screen titles, labels, buttons,
 * empty states. Content fetched from the encyclopedia database (saint bios, feast
 * dates, etc.) is translated separately by [language] passed into the repository
 * and is NOT part of this table.
 *
 * English defaults live directly on the data class, so [EN] is just `Strings()`.
 * Each other language is `EN.copy(fieldsThatAreTranslated = "...")` — any field a
 * language table doesn't override falls back to the English default rather than
 * showing blank text.
 */
data class Strings(
    // Common / reused across screens
    val loading: String = "Loading",
    val back: String = "Back",
    val close: String = "Close",
    val closeSearch: String = "Close search",
    val closeFullImage: String = "Close full image",
    val searchDesc: String = "Search",
    val selectedDesc: String = "Selected",
    val soon: String = "soon",
    val all: String = "All",
    val continueLabel: String = "Continue",
    val skip: String = "Skip",
    val brandTagline: String = "Knowledge · Faith · Tradition",

    // Bottom bar tab labels — shown only for the currently selected tab
    val navHome: String = "Home",
    val navExplore: String = "Explore",
    val navPrayers: String = "Prayers",
    val navSearch: String = "Search",
    val navSettings: String = "Settings",

    // Prayers tab (placeholder copy until the prayer library/detail/Rosary screens land)
    val prayersHomeTitle: String = "Prayers",
    val prayersComingSoon: String = "The prayer library is on its way.",
    val prayerDetailTitle: String = "Prayer",
    val rosaryScreenTitle: String = "Holy Rosary",

    // Content type names (chrome labels for the seven entity kinds)
    val typeSaintsPlural: String = "Saints",
    val typePopesPlural: String = "Popes",
    val typeApostlesPlural: String = "Apostles",
    val typeChurchesPlural: String = "Churches & Shrines",
    val typeApparitionsPlural: String = "Marian Apparitions",
    val typeMiraclesPlural: String = "Eucharistic Miracles",
    val typeFeastsPlural: String = "Liturgical Feasts",
    val typeSaintSingular: String = "Saint",
    val typePopeSingular: String = "Pope",
    val typeApostleSingular: String = "Apostle",
    val typeChurchSingular: String = "Church",
    val typeApparitionSingular: String = "Apparition",
    val typeMiracleSingular: String = "Eucharistic Miracle",
    val typeFeastSingular: String = "Liturgical Feast",

    // Category groupings (Explore / Search filter chips)
    val categoryPeople: String = "People",
    val categoryPlaces: String = "Places",
    val categoryEvents: String = "Events",
    val categoryFeasts: String = "Feasts",

    // Theme mode
    val themeSystem: String = "System",
    val themeLight: String = "Light",
    val themeDark: String = "Dark",

    // Home
    val homeContinueReading: String = "Continue reading",
    val homeDiscover: String = "Discover",
    val homeNothingToShowYet: String = "Nothing to show yet.",
    val homeFeastOfTheDay: String = "Feast of the day",
    val seasonAdvent: String = "Advent",
    val seasonChristmas: String = "Christmas season",
    val seasonOrdinaryTime: String = "Ordinary Time",
    val seasonLent: String = "Lent",
    val seasonEaster: String = "Easter season",

    // Explore
    val exploreTitle: String = "Explore",
    val exploreSubtitle: String = "Discover the richness of the Catholic faith",
    val exploreSearchPlaceholder: String = "Search saints, popes, places...",
    val exploreNoResultsFound: String = "No results found",
    val exploreTryAnotherCategory: String = "Try another category or keyword.",
    val countHolyLives: String = "{count} holy lives",
    val countPontiffs: String = "{count} pontiffs",
    val countWitnesses: String = "{count} witnesses of Christ",
    val countSacredPlaces: String = "{count} sacred places",
    val countApparitions: String = "{count} apparitions",
    val countMiracles: String = "{count} documented miracles",
    val countFeastCelebrations: String = "{count} celebrations in the Church year",
    val countFeastFallback: String = "Journey through the Church year",
    val countEntriesGeneric: String = "{count} entries",

    // Search
    val searchCathopedia: String = "Search Cathopedia",

    // Saved
    val savedTitle: String = "Saved",
    val savedStoredOnDeviceOnly: String = "Stored on this device only.",
    val savedFilterNotes: String = "Notes",
    val savedNothingSavedYet: String = "Nothing saved yet — bookmark anything from its detail page.",
    val savedOneNote: String = "1 note",
    val savedBookmarked: String = "Bookmarked",

    // Language screens (settings + startup)
    val languageAppLanguageSectionTitle: String = "App language",
    val languageScreenTitle: String = "Language",
    val languageScreenSubtitle: String = "Choose your preferred app language",
    val languageInfoText: String = "Changing the language updates the app interface. Some content may not yet be available in every language.",
    val languageWelcomeToCathopedia: String = "Welcome to Cathopedia",
    val languageChoosePrompt: String = "To get started, please choose\nyour preferred language.",
    val languageChangeLaterInSettings: String = "You can change this later in Settings.",
    val languageNameEnglish: String = "English",
    val languageNameFrench: String = "French",
    val languageNameSpanish: String = "Spanish",
    val languageNameItalian: String = "Italian",

    // Appearance
    val appearanceTitle: String = "Appearance",
    val appearanceSubtitle: String = "Customize the look and feel",
    val appearanceThemeModeSectionTitle: String = "Theme mode",
    val appearancePreviewSectionTitle: String = "Preview",
    val appearanceMatchDevice: String = "Match your\ndevice setting",
    val appearanceAlwaysLight: String = "Always use the\nlight theme",
    val appearanceAlwaysDark: String = "Always use the\ndark theme",
    val appearanceInfoSystem: String = "System follows the appearance setting of your device.",
    val appearanceInfoLight: String = "Light keeps Cathopedia in its brighter theme regardless of your device setting.",
    val appearanceInfoDark: String = "Dark keeps Cathopedia in its deep forest-green theme regardless of your device setting.",
    val appearancePreviewTagline: String = "Faith • Knowledge • Tradition",
    val appearancePreviewBody: String = "Explore the richness of our Catholic faith\nwith a beautiful reading experience.",

    // Notifications
    val notificationsTitle: String = "Notifications",
    val notificationsSubtitle: String = "Stay close to the Church calendar",
    val notificationsFeastToggleLabel: String = "Feast of the day",
    val notificationsFeastToggleDescription: String = "A morning reminder whenever a saint's feast, solemnity, or holy day falls on the Church calendar.",
    val notificationsInfoText: String = "Notifications are scheduled entirely on your device — turning this on schedules reminders for upcoming feasts, and turning it off cancels them.",
    val notificationsPermissionDenied: String = "Notifications are turned off in your device settings. Enable them there to receive feast reminders.",

    // Settings
    val settingsTitle: String = "Settings",
    val settingsSubtitle: String = "Personalize your Cathopedia experience",
    val settingsPreferencesSectionTitle: String = "Preferences",
    val settingsLibraryAppSectionTitle: String = "Library & app",
    val settingsLanguageRowLabel: String = "Language",
    val settingsAppearanceRowLabel: String = "Appearance",
    val settingsNotificationsRowLabel: String = "Notifications",
    val settingsNotificationsRowValueOn: String = "Feast of the day reminders on",
    val settingsNotificationsRowValueOff: String = "Off",
    val settingsSavedRowLabel: String = "Saved",
    val settingsSavedRowValue: String = "Your bookmarked entries",
    val settingsAboutRowLabel: String = "About",
    val settingsAboutRowValue: String = "App info, storage, attributions",
    val settingsInspirationQuote: String = "Your word is a lamp to my feet\nand a light to my path.",
    val settingsInspirationReference: String = "Psalm 119:105",

    // About
    val aboutTitle: String = "About",
    val aboutSubtitle: String = "App info, storage & attributions",
    val aboutAppTagline: String = "A Catholic knowledge companion for learning, discovery and reflection.",
    val aboutVersionPrefix: String = "Version",
    val aboutSectionTitle: String = "About Cathopedia",
    val aboutBody: String = "Cathopedia is an offline-first Catholic encyclopedia and spiritual reference " +
        "designed to bring saints, popes, apostles, churches, Marian apparitions, " +
        "Eucharistic miracles and liturgical feasts together in one carefully organized experience.",
    val aboutAppDataSectionTitle: String = "App & data",
    val aboutOfflineFirstTitle: String = "Offline-first",
    val aboutOfflineFirstSubtitle: String = "Core encyclopedia content is stored on your device.",
    val aboutPrivacyTitle: String = "Privacy",
    val aboutPrivacySubtitle: String = "No account is required for the core app experience.",
    val aboutSavedEntriesTitle: String = "Saved entries",
    val aboutSavedEntriesSubtitle: String = "Bookmarks and preferences are kept locally on this device.",
    val aboutDeveloperSectionTitle: String = "Developer",
    val aboutStudioSuffix: String = "· Android & iOS",
    val aboutGithubTitle: String = "GitHub",
    val aboutGithubSubtitle: String = "View projects and source",
    val aboutLinkedinTitle: String = "LinkedIn",
    val aboutLinkedinSubtitle: String = "Connect with the developer",
    val aboutContactTitle: String = "Contact",
    val aboutAcknowledgementsSectionTitle: String = "Acknowledgements",
    val aboutCreditComposePurpose: String = "Cross-platform user interface",
    val aboutCreditSqlDelightPurpose: String = "Local structured data and persistence",
    val aboutCreditCoroutinesPurpose: String = "Asynchronous application logic",
    val aboutCreditSerializationPurpose: String = "Content and data serialization",
    val aboutContentNoteSectionTitle: String = "Content note",
    val aboutContentNoteBody: String = "Cathopedia is an independent educational reference app. " +
        "It is not an official publication of the Holy See or any diocese. " +
        "Historical and devotional material should be read alongside authoritative Church sources.",
    val aboutCopyrightPrefix: String = "© 2026",

    // Intro / onboarding
    val introPage1Title: String = "Everything connects",
    val introPage1Body: String = "Move naturally between saints, feasts, churches and sacred traditions. Cathopedia brings the story of the faith together in one place.",
    val introPage2Title: String = "Works without a signal",
    val introPage2Body: String = "Keep exploring inside a basilica, while travelling, or on pilgrimage. Your core Catholic reference stays available on your device.",
    val introPage3Title: String = "Search across everything",
    val introPage3Body: String = "Find saints, popes, churches, apparitions, Eucharistic miracles and feasts from one unified search experience.",
    val introNext: String = "Next",
    val introGetStarted: String = "Get Started",
    val introAvailableWhereverYouGo: String = "Available wherever you go",

    // Entity list
    val listFilterAndSortDesc: String = "Filter and sort",
    val listFilterAndSortSheetTitle: String = "Filter & Sort",
    val listFilterCentury: String = "CENTURY",
    val listFilterRank: String = "RANK",
    val listSortBy: String = "SORT BY",
    val listSortNameAsc: String = "Name (A–Z)",
    val listSortNameDesc: String = "Name (Z–A)",
    val listSortEarliestFirst: String = "Earliest first",
    val listSortLatestFirst: String = "Latest first",
    val listApplyFilter: String = "Apply Filter",
    val listNothingToShow: String = "Nothing to show",
    val listNoMatchesFound: String = "No matches found",
    val listContentWillAppear: String = "Content will appear here when available.",
    val listTryAnotherNameKeyword: String = "Try another name, keyword, or century.",
    val listPopesSubtitle: String = "Explore the successors of St. Peter through the centuries",
    val listExploreThroughHistory: String = "Explore through history",
    val listSearchByNameOrDescription: String = "Search {type} by name or description…",
    val listOpenEntity: String = "Open {name}",
    val listUnknownEra: String = "Unknown era",
    val listCenturyWord: String = "century",
    val listUseFrenchOrdinals: Boolean = false,

    // Entity detail
    val detailOverviewTitle: String = "Overview",
    val detailLifeAndLegacyTitle: String = "Life & Legacy",
    val detailConnectedTitle: String = "Connected",
    val detailSourceTitle: String = "Source",
    val detailSaintTag: String = "SAINT",
    val detailSave: String = "Save",
    val detailSaved: String = "Saved",
    val detailShare: String = "Share",
    val detailRelated: String = "Related",
    val detailRemoveFromFavorites: String = "Remove from favorites",
    val detailSaveToFavorites: String = "Save to favorites",
    val detailFactFeastDay: String = "Feast day",
    val detailFactCanonized: String = "Canonized",
    val detailFactPatronage: String = "Patronage",
    val detailFactRegnalNumber: String = "Regnal number",
    val detailFactPapacyStart: String = "Papacy start",
    val detailFactPapacyEnd: String = "Papacy end",
    val detailFactPapacy: String = "Papacy",
    val detailFactOriginalName: String = "Original name",
    val detailFactMartyrdom: String = "Martyrdom",
    val detailFactFounded: String = "Founded",
    val detailFactLocation: String = "Location",
    val detailFactYear: String = "Year",
    val detailFactStatus: String = "Status",
    val detailFactDate: String = "Date",
    val detailPresent: String = "Present",
    val detailFeastDayHeroMeta: String = "Feast day · {value}",
    val detailAiGeneratedCaption: String = "AI-GENERATED DEVOTIONAL PORTRAIT · STYLED AFTER TRADITIONAL ICONOGRAPHY",
    val detailHistoricalImageCaption: String = "HISTORICAL IMAGE · SEE SOURCES FOR ATTRIBUTION",
    val detailSourceFallback: String = "Cathopedia source material",
    val detailPortraitIllustrationNote: String = "Portrait is a devotional illustration; see source attribution for the historical content.",
)

/** English is the base table — every default above is already English. */
val EN: Strings = Strings()

/** Resolves the active string table for a language code stored in preferences. */
fun stringsFor(languageCode: String): Strings = when (languageCode.lowercase()) {
    "fr" -> FR
    else -> EN
}

val LocalStrings = compositionLocalOf { EN }
