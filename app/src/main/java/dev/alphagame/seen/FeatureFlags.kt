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

class FeatureFlags {
    companion object {
        const val SETTINGS_EXTENDED_ABOUT = false;
        const val SETTINGS_PACKAGE = false;

        const val SETTINGS_AI_FEATURES = false;
        const val SETTINGS_ANALYTICS = false;
        const val SETTINGS_ASSESSMENT_SETTINGS = false;

        const val ONBOARDING_NO_ADS = false;

        const val UI_ONBOARDING_BUTTON = false;
        const val UI_JOURNAL_ADD_BUTTON = false;
        const val UI_MOOD_HISTORY_GLOBAL_DELETE = false;

        const val UI_BUILD_STRING = true;

        const val UI_DEBUG_BUILD_TEXT = true;

        const val DEBUG_DB_SCREEN_TOAST_MESSAGE = false;

        const val MOODHISTORY_TODAYATAGLANCE_CARD = true;

        const val SETTINGS_STATUS_DOTS = false;

        const val SETTINGS_UPDATE_BUTTON = false

        const val HOME_SEEN_LOGO = true;

        const val ENABLE_ONBOARDING = false;

        const val WELCOME_ANIMATED_SUBTITLE = true;
    }
}
