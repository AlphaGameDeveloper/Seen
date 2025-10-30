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

package dev.alphagame.seen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.alphagame.seen.encryption.DatabaseEncryptionAES
import dev.alphagame.seen.encryption.EncryptedDatabaseHelper
import dev.alphagame.seen.security.KeyManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptionDebugScreen(
    onBackToHome: () -> Unit,
) {
    val context = LocalContext.current
    var encryptionInfo by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        try {
            val encryptedDbPath = DatabaseEncryptionAES.getEncryptedDbPath(context)
            val tempDbPath = DatabaseEncryptionAES.getTempDbPath(context)
            val encryptedFile = File(encryptedDbPath)
            val tempFile = File(tempDbPath)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            encryptionInfo = buildString {
                append("📁 Database Encryption Status\n")
                append("═══════════════════════════════\n\n")

                append("🔐 Encrypted Database:\n")
                append("Path: $encryptedDbPath\n")
                append("Exists: ${encryptedFile.exists()}\n")
                if (encryptedFile.exists()) {
                    append("Size: ${encryptedFile.length()} bytes\n")
                    append("Modified: ${dateFormat.format(Date(encryptedFile.lastModified()))}\n")
                }
                append("\n")

                append("🗂️ Temporary Database:\n")
                append("Path: $tempDbPath\n")
                append("Exists: ${tempFile.exists()}\n")
                if (tempFile.exists()) {
                    append("Size: ${tempFile.length()} bytes\n")
                    append("Modified: ${dateFormat.format(Date(tempFile.lastModified()))}\n")
                }
                append("\n")

                append("🔑 Encryption Details:\n")
                append("Algorithm: AES-256\n")
                append("Key:\n")
                append(KeyManager.getOrCreateAesKey().toString())
                append("Key Manager: ${KeyManager.toString()} (workingo)\n")
                append("\n")

                append("📋 System Info:\n")
                append("Cache Dir: ${context.cacheDir.absolutePath}\n")
                append("Files Dir: ${context.filesDir.absolutePath}\n")
            }
        } catch (e: Exception) {
            encryptionInfo = "Error loading encryption info: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Database Encryption Debug") },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = encryptionInfo,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Test encryption by saving a dummy note
                    try {
                        val dbHelper = EncryptedDatabaseHelper(context)
                        // This should trigger encryption
                        dbHelper.forceEncrypt()

                        // Refresh the info
                        encryptionInfo = "Encryption test completed! Check the file sizes above."
                    } catch (e: Exception) {
                        encryptionInfo = "Encryption test failed: ${e.message}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔐 Force Encrypt Database")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Clean up temp files
                    try {
                        DatabaseEncryptionAES.cleanupTempDb(context)
                        encryptionInfo = "Temporary files cleaned up!"
                    } catch (e: Exception) {
                        encryptionInfo = "Cleanup failed: ${e.message}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🧹 Clean Temp Files")
            }
        }
    }
}
