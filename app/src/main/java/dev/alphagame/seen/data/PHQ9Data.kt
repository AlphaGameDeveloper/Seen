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

package dev.alphagame.seen.data

import dev.alphagame.seen.translations.Translation

data class PHQ9Question(
    val index: Int
)

data class PHQ9Option(
    val score: Int
)

object PHQ9Data {
    val questions = listOf(
        PHQ9Question(0),
        PHQ9Question(1),
        PHQ9Question(2),
        PHQ9Question(3),
        PHQ9Question(4),
        PHQ9Question(5),
        PHQ9Question(6),
        PHQ9Question(7),
        PHQ9Question(8)
    )

    val options = listOf(
        PHQ9Option(0),
        PHQ9Option(1),
        PHQ9Option(2),
        PHQ9Option(3)
    )

    fun getQuestionText(index: Int, translation: Translation): String {
        return if (index < translation.phq9Questions.size) {
            translation.phq9Questions[index]
        } else {
            "Question not found"
        }
    }

    fun getOptionText(index: Int, translation: Translation): String {
        return if (index < translation.phq9Options.size) {
            translation.phq9Options[index]
        } else {
            "Option not found"
        }
    }
}
