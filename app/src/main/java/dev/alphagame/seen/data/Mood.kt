package dev.alphagame.seen.data
import dev.alphagame.seen.translations.Translation
import dev.alphagame.seen.translations.rememberTranslation

enum class Mood(val id: Int, val emoji: String, val label: String) {
    VERY_HAPPY(1, "😄", "Very Happy"),
    HAPPY(2, "😊", "Happy"),
    NEUTRAL(3, "😐", "Neutral"),
    SAD(4, "😞", "Sad"),
    VERY_SAD(5, "😢", "Very Sad"),
    ANGRY(6, "😠", "Angry"),
    ANXIOUS(7, "😰", "Anxious"),
    EXCITED(8, "🤩", "Excited");

    companion object {
        fun fromId(id: Int): Mood? = values().find { it.id == id }
        fun fromLabel(label: String): Mood? = values().find { it.label == label }
    }
}
