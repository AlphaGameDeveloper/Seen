package dev.alphagame.seen.analytics

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.alphagame.seen.analytics.models.AnalyticsBatchRequest
import dev.alphagame.seen.analytics.models.AnalyticsErrorResponse
import dev.alphagame.seen.analytics.models.AnalyticsEvent
import dev.alphagame.seen.analytics.models.SessionInfo
import dev.alphagame.seen.analytics.models.UserProperties
import dev.alphagame.seen.data.AppVersionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Analytics Manager for tracking user interactions and app usage
 * Only collects data when user has explicitly consented during onboarding
 */
class AnalyticsManager(private val context: Context) : DefaultLifecycleObserver {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var sessionStartTime: Long = 0
    private var currentSessionId: String = ""
    private var isSessionActive = false

    // Event deduplication and rate limiting
    private val lastEventTimes = mutableMapOf<String, Long>()
    private val sessionEventsSent = mutableSetOf<String>()
    private var lastSessionEndTime: Long = 0

    // Events that should only be sent once per session
    private val sessionUniqueEvents = setOf(
        EVENT_APP_OPENED,
        EVENT_APP_CLOSED,
        EVENT_ONBOARDING_STARTED,
        EVENT_ONBOARDING_COMPLETED,
        EVENT_ONBOARDING_SKIPPED
    )

    companion object {
        private const val TAG = "AnalyticsManager"
        private const val PREFS_NAME = "analytics_preferences"

        // Preference keys
        private const val KEY_ANALYTICS_ENABLED = "analytics_enabled"
        private const val KEY_USER_UUID = "user_uuid"
        private const val KEY_FIRST_LAUNCH_TIME = "first_launch_time"
        private const val KEY_TOTAL_SESSIONS = "total_sessions"
        private const val KEY_TOTAL_SESSION_TIME = "total_session_time"
        private const val KEY_LAST_EVENT_TIME = "last_event_time"

        // Analytics endpoint
        private const val BASE_URL = "https://seen.alphagame.dev/api"
        private const val ANALYTICS_ENDPOINT = "$BASE_URL/analytics"
        private const val JSON_MEDIA_TYPE = "application/json; charset=utf-8"

        // Rate limiting constants
        private const val MIN_EVENT_INTERVAL_MS = 1000L // Minimum 1 second between duplicate events (for non-unique events)
        private const val MIN_SESSION_END_INTERVAL_MS = 5000L // Minimum 5 seconds between session ends

        // Event names
        const val EVENT_APP_OPENED = "app_opened"
        const val EVENT_APP_CLOSED = "app_closed"
        const val EVENT_ONBOARDING_STARTED = "onboarding_started"
        const val EVENT_ONBOARDING_COMPLETED = "onboarding_completed"
        const val EVENT_ONBOARDING_SKIPPED = "onboarding_skipped"
        const val EVENT_PHQ9_STARTED = "phq9_started"
        const val EVENT_PHQ9_COMPLETED = "phq9_completed"
        const val EVENT_PHQ9_ABANDONED = "phq9_abandoned"
        const val EVENT_AI_ANALYSIS_REQUESTED = "ai_analysis_requested"
        const val EVENT_AI_ANALYSIS_RECEIVED = "ai_analysis_received"
        const val EVENT_AI_ANALYSIS_FAILED = "ai_analysis_failed"
        const val EVENT_NOTE_CREATED = "note_created"
        const val EVENT_NOTE_EDITED = "note_edited"
        const val EVENT_NOTE_DELETED = "note_deleted"
        const val EVENT_MOOD_HISTORY_VIEWED = "mood_history_viewed"
        const val EVENT_SETTINGS_OPENED = "settings_opened"
        const val EVENT_THEME_CHANGED = "theme_changed"
        const val EVENT_LANGUAGE_CHANGED = "language_changed"
        const val EVENT_UPDATE_CHECK_PERFORMED = "update_check_performed"
        const val EVENT_UPDATE_AVAILABLE = "update_available"
        const val EVENT_FEATURE_ENABLED = "feature_enabled"
        const val EVENT_FEATURE_DISABLED = "feature_disabled"
        const val EVENT_ERROR_OCCURRED = "error_occurred"
        const val EVENT_CRASH_DETECTED = "crash_detected"
    }

    init {
        // Initialize first launch time if not set
        if (prefs.getLong(KEY_FIRST_LAUNCH_TIME, 0) == 0L) {
            prefs.edit().putLong(KEY_FIRST_LAUNCH_TIME, System.currentTimeMillis()).apply()
        }

        // Register lifecycle observer
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // === Configuration ===

    /**
     * Check if analytics is enabled
     */
    val isAnalyticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANALYTICS_ENABLED, false)

