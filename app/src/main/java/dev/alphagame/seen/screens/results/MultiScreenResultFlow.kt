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

package dev.alphagame.seen.screens.results

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import dev.alphagame.seen.ai.AIManager
import dev.alphagame.seen.ai.models.PHQ9Response
import dev.alphagame.seen.data.NotesManager
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.Timestamps
import dev.alphagame.seen.translations.rememberTranslation
import kotlinx.coroutines.launch

enum class ResultScreenType {
    BASIC_RESULTS,
    AI_ANALYSIS,
    RESOURCES
}

sealed class AIAnalysisState {
    object Idle : AIAnalysisState()
    object Loading : AIAnalysisState()
    object Success : AIAnalysisState()
    data class Error(val message: String) : AIAnalysisState()
}

@Composable
fun MultiScreenResultFlow(
    scores: List<Int>,
    onRetakeQuiz: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(ResultScreenType.BASIC_RESULTS) }
    val context = LocalContext.current
    val translation = rememberTranslation()
    val notesManager = remember { NotesManager(context) }
    val preferencesManager = remember { PreferencesManager(context) }
    val aiManager = remember { AIManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // AI Analysis State
    var aiAnalysisState by remember { mutableStateOf<AIAnalysisState>(AIAnalysisState.Idle) }
    var aiResponse by remember { mutableStateOf<PHQ9Response?>(null) }

    val totalScore = scores.sum()
    val (level, levelText, color) = when {
        totalScore <= 4 -> Triple("Minimal", translation.resultMinimal, Color(0xFF6ECB63))
        totalScore <= 9 -> Triple("Mild", translation.resultMild, Color(0xFFFFD700))
        totalScore <= 14 -> Triple("Moderate", translation.resultModerate, Color(0xFFFFA500))
        totalScore <= 19 -> Triple("Moderately Severe", translation.phq9SeverityModeratelySevere, Color(0xFFFF6347))
        else -> Triple("Severe", translation.resultSevere, Color(0xFFFF3C38))
    }

    // Save PHQ-9 data if enabled in settings
    LaunchedEffect(scores) {
        if (preferencesManager.isPhq9DataStorageEnabled) {
            notesManager.savePHQ9Result(totalScore, level)
            notesManager.savePHQ9Responses(scores)
        }
    }

    // Start AI analysis when component loads
    LaunchedEffect(scores) {
        if (scores.isNotEmpty()) {
            aiAnalysisState = AIAnalysisState.Loading
            coroutineScope.launch {
                try {
                    // Gather notes and mood entries
                    val notes = notesManager.getAllNotes()
                        .sortedBy { it.timestamp }
                        .joinToString("\n") { "[${Timestamps.formatTimestamp(it.timestamp)}] mood:'${it.mood}', entry: '${it.content}'" }
                        val widgetMoodManager = dev.alphagame.seen.data.WidgetMoodManager(context)
                        val moodEntries = widgetMoodManager.getMoodEntries()
                            .sortedBy { it.timestamp }
                            .map { "${it.timestamp}: ${it.mood.label}" } // it.mood.emoji can be used but not sure

                    aiManager.submitPHQ9ForAnalysis(totalScore, scores, notes, moodEntries)
                        .onSuccess { response ->
                            Log.d("ResultScreen", "AI analysis successful")
                            aiResponse = response
                            aiAnalysisState = AIAnalysisState.Success
                        }
                        .onFailure { error ->
                            Log.e("ResultScreen", "AI analysis failed: ${error.message}")
                            aiAnalysisState = AIAnalysisState.Error(error.message ?: "Unknown error")
                        }
                } catch (e: Exception) {
                    Log.e("ResultScreen", "Unexpected error during AI analysis", e)
                    aiAnalysisState = AIAnalysisState.Error(e.message ?: "Unexpected error")
                }
            }
        }
    }

    when (currentScreen) {
        ResultScreenType.BASIC_RESULTS -> {
            BasicResultsScreen(
                totalScore = totalScore,
                levelText = levelText,
                color = color,
                aiAnalysisState = aiAnalysisState,
                onViewAIAnalysis = { currentScreen = ResultScreenType.AI_ANALYSIS },
                onViewResources = { currentScreen = ResultScreenType.RESOURCES },
                onRetakeQuiz = onRetakeQuiz
            )
        }

        ResultScreenType.AI_ANALYSIS -> {
            AIAnalysisScreen(
                aiAnalysisState = aiAnalysisState,
                aiResponse = aiResponse,
                totalScore = totalScore,
                levelText = levelText,
                color = color,
                onBackToResults = { currentScreen = ResultScreenType.BASIC_RESULTS },
                onViewResources = { currentScreen = ResultScreenType.RESOURCES }
            )
        }

        ResultScreenType.RESOURCES -> {
            ResourcesScreen(
                totalScore = totalScore,
                levelText = levelText,
                color = color,
                onBackToResults = { currentScreen = ResultScreenType.BASIC_RESULTS },
                scores = scores
            )
        }
    }
}
