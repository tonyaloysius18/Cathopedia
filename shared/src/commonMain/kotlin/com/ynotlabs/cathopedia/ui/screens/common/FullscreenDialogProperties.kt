package com.ynotlabs.cathopedia.ui.screens.common

import androidx.compose.ui.window.DialogProperties

/** Platform dialog settings that allow lightboxes to draw behind system bars. */
internal expect fun fullScreenDialogProperties(): DialogProperties
