package com.ynotlabs.cathopedia.mass

import com.ynotlabs.cathopedia.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Mapping of altar furnishing / cloth IDs to their transparent images — the
 * counterpart of [MonstranceImages] / [ThuribleImages] / [VesselImages] /
 * [PostureImages]. The five-crosses marking image is referenced directly by
 * the screen.
 */
object AltarImages {
    private val images: Map<String, DrawableResource> = mapOf(
        "crucifix" to Res.drawable.altar_crucifix,
        "candles" to Res.drawable.altar_candles,
        "flowers" to Res.drawable.altar_flowers,
        "frontal" to Res.drawable.altar_frontal,
        "altarcloth" to Res.drawable.altar_cloth,
        "corporal" to Res.drawable.corporal,
        "purificator" to Res.drawable.purificator,
        "pall" to Res.drawable.pall,
    )

    fun forItem(id: String): DrawableResource? = images[id]
}
