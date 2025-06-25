package dev.alphagame.seen.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.alphagame.seen.MainActivity

class SeenNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "seen_reminders"
        private const val CHANNEL_NAME = "Mental Health Reminders"
        private const val CHANNEL_DESCRIPTION = "Notifications for mental health check-ins and reminders"
        private const val NOTIFICATION_ID_REMINDER = 1001
        private const val NOTIFICATION_ID_UPDATE = 1002
    }

    private val preferencesManager = PreferencesManager(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendReminderNotification(title: String, message: String) {
        if (!preferencesManager.notificationsEnabled) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon for now
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REMINDER, notification)
        } catch (e: SecurityException) {
            // Permission was revoked or notification channel is blocked
            preferencesManager.notificationsEnabled = false
        }
    }

    fun sendWeeklyReminderNotification() {
        sendReminderNotification(
            title = "Weekly Mental Health Check-in",
            message = "Take a moment to assess your mental health with the PHQ-9 questionnaire. Your wellbeing matters! 💚"
        )
    }

    fun sendUpdateNotification(title: String, message: String) {
        if (!preferencesManager.notificationsEnabled) {
            android.util.Log.d("SeenNotificationManager", "Notifications disabled, skipping update notification")
            return
        }

        android.util.Log.d("SeenNotificationManager", "Sending update notification: $title - $message")

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_UPDATE, notification)
            android.util.Log.d("SeenNotificationManager", "Update notification sent successfully")
        } catch (e: SecurityException) {
            // Permission was revoked or notification channel is blocked
            android.util.Log.e("SeenNotificationManager", "Failed to send update notification: Security exception", e)
            preferencesManager.notificationsEnabled = false
        } catch (e: Exception) {
            android.util.Log.e("SeenNotificationManager", "Failed to send update notification: Unexpected error", e)
        }
    }

    fun areNotificationsEnabled(): Boolean {
        return preferencesManager.notificationsEnabled &&
               NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}
