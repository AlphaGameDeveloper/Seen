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
    fun setupEncryption(pin: String): Boolean {
        if (!PinUtils.isValidPin(pin)) {
            return false
        }
        
        val hashedPin = PinUtils.hashPin(pin)
        preferencesManager.pinHash = hashedPin
        preferencesManager.encryptionEnabled = true
        
        return true
    }
    
    /**
     * Disable encryption and remove PIN
     */
    fun disableEncryption() {
        preferencesManager.encryptionEnabled = false
        preferencesManager.pinHash = null
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
}

enum class EncryptionStatus {
    DISABLED,
    SETUP_REQUIRED,
    ENABLED
}