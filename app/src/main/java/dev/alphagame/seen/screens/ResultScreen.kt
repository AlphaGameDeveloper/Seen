package dev.alphagame.seen.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.MainActivity
import dev.alphagame.seen.data.NotesManager
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.translations.rememberTranslation

@Composable
fun ResultScreen(scores: List<Int>, onRetakeQuiz: () -> Unit) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val notesManager = remember { NotesManager(context) }
    val preferencesManager = remember { PreferencesManager(context) }

    val buttonModifier = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        .fillMaxWidth()

    val totalScore = scores.sum()
    val (level, levelText, color) = when {
        totalScore <= 4 -> Triple("Minimal", translation.resultMinimal, Color(0xFF6ECB63))
        totalScore <= 9 -> Triple("Mild", translation.resultMild, Color(0xFFFFD700))
        totalScore <= 14 -> Triple("Moderate", translation.resultModerate, Color(0xFFFFA500))
        totalScore <= 19 -> Triple("Moderately Severe", translation.resultSevere, Color(0xFFFF6347))
        else -> Triple("Severe", translation.resultSevere, Color(0xFFFF3C38))
    }

    // Save PHQ-9 data if enabled in settings
    if (preferencesManager.isPhq9DataStorageEnabled) {
        // Save summary result
        notesManager.savePHQ9Result(totalScore, level)
        // Save detailed responses
        notesManager.savePHQ9Responses(scores)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(translation.resultScore, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        Text("$totalScore", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("$levelText", fontSize = 20.sp, color = color)
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // open phone dialer with 988
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(20.dp),
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(translation.talkToSomeone, color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nimh.nih.gov/health/topics/depression"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(20.dp),
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(translation.learnAboutDepression, color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = onRetakeQuiz,
                shape = RoundedCornerShape(20.dp),
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(translation.returnToHome, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
