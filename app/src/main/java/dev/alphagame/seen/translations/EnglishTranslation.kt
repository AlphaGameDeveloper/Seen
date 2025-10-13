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

class EnglishTranslation : Translation() {
    override val TRANSLATION = TranslationCode.ENGLISH

    override val appName = "Seen"
    override val version = "Version"
    override val settings = "Settings"
    override val about = "About"
    override val back = "Back"
    override val info = "Info"

    // Welcome Screen
    override val welcomeTitle = "Welcome to Seen"
    override val welcomeSubtitle = "Take a moment to check in with yourself"
    override val startQuiz = "Start Assessment"
    override val viewNotes = "View Journal"
    override val viewMoodHistory = "View Mood History"

    override val welcomeScreenMessages = listOf(
        "Take a moment to check in with yourself",
        "It's ok to not be ok",
        "There is hope, even when your brain tells you there isn't",
        "You are not alone. You are seen.",
        "Welcome back!",
        "Good to see you again!",
        "Let's begin something great!",
        "Ready to make progress?",
        "Hope you're having a good day!",
        "Let's make today count!"
    )

    // PHQ-9 Questions
    override val phq9Questions = listOf(
        "Little interest or pleasure in doing things?",
        "Feeling down, depressed, or hopeless?",
        "Trouble falling or staying asleep, or sleeping too much?",
        "Feeling tired or having little energy?",
        "Poor appetite or overeating?",
        "Feeling bad about yourself?",
        "Trouble concentrating on things?",
        "Moving or speaking slowly or being fidgety/restless?",
        "Thoughts that you would be better off dead, or of hurting yourself?"
    )

    override val phq9Options = listOf(
        "😐 Not at all",
        "😕 Several days",
        "😞 More than half the days",
        "😢 Nearly every day"
    )

    // Quiz/Question Screen
    override val questionProgress = "Question %d of %d"

    // Result Screen
    override val resultTitle = "Assessment Complete"
    override val resultScore = "Your Score"
    override val resultMinimal = "Minimal Depression"
    override val resultMild = "Mild Depression"
    override val resultModerate = "Moderate Depression"
    override val resultSevere = "Severe Depression"
    override val resultDescription = "This assessment provides insights into your current mental health state. Consider consulting with a healthcare professional for proper diagnosis and support."
    override val retakeQuiz = "Retake Assessment"
    override val saveResult = "Save Result"
    override val talkToSomeone = "📞 Talk to Someone – 988"
    override val learnAboutDepression = "📚 Learn About Depression"
    override val returnToHome = "🔄 Return to Home"

    // Settings Screen
    override val appearance = "Appearance"
    override val colorScheme = "Color Scheme"
    override val language = "Language"
    override val themeAuto = "Auto (System)"
    override val themeLight = "Light"
    override val themeDark = "Dark"
    override val deleteAllData = "Delete All Data"
    override val deleteDataWarning = "This will permanently delete all your mood history, notes, and settings. This action cannot be undone."
    override val deleteDataConfirmation = "Are you sure you want to delete all data?"
    override val cancel = "Cancel"
    override val delete = "Delete"
    override val createdBy = "Created by Damien Boisvert & Alexander Cameron"

    // Settings Screen - Additional
    override val appTitle = "Seen - Mental Health Assessment"
    override val builtOn = "Built on"
    override val branch = "Branch"
    override val commitMessage = "Commit Message"
    override val assessmentSettings = "Assessment Settings"
    override val savePHQ9Data = "Save PHQ-9 Assessment Data"
    override val savePHQ9DataDescription = "Store detailed question responses locally for tracking progress"
    override val dataManagement = "Data Management"
    override val deleteAllDataDescription = "This will permanently delete all your notes, assessment results, mood entries, and reset all app settings to defaults. This action cannot be undone."
    override val privacyAndData = "Privacy & Data"
    override val privacyDescription = "• All data is encrypted and stored locally on your device\n• No personal information is shared with third parties\n• Assessment results remain completely private\n• You can delete your data at any time"
    override val importantNotice = "Important Notice"
    override val disclaimerText = "This app is not a substitute for professional medical advice, diagnosis, or treatment. If you're experiencing a mental health crisis, please contact emergency services or consult with a qualified healthcare provider immediately."
    override val deleteConfirmTitle = "Delete All Data?"
    override val deleteConfirmText = "This will permanently delete all your notes, assessment results, mood entries, and app settings. All data will be cleared and settings will be reset to default values. This action cannot be undone.\n\nAre you sure you want to continue?"
    override val deleteAll = "Delete All"
    override val dataDeletedTitle = "Data Deleted Successfully"
    override val dataDeletedText = "All your data has been permanently deleted and settings have been reset to defaults. The application will now close."
    override val continueButton = "Continue"

