package dev.alphagame.seen.data

data class PHQ9Question(
    val text: String
)

data class PHQ9Option(
    val text: String,
    val score: Int
)

object PHQ9Data {
    val questions = listOf(
        PHQ9Question("Little interest or pleasure in doing things?"),
        PHQ9Question("Feeling down, depressed, or hopeless?"),
        PHQ9Question("Trouble falling or staying asleep, or sleeping too much?"),
        PHQ9Question("Feeling tired or having little energy?"),
        PHQ9Question("Poor appetite or overeating?"),
        PHQ9Question("Feeling bad about yourself?"),
        PHQ9Question("Trouble concentrating on things?"),
        PHQ9Question("Moving or speaking slowly or being fidgety/restless?"),
        PHQ9Question("Thoughts that you would be better off dead, or of hurting yourself?")
    )

    val options = listOf(
        PHQ9Option("😐 Not at all", 0),
        PHQ9Option("😕 Several days", 1),
        PHQ9Option("😞 More than half the days", 2),
        PHQ9Option("😢 Nearly every day", 3)
    )
}
