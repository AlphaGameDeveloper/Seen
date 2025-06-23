# PHQ-9 Data Storage Feature

## Overview
Added optional PHQ-9 assessment data storage functionality to save detailed assessment responses to a local SQLite database.

## New Database Schema

### Table: `phq9_responses`
Stores detailed question-by-question responses from PHQ-9 assessments:

| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Unique identifier |
| q1 | INTEGER | Response to question 1 (0-3) |
| q2 | INTEGER | Response to question 2 (0-3) |
| q3 | INTEGER | Response to question 3 (0-3) |
| q4 | INTEGER | Response to question 4 (0-3) |
| q5 | INTEGER | Response to question 5 (0-3) |
| q6 | INTEGER | Response to question 6 (0-3) |
| q7 | INTEGER | Response to question 7 (0-3) |
| q8 | INTEGER | Response to question 8 (0-3) |
| q9 | INTEGER | Response to question 9 (0-3) |
| total | INTEGER | Total score (sum of all responses) |
| timestamp | INTEGER | Assessment completion timestamp |

## Settings Option
Added a new setting in the Settings screen:
- **"Save PHQ-9 Assessment Data"** - Toggle to enable/disable data storage
- Default: `false` (disabled)
- When enabled, both summary results and detailed responses are saved

## Implementation Details

### Database Changes
- **DatabaseHelper.kt**: Updated to version 2, added new table for detailed responses
- **Database migration**: Graceful upgrade preserves existing data

### Data Management
- **NotesManager.kt**: Added methods for saving and retrieving PHQ-9 detailed responses
- **PreferencesManager.kt**: Added setting for enabling/disabling PHQ-9 data storage

### UI Updates
- **SettingsScreen.kt**: Added toggle switch for PHQ-9 data storage preference
- **ResultScreen.kt**: Automatically saves data when setting is enabled
- **DatabaseDebugScreen.kt**: Added section to view stored PHQ-9 detailed responses

### Data Format Example
When PHQ-9 assessment data storage is enabled, responses are saved in this format:

```
q1: 1, q2: 2, q3: 0, q4: 1, q5: 0, q6: 1, q7: 2, q8: 0, q9: 0, total: 7
```

This matches the requested format: `q1 | q2 | qN | total`

## Privacy & Security
- All data is stored locally on the device
- No external data transmission
- User has full control via settings toggle
- Data can be deleted through the existing "Delete All Local Data" function
- Setting is disabled by default to ensure privacy

## Usage
1. Go to Settings → Assessment Settings
2. Toggle "Save PHQ-9 Assessment Data" to enable
3. Complete PHQ-9 assessments as normal
4. Data is automatically saved when assessments are completed
5. View stored data in the debug screen (accessible via secret tap sequence on home screen)
