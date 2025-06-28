package dev.alphagame.seen.ai

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import dev.alphagame.seen.ai.models.PHQ9Request
import dev.alphagame.seen.ai.models.PHQ9Response
import dev.alphagame.seen.data.AppVersionInfo
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
        responses: List<Int>
    ): Result<PHQ9Response> {
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

                // Create request
                val phq9Request = PHQ9Request(
                    total = totalScore,
                    responses = responses
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
                return@withContext Result.success(aiResponse)

            } catch (e: JsonSyntaxException) {
                Log.e(TAG, "Failed to parse JSON response", e)
                return@withContext Result.failure(e)
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during AI API request", e)
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
                    responses = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
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

    /**
     * Debug method to test the AI service connection
     * This will provide detailed logging about the connection status
     */
    suspend fun debugConnection(): String {
        return withContext(Dispatchers.IO) {
            val results = mutableListOf<String>()

            results.add("=== AI Service Debug Information ===")
            results.add("Base URL: $BASE_URL")
            results.add("PHQ9 Endpoint: $PHQ9_ENDPOINT")

            // Test 1: Basic connectivity
            try {
                results.add("\n--- Test 1: Base API Connectivity ---")
                val request = Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME} +damien@alphagame.dev")
                    .build()

                val response = client.newCall(request).execute()
                results.add("Status Code: ${response.code}")
                results.add("Status Message: ${response.message}")
                results.add("Headers: ${response.headers}")

                val body = response.body?.string()
                results.add("Response Body: ${body?.take(200) ?: "Empty"}")

            } catch (e: Exception) {
                results.add("Base connectivity failed: ${e.message}")
                results.add("Exception type: ${e.javaClass.simpleName}")
            }

            // Test 2: PHQ9 endpoint with test data
            try {
                results.add("\n--- Test 2: PHQ9 Endpoint Test ---")
                val testRequest = PHQ9Request(
                    total = 5,
                    responses = listOf(1, 0, 1, 1, 0, 1, 0, 1, 0)
                )

                val jsonBody = gson.toJson(testRequest)
                results.add("Test JSON: $jsonBody")

                val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE.toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(PHQ9_ENDPOINT)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME} +damien@alphagame.dev")
                    .build()

                val response = client.newCall(request).execute()
                results.add("PHQ9 Status Code: ${response.code}")
                results.add("PHQ9 Status Message: ${response.message}")

                val body = response.body?.string()
                results.add("PHQ9 Response: ${body ?: "Empty"}")

            } catch (e: Exception) {
                results.add("PHQ9 endpoint test failed: ${e.message}")
                results.add("Exception type: ${e.javaClass.simpleName}")
            }

            results.add("\n=== End Debug Information ===")

            val debugOutput = results.joinToString("\n")
            Log.d(TAG, debugOutput)
            return@withContext debugOutput
        }
    }

    /**
     * Simple network connectivity test to verify basic connection
     */
    suspend fun testBasicConnectivity(): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Testing basic connectivity to: $BASE_URL")

                // Create a very simple request with minimal timeouts for faster feedback
                val testClient = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(BASE_URL)
                    .get()
                    .build()

                Log.d(TAG, "Sending request to: ${request.url}")

                val response = testClient.newCall(request).execute()
                val body = response.body?.string()

                val result = """
                    Status: ${response.code} ${response.message}
                    URL: ${request.url}
                    Response: ${body?.take(100) ?: "Empty"}
                """.trimIndent()

                Log.d(TAG, "Response received: $result")
                return@withContext result

            } catch (e: java.net.ConnectException) {
                val error = "Connection refused - Server not reachable at $BASE_URL"
                Log.e(TAG, error, e)
                return@withContext error
            } catch (e: java.net.UnknownHostException) {
                val error = "Unknown host - Cannot resolve $BASE_URL"
                Log.e(TAG, error, e)
                return@withContext error
            } catch (e: java.net.SocketTimeoutException) {
                val error = "Timeout - Server took too long to respond"
                Log.e(TAG, error, e)
                return@withContext error
            } catch (e: Exception) {
                val error = "Network error: ${e.javaClass.simpleName}: ${e.message}"
                Log.e(TAG, error, e)
                return@withContext error
            }
        }
    }
}
