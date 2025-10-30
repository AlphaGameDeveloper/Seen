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

package dev.alphagame.seen.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent
import dev.alphagame.seen.FeatureFlags
import dev.alphagame.seen.data.Mood
import dev.alphagame.seen.data.MoodEntry
import dev.alphagame.seen.data.NotesManager
import dev.alphagame.seen.data.PHQ9Response
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.WidgetMoodManager
import dev.alphagame.seen.translations.Translation
import dev.alphagame.seen.translations.rememberTranslation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun MoodHistoryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val notesManager = remember { NotesManager(context) }
    val widgetMoodManager = remember { WidgetMoodManager(context) }
    var moodEntries by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPHQ9DeleteDialog by remember { mutableStateOf(false) }
    var phq9Results by remember { mutableStateOf<List<PHQ9Response>>(emptyList()) }
    val modelProducer = remember { CartesianChartModelProducer() }
    var preferencesManager = remember { PreferencesManager(context) }

    // Load mood entries
    LaunchedEffect(Unit) {
        moodEntries = widgetMoodManager.getMoodEntries()
        // Track mood history screen access
        phq9Results = notesManager.getPHQ9Responses()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = translation.back,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = translation.moodHistory,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (FeatureFlags.UI_MOOD_HISTORY_GLOBAL_DELETE) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    enabled = moodEntries.isNotEmpty()
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = translation.clearHistory,
                        tint = if (moodEntries.isNotEmpty()) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics
        if (moodEntries.isNotEmpty()) {
            MoodStatistics(moodEntries = moodEntries, translation = translation)
            Spacer(modifier = Modifier.height(16.dp))
        }

        val phq9Map = (phq9Results.map { it.total }).reversed()

        //Experimenting with using a time on the x axis. Can't seem to use anything but intergers
        val formatter = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
        val dateMap = (phq9Results.map { formatter.format(Date(it.timestamp)) }).reversed()

        Log.d("MoodHistoryScreen", phq9Map.toString())
        Log.d("MoodHistoryScreen", dateMap.toString())

        //making the x axis start at 1 instead of 0
        val x = (1..phq9Map.size).toList()

        // Only update chart data if we have PHQ-9 results
        LaunchedEffect(phq9Results) {
            if (phq9Map.isNotEmpty()) {
                modelProducer.runTransaction {
                    lineSeries { series(x, phq9Map) }
                }
            }
        }


        if (phq9Map.isNotEmpty() && phq9Map.size >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = translation.phqResponses,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = {
                    Log.d("MoodHistoryScreen","PHQ-9 Responses Delete Button pressed")

                    // Cover the existing UI with a dialog listing all the entries, where the user can
                    // select which ones to delete
                    showPHQ9DeleteDialog = true
                }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = translation.clearHistory,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            //Function to determine what color the numbers will use
            @Composable
            fun findColor(): Int {
                if (preferencesManager.themeMode != "auto") {
                    return if (preferencesManager.themeMode == "dark") {
                        0xFFFFFFFF.toInt()
                    } else {
                        0xFF000000.toInt()
                    }
                }
                else {
                    val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme()
                    return if (isDarkMode) {
                        0xFFFFFFFF.toInt()
                    } else {
                        0xFF000000.toInt()
                    }
                }
            }
            Log.e("MoodHistoryScreen", findColor().toString())

            //Creates text component with correct color
            val axisLabelComponent = TextComponent(
                color = findColor()
            )
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(label = axisLabelComponent),
                    bottomAxis = HorizontalAxis.rememberBottom(label = axisLabelComponent),
                    ),
                modelProducer,
            )
        } else {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = translation.graphText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(
                PaddingValues(2.dp)
            ),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.tertiary
        )


        // Mood entries list
        if (moodEntries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = translation.noMoodData,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                item {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 8.dp,
                        color = Color.Transparent
                    )
                }

                item {
                    Text(
                        text = translation.moodEntries,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                items(moodEntries) { entry ->
                    MoodEntryItem(
                        entry = entry,
                        translation = translation,
                        onDeleteClick = { entryToDelete ->
                            // Track individual mood entry deletion
                            widgetMoodManager.deleteMoodEntry(entryToDelete.timestamp)
                            moodEntries = widgetMoodManager.getMoodEntries()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showPHQ9DeleteDialog && phq9Results.isNotEmpty()) {
        // show dialog to select which PHQ-9 entries to delete
        // on confirm, delete selected entries and update phq9Results
        // on dismiss, set showPHQ9DeleteDialog = false

        // show a card that covers the whole screen (minus the top bar) with a list of PHQ-9 entries
        // each entry has a checkbox to select it for deletion
        // at the bottom, there are Cancel and Delete buttons
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showPHQ9DeleteDialog = false }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = translation.back,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = translation.deletePHQ9EntryTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // List of PHQ-9 entries
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(phq9Results) { entry ->
                        PHQ9DeletionItem(
                            onDeleteClick = { entryToDelete ->
                                // Track individual PHQ-9 entry deletion
                                notesManager.deletePHQ9Response(entryToDelete.id)
                                phq9Results = notesManager.getPHQ9Responses()
                                            },
                            entry = entry
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(translation.clearMoodHistory) },
            text = { Text(translation.clearMoodHistoryConfirmation) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val entriesToDelete = moodEntries.size
                        widgetMoodManager.clearAllMoods()
                        moodEntries = emptyList()
                        showDeleteDialog = false
                    }
                ) {
                    Text(translation.delete)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(translation.cancel)
                }
            }
        )
    }
}

