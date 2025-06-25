# Background Update Notification System

## Overview

The Seen app now includes a background update notification system that automatically checks for new versions every 15 minutes and sends notifications to users when updates are available.

## Features

### Automatic Version Checking
- **Frequency**: Checks for updates every 15 minutes (Android minimum for background work)
- **Endpoint**: `https://seen.alphagame.dev/latestVersionTag`
- **Background Operation**: Uses Android WorkManager for reliable background execution
- **Battery Efficient**: Only runs when network is available and respects system battery optimization
- **System Enforced**: Android automatically enforces the 15-minute minimum interval regardless of app configuration

### Smart Notifications
- **Conditional Notifications**: Only sends notifications when:
  - Background update checks are enabled
  - User notifications are enabled
  - A newer version is actually available
- **Localized Messages**: Notifications are displayed in the user's preferred language (English, French, Spanish)
- **Action Integration**: Tapping the notification opens the app, where users can then download the update

### User Controls
- **Settings Integration**: New toggle in Settings > Notifications section
- **Granular Control**: Users can independently control:
  - General notifications (existing)
  - Background update checks (new)
- **Privacy Focused**: Fully opt-in system - disabled by default

## Technical Implementation

### Core Components

#### 1. VersionChecker (`VersionChecker.kt`)
- Polls the `latestVersionTag` endpoint
- Compares version strings using semantic versioning
- Handles network errors gracefully
- Supports version formats with or without 'v' prefix

#### 2. UpdateCheckWorker (`UpdateCheckWorker.kt`)
- Android WorkManager background worker
- Runs every 15 minutes when network is available
- Checks user preferences before executing
- Sends localized notifications when updates are found

#### 3. UpdateCheckManager (`UpdateCheckManager.kt`)
- Manages the lifecycle of background update checks
- Starts/stops periodic work based on user preferences
- Provides status checking capabilities
- Handles work scheduling and cancellation

#### 4. Enhanced NotificationManager (`NotificationManager.kt`)
- Added `sendUpdateNotification()` method
- Uses dedicated notification ID for update notifications
- Integrates with existing notification channel
- Supports high-priority notifications for updates

### New Settings

#### PreferencesManager Extensions
- `backgroundUpdateChecksEnabled`: Boolean preference for enabling/disabling background checks
- `lastBackgroundUpdateCheck`: Timestamp of last background check
- Integration with existing notification preferences

#### User Interface
- New toggle in Settings > Notifications section
- Descriptive text explaining the 15-minute interval
- Automatic start/stop of background service based on toggle state
- Respects existing notification permissions

### Translation Support

#### New Translation Strings
```kotlin
// Update Notifications
abstract val updateNotificationTitle: String
abstract val updateNotificationMessage: String
abstract val enableBackgroundUpdateChecks: String
abstract val enableBackgroundUpdateChecksDescription: String
```

#### Localized in Three Languages
- **English**: "New Update Available", "A new version of Seen is available. Tap to download."
- **French**: "Nouvelle mise à jour disponible", "Une nouvelle version de Seen est disponible. Appuyez pour télécharger."
- **Spanish**: "Nueva actualización disponible", "Una nueva versión de Seen está disponible. Toca para descargar."

## Usage Instructions

### For Users

1. **Enable Background Checks**:
   - Open Settings
   - Navigate to Notifications section
   - Enable "Background Update Checks"
   - Ensure general notifications are also enabled

2. **Receiving Notifications**:
   - Notifications appear automatically when new versions are available
   - Tap notification to open the app
   - Use existing update flow to download the update

3. **Disabling the Feature**:
   - Toggle off "Background Update Checks" in Settings
   - Background service stops immediately
   - No impact on manual update checks

### For Developers

#### Starting Background Checks
```kotlin
val updateCheckManager = UpdateCheckManager(context)
updateCheckManager.startBackgroundUpdateChecks()
```

