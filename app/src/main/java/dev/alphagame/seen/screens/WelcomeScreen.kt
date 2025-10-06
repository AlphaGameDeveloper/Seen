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

package dev.alphagame.seen.screens

import android.widget.Toast
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.FeatureFlags
import dev.alphagame.seen.analytics.AnalyticsManager
import dev.alphagame.seen.data.LogoSelector
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.WelcomeScreenMessage
import dev.alphagame.seen.translations.rememberTranslation

@Composable
fun WelcomeScreen(
    onStartQuiz: () -> Unit,
    onGoToNotes: () -> Unit,
    onGoToMoodHistory: () -> Unit = {},
    onSecretDebugScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val analyticsManager = remember { AnalyticsManager(context) }
    var clickCount by remember { mutableStateOf(0) }
    val preferencesManager = remember { PreferencesManager(context) }

    // Track welcome screen access
    LaunchedEffect(Unit) {
        analyticsManager.trackEvent("welcome_screen_accessed")
    }

    // Reset click count after 30 seconds of inactivity
    LaunchedEffect(clickCount) {
        if (clickCount in 1..9) {
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
        if (FeatureFlags.HOME_SEEN_LOGO) {
            // are we in dark mode?
            val logoRes = LogoSelector.themedIcon(context)

            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(logoRes),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(120.dp)
            )
        }
        // App Title
        Text(
            text = translation.appName,
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
                        if (FeatureFlags.DEBUG_DB_SCREEN_TOAST_MESSAGE) {
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
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (FeatureFlags.WELCOME_ANIMATED_SUBTITLE) {
            WelcomeScreenMessageDisplayDynamic()
        } else {
            WelcomeScreenMessageDisplayStatic()
        }

        // Start Button
        Button(
            onClick = {
                // Track PHQ-9 start from welcome screen
                analyticsManager.trackEvent("phq9_started_from_welcome")
                onStartQuiz()
            },
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
                text = translation.startQuiz,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes Button
        Button(
            onClick = {
                // Track notes screen access from welcome
                analyticsManager.trackEvent("notes_accessed_from_welcome")
                onGoToNotes()
            },
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
                text = "📝 ${translation.viewNotes}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mood History Button
        Button(
            onClick = {
                // Track mood history access from welcome
                analyticsManager.trackEvent("mood_history_accessed_from_welcome")
                onGoToMoodHistory()
            },
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
                text = "😊 ${translation.viewMoodHistory}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun WelcomeScreenMessageDisplayStatic() {
    val default = ""
    val message = remember { mutableStateOf<String>(default) }

    @Composable
    fun _Update() {
        if (message.value == default) {
            message.value = WelcomeScreenMessage.getRandomWelcomeScreenMessage()
        }
    }
    _Update()

    Text(
        text = message.value,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(70.dp))
}

@Composable
private fun WelcomeScreenMessageDisplayDynamic() {
    // Update the message every x seconds

    val message = remember { mutableStateOf<String>("Howdy!") }
    @Composable
    fun UpdateWelcomeMessage(message: MutableState<String>) {
        val getMessage = WelcomeScreenMessage.getRandomWelcomeScreenMessageGenerator()
        LaunchedEffect(Unit) {
            while (true) {
                message.value = getMessage()
                kotlinx.coroutines.delay(5000) // Change message every 3 seconds
            }
        }
    }

    UpdateWelcomeMessage(message)

    androidx.compose.animation.AnimatedContent(
        targetState = message.value,
        transitionSpec = {
            androidx.compose.animation.ContentTransform(
                targetContentEnter = fadeIn(),
                initialContentExit = fadeOut()
            )
        }
    ) { targetMessage ->
        Text(
            text = targetMessage,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(70.dp))
}
