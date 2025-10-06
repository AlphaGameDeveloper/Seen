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
