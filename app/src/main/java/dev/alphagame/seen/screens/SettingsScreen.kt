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

package dev.alphagame.seen.screens

import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.BuildConfig
import dev.alphagame.seen.FeatureFlags
import dev.alphagame.seen.components.HealthStatusDots
import dev.alphagame.seen.components.NoInternetDialog
import dev.alphagame.seen.components.UpdateDialog
import dev.alphagame.seen.data.AppVersionInfo
import dev.alphagame.seen.data.DailyReminderManager
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.UpdateCheckManager
import dev.alphagame.seen.data.UpdateChecker
import dev.alphagame.seen.data.UpdateInfo
import dev.alphagame.seen.data.WidgetMoodManager
import dev.alphagame.seen.health.HealthStatusManager
import dev.alphagame.seen.translations.Translation
import dev.alphagame.seen.translations.rememberTranslation
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import dev.alphagame.seen.encryption.EncryptedDatabaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackToHome: () -> Unit,
    onThemeChanged: (String) -> Unit = {},
    onLanguageChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val preferencesManager = remember { PreferencesManager(context) }
    // AnalyticsManager removed; prefer using PreferencesManager for analytics preference
    val healthStatusManager = remember { HealthStatusManager(context) }
    val databaseHelper = remember { EncryptedDatabaseHelper(context) }
    val widgetMoodManager = remember { WidgetMoodManager(context) }
    val updateChecker = remember { UpdateChecker(context) }
    val updateCheckManager = remember { UpdateCheckManager(context) }
    val translation = rememberTranslation()
    val scope = rememberCoroutineScope()

    var currentTheme by remember { mutableStateOf(preferencesManager.themeMode) }
    var currentLanguage by remember { mutableStateOf(preferencesManager.language) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) } // Force refresh after data deletion
    var isCheckingForUpdates by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showNoUpdateDialog by remember { mutableStateOf(false) }
    var showUpdateErrorDialog by remember { mutableStateOf(false) }
    var showNetworkErrorDialog by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }    // Keep local state in sync with preference changes
    LaunchedEffect(preferencesManager.themeMode, preferencesManager.language, refreshKey) {
        currentTheme = preferencesManager.themeMode
        currentLanguage = preferencesManager.language

        // Track settings screen access (only on first load, not on refreshes)
        if (refreshKey == 0) {
            // Analytics tracking removed
            // Check health status when screen is first accessed
            healthStatusManager.checkAllServices()
        }
    }

    // Cleanup health status manager
    DisposableEffect(Unit) {
        onDispose {
            healthStatusManager.cleanup()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = translation.settings,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackToHome) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (FeatureFlags.SETTINGS_STATUS_DOTS) {
                    HealthStatusDots(healthStatusManager = healthStatusManager)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Settings Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column {
                    if (FeatureFlags.SETTINGS_TITLE) {
                        Text(
                            text = translation.about,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Seen - Mental Health Assistant",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Created by Damien Boisvert and Alexander Cameron.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
                        )
                        if (FeatureFlags.SETTINGS_TITLE_MORE_INFO) {
                            Text(
                                text = "Version ${AppVersionInfo.getVersionFull()}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            Text(
                                text = "Built on ${BuildConfig.PRETTY_BUILD_TIME}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    if (FeatureFlags.SETTINGS_TITLE_PACKAGE) {
                        Text(
                            text = "Package: ${BuildConfig.APPLICATION_ID}@${BuildConfig.GIT_BRANCH}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (FeatureFlags.SETTINGS_EXTENDED_ABOUT) {
                        val gitBranch = AppVersionInfo.getGitBranch()
                        if (gitBranch != "unknown" && gitBranch != "dynamic") {
                            Text(
                                text = "Branch: $gitBranch",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Text(
                            text = "Commit Message: ${AppVersionInfo.GIT_COMMIT_MESSAGE}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = translation.createdBy,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    if (FeatureFlags.SETTINGS_UPDATE_BUTTON) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    isCheckingForUpdates = true
                                    try {
                                        val result = updateChecker.checkForUpdatesForCompose()
                                        when (result) {
                                            is UpdateChecker.UpdateCheckResult.UpdateAvailable -> {
                                                updateInfo = result.updateInfo
                                                showUpdateDialog = true
                                            }

                                            is UpdateChecker.UpdateCheckResult.NoUpdate -> {
                                                showNoUpdateDialog = true
                                            }

                                            is UpdateChecker.UpdateCheckResult.NetworkError -> {
                                                showNetworkErrorDialog = true
                                            }

                                            is UpdateChecker.UpdateCheckResult.Error -> {
                                                showUpdateErrorDialog = true
                                            }
                                        }
                                        preferencesManager.lastUpdateCheckTime =
                                            System.currentTimeMillis()
                                    } finally {
                                        isCheckingForUpdates = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isCheckingForUpdates
                        ) {
                            if (isCheckingForUpdates) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(translation.checkingForUpdates)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(translation.checkForUpdates)
                            }
                        }
                    }
                }

                // Language Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = translation.language,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val languageOptions = Translation.getAvailableLanguages()

                        languageOptions.forEach { (code, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = currentLanguage == code,
                                        onClick = {
                                            if (currentLanguage != code) {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                currentLanguage = code
                                                preferencesManager.language = code
                                                onLanguageChanged(code)
                                            }
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentLanguage == code,
                                    onClick = null
                                )
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

                // Theme Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = translation.appearance,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = translation.colorScheme,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val themeOptions = listOf(
                            "auto" to translation.themeAuto,
                            "light" to translation.themeLight,
                            "dark" to translation.themeDark
                        )

                        themeOptions.forEach { (value, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = currentTheme == value,
                                        onClick = {
                                            if (currentTheme != value) {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                currentTheme = value
                                                preferencesManager.themeMode = value
                                                onThemeChanged(value)
                                            }
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentTheme == value,
                                    onClick = null
                                )
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(start = 8.dp),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

            // Notifications Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = translation.notifications,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = translation.enableReminders,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = translation.enableRemindersDescription,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                lineHeight = 16.sp
                            )
                        }

                        var notificationsEnabled by remember(refreshKey) { mutableStateOf(preferencesManager.notificationsEnabled) }

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { enabled ->
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                notificationsEnabled = enabled
                                preferencesManager.notificationsEnabled = enabled

                                // Start or stop background update checks based on notification permission
                                if (enabled && preferencesManager.backgroundUpdateChecksEnabled) {
                                    updateCheckManager.startBackgroundUpdateChecks()
                                } else if (!enabled) {
                                    updateCheckManager.stopBackgroundUpdateChecks()
                                    DailyReminderManager.cancelDailyReminder(context)
                                }

                                if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val alarmManager = context.getSystemService(AlarmManager::class.java)
                                    if (!alarmManager.canScheduleExactAlarms()) {
                                        // Show a dialog or direct user to settings
                                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                        intent.data = ("package:" + context.packageName).toUri()
                                        context.startActivity(intent)
                                        notificationsEnabled = false
                                        preferencesManager.notificationsEnabled = false
                                    }
                                    else {
                                        DailyReminderManager.scheduleDailyReminder(context)
                                        if (currentLanguage == "en") {
                                            Toast.makeText(context, "You will now receive reminder notifications at noon.", Toast.LENGTH_SHORT).show()
                                        } else if (currentLanguage == "fr") {
                                            Toast.makeText(context, "Vous recevrez désormais des notifications de rappel à midi.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Ahora recibirás notificaciones de recordatorio al mediodía.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )
                    }

                    //Spacer(modifier = Modifier.height(16.dp))

                    // Background Update Checks Setting
                    if (FeatureFlags.SETTINGS_BACKGROUND_UPDATE_CHECKS) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = translation.enableBackgroundUpdateChecks,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = translation.enableBackgroundUpdateChecksDescription,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp
                                )
                            }

                            var backgroundUpdateChecksEnabled by remember(refreshKey) {
                                mutableStateOf(preferencesManager.backgroundUpdateChecksEnabled)
                            }

                            Switch(
                                checked = backgroundUpdateChecksEnabled,
                                onCheckedChange = { enabled ->
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    backgroundUpdateChecksEnabled = enabled
                                    preferencesManager.backgroundUpdateChecksEnabled = enabled

                                    // Start or stop the background update service
                                    if (enabled && preferencesManager.notificationsEnabled) {
                                        updateCheckManager.startBackgroundUpdateChecks()
                                    } else {
                                        updateCheckManager.stopBackgroundUpdateChecks()
                                    }
                                }
                            )
                        }
                    }
                }
            }


            if (FeatureFlags.SETTINGS_AI_FEATURES) {
                // Privacy & Assessment Settings Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = translation.aiFeatures,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = translation.enableAIFeatures,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = translation.aiFeaturesDescription,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp
                                )
                            }

                            var aiEnabled by remember(refreshKey) { mutableStateOf(false) } // AI features not yet implemented

                            Switch(
                                checked = aiEnabled,
                                enabled = false, // Disabled until AI features are implemented
                                onCheckedChange = { enabled ->
                                    // AI features not yet implemented
                                    aiEnabled = enabled
                                }
                            )
                        }
                    }

                }
            }

            // Analytics Settings Section
            if (FeatureFlags.SETTINGS_ANALYTICS) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = translation.analytics,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = translation.enableAnalytics,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = translation.analyticsDescription,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp
                                )
                            }

                            var analyticsEnabled by remember(refreshKey) {
                                mutableStateOf(
                                    preferencesManager.analyticsEnabled
                                )
                            }

                            Switch(
                                checked = analyticsEnabled,
                                onCheckedChange = { enabled ->
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    // Update analytics preference
                                    analyticsEnabled = enabled
                                    preferencesManager.analyticsEnabled = enabled
                                }
                            )
                        }
                    }
                }
            }

            // Assessment Settings Section
            if (FeatureFlags.SETTINGS_ASSESSMENT_SETTINGS) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Assessment Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Save PHQ-9 Assessment Data",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Store detailed question responses locally for tracking progress",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp
                                )
                            }

                            var phq9DataStorageEnabled by remember(refreshKey) {
                                mutableStateOf(
                                    preferencesManager.isPhq9DataStorageEnabled
                                )
                            }

                            Switch(
                                //checked = phq9DataStorageEnabled,
                                checked = true,
                                onCheckedChange = { enabled ->
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    // PHQ-9 data storage preference changed
                                    // analytics tracking removed
                                    phq9DataStorageEnabled = enabled
                                    preferencesManager.isPhq9DataStorageEnabled = enabled
                                }
                            )
                        }
                    }
                }
            }

            // Data Management Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = translation.dataManagement,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (FeatureFlags.SETTINGS_ENCRYPTION) {
                        Spacer(modifier = Modifier.height(16.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = translation.enableEncryption,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = translation.encryptionDescription,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp
                                )
                            }

                            Switch(
                                checked = true,
                                enabled = false, // purely visual
                                onCheckedChange = {
                                    Log.e("SettingsScreen", "haha nope") // purely cosmetic
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = translation.deleteAllData,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = translation.deleteAllDataDescription,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 16.sp
                    )
                }
            }

            // Privacy Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = translation.privacyAndData,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = translation.privacyDescription,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
            }

            // Disclaimer Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = translation.importantNotice,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = translation.disclaimerText,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = translation.deleteConfirmTitle,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = translation.deleteConfirmText,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Track data deletion action (analytics removed)

                        // Clear all data from database
                        databaseHelper.clearAllData()

                        // Clear mood widget data
                        widgetMoodManager.clearAllMoods()

                        // Clear all preferences and reinitialize with defaults
                        preferencesManager.clearAllPreferences()

                        // Update local state with defaults after clearing
                        currentTheme = PreferencesManager.THEME_AUTO

                        // Force refresh of the UI to reflect default values
                        refreshKey += 1

                        showDeleteDialog = false
                        showDeleteConfirmationDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(translation.deleteAll)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(translation.cancel)
                }
            }
        )
    }

    // Delete confirmation dialog (shows after deletion is complete)
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmationDialog = false
                onBackToHome()
            },
            title = {
                Text(
                    text = translation.dataDeletedTitle,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    text = translation.dataDeletedText,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmationDialog = false
                        // close the app
                        android.os.Process.killProcess(android.os.Process.myPid())
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(translation.continueButton)
                }
            }
        )
    }

    // Update Dialog
    if (showUpdateDialog && updateInfo != null) {
        UpdateDialog(
            updateInfo = updateInfo!!,
            onDismiss = {
                showUpdateDialog = false
            },
            onUpdateLater = {
                // Remember that user skipped this version (unless it's a force update)
                if (!updateInfo!!.isForceUpdate) {
                    preferencesManager.skippedVersion = updateInfo!!.latestVersion
                }
            }
        )
    }

    // No Update Dialog
    if (showNoUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showNoUpdateDialog = false },
            title = {
                Text(
                    text = translation.noUpdatesAvailable,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    text = translation.noUpdatesAvailableMessage,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNoUpdateDialog = false }
                ) {
                    Text(translation.ok)
                }
            }
        )
    }

    // Update Error Dialog
    if (showUpdateErrorDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateErrorDialog = false },
            title = {
                Text(
                    text = translation.updateCheckFailed,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = translation.updateCheckFailedMessage,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showUpdateErrorDialog = false }
                ) {
                    Text(translation.ok)
                }
            }
        )
    }

    // Network Error Dialog
    if (showNetworkErrorDialog) {
        NoInternetDialog(
            onDismiss = { showNetworkErrorDialog = false }
        )
    }
}
