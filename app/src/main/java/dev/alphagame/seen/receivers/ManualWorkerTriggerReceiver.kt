package dev.alphagame.seen.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.alphagame.seen.workers.UpdateCheckWorker

class ManualWorkerTriggerReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TRIGGER_UPDATE_CHECK = "dev.alphagame.seen.TRIGGER_UPDATE_CHECK"
        private const val TAG = "ManualWorkerTrigger"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.w(TAG, "Received null context or intent")
            return
        }

        if (intent.action == ACTION_TRIGGER_UPDATE_CHECK) {
            Log.i(TAG, "Manual update check trigger received")

            // Create and enqueue a one-time work request for immediate execution
            val updateCheckWork = OneTimeWorkRequestBuilder<UpdateCheckWorker>()
                .addTag("manual_update_check")
                .build()

            WorkManager.getInstance(context).enqueue(updateCheckWork)
            Log.i(TAG, "Manual update check work enqueued")
        } else {
            Log.w(TAG, "Received unknown action: ${intent.action}")
        }
    }
}
