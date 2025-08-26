package dev.alphagame.seen.ai.models

/**
 * Request model for PHQ-9 AI analysis
 * @param total The total PHQ-9 score (sum of all responses)
 * @param responses List of individual responses for each of the 9 questions (0-3 scale)
 * @param notes Optional notes to send to the AI
 * @param moodEntries Optional mood entries to send to the AI
 */
data class PHQ9Request(
    val total: Int,
    val responses: List<Int>,
    val notes: String? = null,
    val moodEntries: List<String>? = null
)
