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

package dev.alphagame.seen.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import dev.alphagame.seen.receivers.ManualWorkerTriggerReceiver

/**
 * Utility class for manually triggering background update checks
 */
object ManualUpdateTrigger {
    
    private const val TAG = "ManualUpdateTrigger"
    
    /**
     * Triggers a manual update check by sending a broadcast intent
     * @param context The application context
     */
    fun triggerUpdateCheck(context: Context) {
        try {
            val intent = Intent(ManualWorkerTriggerReceiver.ACTION_TRIGGER_UPDATE_CHECK)
            intent.setPackage(context.packageName) // Ensure it only goes to our app
            context.sendBroadcast(intent)
            Log.i(TAG, "Manual update check broadcast sent")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send manual update check broadcast", e)
        }
    }
    
    /**
     * Triggers a manual update check using ADB command
     * This is useful for testing and debugging
     * 
     * Command to run from terminal:
     * adb shell am broadcast -a dev.alphagame.seen.TRIGGER_UPDATE_CHECK -n dev.alphagame.seen/.receivers.ManualWorkerTriggerReceiver
     */
    fun getAdbCommand(): String {
        return "adb shell am broadcast -a dev.alphagame.seen.TRIGGER_UPDATE_CHECK -n dev.alphagame.seen/.receivers.ManualWorkerTriggerReceiver"
    }
}
