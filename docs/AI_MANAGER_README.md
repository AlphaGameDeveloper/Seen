# AI Manager for PHQ-9 Analysis

## Overview
The AI Manager provides intelligent analysis of PHQ-9 assessment results by sending data to the Seen AI API and receiving detailed insights about the user's mental health state.

## Features
- ✅ Automatic PHQ-9 data submission to AI API
- ✅ Comprehensive AI analysis including emotional state assessment
- ✅ Personalized recommendations based on responses
- ✅ Severity level classification
- ✅ Error handling and retry functionality
- ✅ Network connectivity checks

## API Integration

### Endpoint
```
POST https://seen.alphagame.dev/api/ai/phq9
```

### Request Format
```json
{
  "total": 15,
  "responses": [2, 1, 3, 2, 2, 1, 2, 1, 1]
}
```

### Response Format
```json
{
  "emotional_state": "The patient is experiencing moderate depressive symptoms...",
  "recommendations": [
    "Consider scheduling regular therapy sessions...",
    "Practice mindfulness and meditation techniques...",
    "Maintain regular exercise routine..."
  ],
  "severity": "Moderate"
}
```

## Usage

### Basic Implementation
```kotlin
val aiManager = AIManager(context)

// Submit PHQ-9 data for analysis
val result = aiManager.submitPHQ9ForAnalysis(
    totalScore = totalScore,
    responses = listOf(2, 1, 3, 2, 2, 1, 2, 1, 1)
)

result.onSuccess { aiResponse ->
    // Handle successful AI analysis
    println("Severity: ${aiResponse.severity}")
    println("Emotional State: ${aiResponse.emotional_state}")
    aiResponse.recommendations.forEach { recommendation ->
        println("- $recommendation")
    }
}.onFailure { error ->
    // Handle error
    println("AI analysis failed: ${error.message}")
}
```

### Integration in ResultScreen
The AI Manager is automatically integrated into the ResultScreen and provides:
- **Loading State**: Shows progress indicator while analysis is in progress
- **Success State**: Displays comprehensive AI analysis with emotional state and recommendations
- **Error State**: Shows error message with retry option
- **Retry Functionality**: Allows users to retry if the initial request fails

## Components

### Core Classes
- **AIManager**: Main class for handling AI API communication
- **PHQ9Request**: Data class for API request payload
- **PHQ9Response**: Data class for API response payload
- **AIAnalysisCard**: UI component for displaying AI analysis results

### Utility Classes
- **PHQ9Utils**: Helper functions for PHQ-9 data validation and processing

## Error Handling
The AI Manager includes comprehensive error handling:
- **Network Errors**: Handles connectivity issues and timeouts
- **API Errors**: Processes HTTP error responses
- **Parsing Errors**: Manages JSON parsing failures
- **Validation Errors**: Ensures PHQ-9 data integrity

## Privacy & Security
- All data transmission uses HTTPS encryption
- No personally identifiable information is sent to the AI API
- Only PHQ-9 responses and total scores are transmitted
- Responses are processed server-side and not stored permanently

## Configuration
The AI Manager can be configured with different timeouts and endpoints:
```kotlin
// Default configuration (30 second timeouts)
val aiManager = AIManager(context)

// Service availability check
val isAvailable = aiManager.isAIServiceAvailable()
```

## Integration Points
The AI Manager integrates with:
- **ResultScreen**: Automatic analysis after PHQ-9 completion
- **NotesManager**: Optional data storage integration
- **PreferencesManager**: Settings for enabling/disabling AI features

## Future Enhancements
- Offline mode with cached recommendations
- Trend analysis for multiple assessments
- Integration with healthcare provider systems
- Multilingual AI analysis support
