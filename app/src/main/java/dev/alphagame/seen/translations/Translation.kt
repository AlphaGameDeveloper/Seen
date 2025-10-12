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

package dev.alphagame.seen.translations

import dev.alphagame.seen.data.PreferencesManager

enum class TranslationCode {
    ENGLISH,
    FRENCH,
    SPANISH
}

abstract class Translation {
    abstract val TRANSLATION: TranslationCode
    // General App Strings
    abstract val appName: String
    abstract val version: String
    abstract val settings: String
    abstract val about: String
    abstract val back: String
    abstract val info: String

    // Welcome Screen
    abstract val welcomeTitle: String
    abstract val welcomeSubtitle: String
    abstract val startQuiz: String
    abstract val viewNotes: String
    abstract val viewMoodHistory: String
    abstract val welcomeScreenMessages: List<String>

    // PHQ-9 Questions
    abstract val phq9Questions: List<String>
    abstract val phq9Options: List<String>

    // Quiz/Question Screen
    abstract val questionProgress: String // "Question {0} of {1}"

    // Result Screen
    abstract val resultTitle: String
    abstract val resultScore: String
    abstract val resultMinimal: String
    abstract val resultMild: String
    abstract val resultModerate: String
    abstract val resultSevere: String
    abstract val resultDescription: String
    abstract val retakeQuiz: String
    abstract val saveResult: String
    abstract val talkToSomeone: String
    abstract val learnAboutDepression: String
    abstract val returnToHome: String

    // Settings Screen
    abstract val appearance: String
    abstract val colorScheme: String
    abstract val language: String
    abstract val themeAuto: String
    abstract val themeLight: String
    abstract val themeDark: String
    abstract val deleteAllData: String
    abstract val deleteDataWarning: String
    abstract val deleteDataConfirmation: String
    abstract val cancel: String
    abstract val delete: String
    abstract val createdBy: String

    // Notes Screen
    abstract val notes: String
    abstract val addNote: String
    abstract val editNote: String
    abstract val deleteNote: String
    abstract val noteHint: String
    abstract val save: String
    abstract val howAreYouFeeling: String
    abstract val saveNote: String

    // Mood History Screen
    abstract val moodHistory: String
    abstract val noMoodData: String
    abstract val score: String
    abstract val date: String
    abstract val clearHistory: String
    abstract val clearMoodHistory: String
    abstract val clearMoodHistoryConfirmation: String
    abstract val deleteMoodEntry: String
    abstract val deleteMoodEntryConfirmation: String
    abstract val todaysStats: String
    abstract val entriesToday: String
    abstract val totalEntries: String
    abstract val todaysMoods: String
    abstract val graphText: String
    abstract val moodEntries: String
    abstract val phqResponses: String

    // Onboarding Screen
    abstract val onboardingTitle: String
    abstract val onboardingDescription: String
    abstract val onboardingPrivacy: String
    abstract val onboardingContinue: String
    abstract val onboardingSkip: String
    abstract val onboardingWelcomeTitle: String
    abstract val onboardingWelcomeDesc: String
    abstract val onboardingPHQ9Title: String
    abstract val onboardingPHQ9Desc: String
    abstract val onboardingNotesTitle: String
    abstract val onboardingNotesDesc: String
    abstract val onboardingPrivacyTitle: String
    abstract val onboardingPrivacyDesc: String
    abstract val onboardingNoAdsTitle: String
    abstract val onboardingNoAdsDesc: String
    abstract val onboardingNotificationsTitle: String
    abstract val onboardingNotificationsDesc: String
    abstract val next: String

    // Settings Screen - Additional
    abstract val appTitle: String
    abstract val builtOn: String
    abstract val branch: String
    abstract val commitMessage: String
    abstract val assessmentSettings: String
    abstract val savePHQ9Data: String
    abstract val savePHQ9DataDescription: String
    abstract val dataManagement: String
    abstract val deleteAllDataDescription: String
    abstract val privacyAndData: String
    abstract val privacyDescription: String
    abstract val importantNotice: String
    abstract val disclaimerText: String
    abstract val deleteConfirmTitle: String
    abstract val deleteConfirmText: String
    abstract val deleteAll: String
    abstract val dataDeletedTitle: String
    abstract val dataDeletedText: String
    abstract val continueButton: String

    // Settings Screen - Additional Sections
    abstract val notifications: String
    abstract val enableReminders: String
    abstract val enableRemindersDescription: String
    abstract val encryption: String
    abstract val enableEncryption: String
    abstract val encryptionDescription: String
    abstract val aiFeatures: String
    abstract val enableAIFeatures: String
    abstract val aiFeaturesDescription: String
    abstract val analytics: String
    abstract val enableAnalytics: String
    abstract val analyticsDescription: String

    // Welcome Screen - Additional
    abstract val phq9AssessmentTitle: String
    abstract val phq9AssessmentDescription: String

    // Notes Screen - Additional
    abstract val noEntriesMessage: String

    // Debug Screens
    abstract val databaseEncryptionDebug: String

    // Resources Screen
    abstract val backToResults: String
    abstract val resourcesAndSupport: String
    abstract val yourScore: String

