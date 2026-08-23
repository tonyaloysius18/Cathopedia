package com.ynotlabs.cathopedia.stations

import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.station_01
import com.ynotlabs.cathopedia.resources.station_02
import com.ynotlabs.cathopedia.resources.station_03
import com.ynotlabs.cathopedia.resources.station_04
import com.ynotlabs.cathopedia.resources.station_05
import com.ynotlabs.cathopedia.resources.station_06
import com.ynotlabs.cathopedia.resources.station_07
import com.ynotlabs.cathopedia.resources.station_08
import com.ynotlabs.cathopedia.resources.station_09
import com.ynotlabs.cathopedia.resources.station_10
import com.ynotlabs.cathopedia.resources.station_11
import com.ynotlabs.cathopedia.resources.station_12
import com.ynotlabs.cathopedia.resources.station_13
import com.ynotlabs.cathopedia.resources.station_14
import org.jetbrains.compose.resources.DrawableResource

/**
 * Borderless, transparent-background artwork for each station's carousel
 * card, keyed by [Station.number]. Separate registry for the same reason as
 * [PopeCoatsOfArms]/[com.ynotlabs.cathopedia.ui.PrayerPortraits] — see this
 * codebase's convention on why each new drawable needs its own explicit
 * `import com.ynotlabs.cathopedia.resources.*` line above: Compose
 * Resources codegen makes the symbol available, but referencing it via
 * `Res.drawable.x` without the import is an "unresolved reference" despite
 * the generated accessor existing.
 */
object StationImages {
    private val images: Map<Int, DrawableResource> = mapOf(
        1 to Res.drawable.station_01,
        2 to Res.drawable.station_02,
        3 to Res.drawable.station_03,
        4 to Res.drawable.station_04,
        5 to Res.drawable.station_05,
        6 to Res.drawable.station_06,
        7 to Res.drawable.station_07,
        8 to Res.drawable.station_08,
        9 to Res.drawable.station_09,
        10 to Res.drawable.station_10,
        11 to Res.drawable.station_11,
        12 to Res.drawable.station_12,
        13 to Res.drawable.station_13,
        14 to Res.drawable.station_14,
    )

    fun forNumber(number: Int): DrawableResource? = images[number]
}
