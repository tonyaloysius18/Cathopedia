package com.ynotlabs.cathopedia

import androidx.compose.ui.window.ComposeUIViewController
import com.ynotlabs.cathopedia.data.DatabaseDriverFactory
import com.ynotlabs.cathopedia.di.AppContainer
import com.ynotlabs.cathopedia.notifications.FeastNotificationScheduler
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val container = AppContainer(DatabaseDriverFactory())
    val notificationScheduler = FeastNotificationScheduler()
    return ComposeUIViewController { App(container, notificationScheduler) }
}
