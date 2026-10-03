package com.ynotlabs.cathopedia.mass

/**
 * One minister in the Entrance Procession of the Mass, in the order they walk.
 * Plain-language descriptions mirror the hub's procession stepper content, kept
 * as Kotlin strings so the carousel screen is self-contained (as with
 * [com.ynotlabs.cathopedia.sacraments.SacramentsData]).
 */
data class Minister(val number: Int, val id: String) {
    /** Field keys live under this prefix in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.mass.procession.$id"
}

object ProcessionData {
    val ministers: List<Minister> = listOf(
        Minister(1, "thurifer"),
        Minister(2, "crossbearer"),
        Minister(3, "candlebearer"),
        Minister(4, "bellringer"),
        Minister(5, "bookbearer"),
        Minister(6, "priest"),
    )
}
