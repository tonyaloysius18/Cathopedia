package com.ynotlabs.cathopedia.ui.screens

import com.ynotlabs.cathopedia.rosary.rosaryLayout
import com.ynotlabs.cathopedia.ui.components.DiagramCircleShape
import com.ynotlabs.cathopedia.ui.components.DiagramHotspot

/**
 * Tap targets over rosary_marian.png (see InteractiveDiagram.kt), one per prayer step —
 * id is the bead's prayer-order index (0 = crucifix … 61 = last Hail Mary), matching
 * [com.ynotlabs.cathopedia.rosary.RosaryBead.index] so a step's beadIndex still resolves.
 *
 * Derived from the shared [rosaryLayout] rather than a second hand-placed table, so the
 * flat-image map and the sprite-composed landing (A4) can never drift apart.
 */
internal val rosaryBeadHotspots: List<DiagramHotspot> = rosaryLayout.map { bead ->
    DiagramHotspot(
        id = bead.index.toString(),
        label = "",
        shape = DiagramCircleShape(cx = bead.x, cy = bead.y, r = bead.radius),
    )
}
