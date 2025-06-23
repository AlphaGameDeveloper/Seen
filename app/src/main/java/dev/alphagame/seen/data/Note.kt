package dev.alphagame.seen.data

data class Note(
    val id: Long = 0,
    val content: String,
    val timestamp: Long,
    val mood: String? = null
)
