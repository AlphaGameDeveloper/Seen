package dev.alphagame.seen.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class VersionChecker(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "VersionChecker"
        private const val VERSION_URL = "https://seen.alphagame.dev/latestVersionTag"
    }

    suspend fun checkLatestVersion(): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Checking for latest version at $VERSION_URL")

                val request = Request.Builder()
                    .url(VERSION_URL)
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME} +damien@alphagame.dev")
                    .addHeader("Accept", "application/json")
                    .addHeader("x-Seen-Version", AppVersionInfo.VERSION_NAME)
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.w(TAG, "Version check failed with code: ${response.code}")
                    return@withContext null
                }

                val responseBody = response.body?.string()?.trim()
                if (responseBody.isNullOrEmpty()) {
                    Log.w(TAG, "Empty response from version server")
                    return@withContext null
                }

                Log.d(TAG, "Latest version response: $responseBody")
                return@withContext responseBody

            } catch (e: Exception) {
                Log.e(TAG, "Error checking for latest version", e)
                return@withContext null
            }
        }
    }

    suspend fun isUpdateAvailable(): Boolean {
        val latestVersion = checkLatestVersion() ?: return false
        val currentVersion = AppVersionInfo.VERSION_NAME

        return try {
            // Remove 'v' prefix if present
            val cleanLatestVersion = latestVersion.removePrefix("v")
            val cleanCurrentVersion = currentVersion.removePrefix("v")

            val latestVersionCode = parseVersionToCode(cleanLatestVersion)
            val currentVersionCode = parseVersionToCode(cleanCurrentVersion)

            Log.d(TAG, "Current version: $cleanCurrentVersion (code: $currentVersionCode)")
            Log.d(TAG, "Latest version: $cleanLatestVersion (code: $latestVersionCode)")

            latestVersionCode > currentVersionCode
        } catch (e: Exception) {
            Log.e(TAG, "Error comparing versions", e)
            false
        }
    }

    private fun parseVersionToCode(version: String): Int {
        return try {
            val parts = version.split(".")
            if (parts.size >= 3) {
                val major = parts[0].toInt()
                val minor = parts[1].toInt()
                val patch = parts[2].toInt()
                major * 10000 + minor * 100 + patch
            } else {
                0
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing version: $version", e)
            0
        }
    }
}
