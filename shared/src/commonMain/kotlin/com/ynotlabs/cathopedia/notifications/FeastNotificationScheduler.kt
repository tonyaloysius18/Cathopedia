package com.ynotlabs.cathopedia.notifications

/**
 * Schedules local notifications for upcoming feasts, one per date — there's no
 * background daily job on either platform; instead the whole known feast calendar
 * is scheduled ahead of time (see [com.ynotlabs.cathopedia.data.CathopediaRepository.upcomingFeastNotifications])
 * and simply re-scheduled (replacing any previous batch) whenever the app opens
 * with the feature enabled, or the user first turns it on.
 */
expect class FeastNotificationScheduler {
    /**
     * Requests the OS notification permission if it hasn't been decided yet, then
     * schedules [notifications] (replacing anything previously scheduled) if
     * granted. [onResult] reports whether the permission ended up granted —
     * on Android below API 33, and on a re-call after permission was already
     * granted, this resolves immediately with no system prompt.
     */
    fun requestPermissionAndSchedule(notifications: List<UpcomingFeastNotification>, onResult: (granted: Boolean) -> Unit)

    /** Cancels every notification this app has scheduled through this class. */
    fun cancelAll()
}
