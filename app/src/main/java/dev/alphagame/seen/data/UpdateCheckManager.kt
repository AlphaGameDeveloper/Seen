package dev.alphagame.seen.data

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dev.alphagame.seen.workers.UpdateCheckWorker
import java.util.concurrent.TimeUnit

class UpdateCheckManager(private val context: Context) {

    companion object {
        private const val TAG = "UpdateCheckManager"
        private const val UPDATE_CHECK_INTERVAL = 15L // minutes (Android minimum for periodic work)
    }

    fun startBackgroundUpdateChecks() {
        Log.d(TAG, "Starting background update checks")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updateCheckRequest = PeriodicWorkRequestBuilder<UpdateCheckWorker>(
            UPDATE_CHECK_INTERVAL, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UpdateCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            updateCheckRequest
        )

        Log.d(TAG, "Background update checks scheduled every $UPDATE_CHECK_INTERVAL minutes")
    }

    fun stopBackgroundUpdateChecks() {
        Log.d(TAG, "Stopping background update checks")
        WorkManager.getInstance(context).cancelUniqueWork(UpdateCheckWorker.WORK_NAME)
    }

    fun isBackgroundUpdateCheckRunning(): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(UpdateCheckWorker.WORK_NAME)
            .get()

        return workInfos.any { workInfo ->
            workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
        }
    }
}
