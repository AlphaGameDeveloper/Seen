package dev.alphagame.seen.data

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri

object UpdateDialogHelper {

    fun showUpdateAvailableDialog(context: Context, updateInfo: UpdateInfo) {
        val title = if (updateInfo.isForceUpdate) {
            "Required Update Available"
        } else {
            "Update Available"
        }

        val message = buildString {
            append("A new version (${updateInfo.latestVersion}) is available.\n")
            append("Current version: ${updateInfo.currentVersion}\n\n")
            updateInfo.releaseNotes?.let { notes ->
                append("What's new:\n$notes")
            }
        }

        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Download") { _, _ ->
                updateInfo.downloadUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }

        if (!updateInfo.isForceUpdate) {
            builder.setNegativeButton("Later", null)
        }

        builder.setCancelable(!updateInfo.isForceUpdate)
        builder.show()
    }

    fun showNoUpdateDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("No Updates Available")
            .setMessage("You are running the latest version of the app.")
            .setPositiveButton("OK", null)
            .show()
    }

    fun showUpdateCheckErrorDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Update Check Failed")
            .setMessage("Unable to check for updates. Please try again later.")
            .setPositiveButton("OK", null)
            .show()
    }
}
