package dev.alphagame.seen.ai.models

/**
 * Response model for PHQ-9 AI analysis
 * @param emotional_state AI-generated analysis of the patient's emotional state
 * @param recommendations List of AI-generated recommendations for the patient
 * @param severity The severity level assessed by AI (e.g., "Mild", "Moderate", "Severe")
 */
data class PHQ9Response(
    val emotional_state: String?,
    val recommendations: List<String>?,
    val severity: String?
)
