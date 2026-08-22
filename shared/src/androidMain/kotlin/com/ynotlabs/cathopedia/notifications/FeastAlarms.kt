package com.ynotlabs.cathopedia.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

/**
 * The actual [AlarmManager] scheduling — needs only a [Context], not an Activity,
 * so both [FeastNotificationScheduler] (user toggling the setting) and
 * [BootReceiver] (re-arming alarms after a reboot, which clears them) can use it.
 */
internal object FeastAlarms {

    const val CHANNEL_ID = "feast_of_the_day"
    const val EXTRA_TITLE = "title"
    const val EXTRA_BODY = "body"
    const val EXTRA_REQUEST_ID = "requestId"
    private const val PREFS_NAME = "feast_notifications"
    private const val KEY_SCHEDULED_IDS = "scheduled_ids"

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Feast of the day", NotificationManager.IMPORTANCE_DEFAULT),
        )
    }

    @OptIn(ExperimentalTime::class)
    fun scheduleAll(context: Context, notifications: List<UpcomingFeastNotification>) {
        cancelAll(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notifications.forEach { item ->
            val triggerAtMillis = item.date.atTime(item.hour, item.minute)
                .toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()

            val intent = Intent(context, FeastNotificationReceiver::class.java).apply {
                putExtra(EXTRA_TITLE, item.title)
                putExtra(EXTRA_BODY, item.body)
                putExtra(EXTRA_REQUEST_ID, item.requestId)
            }
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                PendingIntent.getBroadcast(
                    context,
                    item.requestId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ),
            )
        }

        prefs(context).edit()
            .putString(KEY_SCHEDULED_IDS, notifications.joinToString(",") { it.requestId.toString() })
            .apply()
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        scheduledRequestIds(context).forEach { requestId ->
            alarmManager.cancel(pendingIntentFor(context, requestId))
        }
        prefs(context).edit().remove(KEY_SCHEDULED_IDS).apply()
    }

    private fun pendingIntentFor(context: Context, requestId: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            requestId,
            Intent(context, FeastNotificationReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun scheduledRequestIds(context: Context): List<Int> =
        prefs(context).getString(KEY_SCHEDULED_IDS, null)
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            .orEmpty()

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
