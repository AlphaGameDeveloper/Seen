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

import dev.alphagame.seen.BuildConfig

object AppVersionInfo {
    val VERSION_NAME: String = BuildConfig.VERSION_NAME
    val VERSION_CODE: Int = BuildConfig.VERSION_CODE
    val VERSION_FULL: String = BuildConfig.VERSION_FULL
    val GIT_COMMIT: String = BuildConfig.GIT_COMMIT_HASH
    val BUILD_TIME: String = BuildConfig.BUILD_TIME
    val GIT_BRANCH: String = BuildConfig.GIT_BRANCH
    val GIT_COMMIT_MESSAGE: String = BuildConfig.GIT_COMMIT_MESSAGE
    val PRETTY_BUILD_TIME: String = BuildConfig.PRETTY_BUILD_TIME
    fun getVersionName(): String = VERSION_NAME
    fun getVersionCode(): Int = VERSION_CODE
    fun getVersionFull(): String = VERSION_FULL
    fun getBuildTime(): String = BUILD_TIME
    fun getGitBranch(): String = GIT_BRANCH
    fun getGitCommit(): String = GIT_COMMIT
    fun getGitCommitMessage(): String = GIT_COMMIT_MESSAGE
    fun getPrettyBuildTime(): String = PRETTY_BUILD_TIME
}
