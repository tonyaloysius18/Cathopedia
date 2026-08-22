package com.ynotlabs.cathopedia.ui.navigation

import com.ynotlabs.cathopedia.model.ContentType

sealed interface Destination {
    data object Splash : Destination
    data object LanguageStartup : Destination
    data object LanguageSettings : Destination
    data object Intro : Destination
    data object Home : Destination
    data object Explore : Destination
    data class EntityList(val type: ContentType) : Destination
    data class EntityDetail(val type: ContentType, val id: String) : Destination
    data object Search : Destination
    data object Saved : Destination
    data object Settings : Destination
    data object Appearance : Destination
    data object Notifications : Destination
    data object About : Destination
}

/** The four bottom-bar destinations. Saved now lives inside Settings as its own card. */
enum class Tab(val label: String, val glyph: String, val destination: Destination) {
    HOME("Home", "✧", Destination.Home),
    EXPLORE("Explore", "☰", Destination.Explore),
    SEARCH("Search", "⌕", Destination.Search),
    SETTINGS("Settings", "⚙", Destination.Settings),
}
