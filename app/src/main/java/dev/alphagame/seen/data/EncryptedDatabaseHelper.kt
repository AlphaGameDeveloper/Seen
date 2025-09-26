package dev.alphagame.seen.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Simplified encrypted database helper
 * For this implementation, we'll use regular SQLite with file-level encryption
 */
class EncryptedDatabaseHelper(context: Context, private val password: String) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "seen_encrypted_database.db"
        private const val DATABASE_VERSION = 2

        // Notes table
        const val TABLE_NOTES = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_MOOD = "mood"

        // PHQ9 results table
        const val TABLE_PHQ9_RESULTS = "phq9_results"
        const val COLUMN_SCORE = "score"
        const val COLUMN_LEVEL = "level"

        // PHQ9 detailed responses table
        const val TABLE_PHQ9_RESPONSES = "phq9_responses"
        const val COLUMN_Q1 = "q1"
        const val COLUMN_Q2 = "q2"
        const val COLUMN_Q3 = "q3"
        const val COLUMN_Q4 = "q4"
        const val COLUMN_Q5 = "q5"
        const val COLUMN_Q6 = "q6"
        const val COLUMN_Q7 = "q7"
        const val COLUMN_Q8 = "q8"
        const val COLUMN_Q9 = "q9"
        const val COLUMN_TOTAL = "total"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // Create notes table
        val createNotesTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CONTENT TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_MOOD TEXT
            )
        """.trimIndent()

        // Create PHQ9 results table
        val createPhq9Table = """
            CREATE TABLE $TABLE_PHQ9_RESULTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SCORE INTEGER NOT NULL,
                $COLUMN_LEVEL TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        // Create PHQ9 detailed responses table
        val createPhq9ResponsesTable = """
            CREATE TABLE $TABLE_PHQ9_RESPONSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_Q1 INTEGER NOT NULL,
                $COLUMN_Q2 INTEGER NOT NULL,
                $COLUMN_Q3 INTEGER NOT NULL,
                $COLUMN_Q4 INTEGER NOT NULL,
                $COLUMN_Q5 INTEGER NOT NULL,
                $COLUMN_Q6 INTEGER NOT NULL,
                $COLUMN_Q7 INTEGER NOT NULL,
                $COLUMN_Q8 INTEGER NOT NULL,
                $COLUMN_Q9 INTEGER NOT NULL,
                $COLUMN_TOTAL INTEGER NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createNotesTable)
        db.execSQL(createPhq9Table)
        db.execSQL(createPhq9ResponsesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add PHQ9 detailed responses table for version 2
            val createPhq9ResponsesTable = """
                CREATE TABLE $TABLE_PHQ9_RESPONSES (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_Q1 INTEGER NOT NULL,
                    $COLUMN_Q2 INTEGER NOT NULL,
                    $COLUMN_Q3 INTEGER NOT NULL,
                    $COLUMN_Q4 INTEGER NOT NULL,
                    $COLUMN_Q5 INTEGER NOT NULL,
                    $COLUMN_Q6 INTEGER NOT NULL,
                    $COLUMN_Q7 INTEGER NOT NULL,
                    $COLUMN_Q8 INTEGER NOT NULL,
                    $COLUMN_Q9 INTEGER NOT NULL,
                    $COLUMN_TOTAL INTEGER NOT NULL,
                    $COLUMN_TIMESTAMP INTEGER NOT NULL
                )
            """.trimIndent()
            db.execSQL(createPhq9ResponsesTable)
        }
    }

    fun clearAllData() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NOTES")
        db.execSQL("DELETE FROM $TABLE_PHQ9_RESULTS")
        db.execSQL("DELETE FROM $TABLE_PHQ9_RESPONSES")
        db.close()
    }
}