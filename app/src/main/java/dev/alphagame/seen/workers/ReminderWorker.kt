package dev.alphagame.seen.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.SeenNotificationManager
import dev.alphagame.seen.data.VersionChecker
import dev.alphagame.seen.translations.Translation

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "ReminderWorker"
        const val WORK_NAME = "background_reminders"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting background reminder work...")

            val preferencesManager = PreferencesManager(applicationContext)

            // Check if background update checks are enabled
            if (!preferencesManager.backgroundUpdateChecksEnabled) {
                Log.d(TAG, "Background update checks disabled, skipping")
                return Result.success()
            }

            // Check if notifications are enabled
            Log.d(TAG, "Checking notification preferences: enabled=${preferencesManager.notificationsEnabled}")
            if (!preferencesManager.notificationsEnabled) {
                Log.d(TAG, "Notifications disabled, skipping update check")
                return Result.success()
            }

            val versionChecker = VersionChecker(applicationContext)
            val isUpdateAvailable = versionChecker.isUpdateAvailable()

            if (isUpdateAvailable) {
                Log.d(TAG, "Update available, checking if notification should be sent")

                // Check if we should send a notification (daily limit)
                if (!preferencesManager.shouldSendUpdateNotification()) {
                    Log.d(TAG, "Update notification was sent recently, skipping notification for today")
                    preferencesManager.lastBackgroundUpdateCheck = System.currentTimeMillis()
                    return Result.success()
                }

                // Get the latest version for the notification
                val latestVersion = versionChecker.checkLatestVersion()

                // Check if this version was already skipped by the user
                if (latestVersion != null && preferencesManager.skippedVersion == latestVersion) {
                    Log.d(TAG, "User already skipped this version ($latestVersion), not sending notification")
                    preferencesManager.lastBackgroundUpdateCheck = System.currentTimeMillis()
                    return Result.success()
                }

                Log.d(TAG, "Sending update notification for version: $latestVersion")

                // Get user's language preference for notification text
                val translation = Translation.getTranslation(preferencesManager.language)

                val notificationManager = SeenNotificationManager(applicationContext)
                val notificationMessage = if (latestVersion != null) {
                    "${translation.updateNotificationMessage} (${latestVersion.removePrefix("v")})"
                } else {
                    translation.updateNotificationMessage
                }

                Log.d(TAG, "About to send notification with title: ${translation.updateNotificationTitle}")
                Log.d(TAG, "Notification message: $notificationMessage")

                notificationManager.sendUpdateNotification(
                    title = translation.updateNotificationTitle,
                    message = notificationMessage,
                    latestVersion = latestVersion
                )

                // Update the last notification time to implement daily limit
                preferencesManager.lastUpdateNotificationTime = System.currentTimeMillis()

                Log.d(TAG, "Notification send call completed")
            } else {
                Log.d(TAG, "No update available")
            }

            // Update last check time
            preferencesManager.lastBackgroundUpdateCheck = System.currentTimeMillis()

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Update check failed", e)
            Result.retry()
        }
    }
}
