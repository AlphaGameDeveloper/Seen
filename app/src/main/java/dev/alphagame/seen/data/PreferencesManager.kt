package dev.alphagame.seen.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "seen_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_PHQ9_DATA_STORAGE = "phq9_data_storage"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_LAST_UPDATE_CHECK = "last_update_check"
        private const val KEY_SKIPPED_VERSION = "skipped_version"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_BACKGROUND_UPDATE_CHECKS = "background_update_checks"
        private const val KEY_LAST_BACKGROUND_UPDATE_CHECK = "last_background_update_check"
        private const val KEY_LAST_UPDATE_NOTIFICATION = "last_update_notification"
        private const val KEY_ANALYTICS_ENABLED = "analytics_enabled"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_AUTO = "auto"

        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_FRENCH = "fr"
        const val LANGUAGE_SPANISH = "es"
    }

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, THEME_AUTO) ?: THEME_AUTO
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var isPhq9DataStorageEnabled: Boolean
        get() = prefs.getBoolean(KEY_PHQ9_DATA_STORAGE, false)
        set(value) = prefs.edit().putBoolean(KEY_PHQ9_DATA_STORAGE, value).apply()

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var lastUpdateCheckTime: Long
        get() = prefs.getLong(KEY_LAST_UPDATE_CHECK, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_UPDATE_CHECK, value).apply()

    var skippedVersion: String?
        get() = prefs.getString(KEY_SKIPPED_VERSION, null)
        set(value) = prefs.edit().putString(KEY_SKIPPED_VERSION, value).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var backgroundUpdateChecksEnabled: Boolean
        get() = prefs.getBoolean(KEY_BACKGROUND_UPDATE_CHECKS, false)
        set(value) = prefs.edit().putBoolean(KEY_BACKGROUND_UPDATE_CHECKS, value).apply()

    var lastBackgroundUpdateCheck: Long
        get() = prefs.getLong(KEY_LAST_BACKGROUND_UPDATE_CHECK, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_BACKGROUND_UPDATE_CHECK, value).apply()

    var lastUpdateNotificationTime: Long
        get() = prefs.getLong(KEY_LAST_UPDATE_NOTIFICATION, 0)
        set(value) = prefs.edit().putLong(KEY_LAST_UPDATE_NOTIFICATION, value).apply()

    var analyticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANALYTICS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ANALYTICS_ENABLED, value).apply()

    fun shouldCheckForUpdates(): Boolean {
        val lastCheck = lastUpdateCheckTime
        val currentTime = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L
        return (currentTime - lastCheck) > twentyFourHours
    }

    fun shouldSendUpdateNotification(): Boolean {
        val lastNotification = lastUpdateNotificationTime
        val currentTime = System.currentTimeMillis()
        val twentyFourHours = 24 * 60 * 60 * 1000L
        return (currentTime - lastNotification) > twentyFourHours
    }

    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }
}
