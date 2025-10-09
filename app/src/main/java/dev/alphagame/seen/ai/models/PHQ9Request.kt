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
    val moodEntries: List<String>? = null,
    val language: String
)
