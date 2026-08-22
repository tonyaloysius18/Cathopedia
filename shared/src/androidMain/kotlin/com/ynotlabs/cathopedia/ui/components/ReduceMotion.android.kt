package com.ynotlabs.cathopedia.ui.components

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/** Android has no single "reduce motion" toggle — the animator duration scale (Settings > Developer options,
 * or the system-wide "Remove animations" accessibility option on newer OEM skins) is the closest equivalent. */
@Composable
actual fun isReduceMotionEnabled(): Boolean {
    val context = LocalContext.current
    val scale = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    return scale == 0f
}
