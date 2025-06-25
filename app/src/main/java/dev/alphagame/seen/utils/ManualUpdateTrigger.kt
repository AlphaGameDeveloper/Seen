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
