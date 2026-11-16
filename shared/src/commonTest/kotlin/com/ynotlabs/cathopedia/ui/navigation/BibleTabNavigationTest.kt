package com.ynotlabs.cathopedia.ui.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class BibleTabNavigationTest {
    @Test
    fun exploreEntryAndReaderPagesHighlightBibleThenRestoreExploreOnBack() {
        val nav = AppNavController(Destination.Explore)
        nav.selectTab(Tab.EXPLORE)
        nav.navigate(Destination.BibleHome)
        assertEquals(Tab.BIBLE, nav.selectedTab)
        nav.navigate(Destination.BibleChapter("GEN", 1, 3))
        assertEquals(Tab.BIBLE, nav.selectedTab)
        nav.back()
        nav.navigate(Destination.BibleBookmarks)
        assertEquals(Tab.BIBLE, nav.selectedTab)
        nav.back()
        nav.back()
        assertEquals(Destination.Explore, nav.current)
        assertEquals(Tab.EXPLORE, nav.selectedTab)
    }

    @Test
    fun readingLinkFromHomeHighlightsBibleAndBackRestoresHome() {
        val nav = AppNavController(Destination.Home)
        nav.navigate(Destination.BibleChapter("LUK", 12, 8))
        assertEquals(Tab.BIBLE, nav.selectedTab)
        nav.back()
        assertEquals(Tab.HOME, nav.selectedTab)
    }

    @Test
    fun bibleTabReturnsToReaderRootAndClearsPreviousStack() {
        val nav = AppNavController(Destination.Home)
        nav.navigate(Destination.BibleChapter("GEN", 1))
        nav.selectTab(Tab.BIBLE)
        assertEquals(Destination.BibleHome, nav.current)
        assertEquals(Tab.BIBLE, nav.selectedTab)
        assertFalse(nav.canGoBack)
        assertEquals(listOf(Tab.HOME, Tab.EXPLORE, Tab.BIBLE, Tab.PRAYERS, Tab.SETTINGS), Tab.entries)
    }
}
