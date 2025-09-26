package dev.alphagame.seen.security

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import dev.alphagame.seen.data.DatabaseHelper
import dev.alphagame.seen.data.EncryptedDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataMigrationManager(private val context: Context) {
    
    /**
     * Migrates data from unencrypted database to encrypted database
     */
    suspend fun migrateToEncrypted(pin: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val unencryptedDb = DatabaseHelper(context)
            val encryptedDb = EncryptedDatabaseHelper(context, pin)
            
            // Get readable databases
            val sourceDb = unencryptedDb.readableDatabase
            val targetDb = encryptedDb.writableDatabase
            
            // Begin transaction for atomic migration
            targetDb.beginTransaction()
            
            try {
                // Migrate notes
                migrateNotesTable(sourceDb, targetDb)
                
                // Migrate PHQ9 results
                migratePhq9ResultsTable(sourceDb, targetDb)
                
                // Migrate PHQ9 responses
                migratePhq9ResponsesTable(sourceDb, targetDb)
                
                // Mark transaction as successful
                targetDb.setTransactionSuccessful()
                
                // Close databases
                sourceDb.close()
                targetDb.endTransaction()
                targetDb.close()
                
                // Delete the unencrypted database file
                deleteUnencryptedDatabase()
                
                true
            } catch (e: Exception) {
                targetDb.endTransaction()
                targetDb.close()
                sourceDb.close()
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Migrates data from encrypted database back to unencrypted
     */
    suspend fun migrateToUnencrypted(pin: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val encryptedDb = EncryptedDatabaseHelper(context, pin)
            val unencryptedDb = DatabaseHelper(context)
            
            // Get readable databases
            val sourceDb = encryptedDb.readableDatabase
            val targetDb = unencryptedDb.writableDatabase
            
            // Begin transaction for atomic migration
            targetDb.beginTransaction()
            
            try {
                // Clear existing data in target database
                targetDb.execSQL("DELETE FROM ${DatabaseHelper.TABLE_NOTES}")
                targetDb.execSQL("DELETE FROM ${DatabaseHelper.TABLE_PHQ9_RESULTS}")
                targetDb.execSQL("DELETE FROM ${DatabaseHelper.TABLE_PHQ9_RESPONSES}")
                
                // Migrate notes
                migrateNotesTable(sourceDb, targetDb)
                
                // Migrate PHQ9 results
                migratePhq9ResultsTable(sourceDb, targetDb)
                
                // Migrate PHQ9 responses
                migratePhq9ResponsesTable(sourceDb, targetDb)
                
                // Mark transaction as successful
                targetDb.setTransactionSuccessful()
                
                // Close databases
                sourceDb.close()
                targetDb.endTransaction()
                targetDb.close()
                
                // Delete the encrypted database file
                deleteEncryptedDatabase()
                
                true
            } catch (e: Exception) {
                targetDb.endTransaction()
                targetDb.close()
                sourceDb.close()
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    private fun migrateNotesTable(sourceDb: SQLiteDatabase, targetDb: SQLiteDatabase) {
        val cursor = sourceDb.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            null,
            null,
            null,
            null,
            null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val content = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP))
                val mood = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MOOD))
                
                val values = android.content.ContentValues().apply {
                    put(DatabaseHelper.COLUMN_CONTENT, content)
                    put(DatabaseHelper.COLUMN_TIMESTAMP, timestamp)
                    put(DatabaseHelper.COLUMN_MOOD, mood)
                }
                
                targetDb.insert(DatabaseHelper.TABLE_NOTES, null, values)
            }
        }
    }
    
    private fun migratePhq9ResultsTable(sourceDb: SQLiteDatabase, targetDb: SQLiteDatabase) {
        val cursor = sourceDb.query(
            DatabaseHelper.TABLE_PHQ9_RESULTS,
            null,
            null,
            null,
            null,
            null,
            null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                val score = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE))
                val level = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL))
                val timestamp = it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP))
                
                val values = android.content.ContentValues().apply {
                    put(DatabaseHelper.COLUMN_SCORE, score)
                    put(DatabaseHelper.COLUMN_LEVEL, level)
                    put(DatabaseHelper.COLUMN_TIMESTAMP, timestamp)
                }
                
                targetDb.insert(DatabaseHelper.TABLE_PHQ9_RESULTS, null, values)
            }
        }
    }
    
    private fun migratePhq9ResponsesTable(sourceDb: SQLiteDatabase, targetDb: SQLiteDatabase) {
        val cursor = sourceDb.query(
            DatabaseHelper.TABLE_PHQ9_RESPONSES,
            null,
            null,
            null,
            null,
            null,
            null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                val values = android.content.ContentValues().apply {
                    put(DatabaseHelper.COLUMN_Q1, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q1)))
                    put(DatabaseHelper.COLUMN_Q2, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q2)))
                    put(DatabaseHelper.COLUMN_Q3, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q3)))
                    put(DatabaseHelper.COLUMN_Q4, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q4)))
                    put(DatabaseHelper.COLUMN_Q5, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q5)))
                    put(DatabaseHelper.COLUMN_Q6, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q6)))
                    put(DatabaseHelper.COLUMN_Q7, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q7)))
                    put(DatabaseHelper.COLUMN_Q8, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q8)))
                    put(DatabaseHelper.COLUMN_Q9, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Q9)))
                    put(DatabaseHelper.COLUMN_TOTAL, it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL)))
                    put(DatabaseHelper.COLUMN_TIMESTAMP, it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP)))
                }
                
                targetDb.insert(DatabaseHelper.TABLE_PHQ9_RESPONSES, null, values)
            }
        }
    }
    
    private fun deleteUnencryptedDatabase() {
        val dbFile = context.getDatabasePath("seen_database.db")
        if (dbFile.exists()) {
            dbFile.delete()
        }
        
        // Also delete associated files (journal, wal, etc.)
        val dbDir = dbFile.parentFile
        dbDir?.listFiles()?.forEach { file ->
            if (file.name.startsWith("seen_database.db")) {
                file.delete()
            }
        }
    }
    
    private fun deleteEncryptedDatabase() {
        val dbFile = context.getDatabasePath("seen_encrypted_database.db")
        if (dbFile.exists()) {
            dbFile.delete()
        }
        
        // Also delete associated files (journal, wal, etc.)
        val dbDir = dbFile.parentFile
        dbDir?.listFiles()?.forEach { file ->
            if (file.name.startsWith("seen_encrypted_database.db")) {
                file.delete()
            }
        }
    }
    
    /**
     * Checks if there's data in the unencrypted database
     */
    fun hasUnencryptedData(): Boolean {
        return try {
            val dbHelper = DatabaseHelper(context)
            val db = dbHelper.readableDatabase
            
            // Check if any table has data
            val notesCursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_NOTES}", null)
            val phq9Cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_PHQ9_RESULTS}", null)
            val responsesCursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_PHQ9_RESPONSES}", null)
            
            var hasData = false
            
            notesCursor.use {
                if (it.moveToFirst() && it.getInt(0) > 0) hasData = true
            }
            
            if (!hasData) {
                phq9Cursor.use {
                    if (it.moveToFirst() && it.getInt(0) > 0) hasData = true
                }
            }
            
            if (!hasData) {
                responsesCursor.use {
                    if (it.moveToFirst() && it.getInt(0) > 0) hasData = true
                }
            }
            
            db.close()
            hasData
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Checks if there's data in the encrypted database
     */
    fun hasEncryptedData(pin: String): Boolean {
        return try {
            val dbHelper = EncryptedDatabaseHelper(context, pin)
            val db = dbHelper.readableDatabase
            
            // Check if any table has data
            val notesCursor = db.rawQuery("SELECT COUNT(*) FROM ${EncryptedDatabaseHelper.TABLE_NOTES}", null)
            val phq9Cursor = db.rawQuery("SELECT COUNT(*) FROM ${EncryptedDatabaseHelper.TABLE_PHQ9_RESULTS}", null)
            val responsesCursor = db.rawQuery("SELECT COUNT(*) FROM ${EncryptedDatabaseHelper.TABLE_PHQ9_RESPONSES}", null)
            
            var hasData = false
            
            notesCursor.use {
                if (it.moveToFirst() && it.getInt(0) > 0) hasData = true
            }
            
            if (!hasData) {
                phq9Cursor.use {
                    if (it.moveToFirst() && it.getInt(0) > 0) hasData = true
                }
            }
            
            if (!hasData) {
                responsesCursor.use {
                    if (it.moveToFirst() && it.getInt(0) > 0) hasData = true
                }
            }
            
            db.close()
            hasData
        } catch (e: Exception) {
            false
        }
    }
}