#### Stopping Background Checks
```kotlin
updateCheckManager.stopBackgroundUpdateChecks()
```

#### Checking Service Status
```kotlin
val isRunning = updateCheckManager.isBackgroundUpdateCheckRunning()
```

## Server Requirements

### Endpoint Specification
- **URL**: `https://seen.alphagame.dev/latestVersionTag`
- **Method**: GET
- **Response**: Plain text version string (e.g., "1.5.0" or "v1.5.0")
- **Headers**: Accepts User-Agent header with app version

### Response Format
- Simple version string without additional JSON
- Supports semantic versioning (major.minor.patch)
- Can include or omit 'v' prefix
- Should be latest stable release version

### Expected Server Response Examples

#### Example 1: Version with 'v' prefix
```
HTTP/1.1 200 OK
Content-Type: text/plain
Content-Length: 6

v1.5.2
```

#### Example 2: Version without 'v' prefix
```
HTTP/1.1 200 OK
Content-Type: text/plain
Content-Length: 5

1.5.2
```

#### Example 3: Error Response
```
HTTP/1.1 404 Not Found
Content-Type: text/plain
Content-Length: 19

Version not found
```

**Note**: The app will handle both formats (with or without 'v' prefix) and gracefully handle error responses by logging them and continuing normal operation.

## Privacy & Performance

### Privacy Considerations
- **Opt-in Only**: Feature is disabled by default
- **Local Processing**: Version comparison happens locally
- **No Personal Data**: Only version information is transmitted
- **Respects Permissions**: Honors user notification preferences

### Performance Optimizations
- **Network Constraints**: Only runs when network is available
- **Battery Efficient**: Uses WorkManager's built-in optimizations
- **Minimal Bandwidth**: Single HTTP request every 15 minutes
- **Graceful Failures**: Retries on failure with exponential backoff

### Error Handling
- Network failures are logged but don't interrupt the app
- Invalid responses are handled gracefully
- Service automatically retries failed requests
- User preferences are respected even during failures

## Integration with Existing Update System

### Compatibility
- Works alongside existing manual update checks
- Uses same notification channel as other app notifications
- Integrates with existing UpdateDialog component
- Respects existing version skip functionality

### No Conflicts
- Background checks don't interfere with manual checks
- Separate tracking of background vs. manual check timestamps
- Independent settings for each update mechanism
- Consistent user experience across all update flows

## File Changes Summary

### New Files
- `app/src/main/java/dev/alphagame/seen/data/VersionChecker.kt`
- `app/src/main/java/dev/alphagame/seen/workers/UpdateCheckWorker.kt`
- `app/src/main/java/dev/alphagame/seen/data/UpdateCheckManager.kt`

### Modified Files
- `app/build.gradle.kts` - Added WorkManager dependency
- `app/src/main/java/dev/alphagame/seen/translations/Translation.kt` - Added notification strings
- `app/src/main/java/dev/alphagame/seen/translations/EnglishTranslation.kt` - English translations
- `app/src/main/java/dev/alphagame/seen/translations/FrenchTranslation.kt` - French translations
- `app/src/main/java/dev/alphagame/seen/translations/SpanishTranslation.kt` - Spanish translations
- `app/src/main/java/dev/alphagame/seen/data/PreferencesManager.kt` - Added background check preferences
- `app/src/main/java/dev/alphagame/seen/data/NotificationManager.kt` - Added update notifications
- `app/src/main/java/dev/alphagame/seen/screens/SettingsScreen.kt` - Added background check toggle
- `app/src/main/java/dev/alphagame/seen/MainActivity.kt` - Added background service initialization
- `app/src/main/java/dev/alphagame/seen/components/UpdateDialog.kt` - Enhanced with fallback URL

## Future Enhancements

### Potential Improvements
- **Configurable Intervals**: Allow users to choose check frequency
- **Update Size Information**: Display download size in notifications
- **Auto-download**: Option to automatically download updates
- **Update History**: Track and display update notification history
- **Beta Channel Support**: Allow users to opt into beta releases

