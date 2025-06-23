package dev.alphagame.seen.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "seen_preferences"
        private const val KEY_THEME_MODE = "theme_mode"
        
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_AUTO = "auto"
    }
    
    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, THEME_AUTO) ?: THEME_AUTO
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()
    
    fun clearAllPreferences() {
        prefs.edit().clear().apply()
    }
}
