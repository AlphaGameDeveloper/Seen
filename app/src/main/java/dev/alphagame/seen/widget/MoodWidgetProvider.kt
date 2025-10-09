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

package dev.alphagame.seen.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dev.alphagame.seen.R
import dev.alphagame.seen.analytics.AnalyticsManager
import dev.alphagame.seen.data.Mood
import dev.alphagame.seen.data.WidgetMoodManager
import java.text.SimpleDateFormat
import java.util.Locale

class MoodWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_MOOD_CLICKED = "dev.alphagame.seen.MOOD_CLICKED"
        const val EXTRA_MOOD_ID = "mood_id"

        private val dateFormat by lazy { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_MOOD_CLICKED) {
            val moodId = intent.getIntExtra(EXTRA_MOOD_ID, -1)
            if (moodId != -1) {
                val mood = Mood.fromId(moodId)
                if (mood != null) {
                    // Save the mood
                    val widgetMoodManager = WidgetMoodManager(context)
                    widgetMoodManager.saveMood(mood)

                    // Track mood widget usage
                    val analyticsManager = AnalyticsManager(context)
                    analyticsManager.trackEvent("mood_logged_via_widget", mapOf(
                        "mood" to mood.label
                    ))

                    // Update all widgets
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, MoodWidgetProvider::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

                    for (appWidgetId in appWidgetIds) {
                        updateAppWidget(context, appWidgetManager, appWidgetId)
                    }
                }
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.mood_widget)

        // Set up click listeners for each mood button
        setupMoodButton(context, views, R.id.btn_anxious, Mood.ANXIOUS.id)
        setupMoodButton(context, views, R.id.btn_sad, Mood.SAD.id)
        setupMoodButton(context, views, R.id.btn_happy, Mood.HAPPY.id)
        setupMoodButton(context, views, R.id.btn_very_happy, Mood.VERY_HAPPY.id)

        // Update the last mood text
        val widgetMoodManager = WidgetMoodManager(context)
        val lastMoodEntry = widgetMoodManager.getLastMood()

        val lastMoodText = if (lastMoodEntry != null) {
            "Last: ${lastMoodEntry.mood.emoji} ${dateFormat.format(lastMoodEntry.timestamp)}"
        } else {
            "Tap to log your mood"
        }

        views.setTextViewText(R.id.tv_last_mood, lastMoodText)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setupMoodButton(
        context: Context,
        views: RemoteViews,
        buttonId: Int,
        moodId: Int
    ) {
        val intent = Intent(context, MoodWidgetProvider::class.java).apply {
            action = ACTION_MOOD_CLICKED
            putExtra(EXTRA_MOOD_ID, moodId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            moodId, // Use moodId as request code to make each PendingIntent unique
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(buttonId, pendingIntent)
    }
}
