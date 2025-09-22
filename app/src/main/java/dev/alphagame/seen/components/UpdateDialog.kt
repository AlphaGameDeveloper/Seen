package dev.alphagame.seen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.data.UpdateInfo
import dev.alphagame.seen.translations.rememberTranslation

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit,
    onUpdateLater: () -> Unit = {}
) {
    val context = LocalContext.current
    val translation = rememberTranslation()

    // Add logging to see if dialog is being created
    android.util.Log.d("UpdateDialog", "UpdateDialog being shown for version ${updateInfo.latestVersion}")

    AlertDialog(
        onDismissRequest = if (updateInfo.isForceUpdate) { {} } else onDismiss,
        title = {
            Text(
                text = if (updateInfo.isForceUpdate) translation.requiredUpdate else translation.updateAvailable,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            val message = buildString {
                append("A new version (${updateInfo.latestVersion}) is available!")
                append("\n${translation.currentVersion} ${updateInfo.currentVersion}")

                if (!updateInfo.releaseNotes.isNullOrEmpty()) {
                    append("\n\n${translation.whatsNew}\n${updateInfo.releaseNotes}")
                }

                if (updateInfo.isForceUpdate) {
                    append("\n\n${translation.updateRequired}")
                }
            }

            Text(
                text = message,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val downloadUrl = updateInfo.downloadUrl
                    if (!downloadUrl.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                        context.startActivity(intent)
                    } else {
                        // Fallback: Open the GitHub releases page
                        val fallbackUrl = "https://github.com/AlphaGameDeveloper/Seen/releases/latest"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                        context.startActivity(intent)
                    }
                    if (!updateInfo.isForceUpdate) {
                        onDismiss()
                    }
                }
            ) {
                Text(if (updateInfo.isForceUpdate) translation.updateNow else translation.downloadUpdate)
            }
        },
        dismissButton = if (!updateInfo.isForceUpdate) {
            {
                TextButton(onClick = {
                    onUpdateLater()
                    onDismiss()
                }) {
                    Text(translation.updateLater)
                }
            }
        } else null
    )
}

@Composable
fun NoInternetDialog(
    onDismiss: () -> Unit
) {
    val translation = rememberTranslation()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = translation.noInternetConnection,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        text = {
            Column {
                Text(
                    text = translation.noInternetConnectionMessage,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = translation.internetTroubleshootingTips,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(translation.ok)
            }
        }
    )
}
