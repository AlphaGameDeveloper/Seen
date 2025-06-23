package dev.alphagame.seen.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "seen_database.db"
        private const val DATABASE_VERSION = 1

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

        db.execSQL(createNotesTable)
        db.execSQL(createPhq9Table)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PHQ9_RESULTS")
        onCreate(db)
    }

    fun clearAllData() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NOTES")
        db.execSQL("DELETE FROM $TABLE_PHQ9_RESULTS")
        db.close()
    }
}
