package com.ynotlabs.cathopedia.ui.components

import androidx.compose.runtime.Composable

/** Whether the platform's reduced-motion accessibility setting is on — gates the Rosary canvas's bead pulse. */
@Composable
expect fun isReduceMotionEnabled(): Boolean
