package com.ynotlabs.cathopedia.mass

/**
 * The altar — its furnishings, its cloths, and its markings — for the dedicated
 * Altar screen. Counterpart of [MonstranceData] / [ThuribleData] /
 * [VesselsData] / [PosturesData], but grouped into decoration and cloths, with
 * a markings block below.
 */
data class AltarItem(val id: String) {
    /** `<prefix>.name` and `<prefix>.desc` in content/strings/screens.<lang>.json. */
    val keyPrefix: String get() = "screen.mass.altar.$id"
}

object AltarData {
    const val INTRO = "screen.mass.altar.intro"
    const val MARKINGS = "screen.mass.altar.markings"
    const val KISS_TITLE = "screen.mass.altar.kiss_title"
    const val KISS_BODY = "screen.mass.altar.kiss_body"

    val decoration: List<AltarItem> = listOf(
        AltarItem("crucifix"),
        AltarItem("candles"),
        AltarItem("flowers"),
        AltarItem("frontal"),
    )
    val cloths: List<AltarItem> = listOf(
        AltarItem("altarcloth"),
        AltarItem("corporal"),
        AltarItem("purificator"),
        AltarItem("pall"),
    )
}
