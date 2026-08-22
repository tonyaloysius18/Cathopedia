package com.ynotlabs.cathopedia.ui.components

import androidx.compose.runtime.Composable

/**
 * While composed with [enabled] true, prevents the device from sleeping —
 * for reading a prayer at length without the screen dimming mid-decade.
 * Always clears the flag on dispose so a forgotten toggle can't leave the
 * screen pinned on after the user navigates away.
 */
@Composable
expect fun KeepScreenOn(enabled: Boolean)
