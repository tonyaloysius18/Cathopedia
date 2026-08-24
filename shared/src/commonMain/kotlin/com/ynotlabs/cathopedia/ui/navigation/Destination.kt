package com.ynotlabs.cathopedia.ui.navigation

import com.ynotlabs.cathopedia.model.ContentType

sealed interface Destination {
    data object Splash : Destination
    data object LanguageStartup : Destination
    data object LanguageSettings : Destination
    data object Intro : Destination
    data object Home : Destination
    data object Explore : Destination
    data class Hub(val hubId: String) : Destination
    data class HubSection(val hubId: String, val sectionId: String) : Destination
    data class HubArticle(val hubId: String, val articleId: String) : Destination
    data class EntityList(val type: ContentType) : Destination
    data class EntityDetail(val type: ContentType, val id: String) : Destination
    data object PrayersHome : Destination
    data class PrayerDetail(val slug: String) : Destination
    data object RosaryScreen : Destination
    data object StationsScreen : Destination
    data object Vestments : Destination
    data object Search : Destination
    data object Saved : Destination
    data object Settings : Destination
    data object Appearance : Destination
    data object Notifications : Destination
    data object About : Destination
}

/**
 * The five bottom-bar destinations, in bar order (Prayers sits in the middle
 * slot per the Prayers brief). Saved lives inside Settings as its own card.
 */
enum class Tab(val label: String, val glyph: String, val destination: Destination) {
    HOME("Home", "✧", Destination.Home),
    EXPLORE("Explore", "☰", Destination.Explore),
    PRAYERS("Prayers", "🕀", Destination.PrayersHome),
    SEARCH("Search", "⌕", Destination.Search),
    SETTINGS("Settings", "⚙", Destination.Settings),
}
