package dev.alphagame.seen.security

import android.content.Context
import dev.alphagame.seen.data.PreferencesManager

class EncryptionSettingsManager(private val context: Context) {
    private val preferencesManager = PreferencesManager(context)
    
    /**
     * Check if encryption is enabled
     */
    fun isEncryptionEnabled(): Boolean {
        return preferencesManager.encryptionEnabled
    }
    
    /**
     * Check if a PIN is set up
     */
    fun isPinSetup(): Boolean {
        return preferencesManager.pinHash != null
    }
    
    /**
     * Set up encryption with a PIN
     */
    suspend fun setupEncryption(pin: String): Boolean {
        if (!PinUtils.isValidPin(pin)) {
            return false
        }
        
        // Try to migrate data first
        val migrationManager = DataMigrationManager(context)
        val migrationSuccess = migrationManager.migrateToEncrypted(pin)
        
        if (migrationSuccess) {
            val hashedPin = PinUtils.hashPin(pin)
            preferencesManager.pinHash = hashedPin
            preferencesManager.encryptionEnabled = true
            return true
        }
        
        return false
    }
    
    /**
     * Disable encryption and remove PIN
     */
    suspend fun disableEncryption(pin: String): Boolean {
        if (!verifyPin(pin)) {
            return false
        }
        
        // Try to migrate data back to unencrypted
        val migrationManager = DataMigrationManager(context)
        val migrationSuccess = migrationManager.migrateToUnencrypted(pin)
        
        if (migrationSuccess) {
            preferencesManager.encryptionEnabled = false
            preferencesManager.pinHash = null
            return true
        }
        
        return false
    }
    
    /**
     * Verify the provided PIN
     */
    fun verifyPin(pin: String): Boolean {
        val storedHash = preferencesManager.pinHash ?: return false
        return PinUtils.verifyPin(pin, storedHash)
    }
    
    /**
     * Change the PIN (requires current PIN verification)
     */
    fun changePin(currentPin: String, newPin: String): Boolean {
        if (!verifyPin(currentPin)) {
            return false
        }
        
        if (!PinUtils.isValidPin(newPin)) {
            return false
        }
        
        val newHashedPin = PinUtils.hashPin(newPin)
        preferencesManager.pinHash = newHashedPin
        
        return true
    }
    
    /**
     * Get encryption status for UI
     */
    fun getEncryptionStatus(): EncryptionStatus {
        return when {
            !isEncryptionEnabled() -> EncryptionStatus.DISABLED
            !isPinSetup() -> EncryptionStatus.SETUP_REQUIRED
            else -> EncryptionStatus.ENABLED
        }
    }
    
    /**
     * Check if PIN verification is required on app startup
     */
    fun requiresPinOnStartup(): Boolean {
        return isEncryptionEnabled() && isPinSetup()
    }
    
    /**
     * Get data migration manager
     */
    fun getDataMigrationManager(): DataMigrationManager {
        return DataMigrationManager(context)
    }
}

enum class EncryptionStatus {
    DISABLED,
    SETUP_REQUIRED,
    ENABLED
}