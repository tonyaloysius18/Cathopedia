package com.ynotlabs.cathopedia.i18n

import androidx.compose.runtime.compositionLocalOf

/**
 * Every piece of UI chrome text the app owns — screen titles, labels, buttons,
 * empty states. Content fetched from the encyclopedia database (saint bios, feast
 * dates, etc.) is translated separately by [language] passed into the repository
 * and is NOT part of this table.
 *
 * English defaults live directly on the properties below, so [EN] is just
 * `Strings()`. Each other language is `Strings().apply { fieldsThatAreTranslated
 * = "..." }` (see StringsFR.kt) — any field a language table doesn't override
 * falls back to the English default rather than showing blank text.
 *
 * A plain `class` with `var` properties, not a `data class` — with 250+ fields,
 * a single constructor taking all of them (needed for `data class` + `.copy()`)
 * crossed a real JVM/D8 method-parameter ceiling and crashed every build with
 * `VerifyError: ... expected N argument registers, method signature has N+1 or
 * more` at `StringsKt.<clinit>`, i.e. app launch. Plain properties with no
 * constructor sidestep that entirely — every `s.foo` call site is unaffected.
 */
class Strings {
    // Common / reused across screens
    var loading: String = "Loading"
    var back: String = "Back"
    var close: String = "Close"
    var closeSearch: String = "Close search"
    var closeFullImage: String = "Close full image"
    var searchDesc: String = "Search"
    var selectedDesc: String = "Selected"
    var soon: String = "soon"
    var all: String = "All"
    var continueLabel: String = "Continue"
    var skip: String = "Skip"
    var brandTagline: String = "Knowledge · Faith · Tradition"

    // Bottom bar tab labels — shown only for the currently selected tab
    var navHome: String = "Home"
    var navExplore: String = "Explore"
    var navPrayers: String = "Prayers"
    var navSearch: String = "Search"
    var navSettings: String = "Settings"

    // Prayers tab
    var prayersHomeTitle: String = "Prayers"
    var prayersComingSoon: String = "The prayer library is on its way."
    var prayerDetailTitle: String = "Prayer"
    var rosaryScreenTitle: String = "Holy Rosary"

    // Prayer categories (chrome labels for PrayerCategory's fixed taxonomy)
    var prayerCategoryEveryday: String = "Everyday"
    var prayerCategoryMarian: String = "Marian"
    var prayerCategoryHolySpirit: String = "Holy Spirit"
    var prayerCategoryEucharistic: String = "Eucharistic"
    var prayerCategorySaints: String = "Saints"
    var prayerCategoryPenitential: String = "Penitential"
    var prayerCategorySequences: String = "Chaplets & Novenas"
    var prayerCategoryOccasional: String = "Occasional"

    // PrayersHome
    var prayersFavoritesSection: String = "Favourites"
    var prayersRosaryCardTitle: String = "The Holy Rosary"
    var prayersRosaryCardSubtitle: String = "Pray a decade, or all twenty mysteries"
    var prayersSearchPlaceholder: String = "Search prayers…"
    var prayersSearchNoResults: String = "No prayers match your search."
    var prayersHeaderSubtitle: String = "Strengthen your faith with daily prayer"
    var prayersPrayNow: String = "Pray Now"
    var prayersWayOfCrossTitle: String = "Way of the Cross"
    var prayersWayOfCrossTagline: String = "Walk with Christ through His Passion"
    var prayersWayOfCrossDescription: String = "Meditate on the fourteen Stations of the Cross."
    var prayersMyFavourites: String = "My Favourites"
    var prayersEverydaySection: String = "Everyday prayers"
    var prayersMarianSection: String = "Marian prayers"
    var prayersHolySpiritSection: String = "Holy Spirit prayers"
    var prayersEucharisticSection: String = "Eucharistic prayers"
    var prayersSaintsSection: String = "Saint prayers"
    var prayersPenitentialSection: String = "Penitential prayers"
    var prayersOccasionalSection: String = "Occasional prayers"
    var prayersFavouriteEmpty: String = "Your favourite prayers will appear here."
    var prayersSavedCount: String = "{count} saved"
    var prayersDailySubtitle: String = "Daily prayers"
    var prayersExploreSubtitle: String = "Explore"
    var prayersSearchSuggestion: String = "Try another prayer name or devotional keyword."
    var prayersSearchResults: String = "Search results"
    var prayersOpeningSignOfCross: String = "In the name of the Father, and of the Son..."
    var prayersOpeningOurFather: String = "Our Father, who art in heaven..."
    var prayersOpeningHailMary: String = "Hail Mary, full of grace..."
    var prayersOpeningGloryBe: String = "Glory be to the Father, and to the Son..."
    var prayersOpeningApostlesCreed: String = "I believe in God, the Father almighty..."
    var prayersOpeningNiceneCreed: String = "I believe in one God, the Father almighty..."
    var prayersOpeningMorningOffering: String = "Begin your day by offering it to God."
    var prayersOpeningEveningPrayer: String = "End the day in gratitude and reflection."
    var prayersOpeningGeneric: String = "Open this prayer to read and pray."

