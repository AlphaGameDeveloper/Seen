package dev.alphagame.seen.screens.results

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.ai.AIManager
import dev.alphagame.seen.ai.models.PHQ9Response
import dev.alphagame.seen.data.NotesManager
import dev.alphagame.seen.data.PreferencesManager
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
        totalScore <= 19 -> Triple("Moderately Severe", translation.resultSevere, Color(0xFFFF6347))
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
                    aiManager.submitPHQ9ForAnalysis(totalScore, scores)
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
                onBackToResults = { currentScreen = ResultScreenType.BASIC_RESULTS }
            )
        }
    }
}
