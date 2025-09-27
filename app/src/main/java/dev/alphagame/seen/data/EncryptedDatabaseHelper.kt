package dev.alphagame.seen.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File

/**
 * Encrypted database helper that manages automatic encryption/decryption of database files.
 * This class extends DatabaseHelper and adds encryption functionality:
 * - Decrypts the database on app start
 * - Encrypts and saves the database after any write operation
 * - Stores encrypted database in app's private storage
 * - Uses temporary files for the decrypted database
 */
class EncryptedDatabaseHelper(private val context: Context) : DatabaseHelper(context, DatabaseEncryption.getTempDbPath(context), null, DATABASE_VERSION) {

    private val TAG = "EncryptedDatabaseHelper"
    init {
        // Decrypt the database on initialization
        decryptDatabase()
    }

    /**
     * Decrypts the encrypted database file to a temporary location
     */
    private fun decryptDatabase() {
        val encryptedDbFile = File(DatabaseEncryption.getEncryptedDbPath(context))
        val tempDbFile = File(DatabaseEncryption.getTempDbPath(context))

        // Ensure cache directory exists
        context.cacheDir.mkdirs()

        try {
            DatabaseEncryption.decryptFile(encryptedDbFile, tempDbFile)
        } catch (e: Exception) {
            // If decryption fails, we'll start with a fresh database
            android.util.Log.w("EncryptedDatabaseHelper", "Could not decrypt database, starting fresh: ${e.message}")
        }
    }

    /**
     * Encrypts the current database and saves it to persistent storage
     */
    private fun encryptAndSaveDatabase() {
        Log.i(TAG, "Encrypting & saving database...")
        val tempDbFile = File(DatabaseEncryption.getTempDbPath(context))
        val encryptedDbFile = File(DatabaseEncryption.getEncryptedDbPath(context))

        // Ensure files directory exists
        context.filesDir.mkdirs()

        try {
            // Close any open database connections to ensure file is fully written
            close()

            // Encrypt the temporary database file
            DatabaseEncryption.encryptFile(tempDbFile, encryptedDbFile)
        } catch (e: Exception) {
            android.util.Log.e("EncryptedDatabaseHelper", "Failed to encrypt database: ${e.message}", e)
            throw e
        }
    }

    /**
     * Override to ensure encryption happens after database creation
     */
    override fun onCreate(db: SQLiteDatabase) {
        super.onCreate(db)
        // Database will be encrypted when first write operation occurs
    }

    /**
     * Override to ensure encryption happens after database upgrade
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        super.onUpgrade(db, oldVersion, newVersion)
        // Database will be encrypted when next write operation occurs
    }

    /**
     * Override getWritableDatabase to ensure encryption after writes
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        val db = super.getWritableDatabase()
        // We'll handle encryption manually in the calling code for now
        return db
    }

    /**
     * Custom insert method that triggers encryption
     */
    fun insertWithEncryption(table: String, nullColumnHack: String?, values: android.content.ContentValues): Long {
        val db = writableDatabase
        val result = db.insert(table, nullColumnHack, values)
        encryptAndSaveDatabase()
        return result
    }

    /**
     * Custom update method that triggers encryption
     */
    fun updateWithEncryption(table: String, values: android.content.ContentValues, whereClause: String?, whereArgs: Array<String>?): Int {
        val db = writableDatabase
        val result = db.update(table, values, whereClause, whereArgs)
        encryptAndSaveDatabase()
        return result
    }

    /**
     * Custom delete method that triggers encryption
     */
    fun deleteWithEncryption(table: String, whereClause: String?, whereArgs: Array<String>?): Int {
        val db = writableDatabase
        val result = db.delete(table, whereClause, whereArgs)
        encryptAndSaveDatabase()
        return result
    }

    /**
     * Custom execSQL method that triggers encryption for write operations
     */
    fun execSQLWithEncryption(sql: String, bindArgs: Array<Any>? = null) {
        val db = writableDatabase
        if (bindArgs != null) {
            db.execSQL(sql, bindArgs)
        } else {
            db.execSQL(sql)
        }
        if (isWriteOperation(sql)) {
            encryptAndSaveDatabase()
        }
    }

    /**
     * Check if SQL statement is a write operation
     */
    private fun isWriteOperation(sql: String): Boolean {
        val trimmed = sql.trim().uppercase()
        return trimmed.startsWith("INSERT") ||
               trimmed.startsWith("UPDATE") ||
               trimmed.startsWith("DELETE") ||
               trimmed.startsWith("CREATE") ||
               trimmed.startsWith("DROP") ||
               trimmed.startsWith("ALTER")
    }

    /**
     * Override getReadableDatabase to return the decrypted database
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        return super.getReadableDatabase()
    }

    /**
     * Clean up temporary files when database helper is closed
     */
    override fun close() {
        Log.i(TAG, "Closing database")
        super.close()
        // Note: We don't clean up temp files here as they might still be needed
        // Cleanup will be handled by the Application class or activity lifecycle
    }

    /**
     * Force encryption of current database state
     */
    fun forceEncrypt() {
        Log.w(TAG, "Forcibly encrypting & saving the database")
        encryptAndSaveDatabase()
    }

    /**
     * Clear all data and encrypt the empty database
     */
    override fun clearAllData() {
        execSQLWithEncryption("DELETE FROM ${DatabaseHelper.TABLE_NOTES}")
        execSQLWithEncryption("DELETE FROM ${DatabaseHelper.TABLE_PHQ9_RESULTS}")
        execSQLWithEncryption("DELETE FROM ${DatabaseHelper.TABLE_PHQ9_RESPONSES}")
    }
}
