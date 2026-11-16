package com.ynotlabs.cathopedia.bible

import androidx.compose.runtime.staticCompositionLocalOf

/** Opens a chapter of the Bible reader from anywhere (e.g. under a Mass reading); null where unavailable. */
val LocalOpenBible = staticCompositionLocalOf<((BibleRef) -> Unit)?> { null }
