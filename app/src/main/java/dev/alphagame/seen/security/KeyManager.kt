// Seen - Mental Health Application
//     Copyright (C) 2025  Damien Boisvert
//                   2025  Alexander Cameron
//
//     Seen is free software: you can redistribute it and/or modify
//     it under the terms of the GNU General Public License as published by
//     the Free Software Foundation, either version 3 of the License, or
//     (at your option) any later version.
//
//     Seen is distributed in the hope that it will be useful,
//     but WITHOUT ANY WARRANTY; without even the implied warranty of
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//     GNU General Public License for more details.
//
//     You should have received a copy of the GNU General Public License
//     along with Seen.  If not, see <https://www.gnu.org/licenses/>.

package dev.alphagame.seen.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object KeyManager {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "SeenAesKey"
    private const val KEY_ALIAS_GCM = "SeenAesKeyGCM" // New alias for GCM mode

    /**
     * Returns the AES key from the Android keystore.
     * it'll generate one if it doesn't exist.
     */
    fun getOrCreateAesKey(): SecretKey {
        Log.d("KeyManager", "Providing secret key")
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        // Check for GCM key first
        keyStore.getKey(KEY_ALIAS_GCM, null)?.let {
            Log.d("KeyManager", "Found existing GCM key")
            return it as SecretKey
        }

        // Check if old CBC key exists and delete it
        if (keyStore.containsAlias(KEY_ALIAS)) {
            Log.w("KeyManager", "Found old CBC key, deleting it to create GCM key")
            keyStore.deleteEntry(KEY_ALIAS)
        }

        // Create new GCM key
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        Log.w("KeyManager", "Cannot find an AES encryption key! Generating new GCM key...")
        // to whatever poor soul who has to read this crappy code,
        // From the bottom of my heart, I'm sorry.
        // I'm really in over my head here.
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS_GCM,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)
            setUserAuthenticationRequired(false)
            setRandomizedEncryptionRequired(true)
        }.build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }
}
