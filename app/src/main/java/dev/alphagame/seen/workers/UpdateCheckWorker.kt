package dev.alphagame.seen.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.SeenNotificationManager
import dev.alphagame.seen.data.VersionChecker
import dev.alphagame.seen.translations.Translation

class UpdateCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "UpdateCheckWorker"
        const val WORK_NAME = "background_update_check"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting background update check...")

            val preferencesManager = PreferencesManager(applicationContext)

            // Check if background update checks are enabled
            if (!preferencesManager.backgroundUpdateChecksEnabled) {
                Log.d(TAG, "Background update checks disabled, skipping")
                return Result.success()
            }

            // Check if notifications are enabled
            if (!preferencesManager.notificationsEnabled) {
                Log.d(TAG, "Notifications disabled, skipping update check")
                return Result.success()
            }

            val versionChecker = VersionChecker(applicationContext)
            val isUpdateAvailable = versionChecker.isUpdateAvailable()

            if (isUpdateAvailable) {
                Log.d(TAG, "Update available, sending notification")

                // Get the latest version for the notification
                val latestVersion = versionChecker.checkLatestVersion()

                // Get user's language preference for notification text
                val translation = Translation.getTranslation(preferencesManager.language)

                val notificationManager = SeenNotificationManager(applicationContext)
                val notificationMessage = if (latestVersion != null) {
                    "${translation.updateNotificationMessage} (${latestVersion.removePrefix("v")})"
                } else {
                    translation.updateNotificationMessage
                }

                notificationManager.sendUpdateNotification(
                    title = translation.updateNotificationTitle,
                    message = notificationMessage
                )
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
