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
import dev.alphagame.seen.translations.rememberTranslation

enum class Mood(val id: Int, val emoji: String, val label: String) {
    VERY_HAPPY(1, "😄", "veryHappy"),
    HAPPY(2, "😊", "happy"),
    NEUTRAL(3, "😐", "neutral"),
    SAD(4, "😞", "sad"),
    VERY_SAD(5, "😢", "verySad"),
    ANGRY(6, "😠", "angry"),
    ANXIOUS(7, "😰", "anxious"),
    EXCITED(8, "🤩", "excited");

    companion object {
        fun fromId(id: Int): Mood? = values().find { it.id == id }
        fun fromLabel(label: String): Mood? = values().find { it.label == label }
        fun getLocalizedLabel(mood: Mood, translation: Translation): String = when (mood) {
            VERY_HAPPY -> translation.veryHappy
            HAPPY -> translation.happy
            NEUTRAL -> translation.neutral
            SAD -> translation.sad
            VERY_SAD -> translation.verySad
            ANGRY -> translation.angry
            ANXIOUS -> translation.anxious
            EXCITED -> translation.excited
        }
    }
}
