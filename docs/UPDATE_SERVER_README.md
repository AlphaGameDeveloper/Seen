# Update Server JSON Response Format

Your update server at `https://seen.alphagame.dev` should return a JSON response in the following format:

## Example Response for Update Available:

```json
{
  "version": "1.5.0",
  "versionCode": 10500,
  "downloadUrl": "https://github.com/your-repo/releases/latest/download/seen-v1.5.0.apk",
  "releaseNotes": "• Added new mood tracking features\n• Improved UI performance\n• Fixed bugs in assessment system\n• Enhanced translations",
  "forceUpdate": false,
  "minimumRequiredVersion": "1.3.0"
}
```

## Example Response for Force Update:

```json
{
  "version": "1.6.0",
  "versionCode": 10600,
  "downloadUrl": "https://github.com/your-repo/releases/latest/download/seen-v1.6.0.apk",
  "releaseNotes": "Critical security update required for all users.",
  "forceUpdate": true,
  "minimumRequiredVersion": "1.6.0"
}
```

## Field Descriptions:

- **version**: The latest version string (e.g., "1.5.0")
- **versionCode**: The version code as an integer (used for comparison)
- **downloadUrl**: Direct download link to the APK file (optional)
- **releaseNotes**: What's new in this version (optional, supports newlines with \n)
- **forceUpdate**: Whether this is a mandatory update (default: false)
- **minimumRequiredVersion**: If force update is true, versions below this require immediate update

## Version Code Calculation:

The app uses this formula for version codes:
- Version "1.4.0" = 10400 (major * 10000 + minor * 100 + patch)
- Version "1.5.2" = 10502
- Version "2.0.0" = 20000

## Update Check Behavior:

1. **Automatic Checks**: App checks for updates on startup (once every 24 hours)
2. **Manual Checks**: Users can manually check from Settings > About > "Check for Updates"
3. **Skip Version**: Users can skip non-mandatory updates (remembered until next version)
4. **Force Updates**: Cannot be skipped and will block app usage until updated

## Server Requirements:

- Must respond to GET requests
- Content-Type: application/json
- Should include proper CORS headers if needed
- Graceful handling of network failures (app will continue without updates)

## HTTP Headers Sent:

The app sends this User-Agent header with requests:
```
User-Agent: Seen-Android/1.4.0
```

You can use this to track usage or provide device-specific responses if needed.
