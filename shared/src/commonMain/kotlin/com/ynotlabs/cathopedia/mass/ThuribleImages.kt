package com.ynotlabs.cathopedia.mass

import com.ynotlabs.cathopedia.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Mapping of Thurible part IDs to their high-fidelity transparent images —
 * the counterpart of [MonstranceImages].
 */
object ThuribleImages {
    private val images: Map<String, DrawableResource> = mapOf(
        "finial" to Res.drawable.cross_thurible,
        "lid" to Res.drawable.lid_thurible,
        "charcoal" to Res.drawable.charcoal_plate_thurible,
        "bowl" to Res.drawable.censer_bowl_thurible,
        "chains" to Res.drawable.chains_thurible,
        "ring" to Res.drawable.suspension_ring_thurible,
        "handle" to Res.drawable.handle_thurible,
    )

    fun forPart(id: String): DrawableResource? = images[id]
}
