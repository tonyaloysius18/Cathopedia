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

data class ThuribleType(val id: String) {
    /** `<prefix>.name` and `<prefix>.desc` in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.mass.thurible.type.$id"
}

object ThuribleData {
    const val INTRO = "screen.mass.thurible.intro"
    const val TYPES_TITLE = "screen.mass.thurible.types.title"
    const val TYPES_INDICATOR = "screen.mass.thurible.types.indicator"
    const val STRUCTURE_LABEL = "screen.mass.thurible.types.structure"
    const val SETTING_LABEL = "screen.mass.thurible.types.setting"

    val types: List<ThuribleType> = listOf(
        ThuribleType("single_chain"),
        ThuribleType("three_chain"),
        ThuribleType("four_chain"),
        ThuribleType("four_chain_bells"),
        ThuribleType("roman"),
        ThuribleType("stationary"),
        ThuribleType("ceremonial"),
        ThuribleType("botafumeiro"),
    )

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