    // PrayerDetail
    var prayerDetailBySource: String = "Source"
    var prayerDetailAttribution: String = "Attribution"
    var prayerDetailShowSource: String = "Show source"
    var prayerDetailHideSource: String = "Hide source"
    var prayerDetailParallelView: String = "Side by side"
    var prayerDetailFontSize: String = "Text size"
    var prayerDetailFontSmaller: String = "Smaller text"
    var prayerDetailFontLarger: String = "Larger text"
    var prayerDetailKeepScreenOn: String = "Keep screen on"
    var prayerLanguageEn: String = "English"
    var prayerLanguageFr: String = "Français"
    var prayerLanguageLa: String = "Latin"
    var prayerTextNotYetAvailable: String = "This prayer's text isn't in the app yet."
    var prayerDetailAboutTitle: String = "About this prayer"
    var prayerDetailAboutFallback: String = "Historical and devotional background for this prayer is currently being curated."

    // Rosary screen
    var rosaryModeGuided: String = "Guided"
    var rosaryModeMap: String = "Map"
    var rosaryCanvasSummary: String = "Rosary bead map: the crucifix, tail, medal, and five decades of the loop, showing your progress. Switch to Guided mode to pray through it."
    var rosaryChooseMysterySet: String = "Mystery set"
    var rosaryMysterySetJoyful: String = "Joyful"
    var rosaryMysterySetSorrowful: String = "Sorrowful"
    var rosaryMysterySetGlorious: String = "Glorious"
    var rosaryMysterySetLuminous: String = "Luminous"
    var rosaryOrganicShape: String = "Organic bead shape"
    var rosaryResumeTitle: String = "Resume your Rosary?"
    var rosaryResumeMessage: String = "You were partway through a Rosary a little while ago."
    var rosaryResumeButton: String = "Resume"
    var rosaryStartOverButton: String = "Start over"
    var rosaryProgressLabel: String = "{current} of {total}"
    var rosaryMysteryFruit: String = "Fruit"
    var rosaryFinishedTitle: String = "Amen."
    var rosaryFinishedMessage: String = "You've completed the Rosary."
    var rosaryDone: String = "Done"
    var rosaryTapToAdvanceHint: String = "Double-tap to continue to the next prayer"
    var rosaryOrdinalFirst: String = "First"
    var rosaryOrdinalSecond: String = "Second"
    var rosaryOrdinalThird: String = "Third"
    var rosaryOrdinalFourth: String = "Fourth"
    var rosaryOrdinalFifth: String = "Fifth"
    var rosaryMysteryAnnouncement: String = "The {ordinal} {set} Mystery"

    // Content type names (chrome labels for the seven entity kinds)
    var typeSaintsPlural: String = "Saints"
    var typePopesPlural: String = "Popes"
    var typeApostlesPlural: String = "Apostles"
    var typeChurchesPlural: String = "Churches & Shrines"
    var typeApparitionsPlural: String = "Marian Apparitions"
    var typeMiraclesPlural: String = "Eucharistic Miracles"
    var typeFeastsPlural: String = "Liturgical Feasts"
    var typeSaintSingular: String = "Saint"
    var typePopeSingular: String = "Pope"
    var typeApostleSingular: String = "Apostle"
    var typeChurchSingular: String = "Church"
    var typeApparitionSingular: String = "Apparition"
    var typeMiracleSingular: String = "Eucharistic Miracle"
    var typeFeastSingular: String = "Liturgical Feast"

