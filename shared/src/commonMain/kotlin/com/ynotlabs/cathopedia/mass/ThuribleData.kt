package com.ynotlabs.cathopedia.mass

/**
 * The parts of a thurible, for the dedicated Thurible anatomy screen — the
 * exact counterpart of [MonstranceData]. Descriptions are plain-language
 * summaries drawn from standard catechetical explanations of the censer.
 */
data class ThuriblePart(val id: String) {
    /** `<prefix>.name` and `<prefix>.desc` in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.mass.thurible.$id"
}

object ThuribleData {
    const val INTRO = "screen.mass.thurible.intro"

    val parts: List<ThuriblePart> = listOf(
        ThuriblePart("finial"),
        ThuriblePart("lid"),
        ThuriblePart("charcoal"),
        ThuriblePart("bowl"),
        ThuriblePart("chains"),
        ThuriblePart("ring"),
        ThuriblePart("handle"),
    )
}