    // Notes Screen
    override val notes = "Journal"
    override val addNote = "Add Journal Entry"
    override val editNote = "Edit Journal Entry"
    override val deleteNote = "Delete Journal Entry"
    override val noteHint = "Write your thoughts here..."
    override val save = "Save"
    override val howAreYouFeeling = "What's new?"
    override val saveNote = "💾 Save Note"
    override val deleteNoteDialogText = "On %s, you wrote \"%s\"\n\nDeleted notes cannot be recovered."

    // Mood History Screen
    override val moodHistory = "Mood History"
    override val noMoodData = "No mood entries yet.\nUse the widget to start tracking!"
    override val score = "Score"
    override val date = "Date"
    override val clearHistory = "Clear History"
    override val clearMoodHistory = "Clear Mood History"
    override val clearMoodHistoryConfirmation = "Are you sure you want to delete all mood entries? This action cannot be undone."
    override val deleteMoodEntry = "Delete Mood Entry"
    override val deleteMoodEntryConfirmation = "Are you sure you want to delete this %s mood entry from %s?"
    override val todaysStats = "Today's Stats"
    override val entriesToday = "Entries today: %d"
    override val totalEntries = "Total entries: %d"
    override val todaysMoods = "Today's moods: %s"
    override val graphText = "A graph will show here once you complete two or more PHQ-9 assessments.\nCome back later!"
    override val moodEntries = "Mood Entries"
    override val phqResponses = "PHQ-9 Responses"
    // PHQ-9 Deletion Dialog & Severity
    override val deletePHQ9ResponsesTitle = "Delete PHQ-9 Responses"
    override val phq9SeverityMinimal = "Minimal Depression"
    override val phq9SeverityMild = "Mild Depression"
    override val phq9SeverityModerate = "Moderate Depression"
    override val phq9SeverityModeratelySevere = "Moderately Severe Depression"
    override val phq9SeveritySevere = "Severe Depression"
    override val phq9SeverityUnknown = "Unknown"
    override val deletePHQ9EntryTitle = "Delete PHQ9 Entry?"
    override val deletePHQ9EntryDescription = "Delete PHQ-9 result with score %s from %s?\n\nDeleted data cannot be recovered."
    override val deletePHQ9EntryConfirm = "Delete"
    override val deletePHQ9EntryCancel = "Cancel"
    override val deletePHQ9EntryContentDescription = "Delete PHQ-9 entry"

    // Onboarding Screen
    override val onboardingTitle = "Welcome to Seen"
    override val onboardingDescription = "Seen is a mental health assessment tool that helps you track your mood and well-being over time using the PHQ-9 questionnaire."
    override val onboardingPrivacy = "Your privacy is important to us. All data is stored locally on your device and is never shared with third parties."
    override val onboardingContinue = "Get Started"
    override val onboardingSkip = "Skip"
    override val onboardingWelcomeTitle = "Welcome to Seen"
    override val onboardingWelcomeDesc = "A simple, private mental health companion designed to help you track your wellbeing."
    override val onboardingPHQ9Title = "PHQ-9 Assessment"
    override val onboardingPHQ9Desc = "Take the clinically validated PHQ-9 questionnaire to assess your mental health and track changes over time."
    override val onboardingNotesTitle = "Personal Notes"
    override val onboardingNotesDesc = "Keep private notes about your thoughts, feelings, and daily experiences in a secure space."
    override val onboardingPrivacyTitle = "Your Privacy First"
    override val onboardingPrivacyDesc = "All your data stays on your device. We never collect, store, or share your personal information."
    override val onboardingNoAdsTitle = "No Advertisements"
    override val onboardingNoAdsDesc = "Enjoy a distraction-free experience. This app is completely free and will never show any advertisements."
    override val onboardingNotificationsTitle = "Stay Connected"
    override val onboardingNotificationsDesc = "Allow notifications to receive reminders for your mental health check-ins and important app updates. You can change this anytime in settings."
    override val next = "Next"

