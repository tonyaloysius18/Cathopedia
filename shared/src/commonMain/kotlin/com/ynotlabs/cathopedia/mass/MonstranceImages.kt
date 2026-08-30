package com.ynotlabs.cathopedia.mass

import com.ynotlabs.cathopedia.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Mapping of Monstrance part IDs to their high-fidelity transparent images.
 */
object MonstranceImages {
    private val images: Map<String, DrawableResource> = mapOf(
        "cross" to Res.drawable.mass_monstrance_part_cross,
        "sunburst" to Res.drawable.mass_monstrance_part_sunburst,
        "ostensorium" to Res.drawable.mass_monstrance_part_window,
        "glass_front" to Res.drawable.mass_monstrance_part_glass_front,
        "lunula" to Res.drawable.mass_monstrance_part_luna,
        "host" to Res.drawable.mass_monstrance_part_host,
        "glass_back" to Res.drawable.mass_monstrance_part_glass_back,
        "back_cover" to Res.drawable.mass_monstrance_part_back_cover,
        "node" to Res.drawable.mass_monstrance_part_node,
        "shaft" to Res.drawable.mass_monstrance_part_shaft,
        "base" to Res.drawable.mass_monstrance_part_base,
    )

    fun forPart(id: String): DrawableResource? = images[id]
}
