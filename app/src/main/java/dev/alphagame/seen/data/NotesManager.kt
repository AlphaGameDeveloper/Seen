package dev.alphagame.seen.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

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

        Log.d("NotesManager", "Retrieved PHQ-9 history: $results, count=${results.size}")
        return results
    }

    fun savePHQ9Responses(responses: List<Int>): Long {
        val db = dbHelper.writableDatabase
        val total = responses.sum()
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_Q1, responses.getOrElse(0) { 0 })
            put(DatabaseHelper.COLUMN_Q2, responses.getOrElse(1) { 0 })
            put(DatabaseHelper.COLUMN_Q3, responses.getOrElse(2) { 0 })
            put(DatabaseHelper.COLUMN_Q4, responses.getOrElse(3) { 0 })
            put(DatabaseHelper.COLUMN_Q5, responses.getOrElse(4) { 0 })
            put(DatabaseHelper.COLUMN_Q6, responses.getOrElse(5) { 0 })
            put(DatabaseHelper.COLUMN_Q7, responses.getOrElse(6) { 0 })
            put(DatabaseHelper.COLUMN_Q8, responses.getOrElse(7) { 0 })
            put(DatabaseHelper.COLUMN_Q9, responses.getOrElse(8) { 0 })
            put(DatabaseHelper.COLUMN_TOTAL, total)
            put(DatabaseHelper.COLUMN_TIMESTAMP, System.currentTimeMillis())
        }

        val id = db.insert(DatabaseHelper.TABLE_PHQ9_RESPONSES, null, values)
        db.close()
        return id
    }

    fun getPHQ9Responses(): List<PHQ9Response> {
        val responses = mutableListOf<PHQ9Response>()
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PHQ9_RESPONSES,
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
                val q1 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q1))
                val q2 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q2))
                val q3 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q3))
                val q4 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q4))
                val q5 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q5))
                val q6 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q6))
                val q7 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q7))
                val q8 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q8))
                val q9 = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q9))
                val total = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL))
                val timestamp = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP))

                responses.add(PHQ9Response(id, q1, q2, q3, q4, q5, q6, q7, q8, q9, total, timestamp))
            }
        }

        cursor.close()
        db.close()

        Log.d("NotesManager", "Retrieved PHQ-9 responses: $responses, count=${responses.size}")
        return responses
    }
}

data class PHQ9Result(
    val id: Long,
    val score: Int,
    val level: String,
    val timestamp: Long
)

data class PHQ9Response(
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
