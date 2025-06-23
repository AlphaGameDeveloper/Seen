package dev.alphagame.seen.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onStartQuiz: () -> Unit,
    onGoToNotes: () -> Unit,
    onGoToMoodHistory: () -> Unit = {},
    onSecretDebugScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    var clickCount by remember { mutableStateOf(0) }

    // Reset click count after 30 seconds of inactivity
    LaunchedEffect(clickCount) {
        if (clickCount > 0 && clickCount < 10) {
            kotlinx.coroutines.delay(30000) // 30 seconds
            clickCount = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title
        Text(
            text = "Seen",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                clickCount++

                when {
                    clickCount >= 10 -> {
                        // Open secret debug screen
                        onSecretDebugScreen()
                        clickCount = 0
                    }
                    clickCount >= 6 -> {
                        // Show toast for remaining clicks
                        val remaining = 10 - clickCount
                        // Cancel any existing toast
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).cancel()

                        // Show new toast
                        Toast.makeText(
                            context,
                            "🔧 Press $remaining more times to view the internal database",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle
        Text(
            text = "Mental Health Matters",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))
        /* <----------- PHQ-9 DESCRIPTION CARD
        // Description
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📋",
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PHQ-9 Assessment",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A confidential questionnaire to help assess your mental health and well-being over the past two weeks.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
        <----------- PHQ-9 DESCRIPTION CARD > */
        Spacer(modifier = Modifier.height(48.dp))

        // Start Button
        Button(
            onClick = onStartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Take PHQ-9 Assessment",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes Button
        Button(
            onClick = onGoToNotes,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(
                text = "📝 My Notes",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mood History Button
        Button(
            onClick = onGoToMoodHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Text(
                text = "😊 Mood History",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        /*
        Spacer(modifier = Modifier.height(24.dp))

        // Disclaimer
        Text(
            text = "This assessment is not a substitute for professional medical advice. If you're experiencing a mental health crisis, please contact emergency services or a mental health professional.\n\nApplication by Damien Boisvert & Alexander Cameron",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) */
    }
}
