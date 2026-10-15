package com.ynotlabs.cathopedia.data

import com.ynotlabs.cathopedia.model.*
import com.ynotlabs.cathopedia.ui.navigation.Destination
import com.ynotlabs.cathopedia.ui.navigation.destination
import kotlin.test.*

class SearchQueryTest {
    @Test fun inputIsLiteralAndSupportsUnicode() {
        assertEquals("\"canon\"* AND \"law\"*", SearchQuery.match("\"canon-law\"?"))
        assertEquals("", SearchQuery.match(" :*()\" "))
        assertEquals(listOf("திருப்பலி", "église"), SearchQuery.tokens("திருப்பலி Église"))
        assertEquals("\"name\"* AND \"or\"* AND \"body\"*", SearchQuery.match("name: OR body:*"))
    }
    @Test fun commonMisspellingsAndTypingPrefixes() {
        assertEquals(listOf("thurible"), SearchQuery.tokens("turnible"))
        assertEquals(listOf("canon", "law"), SearchQuery.tokens("cannon law"))
        assertEquals(listOf("cannon"), SearchQuery.tokens("cannon"))
        assertEquals(listOf("canon", "law"), SearchQuery.tokens("cannonlaw"))
        val dictionary = setOf("thurible", "father", "faith", "fateful")
        assertEquals(listOf("father"), SearchQuery.corrected(listOf("fathre"), dictionary))
        assertEquals(listOf("fath"), SearchQuery.corrected(listOf("fath"), dictionary))
        assertEquals(listOf("cat"), SearchQuery.corrected(listOf("cat"), setOf("hat")))
        assertEquals(listOf("bake"), SearchQuery.corrected(listOf("bake"), setOf("cake", "lake")))
    }
    @Test fun broaderResultsHaveDedicatedDestinations() {
        assertEquals(Destination.HubArticle("mass", "art.thurible.overview"),
            SearchTarget.decode("article", "mass|art.thurible.overview")?.destination())
        assertEquals(Destination.PrayerDetail("our-father"), SearchTarget.decode("prayer", "our-father")?.destination())
        assertEquals(Destination.SacramentsScreen, SearchTarget.Section("catechism", "cat.sacraments").destination())
        assertEquals(Destination.ProcessionScreen, SearchTarget.Section("mass", "mass.procession").destination())
        assertEquals(Destination.RosaryScreen, SearchTarget.Guide("rosary").destination())
        assertEquals(Destination.StationsScreen, SearchTarget.Guide("stations").destination())
        assertEquals(Destination.Vestments, SearchTarget.Guide("vestments").destination())
        assertNull(SearchTarget.decode("article", "broken-id"))
        assertNull(SearchTarget.decode("future-type", "one"))
        assertNull(SearchTarget.decode("guide", "unknown"))
    }
}
