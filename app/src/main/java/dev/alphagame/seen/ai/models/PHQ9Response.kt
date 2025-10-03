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
