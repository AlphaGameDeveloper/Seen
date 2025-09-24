package dev.alphagame.seen.screens

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
import dev.alphagame.seen.analytics.AnalyticsManager
import dev.alphagame.seen.components.HealthStatusDots
import dev.alphagame.seen.components.NoInternetDialog
import dev.alphagame.seen.components.UpdateDialog
import dev.alphagame.seen.data.AppVersionInfo
import dev.alphagame.seen.data.DatabaseHelper
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.UpdateCheckManager
import dev.alphagame.seen.data.UpdateChecker
import dev.alphagame.seen.data.UpdateInfo
import dev.alphagame.seen.data.WidgetMoodManager
import dev.alphagame.seen.health.HealthStatusManager
import dev.alphagame.seen.translations.Translation
import dev.alphagame.seen.translations.rememberTranslation
import kotlinx.coroutines.launch

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
    val analyticsManager = remember { AnalyticsManager(context) }
    val healthStatusManager = remember { HealthStatusManager(context) }
    val databaseHelper = remember { DatabaseHelper(context) }
    val widgetMoodManager = remember { WidgetMoodManager(context) }
    val updateChecker = remember { UpdateChecker(context) }
    val updateCheckManager = remember { UpdateCheckManager(context) }
    val translation = rememberTranslation()
    val scope = rememberCoroutineScope()

    var currentTheme by remember { mutableStateOf(preferencesManager.themeMode) }
    var currentLanguage by remember { mutableStateOf(preferencesManager.language) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) } // Force refresh after data deletion
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
            analyticsManager.trackEvent("settings_screen_accessed")
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
                HealthStatusDots(healthStatusManager = healthStatusManager)
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
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = translation.about,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Seen - Mental Health Assessment",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

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

                    if (FeatureFlags.SETTINGS_PACKAGE) {
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
                                    preferencesManager.lastUpdateCheckTime = System.currentTimeMillis()
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
                        text = "Notifications",
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
                                text = "Enable Reminders",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Receive gentle reminders for mental health check-ins",
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
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background Update Checks Setting
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
                            text = "AI Features",
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
                                    text = "Enable AI Features",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Get personalized insights and suggestions (Coming Soon)",
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
                            text = "Analytics",
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
                                    text = "Enable Analytics",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Help improve Seen by sharing anonymous usage data",
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
                                    // Track analytics setting change
                                    analyticsManager.trackSettingChanged(
                                        "analytics_enabled",
                                        analyticsEnabled.toString(),
                                        enabled.toString()
                                    )
                                    analyticsEnabled = enabled
                                    preferencesManager.analyticsEnabled = enabled

                                    // Enable or disable analytics in the manager
                                    if (enabled) {
                                        analyticsManager.enableAnalytics()
                                    } else {
                                        analyticsManager.disableAnalytics()
                                    }
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
                                    // Track PHQ-9 data storage setting change
                                    analyticsManager.trackSettingChanged(
                                        "phq9_data_storage",
                                        phq9DataStorageEnabled.toString(),
                                        enabled.toString()
                                    )
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
                        text = "Data Management",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

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
                            text = "Delete All Local Data",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This will permanently delete all your notes, assessment results, mood entries, and reset all app settings to defaults. This action cannot be undone.",
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
                        text = "Privacy & Data",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "• All data is stored locally on your device\n• No personal information is shared with third parties\n• Assessment results remain completely private\n• You can delete your data at any time",
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
                        text = "Important Notice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This app is not a substitute for professional medical advice, diagnosis, or treatment. If you're experiencing a mental health crisis, please contact emergency services or consult with a qualified healthcare provider immediately.",
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
                    text = "Delete All Data?",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all your notes, assessment results, mood entries, and app settings. All data will be cleared and settings will be reset to default values. This action cannot be undone.\n\nAre you sure you want to continue?",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Track data deletion action
                        analyticsManager.trackEvent("all_data_deleted")

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
