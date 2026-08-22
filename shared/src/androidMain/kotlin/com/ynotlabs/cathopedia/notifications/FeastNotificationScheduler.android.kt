package com.ynotlabs.cathopedia.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

actual class FeastNotificationScheduler(private val activity: ComponentActivity) {

    private var pendingResult: ((Boolean) -> Unit)? = null

    // Must be registered before the activity reaches STARTED, so this runs as an
    // eagerly-initialized property from MainActivity.onCreate, before setContent.
    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        pendingResult?.invoke(granted)
        pendingResult = null
    }

    actual fun requestPermissionAndSchedule(
        notifications: List<UpcomingFeastNotification>,
        onResult: (granted: Boolean) -> Unit,
    ) {
        FeastAlarms.ensureChannel(activity)

        val alreadyGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) {
            FeastAlarms.scheduleAll(activity, notifications)
            onResult(true)
            return
        }

        pendingResult = { granted ->
            if (granted) FeastAlarms.scheduleAll(activity, notifications)
            onResult(granted)
        }
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    actual fun cancelAll() {
        FeastAlarms.cancelAll(activity)
    }
}
