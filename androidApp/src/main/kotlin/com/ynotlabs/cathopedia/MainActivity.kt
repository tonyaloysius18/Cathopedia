package com.ynotlabs.cathopedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ynotlabs.cathopedia.data.DatabaseDriverFactory
import com.ynotlabs.cathopedia.di.AppContainer
import com.ynotlabs.cathopedia.notifications.FeastNotificationScheduler

class MainActivity : ComponentActivity() {
    // Constructed before setContent — it registers an ActivityResultLauncher,
    // which must happen before the activity reaches STARTED.
    private val notificationScheduler = FeastNotificationScheduler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = AppContainer(DatabaseDriverFactory(applicationContext))

        setContent {
            App(container, notificationScheduler)
        }
    }
}
