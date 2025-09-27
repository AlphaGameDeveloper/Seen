package dev.alphagame.seen.data

import android.content.Context
import android.util.Log
import java.io.*

/**
 * Utility class for handling database encryption and decryption operations.
 * Uses a simple XOR cipher with the key 0x22 for encrypting/decrypting database files.
 */
class DatabaseEncryption {

    companion object {
        // Simple encryption key - using hex 0x22 as requested
        private const val ENCRYPTION_KEY: Byte = 0x22

        // File names
        const val ENCRYPTED_DB_NAME = "seen_database_encrypted.db"
        const val TEMP_DB_NAME = "seen_database_temp.db"

        /**
         * For debug, turn bytes/s to KBytes/s and MBytes/s
         * @param bps = Bytes per second
         */
        fun bps2String(bps: Number): String {
            // note to self - 1 KB is 1000 bytes, and 1 KiB (kibibyte) is 1024 (2^10) bytes.
            return "${bps.toDouble()/1024} KiB/s; ${bps.toDouble() / 1048576} MiB/s"
        }

        /**
         * Encrypts a file using XOR cipher with the predefined key
         */
        fun encryptFile(inputFile: File, outputFile: File) {
            try {
                val inputBytes = inputFile.readBytes()
                val encryptedBytes = ByteArray(inputBytes.size)

                val startTime = System.currentTimeMillis()
                for (i in inputBytes.indices) {
                    encryptedBytes[i] = (inputBytes[i].toInt() xor ENCRYPTION_KEY.toInt()).toByte()
                }

                outputFile.writeBytes(encryptedBytes)
                val endTime = System.currentTimeMillis()

                val bytesPerSecond = if (endTime > startTime) {
                    (inputBytes.size * 1000) / (endTime - startTime)
                } else {
                    inputBytes.size
                }
                Log.d("Encryption", "Encryption completed in ${endTime - startTime} ms (${bps2String(bytesPerSecond)}).  Total file size is ${inputFile.length()/1024} KiB")
            } catch (e: Exception) {
                throw Exception("Failed to encrypt database file: ${e.message}", e)
            }
        }

        /**
         * Decrypts a file using XOR cipher with the predefined key
         * XOR is symmetric, so decryption is the same as encryption
         */
        fun decryptFile(inputFile: File, outputFile: File) {
            try {
                if (!inputFile.exists()) {
                    // If encrypted file doesn't exist, create an empty decrypted file
                    outputFile.createNewFile()
                    return
                }

                val startTime = System.currentTimeMillis()
                val encryptedBytes = inputFile.readBytes()
                val decryptedBytes = ByteArray(encryptedBytes.size)

                for (i in encryptedBytes.indices) {
                    decryptedBytes[i] = (encryptedBytes[i].toInt() xor ENCRYPTION_KEY.toInt()).toByte()
                }

                outputFile.writeBytes(decryptedBytes)
                val endTime = System.currentTimeMillis()
                val bytesPerSecond = if (endTime > startTime) {
                    (inputFile.length() * 1000 / (endTime - startTime)).toInt()
                } else {
                    inputFile.length().toInt()
                }
                Log.d("Encryption", "Decryption completed in ${endTime - startTime} ms (${bps2String(bytesPerSecond)}).  Total file size is ${inputFile.length()/1024} KiB")
            } catch (e: Exception) {
                throw Exception("Failed to decrypt database file: ${e.message}", e)
            }
        }

        /**
         * Gets the path to the encrypted database file in the app's private storage
         */
        fun getEncryptedDbPath(context: Context): String {
            return File(context.filesDir, ENCRYPTED_DB_NAME).absolutePath
        }

        /**
         * Gets the path to the temporary decrypted database file
         */
        fun getTempDbPath(context: Context): String {
            return File(context.cacheDir, TEMP_DB_NAME).absolutePath
        }

        /**
         * Cleans up the temporary database file
         */
        fun cleanupTempDb(context: Context) {
            val tempFile = File(getTempDbPath(context))
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }

        /**
         * Checks if an encrypted database file exists
         */
        fun encryptedDbExists(context: Context): Boolean {
            return File(getEncryptedDbPath(context)).exists()
        }
    }
}
