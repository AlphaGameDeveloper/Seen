package dev.alphagame.seen

import android.app.Application
import android.util.Log
import dev.alphagame.seen.data.DatabaseEncryption

/**
 * Custom Application class to handle encrypted database lifecycle.
 * Ensures temporary database files are cleaned up when the app terminates.
 */
class SeenApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("SeenApp", "Application starting - encrypted database system initialized")
    }

    override fun onTerminate() {
        super.onTerminate()
        // Clean up temporary database files when app terminates
        try {
            DatabaseEncryption.cleanupTempDb(this)
            Log.d("SeenApp", "Cleaned up temporary database files")
        } catch (e: Exception) {
            Log.w("SeenApp", "Failed to cleanup temporary database files: ${e.message}")
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // Also clean up on low memory situations (optional)
        try {
            DatabaseEncryption.cleanupTempDb(this)
            Log.d("SeenApp", "Cleaned up temporary database files due to low memory")
        } catch (e: Exception) {
            Log.w("SeenApp", "Failed to cleanup temporary database files on low memory: ${e.message}")
        }
    }
}
