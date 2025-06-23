# Mood Tracking Widget

## Overview
I've successfully added an Android widget to your Seen app that allows you to quickly track your mood with four emoji options:

- 😰 **Anxious** - For when you're feeling worried or nervous
- 😞 **Sad** - For when you're feeling down or melancholy 
- 😊 **Happy** - For when you're feeling good and content
- 😄 **Very Happy** - For when you're feeling extremely joyful

## How to Use the Widget

### Adding the Widget to Your Home Screen
1. **Build and install the app** on your Android device
2. **Long press** on an empty area of your home screen
3. **Tap "Widgets"** from the options that appear
4. **Find "Seen"** in the widget list
5. **Select the "Mood Tracker"** widget
6. **Drag it** to your desired location on the home screen
7. **Resize if needed** by dragging the corner handles

### Using the Widget
- Simply **tap any of the four emoji buttons** to log your current mood
- The widget will show your **last recorded mood** and timestamp
- All mood data is **automatically saved** to your device

### Viewing Your Mood History
- **Open the Seen app**
- From the main screen, **tap "😊 Mood History"**
- View your complete mood tracking history with:
  - **Daily statistics** showing today's entries
  - **Complete chronological list** of all mood entries
  - **Quick overview** of your emotional patterns

### Features
- **Quick access** - Track mood without opening the app
- **Persistent data** - All entries are saved locally on your device
- **Visual feedback** - See your last mood and when it was recorded
- **History viewing** - Review patterns and trends in the main app
- **Privacy focused** - Data stays on your device

## Technical Details

### Data Storage
- Mood entries are stored locally using SharedPreferences
- Data includes mood type and precise timestamp
- Maximum of 100 most recent entries are kept
- Data persists across app updates

### Widget Specifications
- **Minimum size**: 250dp × 110dp (4×2 grid cells)
- **Resizable**: Can be adjusted horizontally and vertically
- **Update frequency**: Real-time when buttons are pressed
- **Battery efficient**: Only updates when interacted with

### Integration with Main App
- The widget uses the same `Mood` enum as the main application
- Mood data from the widget can be viewed in the dedicated "Mood History" screen
- Data is separate from PHQ-9 assessments and notes

## Files Added/Modified

### New Files
- `app/src/main/res/layout/mood_widget.xml` - Widget layout
- `app/src/main/res/drawable/widget_background.xml` - Widget background design
- `app/src/main/res/drawable/widget_button_background.xml` - Button styling
- `app/src/main/res/xml/mood_widget_info.xml` - Widget configuration
- `app/src/main/java/dev/alphagame/seen/widget/MoodWidgetProvider.kt` - Widget logic
- `app/src/main/java/dev/alphagame/seen/data/WidgetMoodManager.kt` - Data management
- `app/src/main/java/dev/alphagame/seen/screens/MoodHistoryScreen.kt` - History viewer

### Modified Files
- `app/build.gradle.kts` - Added Gson dependency for data serialization
- `app/src/main/AndroidManifest.xml` - Registered widget provider
- `app/src/main/res/values/strings.xml` - Added widget description
- `app/src/main/java/dev/alphagame/seen/MainActivity.kt` - Added navigation
- `app/src/main/java/dev/alphagame/seen/screens/WelcomeScreen.kt` - Added mood history button

The widget is now ready to use! Users can quickly track their mood throughout the day and review their emotional patterns in the main app.
