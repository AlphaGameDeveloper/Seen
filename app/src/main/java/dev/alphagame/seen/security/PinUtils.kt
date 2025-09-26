package dev.alphagame.seen.security

import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.text.Charsets.UTF_8

object PinUtils {
    private const val SALT_LENGTH = 32
    
    /**
     * Generates a random salt for hashing
     */
    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }
    
    /**
     * Converts byte array to hex string
     */
    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Converts hex string to byte array
     */
    private fun hexToBytes(hex: String): ByteArray {
        return hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
    
    /**
     * Creates a hash of the PIN with salt
     * Returns the salt + hash as a hex string
     */
    fun hashPin(pin: String): String {
        val salt = generateSalt()
        val pinBytes = pin.toByteArray(UTF_8)
        val saltedPin = salt + pinBytes
        
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(saltedPin)
        
        // Return salt + hash as hex string
        return bytesToHex(salt + hash)
    }
    
    /**
     * Verifies a PIN against a stored hash
     */
    fun verifyPin(pin: String, storedHash: String): Boolean {
        try {
            val storedBytes = hexToBytes(storedHash)
            
            // Extract salt (first 32 bytes)
            val salt = storedBytes.take(SALT_LENGTH).toByteArray()
            
            // Extract hash (remaining bytes)
            val storedPinHash = storedBytes.drop(SALT_LENGTH).toByteArray()
            
            // Hash the provided PIN with the stored salt
            val pinBytes = pin.toByteArray(UTF_8)
            val saltedPin = salt + pinBytes
            
            val digest = MessageDigest.getInstance("SHA-256")
            val computedHash = digest.digest(saltedPin)
            
            // Compare hashes
            return computedHash.contentEquals(storedPinHash)
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * Validates PIN format (4-6 digits)
     */
    fun isValidPin(pin: String): Boolean {
        return pin.matches(Regex("^\\d{4,6}$"))
    }
}