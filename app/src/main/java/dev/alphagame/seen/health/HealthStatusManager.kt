package dev.alphagame.seen.health

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Manages health status checks for different services
 */
class HealthStatusManager(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    private val _analyticsHealthState = MutableStateFlow(HealthStatus.UNKNOWN)
    private val _releasesHealthState = MutableStateFlow(HealthStatus.UNKNOWN)
    private val _aiHealthState = MutableStateFlow(HealthStatus.UNKNOWN)

    val analyticsHealthState: StateFlow<HealthStatus> = _analyticsHealthState.asStateFlow()
    val releasesHealthState: StateFlow<HealthStatus> = _releasesHealthState.asStateFlow()
    val aiHealthState: StateFlow<HealthStatus> = _aiHealthState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    enum class HealthStatus {
        HEALTHY,
        UNHEALTHY,
        UNKNOWN
    }

    data class HealthCheckResult(
        val status: HealthStatus,
        val message: String? = null,
        val responseTime: Long? = null
    )

    /**
     * Check health status of all services
     */
    fun checkAllServices() {
        scope.launch {
            // Launch all health checks concurrently
            launch { checkAnalyticsHealth() }
            launch { checkReleasesHealth() }
            launch { checkAIHealth() }
        }
    }

    /**
     * Check analytics service health
     */
    private suspend fun checkAnalyticsHealth() {
        val result = performHealthCheck("https://seen.alphagame.dev/api/analytics/health")
        _analyticsHealthState.value = result.status
    }

    /**
     * Check releases service health
     */
    private suspend fun checkReleasesHealth() {
        val result = performHealthCheck("https://seen.alphagame.dev/releases/health")
        _releasesHealthState.value = result.status
    }

    /**
     * Check AI service health
     */
    private suspend fun checkAIHealth() {
        val result = performHealthCheck("https://seen.alphagame.dev/api/ai/health")
        _aiHealthState.value = result.status
    }

    /**
     * Perform a health check on a given URL
     */
    private suspend fun performHealthCheck(url: String): HealthCheckResult {
        return withContext(Dispatchers.IO) {
            try {
                val startTime = System.currentTimeMillis()
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseTime = System.currentTimeMillis() - startTime

                    when {
                        response.isSuccessful -> {
                            val responseBody = response.body?.string()

                            // Try to parse JSON response for more detailed status
                            val status = try {
                                responseBody?.let { body ->
                                    val json = JSONObject(body)
                                    val status = json.optString("status", "unknown").lowercase()
                                    when (status) {
                                        "healthy", "ok", "up" -> HealthStatus.HEALTHY
                                        "unhealthy", "down", "error" -> HealthStatus.UNHEALTHY
                                        else -> HealthStatus.HEALTHY // Default to healthy if we got a 200 response
                                    }
                                } ?: HealthStatus.HEALTHY
                            } catch (e: Exception) {
                                // If JSON parsing fails but we got a 200, assume healthy
                                HealthStatus.HEALTHY
                            }

                            HealthCheckResult(
                                status = status,
                                message = responseBody,
                                responseTime = responseTime
                            )
                        }
                        response.code == 503 -> {
                            HealthCheckResult(
                                status = HealthStatus.UNHEALTHY,
                                message = "Service unavailable",
                                responseTime = responseTime
                            )
                        }
                        else -> {
                            HealthCheckResult(
                                status = HealthStatus.UNHEALTHY,
                                message = "HTTP ${response.code}",
                                responseTime = responseTime
                            )
                        }
                    }
                }
            } catch (e: IOException) {
                HealthCheckResult(
                    status = HealthStatus.UNHEALTHY,
                    message = "Network error: ${e.message}"
                )
            } catch (e: Exception) {
                HealthCheckResult(
                    status = HealthStatus.UNHEALTHY,
                    message = "Error: ${e.message}"
                )
            }
        }
    }

    /**
     * Get overall health status (all services must be healthy for overall healthy status)
     */
    fun getOverallHealthStatus(): HealthStatus {
        val statuses = listOf(
            _analyticsHealthState.value,
            _releasesHealthState.value,
            _aiHealthState.value
        )

        return when {
            statuses.all { it == HealthStatus.HEALTHY } -> HealthStatus.HEALTHY
            statuses.any { it == HealthStatus.UNHEALTHY } -> HealthStatus.UNHEALTHY
            else -> HealthStatus.UNKNOWN
        }
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        scope.cancel()
    }
}
