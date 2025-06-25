/*
 * Git Tag-Based Versioning Strategy
 * ==================================
 *
 * This build configuration automatically derives version information from git tags:
 *
 * - versionName: Extracted from the latest git tag (removes 'v' prefix if present)
 * - versionCode: Generated from version name using format: major*10000 + minor*100 + patch
 *
 * Examples:
 * - Git tag "v1.2.3" → versionName: "1.2.3", versionCode: 10203
 * - Git tag "2.0.0" → versionName: "2.0.0", versionCode: 20000
 *
 * Additional build info available in BuildConfig:
 * - GIT_TAG: The original git tag (e.g., "v1.1.0")
 * - GIT_COMMIT_HASH: Short commit hash (e.g., "cfb785f")
 * - GIT_BRANCH: Current branch name (e.g., "main")
 * - BUILD_TIME: UTC timestamp of build
 * - VERSION_FULL: Combined version with commit hash (e.g., "1.1.0-cfb785f")
 *
 * To create a new version:
 * 1. Create and push a git tag: git tag v1.2.0 && git push origin v1.2.0
 * 2. Build the app - it will automatically use the new version
 *
 * Use './gradlew printVersionInfo' to display current version information.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

import java.time.ZonedDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// Function to get latest git tag
fun getLatestGitTag(): String {
    return try {
        val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0")
            .directory(rootDir)
            .start()
        val result = process.inputStream.bufferedReader().readText().trim()
        if (process.waitFor() == 0 && result.isNotEmpty()) result else "1.0.0"
    } catch (e: Exception) {
        "1.0.0"
    }
}

// Function to get version name from git tag (removes 'v' prefix if present)
fun getVersionName(): String {
    val tag = getLatestGitTag()
    return if (tag.startsWith("v")) tag.substring(1) else tag
}

// Function to generate version code from git tag
fun getVersionCode(): Int {
    val versionName = getVersionName()
    return try {
        val parts = versionName.split(".")
        when (parts.size) {
            3 -> {
                val major = parts[0].toIntOrNull() ?: 1
                val minor = parts[1].toIntOrNull() ?: 0
                val patch = parts[2].toIntOrNull() ?: 0
                major * 10000 + minor * 100 + patch
            }
            2 -> {
                val major = parts[0].toIntOrNull() ?: 1
                val minor = parts[1].toIntOrNull() ?: 0
                major * 10000 + minor * 100
            }
            1 -> {
                val major = parts[0].toIntOrNull() ?: 1
                major * 10000
            }
            else -> 10000 // Default to 1.0.0 equivalent
        }
    } catch (e: Exception) {
        10000 // Default to 1.0.0 equivalent
    }
}

// Function to get git commit hash
fun getGitCommitHash(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
            .directory(rootDir)
            .start()
        val result = process.inputStream.bufferedReader().readText().trim()
        if (process.waitFor() == 0 && result.isNotEmpty()) result else "unknown"
    } catch (e: Exception) {
        "unknown"
    }
}

// Function to get build timestamp
fun getBuildTime(): String {
    return ZonedDateTime.now(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'"))
}

// Function to get git branch name
fun getGitBranch(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
            .directory(rootDir)
            .start()
        val result = process.inputStream.bufferedReader().readText().trim()
        if (process.waitFor() == 0 && result.isNotEmpty()) result else "unknown"
    } catch (e: Exception) {
        "unknown"
    }
}

fun getGitCommitMessage(): String {
    return try {
        val process = ProcessBuilder("git", "log", "-1", "--pretty=%B")
            .directory(rootDir)
            .start()
        val result = process.inputStream.bufferedReader().readText().trim()
        if (process.waitFor() == 0 && result.isNotEmpty()) {
            // Escape newlines and quotes for proper string literal formatting
            result.replace("\n", "\\n").replace("\"", "\\\"")
        } else "\"No commit message\""
    } catch (e: Exception) {
        "\"No commit message\""
    }
}

android {
    namespace = "dev.alphagame.seen"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.alphagame.seen"
        minSdk = 24
        targetSdk = 35
        versionCode = getVersionCode()
        versionName = getVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add build config fields for dynamic versioning
        buildConfigField("String", "GIT_COMMIT_HASH", "\"${getGitCommitHash()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")
        buildConfigField("String", "GIT_BRANCH", "\"${getGitBranch()}\"")
        buildConfigField("String", "GIT_TAG", "\"${getLatestGitTag()}\"")
        buildConfigField("String", "VERSION_FULL", "\"${getVersionName()}-${getGitCommitHash()}\"")
        buildConfigField("String", "GIT_COMMIT_MESSAGE", "\"${getGitCommitMessage()}\"")
    }

    lint {
        disable += "MissingClass"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.okhttp)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

}

// Gradle task to display version information
tasks.register("printVersionInfo") {
    doLast {
        println("=== Version Information ===")
        println("Git Tag: ${getLatestGitTag()}")
        println("Version Name: ${getVersionName()}")
        println("Version Code: ${getVersionCode()}")
        println("Git Commit: ${getGitCommitHash()}")
        println("Git Branch: ${getGitBranch()}")
        println("Build Time: ${getBuildTime()}")
        println("Full Version: ${getVersionName()}-${getGitCommitHash()}")
        println("Git Commit Message: ${getGitCommitMessage()}")
        println("============================")
    }
}
