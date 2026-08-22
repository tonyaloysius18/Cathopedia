package com.ynotlabs.cathopedia.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.data.DatabaseDriverFactory
import com.ynotlabs.cathopedia.data.PreferenceKeys
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * `AlarmManager` alarms are cleared on reboot (and on an app update), so this
 * re-arms them from scratch — same computation [FeastNotificationScheduler] runs
 * whenever the app opens with the feature on, just triggered by the OS instead of
 * an app launch. Skips silently if the feature was never turned on; the actual
 * notification post at fire time re-checks the OS permission regardless, so
 * scheduling here is harmless even if it's since been revoked.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }

        val pendingResult = goAsync()
        val appContext = context.applicationContext

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val repository = CathopediaRepository(
                    CathopediaDatabase(DatabaseDriverFactory(appContext).createDriver()),
                )
                if (repository.getPreference(PreferenceKeys.NOTIFICATIONS_ENABLED) != "true") return@launch

                val language = repository.getPreference(PreferenceKeys.LANGUAGE) ?: "en"
                val items = repository.upcomingFeasts(language).map { (date, summary) ->
                    UpcomingFeastNotification(
                        feastId = summary.id,
                        date = date,
                        title = summary.name,
                        body = summary.summary,
                    )
                }

                FeastAlarms.ensureChannel(appContext)
                FeastAlarms.scheduleAll(appContext, items)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
