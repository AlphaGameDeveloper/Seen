plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

import java.time.ZonedDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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

android {
    namespace = "dev.alphagame.seen"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.alphagame.seen"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Add build config fields for dynamic versioning
        buildConfigField("String", "GIT_COMMIT_HASH", "\"${getGitCommitHash()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")
        buildConfigField("String", "GIT_BRANCH", "\"${getGitBranch()}\"")
        buildConfigField("String", "VERSION_FULL", "\"${defaultConfig.versionName}-${getGitCommitHash()}\"")
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

}