package com.ynotlabs.cathopedia.mass

/**
 * The sacred vessels of the altar, for the dedicated Sacred Vessels screen —
 * the counterpart of [MonstranceData] / [ThuribleData]. Unlike those two, the
 * vessels are distinct objects rather than parts of one, so the screen shows
 * no single "whole" image.
 */
data class Vessel(val id: String) {
    /** `<prefix>.name` and `<prefix>.desc` in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.mass.vessels.$id"
}

object VesselsData {
    const val INTRO = "screen.mass.vessels.intro"

    val vessels: List<Vessel> = listOf(
        Vessel("chalice"),
        Vessel("paten"),
        Vessel("ciborium"),
        Vessel("cruets"),
        Vessel("pyx"),
        Vessel("luna"),
    )
}
