package com.ynotlabs.cathopedia.ui.navigation

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/** Platform drop shadow for the floating pill bar — ported from Itinera's SlidingPillBar. */
expect fun DrawScope.drawPillShadow(
    path: Path,
    radius: Float,
    dy: Float,
    isLightMode: Boolean,
)