    /**
     * Enable analytics tracking
     * This should only be called when user explicitly consents
     */
    fun enableAnalytics() {
        prefs.edit().putBoolean(KEY_ANALYTICS_ENABLED, true).apply()

        // Generate UUID if not exists
        if (getUserUUID().isEmpty()) {
            val uuid = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_USER_UUID, uuid).apply()
            Log.i(TAG, "Analytics enabled with new UUID: ${uuid.take(8)}...")
        } else {
            Log.i(TAG, "Analytics enabled with existing UUID")
        }

        // Ensure we have a valid session before tracking events
        if (!isSessionActive) {
            startSession()
        }

        // Track analytics enablement
        trackEvent(EVENT_FEATURE_ENABLED, mapOf("feature" to "analytics"))
    }

    /**
     * Disable analytics tracking and clear UUID
     */
    fun disableAnalytics() {
        val wasEnabled = isAnalyticsEnabled

        if (wasEnabled) {
            // Track disabling before we disable
            trackEventInternal(EVENT_FEATURE_DISABLED, mapOf("feature" to "analytics"))
        }

        prefs.edit()
            .putBoolean(KEY_ANALYTICS_ENABLED, false)
            .remove(KEY_USER_UUID)
            .apply()

        // Clear rate limiting and session data
        lastEventTimes.clear()
        sessionEventsSent.clear()

        Log.i(TAG, "Analytics disabled and UUID cleared")
    }

    /**
     * Get user UUID (returns empty string if analytics disabled)
     */
    fun getUserUUID(): String {
        return if (isAnalyticsEnabled) {
            prefs.getString(KEY_USER_UUID, "") ?: ""
        } else {
            ""
        }
    }

    // === Session Management ===

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        startSession()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        endSession()
    }

    private fun startSession() {
        if (!isAnalyticsEnabled) return

        // Prevent duplicate session starts
        if (isSessionActive) {
            Log.v(TAG, "Session already active, ignoring duplicate start")
            return
        }

        sessionStartTime = System.currentTimeMillis()
        isSessionActive = true

        // Generate new session ID for this session
        currentSessionId = "session_${System.currentTimeMillis()}_${getUserUUID().take(8)}"

        // Clear session-specific events tracking
        sessionEventsSent.clear()

        // Increment session count
        val sessionCount = prefs.getInt(KEY_TOTAL_SESSIONS, 0) + 1
        prefs.edit().putInt(KEY_TOTAL_SESSIONS, sessionCount).apply()

        // Track app opened (will be automatically deduplicated)
        trackEvent(EVENT_APP_OPENED, mapOf(
            "session_number" to sessionCount,
            "is_first_launch" to (sessionCount == 1)
        ))

        Log.d(TAG, "Session started (#$sessionCount) with ID: $currentSessionId")
    }

    private fun endSession() {
        if (!isAnalyticsEnabled || !isSessionActive) return

        // Prevent rapid duplicate session ends
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSessionEndTime < MIN_SESSION_END_INTERVAL_MS) {
            Log.v(TAG, "Session end called too soon after previous end, ignoring")
            return
        }

        val sessionDuration = currentTime - sessionStartTime
        lastSessionEndTime = currentTime

        // Add to total session time
        val totalTime = prefs.getLong(KEY_TOTAL_SESSION_TIME, 0) + sessionDuration
        prefs.edit().putLong(KEY_TOTAL_SESSION_TIME, totalTime).apply()

        // Track app closed (will be automatically deduplicated)
        trackEvent(EVENT_APP_CLOSED, mapOf(
            "session_duration_ms" to sessionDuration,
            "session_duration_seconds" to (sessionDuration / 1000)
        ))

        Log.d(TAG, "Session ended (duration: ${sessionDuration}ms) ID: $currentSessionId")

        // Clear session state AFTER sending the app_closed event
        isSessionActive = false
        currentSessionId = ""
    }

    // === Event Tracking ===

    /**
     * Track an analytics event with rate limiting and deduplication
     * @param eventName Name of the event
     * @param properties Optional properties for the event
     */
    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap()) {
        if (!isAnalyticsEnabled) {
            Log.v(TAG, "Event '$eventName' not tracked (analytics disabled)")
            return
        }

        // Ensure we have a valid session before tracking events
        if (!isSessionActive) {
            Log.v(TAG, "Starting session for event '$eventName'")
            startSession()
        }

        // Check if this is a session-unique event that has already been sent
        if (sessionUniqueEvents.contains(eventName) && sessionEventsSent.contains(eventName)) {
            Log.v(TAG, "Event '$eventName' already sent in this session, skipping")
            return
        }

        // Check for rate limiting on duplicate events (for non-session-unique events)
        if (!sessionUniqueEvents.contains(eventName)) {
            val currentTime = System.currentTimeMillis()
            val lastEventTime = lastEventTimes[eventName] ?: 0L

            if (currentTime - lastEventTime < MIN_EVENT_INTERVAL_MS) {
                Log.v(TAG, "Event '$eventName' rate limited (last sent ${currentTime - lastEventTime}ms ago)")
                return
            }

            // Update last event time
            lastEventTimes[eventName] = currentTime
        }

        // Track the event and mark as sent if it's session-unique
        trackEventInternal(eventName, properties)

        if (sessionUniqueEvents.contains(eventName)) {
            sessionEventsSent.add(eventName)
        }
    }

    /**
     * Internal method to track events without rate limiting (for internal use)
     */
    private fun trackEventInternal(eventName: String, properties: Map<String, Any> = emptyMap()) {
        if (!isAnalyticsEnabled) return

        scope.launch {
            try {
                val event = createAnalyticsEvent(eventName, properties)
                sendEvent(event)

                // Update last event time
                prefs.edit().putLong(KEY_LAST_EVENT_TIME, System.currentTimeMillis()).apply()

            } catch (e: Exception) {
                Log.e(TAG, "Failed to track event '$eventName'", e)
            }
        }
    }

    /**
     * Track PHQ-9 completion with score
     */
    fun trackPHQ9Completion(score: Int, severity: String, hasAiAnalysis: Boolean) {
        trackEvent(EVENT_PHQ9_COMPLETED, mapOf(
            "score" to score,
            "severity" to severity,
            "ai_analysis_enabled" to hasAiAnalysis,
            "timestamp" to System.currentTimeMillis()
        ))
    }

    /**
     * Track settings change
     */
    fun trackSettingChanged(settingName: String, oldValue: String, newValue: String) {
        trackEvent("setting_changed", mapOf(
            "setting_name" to settingName,
            "old_value" to oldValue,
            "new_value" to newValue
        ))
    }

    /**
     * Track feature usage
     */
    fun trackFeatureUsed(featureName: String, context: Map<String, Any> = emptyMap()) {
        trackEvent("feature_used", mapOf(
            "feature_name" to featureName
        ) + context)
    }

    /**
     * Track error occurrence
     */
    fun trackError(errorType: String, errorMessage: String, context: Map<String, Any> = emptyMap()) {
        trackEvent(EVENT_ERROR_OCCURRED, mapOf(
            "error_type" to errorType,
            "error_message" to errorMessage,
            "timestamp" to System.currentTimeMillis()
        ) + context)
    }

    // === Data Creation ===

    private fun createAnalyticsEvent(eventName: String, properties: Map<String, Any>): AnalyticsEvent {
        return AnalyticsEvent(
            event_name = eventName,
            timestamp = System.currentTimeMillis(),
            properties = properties
        )
    }

    private fun createBatchRequest(events: List<AnalyticsEvent>): AnalyticsBatchRequest {
        return AnalyticsBatchRequest(
            user_id = getUserUUID(),
            session_info = getCurrentSessionInfo(),
            user_properties = getUserProperties(),
            events = events
        )
    }

    private fun getCurrentSessionInfo(): SessionInfo {
        // Ensure we always have a valid session ID and start time
        val currentTime = System.currentTimeMillis()
        val validStartTime = if (sessionStartTime > 0) sessionStartTime else currentTime

        val sessionId = if (currentSessionId.isNotEmpty()) {
            currentSessionId
        } else {
            // Fallback session ID if somehow empty
            "session_${validStartTime}_${getUserUUID().take(8)}"
        }

        // Log warning if we had to use fallback values
        if (sessionStartTime <= 0) {
            Log.w(TAG, "Using fallback start_time for session info (sessionStartTime was $sessionStartTime)")
        }
        if (currentSessionId.isEmpty()) {
            Log.w(TAG, "Using fallback session_id for session info")
        }

        return SessionInfo(
            session_id = sessionId,
            start_time = validStartTime,
            app_version = AppVersionInfo.VERSION_NAME,
            device_info = getDeviceInfo()
        )
    }

    private fun getUserProperties(): UserProperties {
        return UserProperties(
            user_id = getUserUUID(),
            app_version = AppVersionInfo.VERSION_NAME
        )
    }

    private fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "platform" to "android",
            "version" to android.os.Build.VERSION.RELEASE,
            "model" to android.os.Build.MODEL,
            "brand" to android.os.Build.BRAND,
            "sdk_version" to android.os.Build.VERSION.SDK_INT.toString(),
            "locale" to Locale.getDefault().toString()
        )
    }

    private fun calculateDaysSinceFirstLaunch(): Int {
        val firstLaunch = prefs.getLong(KEY_FIRST_LAUNCH_TIME, 0)
        if (firstLaunch == 0L) return 0

        val daysDiff = (System.currentTimeMillis() - firstLaunch) / (24 * 60 * 60 * 1000)
        return daysDiff.toInt()
    }

    // === Network Transmission ===

    private suspend fun sendEvent(event: AnalyticsEvent) = withContext(Dispatchers.IO) {
        try {
            // Create batch request with single event
            val batchRequest = createBatchRequest(listOf(event))
            val jsonBody = gson.toJson(batchRequest)
            Log.v(TAG, "Sending analytics event: ${event.event_name}")

            val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE.toMediaTypeOrNull())

            val request = Request.Builder()
                .url(ANALYTICS_ENDPOINT)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME}")
                .addHeader("X-Analytics-Version", "1.0")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Log.v(TAG, "Analytics event '${event.event_name}' sent successfully")
            } else {
                if (response.code == 400) {
                    // Parse 400 error response for detailed error information
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        try {
                            val errorResponse = gson.fromJson(responseBody, AnalyticsErrorResponse::class.java)
                            Log.w(TAG, "Analytics validation error for event '${event.event_name}': " +
                                    "Code=${errorResponse.error_code}, Message=${errorResponse.message}, " +
                                    "Timestamp=${errorResponse.timestamp}")
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to parse 400 error response: $responseBody")
                        }
                    } else {
                        Log.w(TAG, "Analytics event '${event.event_name}' failed with 400 but no response body")
                    }
                } else {
                    Log.w(TAG, "Failed to send analytics event '${event.event_name}': ${response.code} ${response.message}")
                }
            }

        } catch (e: Exception) {
            Log.w(TAG, "Failed to send analytics event", e)
            // Don't throw - analytics should never crash the app
        }
    }

    // === Debug Information ===

    /**
     * Get analytics debug information
     */
    fun getDebugInfo(): String {
        return buildString {
            appendLine("=== Analytics Debug Info ===")
            appendLine("Enabled: $isAnalyticsEnabled")
            appendLine("UUID: ${if (isAnalyticsEnabled) getUserUUID().take(12) + "..." else "N/A"}")
            appendLine("Total Sessions: ${prefs.getInt(KEY_TOTAL_SESSIONS, 0)}")
            appendLine("Total Session Time: ${prefs.getLong(KEY_TOTAL_SESSION_TIME, 0)}ms")
            appendLine("Days Since First Launch: ${calculateDaysSinceFirstLaunch()}")
            appendLine("Current Session Active: $isSessionActive")
            appendLine("Current Session ID: ${if (currentSessionId.isNotEmpty()) currentSessionId else "None"}")
            appendLine("Session Start Time: $sessionStartTime")
            appendLine("Last Event Time: ${prefs.getLong(KEY_LAST_EVENT_TIME, 0)}")
            appendLine("Rate Limited Events: ${lastEventTimes.size}")
            appendLine("Session Events Sent: ${sessionEventsSent.joinToString(", ")}")
            appendLine("Session-Unique Events: ${sessionUniqueEvents.joinToString(", ")}")
            appendLine("Analytics Endpoint: $ANALYTICS_ENDPOINT")
        }
    }

    /**
     * Test analytics connectivity
     */
    suspend fun testConnectivity(): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = Request.Builder()
                .url(BASE_URL)
                .get()
                .addHeader("User-Agent", "Seen-Android/${AppVersionInfo.VERSION_NAME}")
                .build()

            val response = client.newCall(request).execute()
            "Analytics endpoint reachable: ${response.code} ${response.message}"

        } catch (e: Exception) {
            "Analytics endpoint unreachable: ${e.message}"
        }
    }

    /**
     * Clear all analytics data (for debugging/testing)
     */
    fun clearAnalyticsData() {
        prefs.edit().clear().apply()
        lastEventTimes.clear()
        sessionEventsSent.clear()
        currentSessionId = ""
        isSessionActive = false
        sessionStartTime = 0
        lastSessionEndTime = 0
        Log.i(TAG, "All analytics data cleared")
    }
}
