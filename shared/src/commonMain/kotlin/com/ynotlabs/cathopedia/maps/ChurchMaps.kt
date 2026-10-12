package com.ynotlabs.cathopedia.maps

import io.ktor.http.URLBuilder

/** A stored church destination, never the user's current location. */
@ConsistentCopyVisibility
data class ChurchMapLocation private constructor(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
) {
    val coordinates: String get() = "$latitude, $longitude"

    fun googleMapsUrl(directions: Boolean): String = URLBuilder(
        if (directions) "https://www.google.com/maps/dir/" else "https://www.google.com/maps/search/",
    ).apply {
        parameters.append("api", "1")
        // A coordinate-only query opens an unnamed pin. Search the church name
        // (which includes the city where available) to open its place listing.
        parameters.append(if (directions) "destination" else "query", name)
        // Omitting origin lets Maps choose the starting point or ask the user.
    }.buildString()

    companion object {
        fun from(name: String, latitude: Double?, longitude: Double?, address: String? = null): ChurchMapLocation? {
            if (name.isBlank() || latitude == null || longitude == null ||
                !latitude.isFinite() || !longitude.isFinite() ||
                latitude !in -90.0..90.0 || longitude !in -180.0..180.0
            ) return null
            return ChurchMapLocation(name.trim(), latitude, longitude, address?.trim()?.takeIf { it.isNotEmpty() })
        }
    }
}

expect val supportsAppleChurchMaps: Boolean

/** Opens the exact coordinate as a named map item, including on iOS 18.2. */
expect fun openAppleChurchMap(location: ChurchMapLocation, directions: Boolean): Boolean
