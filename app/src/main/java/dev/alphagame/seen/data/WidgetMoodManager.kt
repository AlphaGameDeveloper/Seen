package dev.alphagame.seen.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

data class MoodEntry(
    val mood: Mood,
    val timestamp: Date
)

class WidgetMoodManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "widget_mood_prefs", 
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val MAX_ENTRIES = 100 // Keep last 100 entries
    }
    
    fun saveMood(mood: Mood) {
        val entries = getMoodEntries().toMutableList()
        val currentTime = Date()
        val oneMinuteAgo = Date(currentTime.time - 60 * 1000) // 1 minute in milliseconds
        
        // Remove any entries from the last minute (they will be overwritten)
        entries.removeAll { it.timestamp >= oneMinuteAgo }
        
        // Add the new mood entry at the beginning
        entries.add(0, MoodEntry(mood, currentTime))
        
        // Keep only the most recent entries
        val trimmedEntries = if (entries.size > MAX_ENTRIES) {
            entries.take(MAX_ENTRIES)
        } else {
            entries
        }
        
        val json = gson.toJson(trimmedEntries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, json).apply()
    }
    
    fun getLastMood(): MoodEntry? {
        return getMoodEntries().firstOrNull()
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        val json = prefs.getString(KEY_MOOD_ENTRIES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getTodaysMoods(): List<MoodEntry> {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        val startOfDay = today.time
        
        return getMoodEntries().filter { it.timestamp >= startOfDay }
    }
    
    fun getMoodsForDate(date: Date): List<MoodEntry> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.time
        
        return getMoodEntries().filter { 
            it.timestamp >= startOfDay && it.timestamp < endOfDay 
        }
    }
    
    fun clearAllMoods() {
        prefs.edit().remove(KEY_MOOD_ENTRIES).apply()
    }
}
