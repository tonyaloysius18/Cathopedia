package com.ynotlabs.cathopedia.data

import android.os.Build

actual fun feedbackPlatformName(): String = "Android ${Build.VERSION.RELEASE}"
