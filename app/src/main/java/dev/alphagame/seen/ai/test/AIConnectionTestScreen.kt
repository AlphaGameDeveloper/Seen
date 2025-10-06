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

package dev.alphagame.seen.ai.test

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.ai.AIManager
import kotlinx.coroutines.launch

@Composable
fun AIConnectionTestScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var testResults by remember { mutableStateOf("No tests run yet") }
    var isLoading by remember { mutableStateOf(false) }

    val aiManager = remember { AIManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "AI Connection Test",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val result = aiManager.testBasicConnectivity()
                        testResults = "Basic Connectivity Test:\n$result"
                        Log.d("AITest", "Basic connectivity result: $result")
                    } catch (e: Exception) {
                        testResults = "Error: ${e.message}"
                        Log.e("AITest", "Test failed", e)
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Basic Connectivity")
        }

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val isAvailable = aiManager.isAIServiceAvailable()
                        testResults = "Service Availability: $isAvailable"
                        Log.d("AITest", "Service availability: $isAvailable")
                    } catch (e: Exception) {
                        testResults = "Error: ${e.message}"
                        Log.e("AITest", "Availability test failed", e)
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Service Availability")
        }

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val testResponses = listOf(1, 1, 2, 1, 0, 1, 1, 0, 1)
                        val totalScore = testResponses.sum()

                        val notes = "Test screen note"
                        val moodEntries = listOf("neutral")
                        val result = aiManager.submitPHQ9ForAnalysis(totalScore, testResponses, notes, moodEntries)

                        result.onSuccess { response ->
                            testResults = "PHQ9 Test Success!\n" +
                                    "Severity: ${response.severity ?: "Unknown"}\n" +
                                    "Emotional State: ${response.emotional_state?.take(100) ?: "None"}...\n" +
                                    "Recommendations: ${response.recommendations?.size ?: 0} items"
                        }.onFailure { error ->
                            testResults = "PHQ9 Test Failed: ${error.message}"
                        }
                    } catch (e: Exception) {
                        testResults = "Error: ${e.message}"
                        Log.e("AITest", "PHQ9 test failed", e)
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test PHQ9 Submission")
        }

        Divider()

        Text(
            text = "Test Results:",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = testResults,
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp
            )
        }

        Divider()

        Text(
            text = "Debug Info:",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Server URL: http://192.168.0.147:8080/api\n" +
                    "PHQ9 Endpoint: http://192.168.0.147:8080/api/ai/phq9\n\n" +
                    "Common Issues:\n" +
                    "• Check server is running on 192.168.0.147:8080\n" +
                    "• Verify /api/ai/phq9 endpoint exists\n" +
                    "• Check device/emulator network connectivity\n" +
                    "• Look at Logcat for detailed error messages",
            fontSize = 12.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}