    // Enhanced Onboarding
    override val onboardingGetStarted = "Get Started"
    override val onboardingConfigureApp = "Configure App"
    override val onboardingAITitle = "AI Features"
    override val onboardingAIDesc = "Enable AI-powered insights and suggestions to enhance your mental health journey. All processing happens locally on your device."
    override val onboardingAIEnabled = "Enable AI Features"
    override val onboardingAIDisabled = "Keep AI Features Disabled"
    override val onboardingNotificationConfigTitle = "Notification Preferences"
    override val onboardingNotificationConfigDesc = "Customize how Seen keeps you informed about your mental health journey and app updates."
    override val onboardingAllowNotifications = "Allow Notifications"
    override val onboardingEnableReminders = "Enable gentle reminders for mental health check-ins"
    override val onboardingEnableUpdateChecks = "Enable automatic update notifications"
    override val onboardingGeneralSettingsTitle = "Personalize Your Experience"
    override val onboardingGeneralSettingsDesc = "Choose your preferred theme and language to make Seen feel just right for you."
    override val onboardingChooseTheme = "Choose Theme"
    override val onboardingChooseLanguage = "Choose Language"
    override val onboardingDataPrivacyTitle = "Data & Privacy"
    override val onboardingDataPrivacyDesc = "Control how your assessment data is stored and ensure your privacy is protected."
    override val onboardingEnableDataStorage = "Save detailed assessment data for progress tracking"
    override val onboardingComplete = "Complete Setup"
    override val onboardingSetupComplete = "You're All Set!"
    override val onboardingSetupCompleteDesc = "Seen is now configured to your preferences. You can always change these settings later."
    override val onboardingReadyToUse = "Start Using Seen"

    // Analytics
    override val onboardingAnalyticsTitle = "Help Improve Seen"
    override val onboardingAnalyticsDesc = "Share anonymous usage data to help us make Seen better for everyone"
    override val onboardingAnalyticsEnabled = "📈 Share Usage Data"
    override val onboardingAnalyticsDisabled = "🥺 Keep Data Private"
    override val analyticsPrivacyNote = "We only collect anonymous usage statistics to improve the app. No personal health data is ever shared."
    override val analyticsDataUsage = "Data collected includes app usage patterns, feature interactions, and crash reports. All data is anonymous and cannot be linked back to you."

    // Settings Screen - Additional Sections
    override val notifications = "Notifications"
    override val enableReminders = "Enable Reminders"
    override val enableRemindersDescription = "Receive gentle reminders for mental health check-ins"
    override val encryption = "Encryption"
    override val enableEncryption = "Enable On-Device Encryption"
    override val encryptionDescription = "Secure & Private data, never gets saved outside your device."
    override val aiFeatures = "AI Features"
    override val enableAIFeatures = "Enable AI Features"
    override val aiFeaturesDescription = "Get personalized insights and suggestions (Coming Soon)"
    override val analytics = "Analytics"
    override val enableAnalytics = "Enable Analytics"
    override val analyticsDescription = "Help improve Seen by sharing anonymous usage data"

    // Welcome Screen - Additional
    override val phq9AssessmentTitle = "PHQ-9 Assessment"
    override val phq9AssessmentDescription = "A confidential questionnaire to help assess your mental health and well-being over the past two weeks."

    // Notes Screen - Additional
    override val noEntriesMessage = "No journal entry data yet. \nTo get started, use the \"Add Entry\" button in the bottom-right corner!"

    // Debug Screens
    override val databaseEncryptionDebug = "Database Encryption Debug"

    // Resources Screen
    override val backToResults = "Back to results"
    override val resourcesAndSupport = "Resources & Support"
    override val yourScore = "Your Score"