    // Category groupings (Explore / Search filter chips)
    var categoryPeople: String = "People"
    var categoryPlaces: String = "Places"
    var categoryEvents: String = "Events"
    var categoryFeasts: String = "Feasts"

    // Theme mode
    var themeSystem: String = "System"
    var themeLight: String = "Light"
    var themeDark: String = "Dark"

    // Home
    var homeContinueReading: String = "Continue reading"
    var homeDiscover: String = "Discover"
    var homeNothingToShowYet: String = "Nothing to show yet."
    var homeFeastOfTheDay: String = "Feast of the day"
    var homeSaintsOfToday: String = "Saints of the day"
    var homeTodaysReadings: String = "Today's Readings"
    var homeFirstReading: String = "First reading"
    var homeSecondReading: String = "Second reading"
    var homeGospel: String = "Gospel"
    var homeVerseOfTheDay: String = "Verse of the day"
    var homeReadingsVary: String = "Readings are selected from the Masses for the day"
    var homeReadingsUnavailable: String = "Daily readings need the latest calendar update"
    var homeHeroDescription: String = "Your Catholic encyclopedia\nfor faith and life."
    var seasonAdvent: String = "Advent"
    var seasonChristmas: String = "Christmas season"
    var seasonOrdinaryTime: String = "Ordinary Time"
    var seasonLent: String = "Lent"
    var seasonEaster: String = "Easter season"

    // Explore
    var exploreTitle: String = "Explore"
    var exploreSubtitle: String = "Discover the richness of the Catholic faith"
    var exploreSearchPlaceholder: String = "Search saints, popes, places..."
    var exploreNoResultsFound: String = "No results found"
    var exploreTryAnotherCategory: String = "Try another category or keyword."
    var exploreHubsHeader: String = "Topic Hubs"
    var hubSectionComingSoon: String = "Coming soon"
    var exploreVestmentsTitle: String = "Sacred Vestments"
    var exploreVestmentsSubtitle: String = "Liturgical garments"
    var exploreCardIconDescription: String = "{title} icon"
    var countHolyLives: String = "{count} holy lives"
    var countPontiffs: String = "{count} pontiffs"
    var countWitnesses: String = "{count} witnesses of Christ"
    var countSacredPlaces: String = "{count} sacred places"
    var countApparitions: String = "{count} apparitions"
    var countMiracles: String = "{count} documented miracles"
    var countFeastCelebrations: String = "{count} celebrations in the Church year"
    var countFeastFallback: String = "Journey through the Church year"
    var countEntriesGeneric: String = "{count} entries"

    // Search
    var searchCathopedia: String = "Search Cathopedia"
    var searchHeroSubtitle: String = "Explore the richness of the Catholic faith"
    var searchPlaceholder: String = "Search saints, popes, places, topics..."
    var searchClear: String = "Clear search"
    var searchRecent: String = "Recent searches"
    var searchClearAll: String = "Clear all"
    var searchReadyTitle: String = "Ready to discover?"
    var searchReadyBody: String = "Type a name, place, or topic to start exploring the richness of the faith."
    var searchRemoveRecent: String = "Remove"
    var searchTipsTitle: String = "Search tips"
    var searchTipsBody: String = "Search by name, title, place, keyword, or topic to quickly find Catholic content."
    var searchOneResult: String = "{count} result"
    var searchManyResults: String = "{count} results"
    var searchNoResultsDetail: String = "Try another name, place, title, or Catholic topic."

    // Saved
    var savedTitle: String = "Saved"
    var savedStoredOnDeviceOnly: String = "Stored on this device only."
    var savedFilterNotes: String = "Notes"
    var savedNothingSavedYet: String = "Nothing saved yet — bookmark anything from its detail page."
    var savedOneNote: String = "1 note"
    var savedBookmarked: String = "Bookmarked"

