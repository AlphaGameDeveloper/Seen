package dev.alphagame.seen.data

data class UpdateInfo(
    val latestVersion: String,
    val currentVersion: String,
    val isUpdateAvailable: Boolean,
    val downloadUrl: String? = null,
    val releaseNotes: String? = null,
    val isForceUpdate: Boolean = false,
    val minimumRequiredVersion: String? = null
)

data class UpdateResponse(
    val version: String,
    val versionCode: Int,
    val downloadUrl: String? = null,
    val releaseNotes: String? = null,
    val forceUpdate: Boolean = false,
    val minimumRequiredVersion: String? = null
)
