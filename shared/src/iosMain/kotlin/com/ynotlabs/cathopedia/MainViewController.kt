package com.ynotlabs.cathopedia

import androidx.compose.ui.window.ComposeUIViewController
import com.ynotlabs.cathopedia.data.DatabaseDriverFactory
import com.ynotlabs.cathopedia.di.AppContainer
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val container = AppContainer(DatabaseDriverFactory())
    return ComposeUIViewController { App(container) }
}