    // AI Analysis & Results
    override val aiAnalysisInProgress = "AI analysis in progress..."
    override val aiAnalysisReady = "AI analysis ready! Tap below to view insights."
    override val aiAnalysisUnavailable = "AI analysis unavailable. View basic resources instead."
    override val preparingAiAnalysis = "Preparing AI analysis..."
    override val viewAiAnalysis = "View AI Analysis"
    override val viewResourcesAndSupport = "View Resources & Support"

    // Resources Screen - Additional Content
    override val crisisSupport = "Crisis Support"
    override val crisisSupportDescription = "If you're having thoughts of self-harm or suicide, please reach out immediately."
    override val severeSymptoms = "Your responses indicate severe symptoms. Professional help is strongly recommended."
    override val about988 = "About 988"
    override val aboutDepression = "About Depression"
    override val aboutDepressionDescription = "It is important to understand how you are feeling, even if you do not have severe symptoms. Here are some resources on the subject of depression."
    override val learnAboutDepression2 = "Learn About Depression"
    override val caringForMentalHealth = "Caring For Your Mental Health"
    override val recommendedResources = "Recommended Resources"
    override val recommendedResourcesDescription = "The following resources are recommended based on your responses to the PHQ-9."
    override val aboutLosingInterest = "About Losing Interest"
    override val aboutSadness = "About Sadness/Hopelessness"
    override val aboutSleep = "About Sleep/Fatigue"
    override val aboutFeelingBad = "About Feeling Bad About Yourself"
    override val aboutConcentration = "About Trouble Concentrating"
    override val additionalYouthResources = "Additional Youth Resources"

    // Common
    override val yes = "Yes"
    override val no = "No"
    override val ok = "OK"
    override val error = "Error"
    override val loading = "Loading..."

    // Update Check
    override val checkForUpdates = "Check for Updates"
    override val checkingForUpdates = "Checking for Updates..."
    override val updateAvailable = "Update Available"
    override val requiredUpdate = "Required Update"
    override val noUpdatesAvailable = "You're All Up to Date!"
    override val noUpdatesAvailableMessage = "You are running the latest version of the app. No updates are available at this time."
    override val updateCheckFailed = "Update Check Failed"
    override val updateCheckFailedMessage = "Unable to check for updates. Please check your internet connection and try again later."
    override val noInternetConnection = "No Internet Connection"
    override val noInternetConnectionMessage = "Please check your internet connection and try again."
    override val internetTroubleshootingTips = "• Check your Wi-Fi connection\n• Check your mobile data\n• Try again in a moment"
    override val updateNow = "Update Now"
    override val updateLater = "Later"
    override val downloadUpdate = "Download"
    override val whatsNew = "What's new:"
    override val currentVersion = "Current version:"
    override val latestVersion = "Latest version:"
    override val updateRequired = "This update is required to continue using the app."

    // Update Notifications
    override val updateNotificationTitle = "New Update Available"
    override val updateNotificationMessage = "A new version of Seen is available. Tap to download."
    override val updateNotificationDontRemind = "Don't remind me"
    override val enableBackgroundUpdateChecks = "Enable Background Update Checks"
    override val enableBackgroundUpdateChecksDescription = "Automatically check for new versions every 15 minutes and receive notifications"

    // Health Status
    override val serviceHealthStatus = "Service Health Status"
    override val analyticsService = "Analytics"
    override val releasesService = "Releases"
    override val aiService = "AI"
    override val healthyStatus = "Healthy"
    override val unhealthyStatus = "Unhealthy"
    override val unknownStatus = "Unknown"
    override val refreshStatus = "Refresh"
    override val closeDialog = "Close"

    //Moods
    override val veryHappy = "Very Happy"
    override val happy = "Happy"
    override val neutral = "Neutral"
    override val sad = "Sad"
    override val verySad = "Very Sad"
    override val angry = "Angry"
    override val anxious = "Anxious"
    override val excited = "Excited"

    override val deletePHQ9Result = "Delete PHQ-9 result with score %s from %s?\n\nDeleted data cannot be recovered."
}
