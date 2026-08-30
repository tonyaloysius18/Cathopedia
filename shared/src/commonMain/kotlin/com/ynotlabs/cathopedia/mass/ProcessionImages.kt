package com.ynotlabs.cathopedia.mass

import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.mass_thurifer
import com.ynotlabs.cathopedia.resources.mass_crossbearer
import com.ynotlabs.cathopedia.resources.mass_candlebearer
import com.ynotlabs.cathopedia.resources.mass_bellringer
import com.ynotlabs.cathopedia.resources.mass_bookbearer
import com.ynotlabs.cathopedia.resources.mass_priest
import org.jetbrains.compose.resources.DrawableResource

/**
 * Transparent-background artwork for each minister in the Entrance Procession
 * carousel, keyed by [Minister.number]. Each drawable needs its own explicit
 * `import com.ynotlabs.cathopedia.resources.*` line, as with
 * [com.ynotlabs.cathopedia.sacraments.SacramentImages].
 */
object ProcessionImages {
    private val images: Map<Int, DrawableResource> = mapOf(
        1 to Res.drawable.mass_thurifer,
        2 to Res.drawable.mass_crossbearer,
        3 to Res.drawable.mass_candlebearer,
        4 to Res.drawable.mass_bellringer,
        5 to Res.drawable.mass_bookbearer,
        6 to Res.drawable.mass_priest,
    )

    fun forNumber(number: Int): DrawableResource? = images[number]
}
