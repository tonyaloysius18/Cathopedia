package com.ynotlabs.cathopedia.ui.navigation

/**
 * Maps a `cathopedia://` URI onto a [Destination]. Pure and platform-agnostic —
 * wiring an actual incoming URI into this (Android intent-filter + onNewIntent,
 * iOS Info.plist URL type + onOpenURL) is deliberately left for when something
 * outside the app actually needs to link in (e.g. encyclopedia entries linking
 * to a prayer), matching how no other [Destination] has platform link handling
 * today either.
 */
fun parseDeepLink(uri: String): Destination? {
    val path = uri.substringAfter("cathopedia://", missingDelimiterValue = "")
    if (path.isEmpty()) return null

    val segments = path.trim('/').split('/')
    return when (segments.getOrNull(0)) {
        "prayer" -> segments.getOrNull(1)?.takeIf { it.isNotBlank() }?.let { Destination.PrayerDetail(it) }
        "rosary" -> Destination.RosaryScreen
        else -> null
    }
}
