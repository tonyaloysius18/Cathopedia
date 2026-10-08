package com.ynotlabs.cathopedia.mass

import com.ynotlabs.cathopedia.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Mapping of Thurible part IDs to their high-fidelity transparent images —
 * the counterpart of [MonstranceImages].
 */
object ThuribleImages {
    private val partImages: Map<String, DrawableResource> = mapOf(
        "finial" to Res.drawable.cross_thurible,
        "lid" to Res.drawable.lid_thurible,
        "charcoal" to Res.drawable.charcoal_plate_thurible,
        "bowl" to Res.drawable.censer_bowl_thurible,
        "chains" to Res.drawable.chains_thurible,
        "ring" to Res.drawable.suspension_ring_thurible,
        "handle" to Res.drawable.handle_thurible,
    )

    private val typeImages: Map<String, DrawableResource> = mapOf(
        "single_chain" to Res.drawable.thurible_type_single_chain,
        "three_chain" to Res.drawable.thurible_type_three_chain,
        "four_chain" to Res.drawable.thurible_type_four_chain,
        "four_chain_bells" to Res.drawable.thurible_type_four_chain_bells,
        "roman" to Res.drawable.thurible_type_roman,
        "stationary" to Res.drawable.thurible_type_stationary,
        "ceremonial" to Res.drawable.thurible_type_ceremonial,
        "botafumeiro" to Res.drawable.thurible_type_botafumeiro,
    )

    fun forPart(id: String): DrawableResource? = partImages[id]
    fun forType(id: String): DrawableResource? = typeImages[id]
}
