package com.ynotlabs.cathopedia.mass

/**
 * The parts of a monstrance, for the dedicated Monstrance anatomy screen.
 * Descriptions are plain-language summaries drawn from standard catechetical
 * explanations of the vessel; kept as Kotlin strings so the screen is
 * self-contained, as with [ProcessionData] / [SacramentsData].
 */
data class MonstrancePart(val id: String) {
    /** `<prefix>.name` and `<prefix>.desc` in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.mass.monstrance.$id"
}

object MonstranceData {
    const val INTRO = "screen.mass.monstrance.intro"

    val parts: List<MonstrancePart> = listOf(
        MonstrancePart("cross"),
        MonstrancePart("sunburst"),
        MonstrancePart("ostensorium"),
        MonstrancePart("glass_front"),
        MonstrancePart("lunula"),
        MonstrancePart("host"),
        MonstrancePart("glass_back"),
        MonstrancePart("back_cover"),
        MonstrancePart("node"),
        MonstrancePart("shaft"),
        MonstrancePart("base"),
    )
}
