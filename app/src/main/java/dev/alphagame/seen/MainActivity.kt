package dev.alphagame.seen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import dev.alphagame.seen.data.PHQ9Data
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.screens.WelcomeScreen
import dev.alphagame.seen.screens.QuestionScreen
import dev.alphagame.seen.screens.ResultScreen
import dev.alphagame.seen.screens.NotesScreen
import dev.alphagame.seen.screens.SettingsScreen
import dev.alphagame.seen.screens.MoodHistoryScreen
import dev.alphagame.seen.screens.DatabaseDebugScreen
import dev.alphagame.seen.ui.theme.SeenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            var themeMode by remember { mutableStateOf(preferencesManager.themeMode) }

            val darkTheme = when (themeMode) {
                PreferencesManager.THEME_DARK -> true
                PreferencesManager.THEME_LIGHT -> false
                else -> isSystemInDarkTheme()
            }

            SeenTheme(darkTheme = darkTheme) {
                SeenApplication(
                    onThemeChanged = { newTheme ->
                        themeMode = newTheme
                        preferencesManager.themeMode = newTheme
                    }
                )
            }
        }
    }
}

@Composable
fun SeenApplication(onThemeChanged: (String) -> Unit = {}) {
    val questions = PHQ9Data.questions
    val options = PHQ9Data.options

    var currentScreen by remember { mutableStateOf("welcome") }
    var currentQuestion by remember { mutableStateOf(0) }
    val scores = remember { mutableStateListOf<Int>() }
    val navigationStack = remember { mutableStateListOf<String>() }

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
            enabled = currentScreen != "welcome"
        ) {
            navigateBack()
        }

        // Main content
        when (currentScreen) {
            "welcome" -> {
                WelcomeScreen(
                    onStartQuiz = {
                        navigateTo("quiz")
                        currentQuestion = 0
                        scores.clear()
                    },
                    onGoToNotes = {
                        navigateTo("notes")
                    },
                    onGoToMoodHistory = {
                        navigateTo("mood_history")
                    },
                    onSecretDebugScreen = {
                        navigateTo("debug_database")
                    }
                )
            }
            "quiz" -> {
                if (currentQuestion < questions.size) {
                    QuestionScreen(
                        question = questions[currentQuestion].text,
                        questionIndex = currentQuestion,
                        totalQuestions = questions.size,
                        options = options.map { it.text to it.score },
                        onAnswerSelected = { score ->
                            scores.add(score)
                            currentQuestion++
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
                        navigateBack()
                    },
                    onThemeChanged = onThemeChanged
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
        }

        // Settings button - only show on welcome screen
        if (currentScreen == "welcome") {
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
        }
    }
}
