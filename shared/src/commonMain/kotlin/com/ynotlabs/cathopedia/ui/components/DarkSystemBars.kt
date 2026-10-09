package com.ynotlabs.cathopedia.ui.components

import androidx.compose.runtime.Composable

/**
 * While composed, draws light status- and navigation-bar icons, for a screen
 * that is always dark (the Rosary praying screen) even when the phone is in
 * light mode. Restores the previous icon colors on dispose.
 */
@Composable
expect fun DarkSystemBars()
