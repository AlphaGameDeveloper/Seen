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
