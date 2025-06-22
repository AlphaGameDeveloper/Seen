package dev.alphagame.seen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import dev.alphagame.seen.data.PHQ9Data
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

    var currentQuestion by remember { mutableStateOf(0) }
    val scores = remember { mutableStateListOf<Int>() }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
            ResultScreen(score = scores.sum())
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuestionScreen(
    question: String,
    questionIndex: Int,
    totalQuestions: Int,
    options: List<Pair<String, Int>>,
    onAnswerSelected: (Int) -> Unit
) {
    AnimatedContent(
        targetState = question,
        transitionSpec = {
            (slideInHorizontally { it / 2 } + fadeIn()) with
                    (slideOutHorizontally { -it / 2 } + fadeOut())
        },
        label = "PHQ9Question"
    ) { targetQuestion ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Question ${questionIndex + 1} of $totalQuestions",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = targetQuestion,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                options.forEach { (text, value) ->
                    Button(
                        onClick = { onAnswerSelected(value) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(text, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultScreen(score: Int) {
    val context = LocalContext.current
    val (level, color) = when {
        score <= 4 -> "Minimal" to Color(0xFF6ECB63)
        score <= 9 -> "Mild" to Color(0xFFFFD700)
        score <= 14 -> "Moderate" to Color(0xFFFFA500)
        score <= 19 -> "Moderately Severe" to Color(0xFFFF6347)
        else -> "Severe" to Color(0xFFFF3C38)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your PHQ-9 Score:", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        Text("$score", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Depression Level: $level", fontSize = 20.sp, color = color)

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://988lifeline.org"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("📞 Talk to Someone – 988", color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nimh.nih.gov/health/topics/depression"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("📚 Learn About Depression", color = MaterialTheme.colorScheme.onSurface)
            }

            val activity = LocalContext.current as? MainActivity
            Button(
                onClick = {
                    // Reset the quiz
                    activity?.recreate()
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("🔄 Retake the Quiz", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
