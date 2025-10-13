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

import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import dev.alphagame.seen.analytics.AnalyticsManager
import dev.alphagame.seen.components.UpdateDialog
import dev.alphagame.seen.data.PHQ9Data
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.UpdateCheckManager
import dev.alphagame.seen.data.UpdateChecker
import dev.alphagame.seen.data.UpdateInfo
import dev.alphagame.seen.onboarding.EnhancedOnboardingScreen
import dev.alphagame.seen.screens.DatabaseDebugScreen
import dev.alphagame.seen.screens.EncryptionDebugScreen
import dev.alphagame.seen.screens.MoodHistoryScreen
import dev.alphagame.seen.screens.NotesScreen
import dev.alphagame.seen.screens.QuestionScreen
import dev.alphagame.seen.screens.ResultScreen
import dev.alphagame.seen.screens.SettingsScreen
import dev.alphagame.seen.screens.WelcomeScreen
import dev.alphagame.seen.translations.TranslationProvider
import dev.alphagame.seen.translations.rememberTranslation
import dev.alphagame.seen.ui.theme.SeenTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            var themeMode by remember { mutableStateOf(preferencesManager.themeMode) }
            var languageMode by remember { mutableStateOf(preferencesManager.language) }

            val darkTheme = when (themeMode) {
                PreferencesManager.THEME_DARK -> true
                PreferencesManager.THEME_LIGHT -> false
                else -> isSystemInDarkTheme()
            }

            SeenTheme(darkTheme = darkTheme) {
                TranslationProvider {
                    SeenApplication(
                        onThemeChanged = { newTheme ->
                            themeMode = newTheme
                            preferencesManager.themeMode = newTheme
                        },
                        onLanguageChanged = { newLanguage ->
                            languageMode = newLanguage
                            preferencesManager.language = newLanguage
                            // Force recomposition by recreating the activity


                            // Alert the user that the application'll restart
                            Toast.makeText(context, "The application will restart to apply changes.", Toast.LENGTH_SHORT).show()
                            recreate()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SeenApplication(
    onThemeChanged: (String) -> Unit = {},
    onLanguageChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val analyticsManager = remember { AnalyticsManager(context) }
    val translation = rememberTranslation()
    val updateChecker = remember { UpdateChecker(context) }
    val updateCheckManager = remember { UpdateCheckManager(context) }
    val scope = rememberCoroutineScope()

    // Update check state
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    val questions = PHQ9Data.questions
    val options = PHQ9Data.options

    var currentScreen by remember {
        mutableStateOf(
            if (!preferencesManager.isOnboardingCompleted && FeatureFlags.ENABLE_ONBOARDING) "onboarding" else "welcome"
        )
    }
    var currentQuestion by remember { mutableIntStateOf(0) }
    val scores = remember { mutableStateListOf<Int>() }
    val navigationStack = remember { mutableStateListOf<String>() }

    // Check for updates on startup
    LaunchedEffect(Unit) {
        scope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(AlarmManager::class.java)
                if (!alarmManager.canScheduleExactAlarms()) {
                    // Show a dialog or direct user to settings
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.data = Uri.parse("package:" + context.packageName)
                    context.startActivity(intent)
                }
            }
            android.util.Log.d("UpdateCheck", "Starting update check...")
            android.util.Log.d("UpdateCheck", "Should check for updates: ${preferencesManager.shouldCheckForUpdates()}")
            android.util.Log.d("UpdateCheck", "Last check time: ${preferencesManager.lastUpdateCheckTime}")
            android.util.Log.d("UpdateCheck", "Current time: ${System.currentTimeMillis()}")

            // Start background update checks if enabled
            if (preferencesManager.backgroundUpdateChecksEnabled && preferencesManager.notificationsEnabled) {
                android.util.Log.d("UpdateCheck", "Starting background update checks")
                updateCheckManager.startBackgroundUpdateChecks()
            } else {
                android.util.Log.d("UpdateCheck", "Background update checks not started:")
                android.util.Log.d("UpdateCheck", "  - backgroundUpdateChecksEnabled: ${preferencesManager.backgroundUpdateChecksEnabled}")
                android.util.Log.d("UpdateCheck", "  - notificationsEnabled: ${preferencesManager.notificationsEnabled}")
            }

            // Test the version checker directly
            try {
                android.util.Log.d("UpdateCheck", "Testing VersionChecker directly...")
                val versionChecker = dev.alphagame.seen.data.VersionChecker(context)
                val latestVersion = versionChecker.checkLatestVersion()
                android.util.Log.d("UpdateCheck", "Latest version from server: $latestVersion")
                val isUpdateAvailable = versionChecker.isUpdateAvailable()
                android.util.Log.d("UpdateCheck", "Is update available: $isUpdateAvailable")
            } catch (e: Exception) {
                android.util.Log.e("UpdateCheck", "Error testing VersionChecker", e)
            }

            // For debugging - always check for updates (remove the shouldCheckForUpdates condition temporarily)
            // if (preferencesManager.shouldCheckForUpdates()) {
                try {
                    android.util.Log.d("UpdateCheck", "Calling updateChecker.checkForUpdates()...")
                    val update = updateChecker.checkForUpdates()
                    android.util.Log.d("UpdateCheck", "Update result: $update")

                    if (update != null && update.isUpdateAvailable) {
                        android.util.Log.d("UpdateCheck", "Update available: ${update.latestVersion}")
                        // Don't show dialog if user already skipped this version (unless it's a force update)
                        val skippedVersion = preferencesManager.skippedVersion
                        android.util.Log.d("UpdateCheck", "Skipped version: $skippedVersion")

                        if (update.isForceUpdate || skippedVersion != update.latestVersion) {
                            android.util.Log.d("UpdateCheck", "Showing update dialog")
                            updateInfo = update
                            showUpdateDialog = true
                        } else {
                            android.util.Log.d("UpdateCheck", "Update skipped (user already dismissed this version)")
                            updateInfo = update
                            showUpdateDialog = false
                        }
                    } else {
                        android.util.Log.d("UpdateCheck", "No update available")
                    }
                    preferencesManager.lastUpdateCheckTime = System.currentTimeMillis()
                } catch (e: Exception) {
                    // Silently fail update check - don't interrupt user experience
                    android.util.Log.e("UpdateCheck", "Failed to check for updates", e)
                }
            // }
        }
    }

    // Helper function to navigate to a new screen
    fun navigateTo(screen: String) {
        if (currentScreen != screen) {
            navigationStack.add(currentScreen)
            currentScreen = screen
        }
    }

    // Helper function to go back to previous screen
    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            currentScreen = navigationStack.removeLastOrNull() ?: "welcome"
        } else {
            currentScreen = "welcome"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Handle Android back button
        BackHandler(
            enabled = currentScreen != "welcome" && currentScreen != "onboarding"
        ) {
            navigateBack()
        }

        // Main content
        when (currentScreen) {
            "onboarding" -> {
                EnhancedOnboardingScreen(
                    onOnboardingComplete = {
                        preferencesManager.isOnboardingCompleted = true
                        currentScreen = "welcome"
                    }
                )
            }
            "welcome" -> {
                WelcomeScreen(
                    onStartQuiz = {
                        analyticsManager.trackEvent(AnalyticsManager.EVENT_PHQ9_STARTED)
                        navigateTo("quiz")
                        currentQuestion = 0
                        scores.clear()
                    },
                    onGoToNotes = {
                        analyticsManager.trackFeatureUsed("notes_screen")
                        navigateTo("notes")
                    },
                    onGoToMoodHistory = {
                        analyticsManager.trackEvent(AnalyticsManager.EVENT_MOOD_HISTORY_VIEWED)
                        navigateTo("mood_history")
                    },
                    onSecretDebugScreen = {
                        analyticsManager.trackFeatureUsed("debug_screen")
                        navigateTo("debug_database")
                    }
                )
            }
            "quiz" -> {
                if (currentQuestion < questions.size) {
                    QuestionScreen(
                        question = PHQ9Data.getQuestionText(currentQuestion, translation),
                        questionIndex = currentQuestion,
                        totalQuestions = questions.size,
                        options = options.map { PHQ9Data.getOptionText(it.score, translation) to it.score },
                        onAnswerSelected = { score ->
                            scores.add(score)
                            currentQuestion++

                            // Track if quiz was abandoned
                            if (currentQuestion == questions.size) {
                                val totalScore = scores.sum()
                                val severity = when {
                                    totalScore <= 4 -> "Minimal"
                                    totalScore <= 9 -> "Mild"
                                    totalScore <= 14 -> "Moderate"
                                    totalScore <= 19 -> "Moderately Severe"
                                    else -> "Severe"
                                }
                                analyticsManager.trackPHQ9Completion(totalScore, severity, false)
                            }
                        }
                        ,
                        onBack = {
                            if (currentQuestion > 0) {
                                currentQuestion--
                                if (scores.isNotEmpty()) scores.removeAt(scores.lastIndex)
                            } else {
                                navigateBack()
                            }
                        }
                    )
                } else {
                    ResultScreen(
                        scores = scores,
                        onRetakeQuiz = {
                            navigationStack.clear()
                            currentScreen = "welcome"
                            currentQuestion = 0
                            scores.clear()
                        }
                    )
                }
            }
            "notes" -> {
                NotesScreen(
                    onBackToHome = {
                        navigateBack()
                    }
                )
            }
            "settings" -> {
                SettingsScreen(
                    onBackToHome = {
                        analyticsManager.trackEvent(AnalyticsManager.EVENT_SETTINGS_OPENED)
                        navigateBack()
                    },
                    onThemeChanged = onThemeChanged,
                    onLanguageChanged = onLanguageChanged
                )
            }
            "mood_history" -> {
                MoodHistoryScreen(
                    onBackClick = {
                        navigateBack()
                    }
                )
            }
            "debug_database" -> {
                DatabaseDebugScreen(
                    onBackClick = {
                        navigateBack()
                    }
                )
            }
            "debug_encryption" -> {
                EncryptionDebugScreen(
                    onBackToHome = {
                        navigateBack()
                    }
                )
            }
            "info_onboarding" -> {
                EnhancedOnboardingScreen(
                    onOnboardingComplete = {
                        navigateBack()
                    }
                )
            }
        }

        // Settings button - only show on welcome screen
        if (currentScreen == "welcome") {
            // Info button in top left corner

            if (FeatureFlags.UI_ONBOARDING_BUTTON) {
                IconButton(
                    onClick = { navigateTo("info_onboarding") },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(16.dp)
                        .size(40.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            // Settings button in bottom left corner
            IconButton(
                onClick = { navigateTo("settings") },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(40.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shadowElevation = 4.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            // text in the bottom right corner with version info
            if (FeatureFlags.UI_BUILD_STRING && BuildConfig.DEBUG) {
                val extra = "\nBuilt on ${BuildConfig.PRETTY_BUILD_TIME}\nPackage: ${BuildConfig.APPLICATION_ID}@${BuildConfig.GIT_BRANCH}"
                var text = "Version: ${BuildConfig.VERSION_FULL}"
                if (BuildConfig.DEBUG) {
                    Log.d("MainActivity", "Using debug extended version information :3");
                    text = text + "\n" + extra
                }
                Text(
                    text = text,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        textAlign = TextAlign.Right,
                        fontSize = 12.sp // 1 pt ≈ 1 sp in Compose - what the actual fuck
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        if (BuildConfig.DEBUG && FeatureFlags.UI_DEBUG_BUILD_TEXT) {
            // red DEBUG BUILD in top left corner
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(110.dp)

            ) {
                Text(
                    text = "Debug Build",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Update Dialog
        android.util.Log.d("UpdateCheck", "Rendering UI - showUpdateDialog: $showUpdateDialog, updateInfo: $updateInfo")
        if (showUpdateDialog && updateInfo != null) {
            android.util.Log.d("UpdateCheck", "Showing UpdateDialog")
            UpdateDialog(
                updateInfo = updateInfo!!,
                onDismiss = {
                    showUpdateDialog = false
                },
                onUpdateLater = {
                    // Remember that user skipped this version (unless it's a force update)
                    if (!updateInfo!!.isForceUpdate) {
                        preferencesManager.skippedVersion = updateInfo!!.latestVersion
                    }
                }
            )
        }
    }
}