    // Language screens (settings + startup)
    var languageAppLanguageSectionTitle: String = "App language"
    var languageScreenTitle: String = "Language"
    var languageScreenSubtitle: String = "Choose your preferred app language"
    var languageInfoText: String = "Changing the language updates the app interface. Some content may not yet be available in every language."
    var languageWelcomeToCathopedia: String = "Welcome to Cathopedia"
    var languageChoosePrompt: String = "To get started, please choose\nyour preferred language."
    var languageChangeLaterInSettings: String = "You can change this later in Settings."
    var languageNameEnglish: String = "English"
    var languageNameFrench: String = "French"
    var languageNameSpanish: String = "Spanish"
    var languageNameItalian: String = "Italian"
    var languageNameGerman: String = "German"

    // Appearance
    var appearanceTitle: String = "Appearance"
    var appearanceSubtitle: String = "Customize the look and feel"
    var appearanceThemeModeSectionTitle: String = "Theme mode"
    var appearancePreviewSectionTitle: String = "Preview"
    var appearanceMatchDevice: String = "Match your\ndevice setting"
    var appearanceAlwaysLight: String = "Always use the\nlight theme"
    var appearanceAlwaysDark: String = "Always use the\ndark theme"
    var appearanceInfoSystem: String = "System follows the appearance setting of your device."
    var appearanceInfoLight: String = "Light keeps Cathopedia in its brighter theme regardless of your device setting."
    var appearanceInfoDark: String = "Dark keeps Cathopedia in its deep forest-green theme regardless of your device setting."
    var appearancePreviewTagline: String = "Faith • Knowledge • Tradition"
    var appearancePreviewBody: String = "Explore the richness of our Catholic faith\nwith a beautiful reading experience."

    // Notifications
    var notificationsTitle: String = "Notifications"
    var notificationsSubtitle: String = "Stay close to the Church calendar"
    var notificationsFeastToggleLabel: String = "Feast of the day"
    var notificationsFeastToggleDescription: String = "A morning reminder whenever a saint's feast, solemnity, or holy day falls on the Church calendar."
    var notificationsInfoText: String = "Notifications are scheduled entirely on your device — turning this on schedules reminders for upcoming feasts, and turning it off cancels them."
    var notificationsPermissionDenied: String = "Notifications are turned off in your device settings. Enable them there to receive feast reminders."

    // Settings
    var settingsTitle: String = "Settings"
    var settingsSubtitle: String = "Personalize your Cathopedia experience"
    var settingsPreferencesSectionTitle: String = "Preferences"
    var settingsLibraryAppSectionTitle: String = "Library & app"
    var settingsLanguageRowLabel: String = "Language"
    var settingsAppearanceRowLabel: String = "Appearance"
    var settingsNotificationsRowLabel: String = "Notifications"
    var settingsNotificationsRowValueOn: String = "Feast of the day reminders on"
    var settingsNotificationsRowValueOff: String = "Off"
    var settingsSavedRowLabel: String = "Saved"
    var settingsSavedRowValue: String = "Your bookmarked entries"
    var settingsAboutRowLabel: String = "About"
    var settingsAboutRowValue: String = "App info, storage, attributions"
    var settingsFeedbackRowLabel: String = "Send Feedback"
    var settingsFeedbackRowValue: String = "Report a problem or share an idea"
    var settingsInspirationQuote: String = "Your word is a lamp to my feet\nand a light to my path."

    // Feedback
    var sendFeedback: String = "Send Feedback"
    var feedbackIntroTitle: String = "We'd love your feedback"
    var feedbackIntroSubtitle: String = "Tell us what's working and what could be better."
    var feedbackTopic: String = "Topic"
    var feedbackProblem: String = "Problem"
    var feedbackSuggestion: String = "Suggestion"
    var feedbackGeneral: String = "General"
    var feedbackYourFeedback: String = "Your feedback"
    var feedbackPlaceholder: String = "Describe your feedback in as much detail as you can…"
    var feedbackInvalidMessage: String = "Please enter at least 10 characters."
    var feedbackScreenshots: String = "Screenshots"
    var feedbackOptional: String = "optional"
    var feedbackAddPhoto: String = "Add a screenshot"
    var feedbackImageLimit: String = "Up to 3 images, 5 MB total."
    var feedbackImagesTooLarge: String = "Those images are too large. Please remove one and try again."
    var feedbackRemovePhoto: String = "Remove photo"
    var feedbackIncludeAppDetails: String = "Include app details"
    var feedbackAppDetailsDescription: String = "Adds the app version and device type so we can diagnose issues faster."
    var feedbackSensitiveImageWarning: String = "Please avoid including passwords or other sensitive personal information in screenshots."
    var feedbackReviewAndSend: String = "Send feedback"
    var feedbackSendFailed: String = "We couldn't send your feedback. Please try again."
    var feedbackSent: String = "Thank you!"
    var feedbackSentSubtitle: String = "Your feedback has been sent. We're grateful for your help improving Cathopedia."
    var feedbackDone: String = "Done"
    var settingsInspirationReference: String = "Psalm 119:105"

