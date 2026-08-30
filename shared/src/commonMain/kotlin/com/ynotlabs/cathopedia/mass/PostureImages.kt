package com.ynotlabs.cathopedia.mass

import com.ynotlabs.cathopedia.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Mapping of Mass-posture IDs to their transparent images — the counterpart of
 * [MonstranceImages] / [ThuribleImages] / [VesselImages].
 */
object PostureImages {
    private val images: Map<String, DrawableResource> = mapOf(
        "stand" to Res.drawable.posture_standing,
        "sit" to Res.drawable.posture_sitting,
        "kneel" to Res.drawable.posture_kneeling,
        "genuflect" to Res.drawable.posture_genuflection,
        "bow" to Res.drawable.posture_bowing,
        "sign" to Res.drawable.posture_sign_of_cross,
    )

    fun forPosture(id: String): DrawableResource? = images[id]
}
