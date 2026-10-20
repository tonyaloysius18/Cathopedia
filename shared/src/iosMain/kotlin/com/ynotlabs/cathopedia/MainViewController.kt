package com.ynotlabs.cathopedia

import androidx.compose.ui.window.ComposeUIViewController
import com.ynotlabs.cathopedia.data.DatabaseDriverFactory
import com.ynotlabs.cathopedia.data.RemoteCache
import com.ynotlabs.cathopedia.di.AppContainer
import com.ynotlabs.cathopedia.notifications.FeastNotificationScheduler
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val container = AppContainer(DatabaseDriverFactory())
    RemoteCache.directory = ((NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true)
        .firstOrNull() as? String) ?: NSTemporaryDirectory()) + "/remote"
    val notificationScheduler = FeastNotificationScheduler()
    return ComposeUIViewController { App(container, notificationScheduler) }
}
