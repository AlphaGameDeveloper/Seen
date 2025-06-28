package dev.alphagame.seen.ai.examples

import android.content.Context
import dev.alphagame.seen.ai.AIManager
import dev.alphagame.seen.ai.utils.PHQ9Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Example usage of the AI Manager for PHQ-9 analysis
 * This demonstrates how to integrate AI analysis into your application
 */
class AIManagerExample(private val context: Context) {

    private val aiManager = AIManager(context)

    /**
     * Example: Analyze PHQ-9 responses and handle results
     */
    suspend fun analyzePHQ9Responses(
        responses: List<Int>,
        scope: CoroutineScope
    ) {
        // Validate responses first
        if (!PHQ9Utils.validateResponses(responses)) {
            println("Invalid PHQ-9 responses. Must be exactly 9 responses with values 0-3.")
            return
        }

        // Calculate total score
        val totalScore = PHQ9Utils.calculateTotalScore(responses)
        val severity = PHQ9Utils.getSeverityLevel(totalScore)

        println("PHQ-9 Results:")
        println("Total Score: $totalScore")
        println("Severity: $severity")

        // Check if AI analysis is recommended
        if (PHQ9Utils.shouldRecommendAIAnalysis(totalScore)) {
            println("Requesting AI analysis...")

            // Submit for AI analysis
            scope.launch {
                aiManager.submitPHQ9ForAnalysis(totalScore, responses)
                    .onSuccess { aiResponse ->
                        println("\n=== AI Analysis ===")
                        println("AI Severity Assessment: ${aiResponse.severity}")
                        println("Emotional State: ${aiResponse.emotional_state}")
                        println("\nRecommendations:")
                        aiResponse.recommendations?.forEachIndexed { index, recommendation ->
                            println("${index + 1}. $recommendation")
                        }
                    }
                    .onFailure { error ->
                        println("AI analysis failed: ${error.message}")
                        println("Falling back to traditional assessment...")

                        // Provide fallback recommendations based on score
                        provideFallbackRecommendations(totalScore)
                    }
            }
        } else {
            println("Score indicates minimal symptoms. AI analysis not required.")
            provideFallbackRecommendations(totalScore)
        }
    }

    /**
     * Example: Check service availability before using AI features
     */
    suspend fun checkAIServiceStatus(): Boolean {
        return try {
            val isAvailable = aiManager.isAIServiceAvailable()
            println("AI Service Status: ${if (isAvailable) "Available" else "Unavailable"}")
            isAvailable
        } catch (e: Exception) {
            println("Failed to check AI service status: ${e.message}")
            false
        }
    }

    /**
     * Provide basic recommendations when AI is not available
     */
    private fun provideFallbackRecommendations(totalScore: Int) {
        val recommendations = when {
            totalScore <= 4 -> listOf(
                "Continue maintaining healthy lifestyle habits",
                "Stay connected with friends and family",
                "Consider regular exercise and good sleep hygiene"
            )
            totalScore <= 9 -> listOf(
                "Consider speaking with a counselor or therapist",
                "Practice stress management techniques",
                "Maintain regular social connections",
                "Focus on self-care activities"
            )
            totalScore <= 14 -> listOf(
                "Strongly consider professional counseling",
                "Contact your healthcare provider",
                "Avoid isolation and maintain social support",
                "Consider mindfulness or meditation practices"
            )
            else -> listOf(
                "Seek professional help immediately",
                "Contact a mental health crisis line if needed",
                "Reach out to trusted friends or family",
                "Consider contacting your healthcare provider today"
            )
        }

        println("\nGeneral Recommendations:")
        recommendations.forEachIndexed { index, recommendation ->
            println("${index + 1}. $recommendation")
        }
    }
}

/**
 * Sample usage
 */
class ExampleUsage {
    fun demonstrateAIManager(context: Context, scope: CoroutineScope) {
        val example = AIManagerExample(context)

        // Example PHQ-9 responses (9 questions, each scored 0-3)
        val sampleResponses = listOf(2, 1, 3, 2, 2, 1, 2, 1, 1) // Total: 15 (Moderate)

        scope.launch {
            // Check service availability first
            val isAvailable = example.checkAIServiceStatus()

            if (isAvailable) {
                // Proceed with AI analysis
                example.analyzePHQ9Responses(sampleResponses, scope)
            } else {
                println("AI service unavailable, using fallback analysis")
                // Could still use the traditional scoring system
            }
        }
    }
}
