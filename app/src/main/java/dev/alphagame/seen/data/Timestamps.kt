package dev.alphagame.seen.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Timestamps {
    companion object {
        fun formatTimestamp(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return format.format(date)
        }
    }
}
