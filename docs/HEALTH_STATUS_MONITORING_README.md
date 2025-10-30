# Health Status Monitoring Feature

## Overview

The Seen app includes a health status monitoring system that provides real-time visibility into the status of backend services. This feature displays colored dots in the Settings screen header, representing the health of backend services such as Releases and AI.

## Feature Components

### 1. Health Status Manager (`HealthStatusManager.kt`)

**Location:** `app/src/main/java/dev/alphagame/seen/health/HealthStatusManager.kt`

**Purpose:** Manages health status checks for all backend services.

**Key Features:**
- Concurrent health checks for all services
- Network timeout handling (5 seconds)
- JSON response parsing for detailed status information
- State management using Kotlin StateFlow
- Automatic resource cleanup

**Health Check Endpoints:**
- Releases: `https://seen.alphagame.dev/releases/health`
- AI: `https://seen.alphagame.dev/api/ai/health`

**Health Status Types:**
- `HEALTHY`: Service is operational (green dot)
- `UNHEALTHY`: Service has issues (red dot)
- `UNKNOWN`: Status cannot be determined (gray dot)

### 2. Health Status UI Components (`HealthStatusDots.kt`)

**Location:** `app/src/main/java/dev/alphagame/seen/components/HealthStatusDots.kt`

**Purpose:** Provides the visual interface for health status monitoring.

**Components:**
- **HealthStatusDots**: Main composable showing the three dots
- **HealthDot**: Individual colored dot component
- **HealthStatusDialog**: Detailed status information dialog
- **ServiceHealthRow**: Individual service status display

**Visual Design:**
- Three 8dp circular dots with 4dp spacing
- Color coding: Green (healthy), Red (unhealthy), Gray (unknown)
- Clickable interface to show detailed status
- Material Design 3 styling

### 3. Settings Screen Integration

**Location:** `app/src/main/java/dev/alphagame/seen/screens/SettingsScreen.kt`

**Integration Points:**
- Health status dots added to TopAppBar actions
- Automatic health check on screen access
- Resource cleanup on screen disposal

## User Experience

### 1. Initial State
- All dots appear gray (unknown status) when first loaded
- Health checks are automatically triggered when Settings screen opens

### 2. Status Display
- **Green Dots**: All services are healthy and operational
- **Red Dots**: One or more services have issues
- **Gray Dots**: Service status is unknown (network issues, loading, etc.)

### 3. Detailed Information
- Tap the dots to open detailed status dialog
- View individual service status with descriptive text
- Refresh button to manually trigger new health checks
- Multilingual support (English, French, Spanish)

## Technical Implementation

### 1. Network Implementation
```kotlin
// HTTP client with 5-second timeouts
private val client = OkHttpClient.Builder()
    .connectTimeout(5, TimeUnit.SECONDS)
    .readTimeout(5, TimeUnit.SECONDS)
    .writeTimeout(5, TimeUnit.SECONDS)
    .build()
```

### 2. Status Response Parsing
```kotlin
// Supports standard health check JSON format
{
  "status": "healthy" | "unhealthy",
  "timestamp": 1673123456789,
  "services": { ... }
}
```

### 3. State Management
```kotlin
// Reactive state using StateFlow
val releasesHealthState: StateFlow<HealthStatus>
val aiHealthState: StateFlow<HealthStatus>
```

### 4. Concurrent Health Checks
```kotlin
// All services checked simultaneously
scope.launch {
    launch { checkAnalyticsHealth() }
    launch { checkReleasesHealth() }
    launch { checkAIHealth() }
}
```

## Error Handling

### 1. Network Errors
- Connection timeouts are handled gracefully
- Network errors result in UNHEALTHY status
- Error messages are logged but not exposed to users

### 2. Response Parsing
- Invalid JSON responses default to HEALTHY if HTTP 200
- HTTP 503 responses are treated as UNHEALTHY
- Other HTTP error codes are treated as UNHEALTHY

### 3. Resource Management
- OkHttp client properly configured with timeouts
- Coroutine scope cleanup on component disposal
- No memory leaks from health check operations

## Internationalization

### Supported Languages
- **English**: Service Health Status, Analytics, Releases, AI, etc.
- **French**: État de santé des services, Analytiques, Versions, IA, etc.
- **Spanish**: Estado de salud de servicios, Análisis, Versiones, IA, etc.

### Translation Keys
```kotlin
abstract val serviceHealthStatus: String
abstract val analyticsService: String
abstract val releasesService: String
abstract val aiService: String
abstract val healthyStatus: String
abstract val unhealthyStatus: String
abstract val unknownStatus: String
abstract val refreshStatus: String
abstract val closeDialog: String
```

## Server-Side Requirements

### 1. Health Check Endpoints
Each service must implement a health check endpoint that returns:

```json
{
  "status": "healthy" | "unhealthy",
  "timestamp": 1673123456789,
  "services": {
    "service_name": {
      "status": "healthy" | "unhealthy" | "skipped",
      "message": "Descriptive status message"
    }
  }
}
```

### 2. HTTP Status Codes
- `200 OK`: All services are healthy
- `503 Service Unavailable`: One or more services are unhealthy

### 3. CORS Configuration
Health check endpoints should allow cross-origin requests from the mobile app.

## Monitoring and Analytics

### 1. Health Check Events
- Health status changes can be tracked via analytics
- Service availability metrics can be collected
- User interaction with health status can be monitored

### 2. Performance Metrics
- Health check response times are measured
- Network timeout occurrences are tracked
- Service availability percentages can be calculated

## Future Enhancements

### 1. Additional Status Indicators
- Response time indicators
- Service-specific error details
- Historical status trends

### 2. Notification System
- Push notifications for critical service outages
- In-app alerts for degraded service performance
- Scheduled health check reminders

### 3. Advanced Configuration
- User-configurable health check intervals
- Custom endpoint URLs for different environments
- Health check result caching

## Testing Considerations

### 1. Unit Tests
- Mock HTTP responses for different scenarios
- Test state management and UI updates
- Verify error handling and edge cases

### 2. Integration Tests
- Test with actual backend endpoints
- Verify network timeout behavior
- Test UI interaction flows

### 3. Manual Testing
- Test in various network conditions
- Verify UI responsiveness and updates
- Test multilingual interface elements

## Security Considerations

### 1. Network Security
- HTTPS-only connections for health checks
- Certificate pinning for enhanced security
- Request timeout limits to prevent resource exhaustion

### 2. Data Privacy
- No sensitive data transmitted in health checks
- Health status information is not stored locally
- No user-identifiable information in health check requests

This health status monitoring feature provides users with real-time visibility into backend service availability while maintaining a clean, intuitive interface that fits seamlessly into the existing Settings screen design.
