package com.ynotlabs.cathopedia.notifications

import kotlinx.datetime.LocalDate

/**
 * One scheduled local notification for a feast that falls on [date]. [title]/[body]
 * are already resolved to the user's language at schedule time — local notification
 * APIs on both platforms need their content up front, not fetched when they fire.
 */
data class UpcomingFeastNotification(
    val feastId: String,
    val date: LocalDate,
    val title: String,
    val body: String,
    val hour: Int = 8,
    val minute: Int = 0,
) {
    /** Stable per (feastId, date) so re-scheduling replaces rather than duplicates. */
    val requestId: Int
        get() = ((feastId.hashCode() * 31 + date.toEpochDays()) and 0x7fffffffL).toInt()
}
