package dev.alphagame.seen.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import dev.alphagame.seen.data.PreferencesManager

class UpdateNotificationActionReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "UpdateNotificationActionReceiver"
        const val ACTION_DONT_REMIND = "dev.alphagame.seen.action.DONT_REMIND"
        const val EXTRA_VERSION = "extra_version"
        private const val NOTIFICATION_ID_UPDATE = 1002
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DONT_REMIND -> {
                val version = intent.getStringExtra(EXTRA_VERSION)
                Log.d(TAG, "User chose 'Don't remind me' for version: $version")
                
                if (version != null) {
                    val preferencesManager = PreferencesManager(context)
                    preferencesManager.skippedVersion = version
                    Log.d(TAG, "Marked version $version as skipped")
                    
                    // Dismiss the notification
                    try {
                        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_UPDATE)
                        Log.d(TAG, "Update notification dismissed")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to dismiss notification", e)
                    }
                } else {
                    Log.w(TAG, "No version provided in 'Don't remind me' action")
                }
            }
        }
    }
}