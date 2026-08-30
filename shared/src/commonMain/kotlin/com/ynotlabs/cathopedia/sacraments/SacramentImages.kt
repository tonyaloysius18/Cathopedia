package com.ynotlabs.cathopedia.sacraments

import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.sacrament_baptism
import com.ynotlabs.cathopedia.resources.sacrament_confirmation
import com.ynotlabs.cathopedia.resources.sacrament_eucharist
import com.ynotlabs.cathopedia.resources.sacrament_penance
import com.ynotlabs.cathopedia.resources.sacrament_anointing_of_the_sick
import com.ynotlabs.cathopedia.resources.sacrament_holy_orders
import com.ynotlabs.cathopedia.resources.sacrament_matrimony
import org.jetbrains.compose.resources.DrawableResource

/**
 * Borderless, transparent-background artwork for each sacrament's carousel
 * card, keyed by [Sacrament.number]. Separate registry for the same reason as
 * [com.ynotlabs.cathopedia.stations.StationImages] — each new drawable needs
 * its own explicit `import com.ynotlabs.cathopedia.resources.*` line, since
 * referencing it via `Res.drawable.x` without the import is an "unresolved
 * reference" despite the generated accessor existing.
 */
object SacramentImages {
    private val images: Map<Int, DrawableResource> = mapOf(
        1 to Res.drawable.sacrament_baptism,
        2 to Res.drawable.sacrament_eucharist,
        3 to Res.drawable.sacrament_confirmation,
        4 to Res.drawable.sacrament_penance,
        5 to Res.drawable.sacrament_matrimony,
        6 to Res.drawable.sacrament_holy_orders,
        7 to Res.drawable.sacrament_anointing_of_the_sick,
    )

    fun forNumber(number: Int): DrawableResource? = images[number]
}
