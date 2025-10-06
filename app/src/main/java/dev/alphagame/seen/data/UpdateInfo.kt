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

data class UpdateInfo(
    val latestVersion: String,
    val currentVersion: String,
    val isUpdateAvailable: Boolean,
    val downloadUrl: String? = null,
    val releaseNotes: String? = null,
    val isForceUpdate: Boolean = false,
    val minimumRequiredVersion: String? = null
)

data class UpdateResponse(
    val version: String,
    val versionCode: Int,
    val downloadUrl: String? = null,
    val releaseNotes: String? = null,
    val forceUpdate: Boolean = false,
    val minimumRequiredVersion: String? = null
)
