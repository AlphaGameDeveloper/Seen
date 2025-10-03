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

package dev.alphagame.seen.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class UpdateChecker(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    companion object {
        private const val TAG = "UpdateChecker"
    }

    suspend fun checkForUpdates(): UpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val UPDATE_URL = "https://seen.alphagame.dev/update-check?currentVersion=${AppVersionInfo.VERSION_NAME}&currentVersionCode=${AppVersionInfo.VERSION_CODE}&gitCommit=${AppVersionInfo.GIT_COMMIT}&gitBranch=${AppVersionInfo.GIT_BRANCH}"
                Log.d(TAG, "Checking for updates at $UPDATE_URL")

                val request = Request.Builder()
                    .url(UPDATE_URL)
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME}")
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.w(TAG, "Update check failed with code: ${response.code}")
                    return@withContext null
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    Log.w(TAG, "Empty response from update server")
                    return@withContext null
                }

                Log.d(TAG, "Update response: $responseBody")

                val updateResponse = try {
                    gson.fromJson(responseBody, UpdateResponse::class.java)
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "Failed to parse update response JSON", e)
                    return@withContext null
                }

                val currentVersion = AppVersionInfo.VERSION_NAME
                val currentVersionCode = AppVersionInfo.VERSION_CODE

                Log.d(TAG, "Current version: $currentVersion (code: $currentVersionCode)")
                Log.d(TAG, "Latest version: ${updateResponse.version} (code: ${updateResponse.versionCode})")

                val isUpdateAvailable = updateResponse.versionCode > currentVersionCode

                return@withContext UpdateInfo(
                    latestVersion = updateResponse.version,
                    currentVersion = currentVersion,
                    isUpdateAvailable = isUpdateAvailable,
                    downloadUrl = updateResponse.downloadUrl,
                    releaseNotes = updateResponse.releaseNotes,
                    isForceUpdate = updateResponse.forceUpdate,
                    minimumRequiredVersion = updateResponse.minimumRequiredVersion
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error checking for updates", e)
                null
            }
        }
    }

    suspend fun checkForUpdatesWithDialog(context: Context) {
        val updateInfo = checkForUpdates()

        withContext(Dispatchers.Main) {
            when {
                updateInfo == null -> {
                    UpdateDialogHelper.showUpdateCheckErrorDialog(context)
                }
                updateInfo.isUpdateAvailable -> {
                    UpdateDialogHelper.showUpdateAvailableDialog(context, updateInfo)
                }
                else -> {
                    UpdateDialogHelper.showNoUpdateDialog(context)
                }
            }
        }
    }

    // New method for Compose UI - returns result instead of showing dialogs
    suspend fun checkForUpdatesForCompose(): UpdateCheckResult {
        return withContext(Dispatchers.IO) {
            try {
                val UPDATE_URL = "https://seen.alphagame.dev/update-check?currentVersion=${AppVersionInfo.VERSION_NAME}&currentVersionCode=${AppVersionInfo.VERSION_CODE}&gitCommit=${AppVersionInfo.GIT_COMMIT}&gitBranch=${AppVersionInfo.GIT_BRANCH}"
                Log.d(TAG, "Checking for updates at $UPDATE_URL")

                val request = Request.Builder()
                    .url(UPDATE_URL)
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME}")
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.w(TAG, "Update check failed with code: ${response.code}")
                    return@withContext UpdateCheckResult.Error
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    Log.w(TAG, "Empty response from update server")
                    return@withContext UpdateCheckResult.Error
                }

                Log.d(TAG, "Update response: $responseBody")

                val updateResponse = try {
                    gson.fromJson(responseBody, UpdateResponse::class.java)
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "Failed to parse update response JSON", e)
                    return@withContext UpdateCheckResult.Error
                }

                val currentVersion = AppVersionInfo.VERSION_NAME
                val currentVersionCode = AppVersionInfo.VERSION_CODE

                Log.d(TAG, "Current version: $currentVersion (code: $currentVersionCode)")
                Log.d(TAG, "Latest version: ${updateResponse.version} (code: ${updateResponse.versionCode})")

                val isUpdateAvailable = updateResponse.versionCode > currentVersionCode

                val updateInfo = UpdateInfo(
                    latestVersion = updateResponse.version,
                    currentVersion = currentVersion,
                    isUpdateAvailable = isUpdateAvailable,
                    downloadUrl = updateResponse.downloadUrl,
                    releaseNotes = updateResponse.releaseNotes,
                    isForceUpdate = updateResponse.forceUpdate,
                    minimumRequiredVersion = updateResponse.minimumRequiredVersion
                )

                when {
                    updateInfo.isUpdateAvailable -> UpdateCheckResult.UpdateAvailable(updateInfo)
                    else -> UpdateCheckResult.NoUpdate
                }

            } catch (e: java.net.UnknownHostException) {
                Log.e(TAG, "Network error - no internet connection", e)
                UpdateCheckResult.NetworkError
            } catch (e: java.net.ConnectException) {
                Log.e(TAG, "Network error - connection failed", e)
                UpdateCheckResult.NetworkError
            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "Network error - timeout", e)
                UpdateCheckResult.NetworkError
            } catch (e: Exception) {
                Log.e(TAG, "Error checking for updates", e)
                UpdateCheckResult.Error
            }
        }
    }

    sealed class UpdateCheckResult {
        object NoUpdate : UpdateCheckResult()
        object Error : UpdateCheckResult()
        object NetworkError : UpdateCheckResult()
        data class UpdateAvailable(val updateInfo: UpdateInfo) : UpdateCheckResult()
    }

    fun shouldForceUpdate(updateInfo: UpdateInfo): Boolean {
        if (!updateInfo.isForceUpdate || updateInfo.minimumRequiredVersion == null) {
            return false
        }

        return try {
            val currentVersionCode = AppVersionInfo.VERSION_CODE
            val minimumVersionCode = parseVersionToCode(updateInfo.minimumRequiredVersion)
            currentVersionCode < minimumVersionCode
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing minimum required version", e)
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
            0
        }
    }
}
