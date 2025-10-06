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
