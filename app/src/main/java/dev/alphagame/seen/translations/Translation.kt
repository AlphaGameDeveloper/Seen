package dev.alphagame.seen.translations

abstract class Translation {
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
    abstract val individualScores: String
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
    
    // Common
    abstract val yes: String
    abstract val no: String
    abstract val ok: String
    abstract val error: String
    abstract val loading: String
    
    companion object {
        const val ENGLISH = "en"
        const val FRENCH = "fr"
        const val SPANISH = "es"
        
        fun getAvailableLanguages(): List<Pair<String, String>> = listOf(
            ENGLISH to "English",
            FRENCH to "Français",
            SPANISH to "Español"
        )
        
        fun getTranslation(languageCode: String): Translation = when (languageCode) {
            FRENCH -> FrenchTranslation()
            SPANISH -> SpanishTranslation()
            else -> EnglishTranslation()
        }
    }
}
