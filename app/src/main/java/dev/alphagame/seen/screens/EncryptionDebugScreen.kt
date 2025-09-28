package dev.alphagame.seen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.alphagame.seen.data.DatabaseEncryption
import dev.alphagame.seen.data.EncryptedDatabaseHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptionDebugScreen(
    onBackToHome: () -> Unit
) {
    val context = LocalContext.current
    var encryptionInfo by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        try {
            val encryptedDbPath = DatabaseEncryption.getEncryptedDbPath(context)
            val tempDbPath = DatabaseEncryption.getTempDbPath(context)
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
                append("Algorithm: XOR Cipher\n")
                append("Key: 0x22 (hex)\n")
                append("Key: ${0x22} (decimal)\n")
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                        DatabaseEncryption.cleanupTempDb(context)
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
