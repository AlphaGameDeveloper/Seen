package dev.alphagame.seen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.FeatureFlags
import dev.alphagame.seen.analytics.AnalyticsManager
import dev.alphagame.seen.data.Note
import dev.alphagame.seen.data.NotesManager
import dev.alphagame.seen.data.Mood
import dev.alphagame.seen.components.NoteItem
import dev.alphagame.seen.translations.rememberTranslation

@Composable
fun NotesScreen(onBackToHome: () -> Unit) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val configuration = LocalConfiguration.current
    val notesManager = remember { NotesManager(context) }
    val analyticsManager = remember { AnalyticsManager(context) }
    var noteText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var isAddingNote by remember { mutableStateOf(false) }

    // Screen dimensions for responsive design
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp

    // Responsive padding and sizing
    val horizontalPadding = if (isCompact) 16.dp else 32.dp
    val verticalPadding = if (isCompact) 8.dp else 16.dp
    val titleSize = if (isCompact) 24.sp else 32.sp

    // Load notes when screen opens
    LaunchedEffect(Unit) {
        notes = notesManager.getAllNotes()
        // Track notes screen access
        analyticsManager.trackEvent("notes_screen_accessed")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToHome) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = translation.back,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = translation.notes,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    // textAlign = TextAlign.Center
                )

                // Add note FAB as icon button in header for compact screens
                if (isCompact && FeatureFlags.UI_JOURNAL_ADDBUTTON) { // alexander doesn't like it
                    IconButton(
                        onClick = { isAddingNote = !isAddingNote }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = translation.addNote,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    // Placeholder to maintain balance
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(verticalPadding))

            // Notes Grid/List
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(minSize = if (isCompact) 280.dp else 320.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = if (isCompact) 88.dp else 16.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Add note card (for larger screens or when adding)
                if (!isCompact || isAddingNote) {
                    item {
                        AddNoteCard(
                            noteText = noteText,
                            selectedMood = selectedMood,
                            translation = translation,
                            onNoteTextChange = { noteText = it },
                            onMoodSelected = { mood ->
                                selectedMood = if (selectedMood == mood) null else mood
                            },
                            onSaveNote = {
                                if (noteText.isNotBlank()) {
                                    notesManager.saveNote(noteText.trim(), selectedMood?.label)
                                    // Track note creation
                                    analyticsManager.trackEvent("note_created", mapOf(
                                        "has_mood" to (selectedMood != null).toString(),
                                        "mood" to (selectedMood?.label ?: "none"),
                                        "note_length" to noteText.trim().length.toString()
                                    ))
                                    noteText = ""
                                    selectedMood = null
                                    notes = notesManager.getAllNotes()
                                    if (isCompact) isAddingNote = false
                                }
                            },
                            onCancel = if (isCompact) { { isAddingNote = false } } else null,
                            isCompact = isCompact
                        )
                    }
                }

                // Notes
                items(notes) { note ->
                    NoteItem(
                        note = note,
                        onDelete = {
                            // Track note deletion
                            analyticsManager.trackEvent("note_deleted", mapOf(
                                "note_id" to note.id.toString(),
                                "had_mood" to (note.mood?.isNotEmpty() == true).toString(),
                                "note_length" to note.content.length.toString()
                            ))
                            notesManager.deleteNote(note.id)
                            notes = notesManager.getAllNotes()
                        }
                    )
                }
            }
        }
        // Mood entries list
        if (notes.isEmpty() && !isAddingNote) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    // TODO: Add translations for this entry; if not, don't show in different language!
                    text = "No entry data yet.\nUse the \"Add Entry\" button in the bottom-right corner to get started!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(PaddingValues(60.dp, 0.dp)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // Floating Action Button for compact screens
        if (isCompact && !isAddingNote) {
            FloatingActionButton(
                onClick = { isAddingNote = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = translation.addNote,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun AddNoteCard(
    noteText: String,
    selectedMood: Mood?,
    translation: dev.alphagame.seen.translations.Translation,
    onNoteTextChange: (String) -> Unit,
    onMoodSelected: (Mood) -> Unit,
    onSaveNote: () -> Unit,
    onCancel: (() -> Unit)? = null,
    isCompact: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = translation.addNote,
                    fontSize = if (isCompact) 18.sp else 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                onCancel?.let {
                    TextButton(onClick = it) {
                        Text(translation.cancel)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mood selection
            Text(
                text = translation.howAreYouFeeling,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(Mood.values()) { mood ->
                    FilterChip(
                        onClick = { onMoodSelected(mood) },
                        label = {
                            Text(
                                text = if (isCompact) mood.emoji else "${mood.emoji} ${mood.label}",
                                fontSize = if (isCompact) 14.sp else 12.sp
                            )
                        },
                        selected = selectedMood == mood,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text input area
            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = if (isCompact) 100.dp else 120.dp),
                placeholder = {
                    Text(
                        text = translation.noteHint,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = if (isCompact) 4 else 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = onSaveNote,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 44.dp else 48.dp),
                shape = RoundedCornerShape(if (isCompact) 22.dp else 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = noteText.isNotBlank()
            ) {
                Text(
                    text = translation.saveNote,
                    fontSize = if (isCompact) 14.sp else 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
