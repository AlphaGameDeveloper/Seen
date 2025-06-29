package dev.alphagame.seen.analytics.models

/**
 * Analytics batch request structure (what gets sent to server)
 */
data class AnalyticsBatchRequest(
    val user_id: String,
    val session_info: SessionInfo,
    val user_properties: UserProperties,
    val events: List<AnalyticsEvent>
)

/**
 * Individual analytics event
 */
data class AnalyticsEvent(
    val event_name: String,
    val timestamp: Long,
    val properties: Map<String, Any>
)

/**
 * Session information
 */
data class SessionInfo(
    val session_id: String,
    val start_time: Long,
    val app_version: String,
    val device_info: Map<String, String>
)

/**
 * User properties (simplified for server)
 */
data class UserProperties(
    val user_id: String,
    val app_version: String
)

/**
 * Analytics error response for parsing server error messages
 */
data class AnalyticsErrorResponse(
    val status: String,
    val error_code: String,
    val message: String,
    val timestamp: Long
)
