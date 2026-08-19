package com.ynotlabs.cathopedia.ui.navigation

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

// No native drop-shadow path API on this Skia-based target — same no-op
// fallback Itinera uses on iOS. The pill still reads fine from its fill/
// outline contrast alone; revisit with a Skia blur if it's ever missed.
actual fun DrawScope.drawPillShadow(
    path: Path,
    radius: Float,
    dy: Float,
    isLightMode: Boolean,
) {
}
