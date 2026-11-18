package com.ynotlabs.cathopedia.profile

import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.i18n.ScreenText
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import com.ynotlabs.cathopedia.notifications.UpcomingFeastNotification
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus

/**
 * The profile's greetings for the coming year, as local notifications at 8:00,
 * scheduled with the feast notifications (when those are switched on). Built
 * on the phone from the local profile; nothing is sent anywhere.
 */
suspend fun profileGreetingNotifications(repository: CathopediaRepository, language: String): List<UpcomingFeastNotification> {
    val profile = ProfileStore(repository).load()
    if (profile.isEmpty) return emptyList()
    val text = ScreenText(repository.resolveHubStringsWithPrefix("screen.profile.", language))
    val today = LiturgicalCalendar.today()
    return (0 until 366).flatMap { offset ->
        val day = today.plus(DatePeriod(days = offset))
        ProfileGreetings.occasionsOn(profile, day).map { occasion ->
            UpcomingFeastNotification(
                feastId = "profile-${occasion.kind.key}",
                date = day,
                title = greetingTitle(occasion.kind, profile, text::get),
                body = greetingMessage(occasion.kind, profile, text::get),
            )
        }
    }
}