    // About
    var aboutTitle: String = "About"
    var aboutSubtitle: String = "App info, storage & attributions"
    var aboutAppTagline: String = "A Catholic knowledge companion for learning, discovery and reflection."
    var aboutVersionPrefix: String = "Version"
    var aboutSectionTitle: String = "About Cathopedia"
    var aboutBody: String = "Cathopedia is an offline-first Catholic encyclopedia and spiritual reference " +
        "designed to bring saints, popes, apostles, churches, Marian apparitions, " +
        "Eucharistic miracles and liturgical feasts together in one carefully organized experience."
    var aboutAppDataSectionTitle: String = "App & data"
    var aboutOfflineFirstTitle: String = "Offline-first"
    var aboutOfflineFirstSubtitle: String = "Core encyclopedia content is stored on your device."
    var aboutPrivacyTitle: String = "Privacy"
    var aboutPrivacySubtitle: String = "No account is required for the core app experience."
    var aboutSavedEntriesTitle: String = "Saved entries"
    var aboutSavedEntriesSubtitle: String = "Bookmarks and preferences are kept locally on this device."
    var aboutDeveloperSectionTitle: String = "Developer"
    var aboutStudioSuffix: String = "· Android & iOS"
    var aboutGithubTitle: String = "GitHub"
    var aboutGithubSubtitle: String = "View projects and source"
    var aboutLinkedinTitle: String = "LinkedIn"
    var aboutLinkedinSubtitle: String = "Connect with the developer"
    var aboutContactTitle: String = "Contact"
    var aboutAcknowledgementsSectionTitle: String = "Acknowledgements"
    var aboutCreditComposePurpose: String = "Cross-platform user interface"
    var aboutCreditSqlDelightPurpose: String = "Local structured data and persistence"
    var aboutCreditCoroutinesPurpose: String = "Asynchronous application logic"
    var aboutCreditSerializationPurpose: String = "Content and data serialization"
    var aboutContentNoteSectionTitle: String = "Content note"
    var aboutContentNoteBody: String = "Cathopedia is an independent educational reference app. " +
        "It is not an official publication of the Holy See or any diocese. " +
        "Historical and devotional material should be read alongside authoritative Church sources."
    var aboutCopyrightPrefix: String = "© 2026"

    // Intro / onboarding
    var introPage1Title: String = "Everything connects"
    var introPage1Body: String = "Move naturally between saints, feasts, churches and sacred traditions. Cathopedia brings the story of the faith together in one place."
    var introPage2Title: String = "Works without a signal"
    var introPage2Body: String = "Keep exploring inside a basilica, while travelling, or on pilgrimage. Your core Catholic reference stays available on your device."
    var introPage3Title: String = "Search across everything"
    var introPage3Body: String = "Find saints, popes, churches, apparitions, Eucharistic miracles and feasts from one unified search experience."
    var introNext: String = "Next"
    var introGetStarted: String = "Get Started"
    var introAvailableWhereverYouGo: String = "Available wherever you go"

