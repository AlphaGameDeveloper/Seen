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
