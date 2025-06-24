package dev.alphagame.seen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    // Add logging to see if dialog is being created
    android.util.Log.d("UpdateDialog", "UpdateDialog being shown for version ${updateInfo.latestVersion}")

    AlertDialog(
        onDismissRequest = if (updateInfo.isForceUpdate) { {} } else onDismiss,
        title = {
            Text(
                text = if (updateInfo.isForceUpdate) "Required Update" else "Update Available",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            val message = buildString {
                append("A new version (${updateInfo.latestVersion}) is available!")
                append("\nCurrent version: ${updateInfo.currentVersion}")

                if (!updateInfo.releaseNotes.isNullOrEmpty()) {
                    append("\n\nWhat's new:\n${updateInfo.releaseNotes}")
                }

                if (updateInfo.isForceUpdate) {
                    append("\n\nThis update is required to continue using the app.")
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
                    }
                    if (!updateInfo.isForceUpdate) {
                        onDismiss()
                    }
                }
            ) {
                Text(if (updateInfo.isForceUpdate) "Update Now" else "Update")
            }
        },
        dismissButton = if (!updateInfo.isForceUpdate) {
            {
                TextButton(onClick = {
                    onUpdateLater()
                    onDismiss()
                }) {
                    Text("Later")
                }
            }
        } else null
    )
}
