package dev.alphagame.seen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import dev.alphagame.seen.data.PHQ9Data
import dev.alphagame.seen.screens.WelcomeScreen
import dev.alphagame.seen.screens.QuestionScreen
import dev.alphagame.seen.screens.ResultScreen
import dev.alphagame.seen.screens.NotesScreen
import dev.alphagame.seen.ui.theme.SeenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SeenTheme {
                PHQ9App()
            }
        }
    }
}

@Composable
fun PHQ9App() {
    val questions = PHQ9Data.questions
    val options = PHQ9Data.options

    var currentScreen by remember { mutableStateOf("welcome") }
    var currentQuestion by remember { mutableStateOf(0) }
    val scores = remember { mutableStateListOf<Int>() }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (currentScreen) {
            "welcome" -> {
                WelcomeScreen(
                    onStartQuiz = { 
                        currentScreen = "quiz"
                        currentQuestion = 0
                        scores.clear()
                    },
                    onGoToNotes = {
                        currentScreen = "notes"
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
                        score = scores.sum(),
                        onRetakeQuiz = {
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
                        currentScreen = "welcome"
                    }
                )
            }
        }
    }
}