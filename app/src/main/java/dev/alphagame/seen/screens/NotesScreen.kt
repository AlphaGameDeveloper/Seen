package dev.alphagame.seen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.data.Note
import dev.alphagame.seen.data.NotesManager
import dev.alphagame.seen.data.Mood
import dev.alphagame.seen.components.NoteItem

@Composable
fun NotesScreen(onBackToHome: () -> Unit) {
    val context = LocalContext.current
    val notesManager = remember { NotesManager(context) }
    var noteText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }

    // Load notes when screen opens
    LaunchedEffect(Unit) {
        notes = notesManager.getAllNotes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Notes",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mood selection
        Text(
            text = "How are you feeling?",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Mood.values().size) { index ->
                val mood = Mood.values()[index]
                FilterChip(
                    onClick = { 
                        selectedMood = if (selectedMood == mood) null else mood
                    },
                    label = { 
                        Text(
                            text = "${mood.emoji} ${mood.label}",
                            fontSize = 12.sp
                        )
                    },
                    selected = selectedMood == mood,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Text input area
        TextField(
            value = noteText,
            onValueChange = { noteText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    text = "Write your thoughts here...",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                if (noteText.isNotBlank()) {
                    notesManager.saveNote(noteText.trim(), selectedMood?.label)
                    noteText = ""
                    selectedMood = null
                    notes = notesManager.getAllNotes()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(
                text = "💾 Save Note",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notes list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onDelete = {
                        notesManager.deleteNote(note.id)
                        notes = notesManager.getAllNotes()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back button
        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Back to Home",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
