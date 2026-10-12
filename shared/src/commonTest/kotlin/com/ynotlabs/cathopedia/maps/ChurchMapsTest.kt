package com.ynotlabs.cathopedia.maps

import io.ktor.http.Url
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ChurchMapsTest {
    @Test
    fun viewLinkSearchesTheNamedChurchInsteadOfOpeningACoordinatePin() {
        val location = assertNotNull(ChurchMapLocation.from("St. Mark's Basilica, Venice", 45.4345, 12.3397))
        val url = Url(location.googleMapsUrl(directions = false))
        assertEquals("https", url.protocol.name)
        assertEquals("www.google.com", url.host)
        assertEquals("/maps/search/", url.encodedPath)
        assertEquals("1", url.parameters["api"])
        assertEquals("St. Mark's Basilica, Venice", url.parameters["query"])
        assertNull(url.parameters["destination"])
    }

    @Test
    fun directionsLinkUsesTheNamedChurchAndLeavesTheOriginToMaps() {
        val location = assertNotNull(ChurchMapLocation.from("St. Peter's Basilica", 41.9022, 12.4539))
        val url = Url(location.googleMapsUrl(directions = true))
        assertEquals("/maps/dir/", url.encodedPath)
        assertEquals("1", url.parameters["api"])
        assertEquals("St. Peter's Basilica", url.parameters["destination"])
        assertNull(url.parameters["origin"])
        assertNull(url.parameters["travelmode"])
        assertNull(url.parameters["query"])
    }

    @Test
    fun namesAreEncodedAsOneParameterIncludingAccentsAndAmpersands() {
        val name = " Basilique Notre-Dame de la Paix & Saint-José "
        val location = assertNotNull(ChurchMapLocation.from(name, -34.5703, -59.105))
        val url = Url(location.googleMapsUrl(directions = false))
        assertEquals(name.trim(), url.parameters["query"])
        assertEquals(setOf("api", "query"), url.parameters.names())
        assertEquals(-34.5703, location.latitude)
        assertEquals(-59.105, location.longitude)
    }

    @Test
    fun invalidOrMissingLocationsDoNotProduceNavigationActions() {
        assertNull(ChurchMapLocation.from("  ", 41.0, 12.0))
        assertNull(ChurchMapLocation.from("Church", null, 12.0))
        assertNull(ChurchMapLocation.from("Church", 41.0, null))
        assertNull(ChurchMapLocation.from("Church", Double.NaN, 12.0))
        assertNull(ChurchMapLocation.from("Church", 41.0, Double.POSITIVE_INFINITY))
        assertNull(ChurchMapLocation.from("Church", 91.0, 12.0))
        assertNull(ChurchMapLocation.from("Church", 41.0, -181.0))
        assertNotNull(ChurchMapLocation.from("Church", 0.0, 0.0))
        assertNotNull(ChurchMapLocation.from("Church", -90.0, 180.0))
    }
}
