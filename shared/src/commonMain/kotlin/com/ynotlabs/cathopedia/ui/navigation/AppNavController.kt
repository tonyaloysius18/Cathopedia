package com.ynotlabs.cathopedia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** Which way the stack last moved, so the router can pick a matching transition. */
enum class NavDirection {
    /** A page was pushed on top — it should come in from the right. */
    PUSH,

    /** A page was popped — the one underneath comes back from the left. */
    POP,

    /** A tab change or a stack reset: no direction, so no slide. */
    NONE,
}

/**
 * A deliberately minimal back-stack, plus the notion of "which tab am I in"
 * that a flat stack doesn't otherwise carry — needed so the bottom bar can
 * keep the right tab highlighted while a detail page is pushed on top of it.
 */
class AppNavController(startDestination: Destination) {
    private val backStack = mutableStateListOf(startDestination)
    var selectedTab by mutableStateOf(Tab.HOME)
        private set

    /** Set by every stack change; read by the router's transitionSpec. */
    var lastDirection by mutableStateOf(NavDirection.NONE)
        private set

    val current: Destination get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    fun navigate(destination: Destination) {
        lastDirection = NavDirection.PUSH
        backStack.add(destination)
    }

    fun back() {
        if (!canGoBack) return
        lastDirection = NavDirection.POP
        backStack.removeAt(backStack.lastIndex)
    }

    /** Tapping a tab always returns to that tab's root, dropping any pushed detail pages. */
    fun selectTab(tab: Tab) {
        lastDirection = NavDirection.NONE
        selectedTab = tab
        backStack.clear()
        backStack.add(tab.destination)
    }

    /** Replaces the whole stack — used for the first-run → Home handoff. */
    fun resetTo(destination: Destination) {
        lastDirection = NavDirection.NONE
        backStack.clear()
        backStack.add(destination)
    }
}

@Composable
fun rememberAppNavController(startDestination: Destination): AppNavController =
    remember { AppNavController(startDestination) }
