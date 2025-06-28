package dev.alphagame.seen.ai.debug

import android.content.Context
import android.util.Log
import dev.alphagame.seen.ai.AIManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Debug utilities for testing AI Manager connectivity
 */
class AIDebugHelper(private val context: Context) {
    
    private val aiManager = AIManager(context)
    
    companion object {
        private const val TAG = "AIDebugHelper"
    }
    
    /**
     * Run comprehensive debugging tests
     */
    fun runDebugTests(scope: CoroutineScope) {
        scope.launch {
            Log.d(TAG, "Starting AI Manager debug tests...")
            
            // Test 1: Basic availability check
            Log.d(TAG, "\n=== Test 1: Basic Availability ===")
            val isAvailable = aiManager.isAIServiceAvailable()
            Log.d(TAG, "Basic availability: $isAvailable")
            
            // Test 2: PHQ9 endpoint check
            Log.d(TAG, "\n=== Test 2: PHQ9 Endpoint ===")
            val isPHQ9Available = aiManager.isPHQ9EndpointAvailable()
            Log.d(TAG, "PHQ9 endpoint available: $isPHQ9Available")
            
            // Test 3: Detailed debug information
            Log.d(TAG, "\n=== Test 3: Detailed Debug ===")
            aiManager.debugConnection()
            Log.d(TAG, "Debug complete. Check logs for detailed output.")
            
            // Test 4: Actual PHQ9 submission
            if (isPHQ9Available) {
                Log.d(TAG, "\n=== Test 4: Actual PHQ9 Submission ===")
                val testResponses = listOf(1, 1, 2, 1, 0, 1, 1, 0, 1) // Total: 8 (Mild)
                val totalScore = testResponses.sum()
                
                val result = aiManager.submitPHQ9ForAnalysis(totalScore, testResponses)
                result.onSuccess { response ->
                    Log.d(TAG, "✅ PHQ9 submission successful!")
                    Log.d(TAG, "Severity: ${response.severity}")
                    Log.d(TAG, "Emotional State: ${response.emotional_state}")
                    Log.d(TAG, "Recommendations count: ${response.recommendations?.size ?: 0}")
                }.onFailure { error ->
                    Log.e(TAG, "❌ PHQ9 submission failed: ${error.message}")
                }
            } else {
                Log.w(TAG, "Skipping PHQ9 submission test - endpoint not available")
            }
            
            Log.d(TAG, "AI Manager debug tests completed!")
        }
    }
    
    /**
     * Quick connectivity test
     */
    fun quickConnectivityTest(scope: CoroutineScope, callback: (Boolean) -> Unit) {
        scope.launch {
            val isAvailable = aiManager.isAIServiceAvailable()
            Log.d(TAG, "Quick connectivity test result: $isAvailable")
            callback(isAvailable)
        }
    }
    
    /**
     * Test with specific PHQ9 data
     */
    fun testWithCustomData(
        scope: CoroutineScope,
        responses: List<Int>,
        callback: (Boolean, String?) -> Unit
    ) {
        scope.launch {
            if (responses.size != 9 || responses.any { it < 0 || it > 3 }) {
                callback(false, "Invalid PHQ9 data: must be 9 responses with values 0-3")
                return@launch
            }
            
            val totalScore = responses.sum()
            val result = aiManager.submitPHQ9ForAnalysis(totalScore, responses)
            
            result.onSuccess { response ->
                Log.d(TAG, "Custom data test successful: ${response.severity}")
                callback(true, "Success: ${response.severity}")
            }.onFailure { error ->
                Log.e(TAG, "Custom data test failed: ${error.message}")
                callback(false, "Failed: ${error.message}")
            }
        }
    }
}
