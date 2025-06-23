package dev.alphagame.seen.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class NotesManager(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun saveNote(content: String, mood: String? = null): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CONTENT, content)
            put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis())
            put(DatabaseHelper.COLUMN_MOOD, mood)
        }
        
        val id = db.insert(DatabaseHelper.TABLE_NOTES, null, values)
        db.close()
        return id
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = dbHelper.readableDatabase
        
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_TIMESTAMP} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val content = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT))
                val timestamp = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP))
                val mood = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOOD))
                
                notes.add(Note(id, content, timestamp, mood))
            }
        }
        
        cursor.close()
        db.close()
        return notes
    }

    fun deleteNote(noteId: Long): Boolean {
        val db = dbHelper.writableDatabase
        val deletedRows = db.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(noteId.toString())
        )
        db.close()
        return deletedRows > 0
    }

    fun updateNote(noteId: Long, content: String, mood: String? = null): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CONTENT, content)
            put(DatabaseHelper.COLUMN_MOOD, mood)
        }
        
        val updatedRows = db.update(
            DatabaseHelper.TABLE_NOTES,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(noteId.toString())
        )
        db.close()
        return updatedRows > 0
    }

    fun savePHQ9Result(score: Int, level: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_SCORE, score)
            put(DatabaseHelper.COLUMN_LEVEL, level)
            put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis())
        }
        
        val id = db.insert(DatabaseHelper.TABLE_PHQ9_RESULTS, null, values)
        db.close()
        return id
    }

    fun getPHQ9History(): List<PHQ9Result> {
        val results = mutableListOf<PHQ9Result>()
        val db = dbHelper.readableDatabase
        
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PHQ9_RESULTS,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_TIMESTAMP} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val score = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE))
                val level = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL))
                val timestamp = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP))
                
                results.add(PHQ9Result(id, score, level, timestamp))
            }
        }
        
        cursor.close()
        db.close()
        return results
    }
}

data class PHQ9Result(
    val id: Long,
    val score: Int,
    val level: String,
    val timestamp: Long
)
