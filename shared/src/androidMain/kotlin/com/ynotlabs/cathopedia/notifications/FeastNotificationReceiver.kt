package com.ynotlabs.cathopedia.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ynotlabs.cathopedia.shared.R

/** Fired by the [android.app.AlarmManager] alarms [FeastAlarms] schedules ahead of time. */
class FeastNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(FeastAlarms.EXTRA_TITLE) ?: return
        val body = intent.getStringExtra(FeastAlarms.EXTRA_BODY).orEmpty()
        val requestId = intent.getIntExtra(FeastAlarms.EXTRA_REQUEST_ID, 0)

        val canPost = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!canPost) return

        val notification = NotificationCompat.Builder(context, FeastAlarms.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(requestId, notification)
    }
}
