package com.ynotlabs.cathopedia.ui.components

import androidx.compose.runtime.Composable

/** True in development builds. Used to show content that is still awaiting review. */
@Composable
expect fun isDebugBuild(): Boolean
