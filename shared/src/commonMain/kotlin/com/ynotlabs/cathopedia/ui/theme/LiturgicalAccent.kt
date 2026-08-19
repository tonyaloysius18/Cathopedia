package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * The one accent that's meant to follow the liturgical calendar (Today header,
 * active tab, season indicator) — everything else in the palette stays fixed.
 * Per the colour system doc this needs the calendar engine, which is a v1.0
 * feature. For the POC it's a single hardcoded token so nothing here needs a
 * refactor once `romcal` or precomputed tables land: swap the value provided
 * to [LocalLiturgicalAccent], not any of its call sites.
 */
val LocalLiturgicalAccent = compositionLocalOf { LiturgicalOrdinary }
