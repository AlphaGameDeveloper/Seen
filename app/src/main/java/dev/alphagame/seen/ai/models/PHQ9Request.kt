package dev.alphagame.seen.ai.models

/**
 * Request model for PHQ-9 AI analysis
 * @param total The total PHQ-9 score (sum of all responses)
 * @param responses List of individual responses for each of the 9 questions (0-3 scale)
 */
data class PHQ9Request(
    val total: Int,
    val responses: List<Int>
)
