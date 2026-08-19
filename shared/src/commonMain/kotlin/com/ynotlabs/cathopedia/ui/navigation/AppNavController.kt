package com.ynotlabs.cathopedia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * A deliberately minimal back-stack, plus the notion of "which tab am I in"
 * that a flat stack doesn't otherwise carry — needed so the bottom bar can
 * keep the right tab highlighted while a detail page is pushed on top of it.
 */
class AppNavController(startDestination: Destination) {
    private val backStack = mutableStateListOf(startDestination)
    var selectedTab by mutableStateOf(Tab.HOME)
        private set

    val current: Destination get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    fun navigate(destination: Destination) {
        backStack.add(destination)
    }

    fun back() {
        if (canGoBack) backStack.removeAt(backStack.lastIndex)
    }

    /** Tapping a tab always returns to that tab's root, dropping any pushed detail pages. */
    fun selectTab(tab: Tab) {
        selectedTab = tab
        backStack.clear()
        backStack.add(tab.destination)
    }

    /** Replaces the whole stack — used for the first-run → Home handoff. */
    fun resetTo(destination: Destination) {
        backStack.clear()
        backStack.add(destination)
    }
}

@Composable
fun rememberAppNavController(startDestination: Destination): AppNavController =
    remember { AppNavController(startDestination) }
