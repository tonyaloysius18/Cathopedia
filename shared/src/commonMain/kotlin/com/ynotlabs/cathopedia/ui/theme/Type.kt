package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

// Platform default font family for now; a serif/sans pairing matching the
// roadmap's editorial tone (Fraunces + Inter) can slot in once resources are set up.
val CathopediaTypography = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontSize = 13.sp, lineHeight = 16.sp),
)
