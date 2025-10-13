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

package dev.alphagame.seen.ai

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dev.alphagame.seen.ai.models.PHQ9Request
import dev.alphagame.seen.ai.models.PHQ9Response
import dev.alphagame.seen.data.AppVersionInfo
import dev.alphagame.seen.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * AI Manager for handling PHQ-9 data submission and AI analysis
 */
class AIManager(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val analyticsManager by lazy { dev.alphagame.seen.analytics.AnalyticsManager(context) }

    companion object {
        private const val TAG = "AIManager"
        private const val BASE_URL = "https://seen.alphagame.dev/api"
        private const val PHQ9_ENDPOINT = "$BASE_URL/ai/phq9"
        private const val JSON_MEDIA_TYPE = "application/json; charset=utf-8"
    }

    /**
     * Submit PHQ-9 data for AI analysis
     * @param totalScore The total PHQ-9 score
     * @param responses List of individual responses (must be exactly 9 responses, each 0-3)
     * @return PHQ9Response containing AI analysis or null if failed
     */
    suspend fun submitPHQ9ForAnalysis(
        totalScore: Int,
        responses: List<Int>,
        notes: String? = null,
        moodEntries: List<String>? = null
    ): Result<PHQ9Response> {
        // Track AI analysis request
        analyticsManager.trackEvent("ai_analysis_requested", mapOf(
            "total_score" to totalScore,
            "severity" to when {
                totalScore <= 4 -> "Minimal"
                totalScore <= 9 -> "Mild"
                totalScore <= 14 -> "Moderate"
                totalScore <= 19 -> "Moderately Severe"
                else -> "Severe"
            }
        ))

        return withContext(Dispatchers.IO) {
            try {
                // Validate input
                if (responses.size != 9) {
                    Log.e(TAG, "Invalid responses size: ${responses.size}. Expected 9 responses.")
                    return@withContext Result.failure(
                        IllegalArgumentException("PHQ-9 requires exactly 9 responses")
                    )
                }

                if (responses.any { it < 0 || it > 3 }) {
                    Log.e(TAG, "Invalid response values: $responses. All responses must be 0-3.")
                    return@withContext Result.failure(
                        IllegalArgumentException("All PHQ-9 responses must be between 0-3")
                    )
                }

                val preferencesManager = PreferencesManager(context)
                // Create request
                val phq9Request = PHQ9Request(
                    total = totalScore,
                    responses = responses,
                    notes = notes,
                    moodEntries = moodEntries,
                    language = preferencesManager.language.uppercase()
                )

                val jsonBody = gson.toJson(phq9Request)
                Log.d(TAG, "Submitting PHQ-9 data to AI: $jsonBody")

                val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE.toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(PHQ9_ENDPOINT)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME} +damien@alphagame.dev")
                    .addHeader("x-Seen-Version", AppVersionInfo.VERSION_NAME)
                    .build()

                // Execute request
                val response = client.newCall(request).execute()

                val responseBody = response.body?.string()

                if (!response.isSuccessful) {
                    Log.e(TAG, "AI API request failed with code: ${response.code}")
                    Log.e(TAG, "Response body: $responseBody")
                    return@withContext Result.failure(
                        Exception("AI API request failed: ${response.code} ${response.message}")
                    )
                }

                if (responseBody.isNullOrEmpty()) {
                    Log.e(TAG, "Empty response from AI API")
                    return@withContext Result.failure(
                        Exception("Empty response from AI API")
                    )
                }

                Log.d(TAG, "AI API response: $responseBody")

                // Parse response with error handling
                val aiResponse = try {
                    gson.fromJson(responseBody, PHQ9Response::class.java)
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "JSON parsing failed for response: $responseBody", e)
                    throw e
                }

                if (aiResponse == null) {
                    Log.e(TAG, "Failed to parse AI response - response was null")
                    return@withContext Result.failure(
                        Exception("Failed to parse AI response")
                    )
                }

                // Validate response fields
                if (aiResponse.emotional_state == null && aiResponse.recommendations == null && aiResponse.severity == null) {
                    Log.e(TAG, "AI response contains no valid data")
                    return@withContext Result.failure(
                        Exception("AI response contains no valid data")
                    )
                }

                Log.i(TAG, "Successfully received AI analysis - Severity: ${aiResponse.severity ?: "Unknown"}")

                // Track successful AI analysis
                analyticsManager.trackEvent("ai_analysis_received", mapOf(
                    "ai_severity" to (aiResponse.severity ?: "Unknown"),
                    "has_recommendations" to (aiResponse.recommendations?.isNotEmpty() == true),
                    "recommendations_count" to (aiResponse.recommendations?.size ?: 0),
                    "total_score" to totalScore
                ))

                return@withContext Result.success(aiResponse)

            } catch (e: JsonSyntaxException) {
                Log.e(TAG, "Failed to parse JSON response", e)
                analyticsManager.trackError("ai_analysis_parse_error", e.message ?: "JSON parsing failed")
                return@withContext Result.failure(e)
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during AI API request", e)
                analyticsManager.trackError("ai_analysis_network_error", e.message ?: "Network request failed")
                return@withContext Result.failure(e)
            }
        }
    }

    /**
     * Check if AI analysis is available (network connectivity test)
     */
    suspend fun isAIServiceAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Try a simple GET request to the base API endpoint
                val request = Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME} +damien@alphagame.dev")
                    .addHeader("Accept", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val available = response.isSuccessful || response.code == 404 // 404 is OK for base endpoint

                Log.d(TAG, "AI service availability check: $available (${response.code})")
                Log.d(TAG, "Response body: ${response.body?.string()?.take(100)}")

                return@withContext available

            } catch (e: Exception) {
                Log.w(TAG, "AI service availability check failed", e)
                return@withContext false
            }
        }
    }

    /**
     * Check if the specific PHQ9 AI endpoint is available
     */
    suspend fun isPHQ9EndpointAvailable(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Create a minimal test request to see if the endpoint exists
                val testRequest = PHQ9Request(
                    total = 0,
                    responses = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0),
                    language = "English" // test only LMAO
                )

                val jsonBody = gson.toJson(testRequest)
                val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE.toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(PHQ9_ENDPOINT)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME} +damien@alphagame.dev")
                    .build()

                val response = client.newCall(request).execute()
                val available = response.isSuccessful || response.code in 400..499 // Client errors are OK, means endpoint exists

                Log.d(TAG, "PHQ9 endpoint availability check: $available (${response.code})")
                if (!response.isSuccessful) {
                    Log.d(TAG, "Response body: ${response.body?.string()}")
                }

                return@withContext available

            } catch (e: Exception) {
                Log.w(TAG, "PHQ9 endpoint availability check failed", e)
                return@withContext false
            }
        }
    }
}
