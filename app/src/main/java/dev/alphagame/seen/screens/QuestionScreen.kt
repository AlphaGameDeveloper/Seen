package dev.alphagame.seen.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.data.PHQ9Data
import dev.alphagame.seen.translations.rememberTranslation

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuestionScreen(
    question: String,
    questionIndex: Int,
    totalQuestions: Int,
    options: List<Pair<String, Int>>,
    onAnswerSelected: (Int) -> Unit,
    onBack: () -> Unit = {}
) {
    val translation = rememberTranslation()

    val buttonModifier = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        .fillMaxWidth()

    AnimatedContent(
        targetState = questionIndex,
        transitionSpec = {
            // slide left when going forward, slide right when going back
            if (targetState > initialState) {
                (slideInHorizontally { it / 2 } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 2 } + fadeOut())
            } else {
                (slideInHorizontally { -it / 2 } + fadeIn()) togetherWith
                        (slideOutHorizontally { it / 2 } + fadeOut())
            }
        },
        label = "PHQ9Question"
    ) { targetIndex ->
        // Derive the question text from the animated target index so the
        // content shown matches the animation direction (prevents mismatch)
        val displayedQuestion = PHQ9Data.getQuestionText(targetIndex, translation)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Text(
                text = String.format(translation.questionProgress, targetIndex + 1, totalQuestions),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = displayedQuestion,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                options.forEach { (text, value) ->
                    Button(
                        onClick = { onAnswerSelected(value) },
                        shape = RoundedCornerShape(20.dp),
                        modifier = buttonModifier
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(text, fontSize = 21.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
