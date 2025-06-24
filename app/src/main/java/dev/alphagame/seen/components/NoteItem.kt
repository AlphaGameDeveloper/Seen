package dev.alphagame.seen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.data.Note
import dev.alphagame.seen.data.Mood
import dev.alphagame.seen.translations.rememberTranslation
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteItem(note: Note, onDelete: () -> Unit) {
    val translation = rememberTranslation()
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp.dp < 600.dp
    val dateFormat = SimpleDateFormat(
        if (isCompact) "MMM dd, HH:mm" else "MMM dd, yyyy 'at' HH:mm",
        Locale.getDefault()
    )
    val mood = note.mood?.let { Mood.fromLabel(it) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (isCompact) 12.dp else 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 12.dp else 16.dp)
        ) {
            // Header with mood and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Mood display
                mood?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = it.emoji,
                            fontSize = if (isCompact) 18.sp else 20.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = it.label,
                            fontSize = if (isCompact) 12.sp else 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                } ?: Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(if (isCompact) 32.dp else 40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = translation.deleteNote,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(if (isCompact) 18.dp else 20.dp)
                    )
                }
            }

            if (mood != null) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Note content
            Text(
                text = note.content,
                fontSize = if (isCompact) 14.sp else 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = if (isCompact) 20.sp else 22.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp
            Text(
                text = dateFormat.format(Date(note.timestamp)),
                fontSize = if (isCompact) 11.sp else 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Light
            )
        }
    }
}
