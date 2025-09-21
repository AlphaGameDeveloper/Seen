package dev.alphagame.seen.screens

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent
import dev.alphagame.seen.analytics.AnalyticsManager
import dev.alphagame.seen.data.MoodEntry
import dev.alphagame.seen.data.NotesManager
import dev.alphagame.seen.data.PHQ9Response
import dev.alphagame.seen.data.WidgetMoodManager
import dev.alphagame.seen.translations.rememberTranslation
import dev.alphagame.seen.data.PreferencesManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date


@Composable
fun MoodHistoryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val notesManager = remember { NotesManager(context) }
    val widgetMoodManager = remember { WidgetMoodManager(context) }
    val analyticsManager = remember { AnalyticsManager(context) }
    var moodEntries by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var phq9Results by remember { mutableStateOf<List<PHQ9Response>>(emptyList()) }
    val modelProducer = remember { CartesianChartModelProducer() }
    var preferencesManager = remember { PreferencesManager(context) }

    // Load mood entries
    LaunchedEffect(Unit) {
        moodEntries = widgetMoodManager.getMoodEntries()
        // Track mood history screen access
        phq9Results = notesManager.getPHQ9Responses()
        analyticsManager.trackEvent("mood_history_accessed", mapOf(
            "total_entries" to moodEntries.size.toString()
        ))
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


        if (phq9Map.isNotEmpty()) {
            Text(
                text = "PHQ-9 Responses",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            //Function to determine what color the numbers will use
            @Composable
            fun findColor(): Int {
                if (preferencesManager.themeMode == "dark") {
                    return 0xFFFFFFFF.toInt()
                } else {
                    return 0xFF000000.toInt()
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
                        text = "Mood Entries",
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
                            analyticsManager.trackEvent("mood_entry_deleted", mapOf(
                                "mood" to entryToDelete.mood.label
                            ))
                            widgetMoodManager.deleteMoodEntry(entryToDelete.timestamp)
                            moodEntries = widgetMoodManager.getMoodEntries()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                        // Track mood history clearing
                        analyticsManager.trackEvent("mood_history_cleared", mapOf(
                            "entries_deleted" to entriesToDelete.toString()
                        ))
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
    translation: dev.alphagame.seen.translations.Translation
) {
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
                    text = String.format(translation.todaysMoods, todaysMoods.joinToString(" ") { it.mood.emoji }),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun MoodEntryItem(
    entry: MoodEntry,
    translation: dev.alphagame.seen.translations.Translation,
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
                    text = entry.mood.label,
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
