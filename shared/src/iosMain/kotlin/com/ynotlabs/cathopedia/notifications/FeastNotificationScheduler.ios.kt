package com.ynotlabs.cathopedia.notifications

import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class FeastNotificationScheduler {

    actual fun requestPermissionAndSchedule(
        notifications: List<UpcomingFeastNotification>,
        onResult: (granted: Boolean) -> Unit,
    ) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options) { granted, _ ->
            if (granted) {
                scheduleAll(center, notifications)
            }
            dispatch_async(dispatch_get_main_queue()) {
                onResult(granted)
            }
        }
    }

    actual fun cancelAll() {
        UNUserNotificationCenter.currentNotificationCenter().removeAllPendingNotificationRequests()
    }

    private fun scheduleAll(center: UNUserNotificationCenter, notifications: List<UpcomingFeastNotification>) {
        center.removeAllPendingNotificationRequests()

        // iOS caps pending local notification requests at 64 app-wide.
        notifications.take(64).forEach { item ->
            val content = UNMutableNotificationContent().apply {
                setTitle(item.title)
                setBody(item.body)
                setSound(UNNotificationSound.defaultSound)
            }

            val components = NSDateComponents().apply {
                year = item.date.year.toLong()
                month = item.date.monthNumber.toLong()
                day = item.date.dayOfMonth.toLong()
                hour = item.hour.toLong()
                minute = item.minute.toLong()
            }
            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = components,
                repeats = false,
            )

            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = "${item.feastId}-${item.date}",
                content = content,
                trigger = trigger,
            )
            center.addNotificationRequest(request, withCompletionHandler = null)
        }
    }
}
