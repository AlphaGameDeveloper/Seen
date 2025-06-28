package dev.alphagame.seen.ai.utils

/**
 * Utility functions for PHQ-9 AI integration
 */
object PHQ9Utils {

    /**
     * Validates PHQ-9 responses
     * @param responses List of responses (should be 9 items, each 0-3)
     * @return True if valid, false otherwise
     */
    fun validateResponses(responses: List<Int>): Boolean {
        return responses.size == 9 && responses.all { it in 0..3 }
    }

    /**
     * Calculates total PHQ-9 score from responses
     * @param responses List of individual responses
     * @return Total score
     */
    fun calculateTotalScore(responses: List<Int>): Int {
        return responses.sum()
    }

    /**
     * Gets severity level from total score
     * @param totalScore The calculated total score
     * @return Severity level string
     */
    fun getSeverityLevel(totalScore: Int): String {
        return when {
            totalScore <= 4 -> "Minimal"
            totalScore <= 9 -> "Mild"
            totalScore <= 14 -> "Moderate"
            totalScore <= 19 -> "Moderately Severe"
            else -> "Severe"
        }
    }

    /**
     * Checks if AI analysis should be recommended based on score
     * @param totalScore The calculated total score
     * @return True if AI analysis is recommended
     */
    fun shouldRecommendAIAnalysis(totalScore: Int): Boolean {
        // Recommend AI analysis for mild depression and above
        return totalScore >= 5
    }
}
