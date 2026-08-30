package com.ynotlabs.cathopedia.mass

import com.ynotlabs.cathopedia.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Mapping of sacred-vessel IDs to their high-fidelity transparent images —
 * the counterpart of [MonstranceImages] / [ThuribleImages].
 */
object VesselImages {
    private val images: Map<String, DrawableResource> = mapOf(
        "chalice" to Res.drawable.chalice,
        "paten" to Res.drawable.paten,
        "ciborium" to Res.drawable.ciborium,
        "cruets" to Res.drawable.cruets,
        "pyx" to Res.drawable.pyx,
        "luna" to Res.drawable.luna,
    )

    fun forVessel(id: String): DrawableResource? = images[id]
}
