// Seen - Mental Health Application
//     Copyright (C) 2025  Damien Boisvert
//                   2025  Alexander Cameron
// 
//     Seen is free software: you can redistribute it and/or modify
//     it under the terms of the GNU General Public License as published by
//     the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.
// 
//     Seen is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU General Public License for more details.
// 
//     You should have received a copy of the GNU General Public License
//     along with Seen.  If not, see <https://www.gnu.org/licenses/>.

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