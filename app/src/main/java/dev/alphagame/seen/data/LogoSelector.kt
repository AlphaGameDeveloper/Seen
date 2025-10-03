package dev.alphagame.seen.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class LogoSelector {
    companion object {
        @Composable
        fun themedIcon(context: Context): Int {
            val preferencesManager = remember { PreferencesManager(context) }

            val theme = preferencesManager.themeMode
            val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme()

            val logoRes: Int = if (theme == "auto") {                            // Auto mode
                if (isDarkMode) {
                    Log.d("ThemedIcon", "Selected Icon by Auto Mode, System Dark")
                    dev.alphagame.seen.R.drawable.seen_logo_darkmode_transparent // Auto Mode -> System Dark mode
                } else {
                    Log.d("ThemedIcon", "Selected Icon by Auto Mode, System Light")
                    dev.alphagame.seen.R.drawable.seen_logo_transparent          // Auto Mode -> System Light mode
                }
            } else {
                if (theme == "light") {
                    Log.d("ThemedIcon", "Selected Icon by Manual Mode, Light")
                    dev.alphagame.seen.R.drawable.seen_logo_transparent          // Manual Mode -> Light
                } else {
                    Log.d("ThemedIcon", "Selected Icon by Manual Mode, Dark")
                    dev.alphagame.seen.R.drawable.seen_logo_darkmode_transparent // Manual Mode -> Dark
                }
            } // to be overridden

            return logoRes
        }
    }
}