    // Entity list
    var listFilterAndSortDesc: String = "Filter and sort"
    var listFilterAndSortSheetTitle: String = "Filter & Sort"
    var listFilterCentury: String = "CENTURY"
    var listFilterRank: String = "RANK"
    var listSortBy: String = "SORT BY"
    var listSortNameAsc: String = "Name (A–Z)"
    var listSortNameDesc: String = "Name (Z–A)"
    var listSortEarliestFirst: String = "Earliest first"
    var listSortLatestFirst: String = "Latest first"
    var listApplyFilter: String = "Apply Filter"
    var listNothingToShow: String = "Nothing to show"
    var listNoMatchesFound: String = "No matches found"
    var listContentWillAppear: String = "Content will appear here when available."
    var listTryAnotherNameKeyword: String = "Try another name, keyword, or century."
    var listPopesSubtitle: String = "Explore the successors of St. Peter through the centuries"
    var listExploreThroughHistory: String = "Explore through history"
    var listSearchByNameOrDescription: String = "Search {type} by name or description…"
    var listOpenEntity: String = "Open {name}"
    var listUnknownEra: String = "Unknown era"
    var listCenturyWord: String = "century"
    var listUseFrenchOrdinals: Boolean = false

    // Entity detail
    var detailOverviewTitle: String = "Overview"
    var detailCoatOfArmsTitle: String = "Coat of Arms"
    var detailLifeAndLegacyTitle: String = "Life & Legacy"

    // Stations of the Cross
    var stationsScreenTitle: String = "Way of the Cross"
    var stationsIndicator: String = "Station {number} of 14"
    var stationsVersicleLabel: String = "Versicle"
    var stationsResponseLabel: String = "Response"
    var stationsMeditationLabel: String = "Meditation"
    var stationsNumberLabel: String = "Station {number}"

    // Sacred Vestments
    var vestmentsTitle: String = "Sacred Vestments"
    var vestmentsSubtitle: String = "The sacred garments worn at the Holy Eucharist"
    var vestmentsRegaliaSection: String = "Vestments & Regalia"
    var vestmentsColorsSection: String = "Liturgical Colors"
    var vestmentsColorsDescription: String = "The color of many vestments changes with the season or feast being celebrated."
    var vestmentsPurposeLabel: String = "Purpose"
    var vestmentsSignificanceLabel: String = "Significance"
    var vestmentsGoodToKnowLabel: String = "Good to know"
    var vestmentsWornForLabel: String = "Worn for"
    var vestmentTranslations: Map<String, String> = emptyMap()
    var detailConnectedTitle: String = "Connected"
    var detailSourceTitle: String = "Source"
    var detailSaintTag: String = "SAINT"
    var detailSave: String = "Save"
    var detailSaved: String = "Saved"
    var detailShare: String = "Share"
    var detailRelated: String = "Related"
    var detailRemoveFromFavorites: String = "Remove from favorites"
    var detailSaveToFavorites: String = "Save to favorites"
    var detailFactFeastDay: String = "Feast day"
    var detailFactCanonized: String = "Canonized"
    var detailFactPatronage: String = "Patronage"
    var detailFactRegnalNumber: String = "Regnal number"
    var detailFactPapacyStart: String = "Papacy start"
    var detailFactPapacyEnd: String = "Papacy end"
    var detailFactPapacy: String = "Papacy"
    var detailFactOriginalName: String = "Original name"
    var detailFactMartyrdom: String = "Martyrdom"
    var detailFactFounded: String = "Founded"
    var detailFactLocation: String = "Location"
    var detailFactYear: String = "Year"
    var detailFactStatus: String = "Status"
    var detailFactDate: String = "Date"
    var detailPresent: String = "Present"
    var detailFeastDayHeroMeta: String = "Feast day · {value}"
    var detailAiGeneratedCaption: String = "AI-GENERATED DEVOTIONAL PORTRAIT · STYLED AFTER TRADITIONAL ICONOGRAPHY"
    var detailHistoricalImageCaption: String = "HISTORICAL IMAGE · SEE SOURCES FOR ATTRIBUTION"
    var detailSourceFallback: String = "Cathopedia source material"
    var detailPortraitIllustrationNote: String = "Portrait is a devotional illustration; see source attribution for the historical content."
}

/** English is the base table — every default above is already English. */
val EN: Strings = Strings()

/** Resolves the active string table for a language code stored in preferences. */
fun stringsFor(languageCode: String): Strings = when (languageCode.lowercase()) {
    "fr" -> FR
    else -> EN
}

val LocalStrings = compositionLocalOf { EN }