### Monitoring & Analytics
- **Usage Metrics**: Track background check success rates
- **Performance Monitoring**: Monitor battery and network impact
- **User Engagement**: Track notification open rates
- **Error Reporting**: Log and analyze failure patterns

This implementation provides a robust, user-friendly, and privacy-conscious background update notification system that enhances the user experience while respecting user preferences and device resources.

## Troubleshooting

### Common Issues

#### Background Checks Not Starting
**Problem**: The background update service doesn't start when the app launches.

**Solution**: Check that both settings are enabled:
1. Go to Settings → Notifications
2. Enable "Enable Reminders" (general notifications)
3. Enable "Background Update Checks"
4. Restart the app

**Debug**: Check logcat for messages like:
```
UpdateCheck: Background update checks not started:
UpdateCheck:   - backgroundUpdateChecksEnabled: false
UpdateCheck:   - notificationsEnabled: false
```

#### Test Button Not Working
**Problem**: The "🧪 Test Background Update Check" button doesn't send notifications.

**Possible Causes**:
1. Network connectivity issues
2. Server endpoint `https://seen.alphagame.dev/latestVersionTag` not responding
3. Version comparison logic detecting no update needed

**Debug**: Check logcat for:
```
SettingsScreen: Testing background update check...
SettingsScreen: Latest version: [version from server]
SettingsScreen: Update available: [true/false]
```

#### No Notifications Received
**Problem**: Background service is running but notifications aren't appearing.

**Possible Causes**:
1. Android system notification permissions revoked
2. App in battery optimization (Doze mode)
3. Notification channel disabled
4. No actual update available

**Debug Steps**:
1. Check Android notification settings for the app
2. Disable battery optimization for the app
3. Use the test button to manually trigger a check
4. Check logcat for worker execution logs

#### Version Comparison Issues
**Problem**: App reports no update available when one should exist.

**Check**: Verify the server response format:
- Should return plain text version string
- Examples: "1.5.0", "v1.5.0"
- Must follow semantic versioning (major.minor.patch)

### Debug Logging

Enable verbose logging to troubleshoot issues:

```bash
# Filter logs for update-related messages
adb logcat -s UpdateCheck UpdateCheckWorker UpdateCheckManager VersionChecker SettingsScreen
```

Key log messages to look for:
- `UpdateCheck: Starting background update checks`
- `UpdateCheckWorker: Starting background update check...`
- `VersionChecker: Latest version response: [version]`
- `UpdateCheckWorker: Update available, sending notification`

### Testing the Feature

1. **Enable Settings**: Turn on both notification toggles in Settings
2. **Use Test Button**: Tap "🧪 Test Background Update Check" to manually test
3. **Check Logs**: Monitor logcat for debug messages
4. **Verify Server**: Test the endpoint manually:
   ```bash
   curl https://seen.alphagame.dev/latestVersionTag
   ```
5. **Wait for Background Check**: Service runs every 15 minutes (Android WorkManager minimum interval)

### Important Notes

#### WorkManager Interval Limitations
Android WorkManager enforces a **minimum interval of 15 minutes** for periodic work requests. This is a system limitation to preserve battery life and prevent apps from running too frequently in the background.

**Warning Message**: If you see this log message:
```
WM-WorkSpec: Interval duration lesser than minimum allowed value; Changed to 900000
```

This is **normal and expected behavior**. It indicates that WorkManager automatically adjusted the interval to the minimum allowed value (15 minutes = 900,000 milliseconds). This ensures compliance with Android's background execution policies and optimal battery performance.

#### Testing Considerations
For immediate testing of the update notification functionality:
1. Use the "🧪 Test Background Update Check" button in Settings
2. Monitor logcat for real-time feedback
3. The background service will start working after 15 minutes of the app being backgrounded
