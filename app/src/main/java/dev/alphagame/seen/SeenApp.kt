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

package dev.alphagame.seen

import android.app.Application
import android.util.Log
import dev.alphagame.seen.encryption.DatabaseEncryptionAES

/**
 * Custom Application class to handle encrypted database lifecycle.
 * Ensures temporary database files are cleaned up when the app terminates.
 */
class SeenApp : Application() {

    override fun onCreate() {
        super.onCreate()
            Log.d("SeenApp", "Application starting - encrypted database system initialized")
            // Schedule daily reminder notification at 7:00 am
            dev.alphagame.seen.data.DailyReminderManager.scheduleDailyReminder(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        // Clean up temporary database files when app terminates
        try {
            DatabaseEncryptionAES.cleanupTempDb(this)
            Log.d("SeenApp", "Cleaned up temporary database files")
        } catch (e: Exception) {
            Log.w("SeenApp", "Failed to cleanup temporary database files: ${e.message}")
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // Also clean up on low memory situations (optional)
        try {
            DatabaseEncryptionAES.cleanupTempDb(this)
            Log.d("SeenApp", "Cleaned up temporary database files due to low memory")
        } catch (e: Exception) {
            Log.w("SeenApp", "Failed to cleanup temporary database files on low memory: ${e.message}")
        }
    }
}
