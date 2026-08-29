package com.ynotlabs.cathopedia.ui

import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.sacrament_anointing_of_the_sick
import com.ynotlabs.cathopedia.resources.sacrament_baptism
import com.ynotlabs.cathopedia.resources.sacrament_confirmation
import com.ynotlabs.cathopedia.resources.sacrament_eucharist
import com.ynotlabs.cathopedia.resources.sacrament_holy_orders
import com.ynotlabs.cathopedia.resources.sacrament_matrimony
import com.ynotlabs.cathopedia.resources.sacrament_penance
import org.jetbrains.compose.resources.DrawableResource

/** Artwork used by the Seven Sacraments fact-sheet carousel. */
object SacramentImages {
    private val images: Map<String, DrawableResource> = mapOf(
        "sac.baptism" to Res.drawable.sacrament_baptism,
        "sac.confirmation" to Res.drawable.sacrament_confirmation,
        "sac.eucharist" to Res.drawable.sacrament_eucharist,
        "sac.penance" to Res.drawable.sacrament_penance,
        "sac.anointing" to Res.drawable.sacrament_anointing_of_the_sick,
        "sac.orders" to Res.drawable.sacrament_holy_orders,
        "sac.matrimony" to Res.drawable.sacrament_matrimony,
    )

    fun forFact(id: String): DrawableResource? = images[id]
}