@Composable
private fun MoodStatistics(
    moodEntries: List<MoodEntry>,
    translation: Translation
) {
    if (FeatureFlags.MOOD_HISTORY_TODAY_AT_A_GLANCE_CARD) {
        val todaysMoods = remember(moodEntries) {
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            val startOfDay = today.time

            moodEntries.filter { it.timestamp >= startOfDay }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = translation.todaysStats,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(String.format(translation.entriesToday, todaysMoods.size))
                    Text(String.format(translation.totalEntries, moodEntries.size))
                }

                if (todaysMoods.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(
                            translation.todaysMoods,
                            todaysMoods.joinToString(" ") { it.mood.emoji }),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodEntryItem(
    entry: MoodEntry,
    translation: Translation,
    onDeleteClick: (MoodEntry) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.mood.emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = Mood.getLocalizedLabel(entry.mood, translation),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(entry.timestamp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = translation.deleteMoodEntry,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Individual delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(translation.deleteMoodEntry) },
            text = {
                Text(String.format(translation.deleteMoodEntryConfirmation, entry.mood.label, dateFormat.format(entry.timestamp)))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(entry)
                        showDeleteDialog = false
                    }
                ) {
                    Text(translation.delete)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(translation.cancel)
                }
            }
        )
    }
}

@Composable
private fun PHQ9DeletionItem(
    entry: PHQ9Response,
    onDeleteClick: (PHQ9Response) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val translation = rememberTranslation()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            @Composable
            fun findColor(_entry: Int): Color {
                return when (_entry) {
                    in 0..4 -> Color(0xFF00C853) // Green
                    in 5..9 -> Color(0xFFFFAB00) // Yellow
                    in 10..14 -> Color(0xFFFF6D00) // Orange
                    in 15..19 -> Color(0xFFD50000) // Red
                    in 20..27 -> Color(0xFFB00020) // Dark Red
                    else -> Color.Black // Fallback color
                }
            }

            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .width(50.dp)
                    .height(50.dp) // Set a fixed height to make it circular
                    .align(Alignment.CenterVertically)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp, // Border width
                        color = MaterialTheme.colorScheme.onSurface, // Border color
                        shape = CircleShape // Circular shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.total.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = findColor(entry.total) // Text color to contrast with the black background
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when( entry.total ) {
                        in 0..4 -> translation.resultMinimal
                        in 5..9 -> translation.resultMild
                        in 10..14 -> translation.resultModerate
                        in 15..19 -> translation.phq9SeverityModerate
                        in 20..27 -> translation.resultSevere
                        else -> "Unknown"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(entry.timestamp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "OwO what's this",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Individual delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },

            title = { Text(translation.deletePHQ9EntryTitle) },
            text = {
                val id = entry.id

                Text(
                    String.format(
                        translation.deletePHQ9Result,
                        entry.total.toString(),
                        dateFormat.format(entry.timestamp)
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(entry)
                        showDeleteDialog = false
                    }
                ) {
                    Text(translation.deletePHQ9EntryConfirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(translation.deletePHQ9EntryCancel)
                }
            }
        )
    }
}
