package com.ynotlabs.cathopedia.stations

/**
 * One of the 14 traditional Stations of the Cross. Titles are standard
 * devotional labels (like a category name, not quoted prayer text) and live in
 * content/strings/screens.<lang>.json, so tools/translate covers them;
 * meditation/versicle/response are real prayer text sourced from a real
 * citable publication, kept in content/strings/stations.<lang>.json, which the
 * translator never touches -- see
 * content/stations/<slug>.json for the full citation backing each field.
 * English: St. Alphonsus de Liguori's "Way of the Cross" (eCatholic2000.com).
 * French: spiritualite-chretienne.com (archived) + Diocese de Saint-Etienne,
 * provisional pending AELF licence per content/README.md's convention.
 */
data class Station(val number: Int, val id: String) {
    /** Field keys live under this prefix in content/strings/screens.<lang>.json (titles) and content/strings/stations.<lang>.json (sourced prayer text). */
    val keyPrefix: String get() = "screen.stations.$id"
}

object StationsData {
    val stations: List<Station> = listOf(
        Station(1, "01-jesus-condemned-to-death"),
        Station(2, "02-jesus-carries-his-cross"),
        Station(3, "03-jesus-falls-the-first-time"),
        Station(4, "04-jesus-meets-his-mother"),
        Station(5, "05-simon-of-cyrene-helps-jesus-carry-the-cross"),
        Station(6, "06-veronica-wipes-the-face-of-jesus"),
        Station(7, "07-jesus-falls-the-second-time"),
        Station(8, "08-jesus-meets-the-women-of-jerusalem"),
        Station(9, "09-jesus-falls-the-third-time"),
        Station(10, "10-jesus-is-stripped-of-his-garments"),
        Station(11, "11-jesus-is-nailed-to-the-cross"),
        Station(12, "12-jesus-dies-on-the-cross"),
        Station(13, "13-jesus-is-taken-down-from-the-cross"),
        Station(14, "14-jesus-laid-in-the-tomb"),
    )
}
