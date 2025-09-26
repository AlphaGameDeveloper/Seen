package dev.alphagame.seen.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Simple encrypted data storage using EncryptedSharedPreferences
 * This provides basic encryption for sensitive data without complex database setup
 */
class EncryptedDataManager(private val context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
        
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "encrypted_data",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    private val gson = Gson()
    
    companion object {
        private const val KEY_NOTES = "encrypted_notes"
        private const val KEY_PHQ9_RESULTS = "encrypted_phq9_results"
        private const val KEY_PHQ9_RESPONSES = "encrypted_phq9_responses"
    }
    
    // Data classes for encrypted storage
    data class EncryptedNote(
        val id: Long,
        val content: String,
        val timestamp: Long,
        val mood: String?
    )
    
    data class EncryptedPhq9Result(
        val id: Long,
        val score: Int,
        val level: String,
        val timestamp: Long
    )
    
    data class EncryptedPhq9Response(
        val id: Long,
        val q1: Int,
        val q2: Int,
        val q3: Int,
        val q4: Int,
        val q5: Int,
        val q6: Int,
        val q7: Int,
        val q8: Int,
        val q9: Int,
        val total: Int,
        val timestamp: Long
    )
    
    // Notes management
    fun saveNote(note: EncryptedNote) {
        val notes = getNotes().toMutableList()
        val existingIndex = notes.indexOfFirst { it.id == note.id }
        if (existingIndex >= 0) {
            notes[existingIndex] = note
        } else {
            notes.add(note)
        }
        saveNotes(notes)
    }
    
    fun getNotes(): List<EncryptedNote> {
        val notesJson = encryptedPrefs.getString(KEY_NOTES, null) ?: return emptyList()
        val type = object : TypeToken<List<EncryptedNote>>() {}.type
        return try {
            gson.fromJson(notesJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveNotes(notes: List<EncryptedNote>) {
        val notesJson = gson.toJson(notes)
        encryptedPrefs.edit().putString(KEY_NOTES, notesJson).apply()
    }
    
    fun deleteNote(noteId: Long) {
        val notes = getNotes().filterNot { it.id == noteId }
        saveNotes(notes)
    }
    
    // PHQ9 Results management
    fun savePhq9Result(result: EncryptedPhq9Result) {
        val results = getPhq9Results().toMutableList()
        results.add(result)
        val resultsJson = gson.toJson(results)
        encryptedPrefs.edit().putString(KEY_PHQ9_RESULTS, resultsJson).apply()
    }
    
    fun getPhq9Results(): List<EncryptedPhq9Result> {
        val resultsJson = encryptedPrefs.getString(KEY_PHQ9_RESULTS, null) ?: return emptyList()
        val type = object : TypeToken<List<EncryptedPhq9Result>>() {}.type
        return try {
            gson.fromJson(resultsJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // PHQ9 Responses management
    fun savePhq9Response(response: EncryptedPhq9Response) {
        val responses = getPhq9Responses().toMutableList()
        responses.add(response)
        val responsesJson = gson.toJson(responses)
        encryptedPrefs.edit().putString(KEY_PHQ9_RESPONSES, responsesJson).apply()
    }
    
    fun getPhq9Responses(): List<EncryptedPhq9Response> {
        val responsesJson = encryptedPrefs.getString(KEY_PHQ9_RESPONSES, null) ?: return emptyList()
        val type = object : TypeToken<List<EncryptedPhq9Response>>() {}.type
        return try {
            gson.fromJson(responsesJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Clear all data
    fun clearAllData() {
        encryptedPrefs.edit().clear().apply()
    }
    
    // Get next ID for new entries
    fun getNextNoteId(): Long {
        val notes = getNotes()
        return if (notes.isEmpty()) 1 else notes.maxOf { it.id } + 1
    }
    
    fun getNextPhq9Id(): Long {
        val results = getPhq9Results()
        val responses = getPhq9Responses()
        val maxResultId = if (results.isEmpty()) 0 else results.maxOf { it.id }
        val maxResponseId = if (responses.isEmpty()) 0 else responses.maxOf { it.id }
        return maxOf(maxResultId, maxResponseId) + 1
    }
}