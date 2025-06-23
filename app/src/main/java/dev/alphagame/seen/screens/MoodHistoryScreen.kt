package dev.alphagame.seen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.data.MoodEntry
import dev.alphagame.seen.data.WidgetMoodManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHistoryScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val widgetMoodManager = remember { WidgetMoodManager(context) }
    var moodEntries by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Load mood entries
    LaunchedEffect(Unit) {
        moodEntries = widgetMoodManager.getMoodEntries()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Mood History",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showDeleteDialog = true },
                enabled = moodEntries.isNotEmpty()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Clear History")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Statistics
        if (moodEntries.isNotEmpty()) {
            MoodStatistics(moodEntries = moodEntries)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Mood entries list
        if (moodEntries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No mood entries yet.\nUse the widget to start tracking!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(moodEntries) { entry ->
                    MoodEntryItem(entry = entry)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Clear Mood History") },
            text = { Text("Are you sure you want to delete all mood entries? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        widgetMoodManager.clearAllMoods()
                        moodEntries = emptyList()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MoodStatistics(moodEntries: List<MoodEntry>) {
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
                text = "Today's Stats",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Entries today: ${todaysMoods.size}")
                Text("Total entries: ${moodEntries.size}")
            }
            
            if (todaysMoods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Today's moods: ${todaysMoods.joinToString(" ") { it.mood.emoji }}",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun MoodEntryItem(entry: MoodEntry) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()) }
    
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
            
            Column {
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
        }
    }
}
