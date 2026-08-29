package com.ynotlabs.cathopedia.data

import platform.UIKit.UIDevice

actual fun feedbackPlatformName(): String {
    val device = UIDevice.currentDevice
    return "${device.systemName} ${device.systemVersion}"
}
