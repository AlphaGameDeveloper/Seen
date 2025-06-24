package dev.alphagame.seen.translations

class EnglishTranslation : Translation() {
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
    override val viewNotes = "View Notes"
    override val viewMoodHistory = "View Mood History"

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
    override val privacyDescription = "• All data is stored locally on your device\n• No personal information is shared with third parties\n• Assessment results remain completely private\n• You can delete your data at any time"
    override val importantNotice = "Important Notice"
    override val disclaimerText = "This app is not a substitute for professional medical advice, diagnosis, or treatment. If you're experiencing a mental health crisis, please contact emergency services or consult with a qualified healthcare provider immediately."
    override val deleteConfirmTitle = "Delete All Data?"
    override val deleteConfirmText = "This will permanently delete all your notes, assessment results, mood entries, and app settings. All data will be cleared and settings will be reset to default values. This action cannot be undone.\n\nAre you sure you want to continue?"
    override val deleteAll = "Delete All"
    override val dataDeletedTitle = "Data Deleted Successfully"
    override val dataDeletedText = "All your data has been permanently deleted and settings have been reset to defaults. The application will now close."
    override val continueButton = "Continue"

    // Notes Screen
    override val notes = "Notes"
    override val addNote = "Add Note"
    override val editNote = "Edit Note"
    override val deleteNote = "Delete Note"
    override val noteHint = "Write your thoughts here..."
    override val save = "Save"
    override val howAreYouFeeling = "How are you feeling?"
    override val saveNote = "💾 Save Note"

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

    // Common
    override val yes = "Yes"
    override val no = "No"
    override val ok = "OK"
    override val error = "Error"
    override val loading = "Loading..."
}
