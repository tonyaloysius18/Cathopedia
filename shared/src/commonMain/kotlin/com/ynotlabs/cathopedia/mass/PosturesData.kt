package com.ynotlabs.cathopedia.mass

/**
 * The postures of the Mass, for the dedicated Postures & Responses screen —
 * the counterpart of [MonstranceData] / [ThuribleData] / [VesselsData]. The
 * screen adds a responses block below the posture cards.
 */
data class Posture(val id: String) {
    /** `<prefix>.name` and `<prefix>.desc` in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.mass.postures.$id"
}

object PosturesData {
    const val INTRO = "screen.mass.postures.intro"
    const val RESPONSES = "screen.mass.postures.responses"

    val postures: List<Posture> = listOf(
        Posture("stand"),
        Posture("sit"),
        Posture("kneel"),
        Posture("genuflect"),
        Posture("bow"),
        Posture("sign"),
    )
}
