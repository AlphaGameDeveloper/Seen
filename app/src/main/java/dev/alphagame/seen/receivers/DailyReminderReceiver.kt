package dev.alphagame.seen.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.SeenNotificationManager
import dev.alphagame.seen.translations.Translation
import java.util.Calendar

class DailyReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "DailyReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received daily reminder broadcast")
        val preferencesManager = PreferencesManager(context)
        if (!preferencesManager.notificationsEnabled) {
            Log.d(TAG, "Notifications disabled, skipping daily reminder")
            return
        }
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val lastNotificationDay = preferencesManager.lastBackgroundUpdateCheck / (1000 * 60 * 60 * 24)
        val currentDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        if (lastNotificationDay == currentDay) {
            Log.d(TAG, "Daily reminder already sent today")
            return
        }
        val translation = Translation.getTranslation(preferencesManager.language)
        val notificationManager = SeenNotificationManager(context)
        notificationManager.sendReminderNotification(
            title = translation.updateNotificationTitle,
            message = "Don't forget to check in on your wellbeing today! 🐾"
        )
        preferencesManager.lastBackgroundUpdateCheck = System.currentTimeMillis()
        Log.d(TAG, "Daily reminder notification sent")
        // Reschedule for next day
        dev.alphagame.seen.data.DailyReminderManager.scheduleDailyReminder(context)
    }
}
