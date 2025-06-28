package dev.alphagame.seen.screens

import androidx.compose.runtime.Composable
import dev.alphagame.seen.screens.results.MultiScreenResultFlow

@Composable
fun ResultScreen(scores: List<Int>, onRetakeQuiz: () -> Unit) {
    MultiScreenResultFlow(
        scores = scores,
        onRetakeQuiz = onRetakeQuiz
    )
}
