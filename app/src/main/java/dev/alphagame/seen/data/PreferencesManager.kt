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

    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }
}