    // AI Analysis & Results
    abstract val aiAnalysisInProgress: String
    abstract val aiAnalysisReady: String
    abstract val aiAnalysisUnavailable: String
    abstract val preparingAiAnalysis: String
    abstract val viewAiAnalysis: String
    abstract val viewResourcesAndSupport: String

    // Resources Screen - Additional Content
    abstract val crisisSupport: String
    abstract val crisisSupportDescription: String
    abstract val severeSymptoms: String
    abstract val about988: String
    abstract val aboutDepression: String
    abstract val aboutDepressionDescription: String
    abstract val learnAboutDepression2: String
    abstract val caringForMentalHealth: String
    abstract val recommendedResources: String
    abstract val recommendedResourcesDescription: String
    abstract val localResources: String

    // Common
    abstract val yes: String
    abstract val no: String
    abstract val ok: String
    abstract val error: String
    abstract val loading: String

    // Update Check
    abstract val checkForUpdates: String
    abstract val checkingForUpdates: String
    abstract val updateAvailable: String
    abstract val requiredUpdate: String
    abstract val noUpdatesAvailable: String
    abstract val noUpdatesAvailableMessage: String
    abstract val updateCheckFailed: String
    abstract val updateCheckFailedMessage: String
    abstract val noInternetConnection: String
    abstract val noInternetConnectionMessage: String
    abstract val internetTroubleshootingTips: String
    abstract val updateNow: String
    abstract val updateLater: String
    abstract val downloadUpdate: String
    abstract val whatsNew: String
    abstract val currentVersion: String
    abstract val latestVersion: String
    abstract val updateRequired: String

    // Update Notifications
    abstract val updateNotificationTitle: String
    abstract val updateNotificationMessage: String
    abstract val updateNotificationDontRemind: String
    abstract val enableBackgroundUpdateChecks: String
    abstract val enableBackgroundUpdateChecksDescription: String

    // Enhanced Onboarding
    abstract val onboardingGetStarted: String
    abstract val onboardingConfigureApp: String
    abstract val onboardingAITitle: String
    abstract val onboardingAIDesc: String
    abstract val onboardingAIEnabled: String
    abstract val onboardingAIDisabled: String
    abstract val onboardingNotificationConfigTitle: String
    abstract val onboardingNotificationConfigDesc: String
    abstract val onboardingAllowNotifications: String
    abstract val onboardingEnableReminders: String
    abstract val onboardingEnableUpdateChecks: String
    abstract val onboardingGeneralSettingsTitle: String
    abstract val onboardingGeneralSettingsDesc: String
    abstract val onboardingChooseTheme: String
    abstract val onboardingChooseLanguage: String
    abstract val onboardingDataPrivacyTitle: String
    abstract val onboardingDataPrivacyDesc: String
    abstract val onboardingEnableDataStorage: String
    abstract val onboardingComplete: String
    abstract val onboardingSetupComplete: String
    abstract val onboardingSetupCompleteDesc: String
    abstract val onboardingReadyToUse: String

    // Analytics
    abstract val onboardingAnalyticsTitle: String
    abstract val onboardingAnalyticsDesc: String
    abstract val onboardingAnalyticsEnabled: String
    abstract val onboardingAnalyticsDisabled: String
    abstract val analyticsPrivacyNote: String
    abstract val analyticsDataUsage: String

    // Health Status
    abstract val serviceHealthStatus: String
    abstract val analyticsService: String
    abstract val releasesService: String
    abstract val aiService: String
    abstract val healthyStatus: String
    abstract val unhealthyStatus: String
    abstract val unknownStatus: String
    abstract val refreshStatus: String
    abstract val closeDialog: String

    abstract val veryHappy: String
    abstract val happy: String
    abstract val neutral: String
    abstract val sad: String
    abstract val verySad: String
    abstract val angry: String
    abstract val anxious: String
    abstract val excited: String

    abstract val deletePHQ9Result: String
    // PHQ-9 Deletion Dialog & Severity
    abstract val deletePHQ9ResponsesTitle: String
    abstract val phq9SeverityMinimal: String
    abstract val phq9SeverityMild: String
    abstract val phq9SeverityModerate: String
    abstract val phq9SeverityModeratelySevere: String
    abstract val phq9SeveritySevere: String
    abstract val phq9SeverityUnknown: String
    abstract val deletePHQ9EntryTitle: String
    abstract val deletePHQ9EntryDescription: String
    abstract val deletePHQ9EntryConfirm: String
    abstract val deletePHQ9EntryCancel: String
    abstract val deletePHQ9EntryContentDescription: String
    companion object {
        fun getTranslation(languageCode: String): Translation {
            return when (languageCode) {
                PreferencesManager.LANGUAGE_FRENCH -> FrenchTranslation()
                PreferencesManager.LANGUAGE_SPANISH -> SpanishTranslation()
                else -> EnglishTranslation()
            }
        }

        fun getAvailableLanguages(): List<Pair<String, String>> {
            return listOf(
                PreferencesManager.LANGUAGE_ENGLISH to "English",
                PreferencesManager.LANGUAGE_SPANISH to "Español",
                PreferencesManager.LANGUAGE_FRENCH to "Français"
            )
        }
    }
}
