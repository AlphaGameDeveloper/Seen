package dev.alphagame.seen.onboarding

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dev.alphagame.seen.analytics.AnalyticsManager
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.UpdateCheckManager
import dev.alphagame.seen.onboarding.components.ConfigurationFlow
import dev.alphagame.seen.onboarding.components.WelcomeCarousel
import dev.alphagame.seen.onboarding.components.WelcomePage
import dev.alphagame.seen.translations.Translation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val updateCheckManager = remember { UpdateCheckManager(context) }
    val analyticsManager = remember { AnalyticsManager(context) }

    var currentStage by remember { mutableStateOf(OnboardingStage.WELCOME_CAROUSEL) }
    var currentConfigStep by remember { mutableStateOf(ConfigurationStep.AI_FEATURES) }

    // Configuration states - Initialize with current preferences
    var aiEnabled by remember { mutableStateOf(false) } // AI features not yet implemented, so default false
    var notificationsEnabled by remember { mutableStateOf(preferencesManager.notificationsEnabled) }
    var remindersEnabled by remember { mutableStateOf(preferencesManager.notificationsEnabled) } // Default to same as notifications
    var updateChecksEnabled by remember { mutableStateOf(preferencesManager.backgroundUpdateChecksEnabled) }
    var analyticsEnabled by remember { mutableStateOf(preferencesManager.analyticsEnabled) }
    var selectedTheme by remember { mutableStateOf(preferencesManager.themeMode) }
    var selectedLanguage by remember { mutableStateOf(preferencesManager.language) }
    var dataStorageEnabled by remember { mutableStateOf(preferencesManager.isPhq9DataStorageEnabled) }

    // Track onboarding start
    LaunchedEffect(Unit) {
        analyticsManager.trackEvent(AnalyticsManager.EVENT_ONBOARDING_STARTED)
    }

    // Translation that reacts to language changes
    val translation = remember(selectedLanguage) {
        Translation.getTranslation(selectedLanguage)
    }

    // Check actual notification permission status on initialization
    LaunchedEffect(Unit) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Pre-API 33 doesn't require runtime permission
        }

        // Update notification state based on actual permission
        if (!hasPermission) {
            notificationsEnabled = false
            remindersEnabled = false
            updateChecksEnabled = false
        }
    }

    // Dialog states
    var showNotificationSuccessDialog by remember { mutableStateOf(false) }
    var showNotificationDeniedDialog by remember { mutableStateOf(false) }

    // Notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
        preferencesManager.notificationsEnabled = isGranted
        if (isGranted) {
            showNotificationSuccessDialog = true
        } else {
            showNotificationDeniedDialog = true
            // Also disable dependent features
            remindersEnabled = false
            updateChecksEnabled = false
            preferencesManager.backgroundUpdateChecksEnabled = false
        }
    }

    // Welcome carousel pages
    val welcomePages = listOf(
        WelcomePage(
            title = translation.onboardingWelcomeTitle,
            description = translation.onboardingWelcomeDesc,
            emoji = "👋"
        ),
        WelcomePage(
            title = translation.onboardingPHQ9Title,
            description = translation.onboardingPHQ9Desc,
            emoji = "📋"
        ),
        WelcomePage(
            title = translation.onboardingNotesTitle,
            description = translation.onboardingNotesDesc,
            emoji = "📝"
        ),
        WelcomePage(
            title = translation.onboardingPrivacyTitle,
            description = translation.onboardingPrivacyDesc,
            emoji = "🔒"
        ),
        WelcomePage(
            title = translation.onboardingNoAdsTitle,
            description = translation.onboardingNoAdsDesc,
            emoji = "🚫"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        when (currentStage) {
            OnboardingStage.WELCOME_CAROUSEL -> {
                WelcomeCarousel(
                    pages = welcomePages,
                    onGetStarted = {
                        currentStage = OnboardingStage.CONFIGURATION
                    },
                    onSkip = onOnboardingComplete
                )
            }
            OnboardingStage.CONFIGURATION -> {
                ConfigurationFlow(
                    currentStep = currentConfigStep,
                    aiEnabled = aiEnabled,
                    notificationsEnabled = notificationsEnabled,
                    remindersEnabled = remindersEnabled,
                    updateChecksEnabled = updateChecksEnabled,
                    analyticsEnabled = analyticsEnabled,
                    selectedTheme = selectedTheme,
                    selectedLanguage = selectedLanguage,
                    dataStorageEnabled = dataStorageEnabled,
                    onAIEnabledChange = {
                        aiEnabled = it
                        // AI features not yet implemented, so no preference to save
                    },
                    onNotificationsEnabledChange = { enabled ->
                        if (enabled) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                                    PackageManager.PERMISSION_GRANTED -> {
                                        // Permission already granted, just update state without showing dialog
                                        notificationsEnabled = true
                                        preferencesManager.notificationsEnabled = true
                                    }
                                    else -> {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            } else {
                                // Pre-API 33, notifications don't require runtime permission
                                notificationsEnabled = true
                                preferencesManager.notificationsEnabled = true
                            }
                        } else {
                            notificationsEnabled = false
                            preferencesManager.notificationsEnabled = false
                            // Also disable dependent features
                            remindersEnabled = false
                            updateChecksEnabled = false
                            preferencesManager.backgroundUpdateChecksEnabled = false
                            updateCheckManager.stopBackgroundUpdateChecks()
                        }
                    },
                    onRemindersEnabledChange = {
                        remindersEnabled = it
                        // Reminder preference could be saved here when implemented
                    },
                    onUpdateChecksEnabledChange = {
                        updateChecksEnabled = it
                        preferencesManager.backgroundUpdateChecksEnabled = it && notificationsEnabled

                        // Start or stop background update checks immediately
                        if (it && notificationsEnabled) {
                            updateCheckManager.startBackgroundUpdateChecks()
                        } else {
                            updateCheckManager.stopBackgroundUpdateChecks()
                        }
                    },
                    onAnalyticsEnabledChange = {
                        analyticsEnabled = it
                        preferencesManager.analyticsEnabled = it

                        // Enable or disable analytics in the manager
                        if (it) {
                            analyticsManager.enableAnalytics()
                        } else {
                            analyticsManager.disableAnalytics()
                        }
                    },
                    onThemeChange = {
                        selectedTheme = it
                        preferencesManager.themeMode = it
                    },
                    onLanguageChange = {
                        selectedLanguage = it
                        preferencesManager.language = it
                    },
                    onDataStorageEnabledChange = {
                        dataStorageEnabled = it
                        preferencesManager.isPhq9DataStorageEnabled = it
                    },
                    onNext = {
                        when (currentConfigStep) {
                            ConfigurationStep.AI_FEATURES -> currentConfigStep = ConfigurationStep.NOTIFICATIONS
                            ConfigurationStep.NOTIFICATIONS -> currentConfigStep = ConfigurationStep.ANALYTICS
                            ConfigurationStep.ANALYTICS -> currentConfigStep = ConfigurationStep.THEME_SETTINGS
                            ConfigurationStep.THEME_SETTINGS -> currentConfigStep = ConfigurationStep.LANGUAGE_SETTINGS
                            ConfigurationStep.LANGUAGE_SETTINGS -> currentConfigStep = ConfigurationStep.DATA_PRIVACY
                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep = ConfigurationStep.COMPLETE
                            ConfigurationStep.COMPLETE -> {
                                // Track onboarding completion
                                analyticsManager.trackEvent(AnalyticsManager.EVENT_ONBOARDING_COMPLETED, mapOf(
                                    "ai_enabled" to aiEnabled,
                                    "notifications_enabled" to notificationsEnabled,
                                    "analytics_enabled" to analyticsEnabled,
                                    "theme" to selectedTheme,
                                    "language" to selectedLanguage,
                                    "data_storage_enabled" to dataStorageEnabled
                                ))

                                // All settings are already applied immediately when changed
                                // Just complete the onboarding
                                onOnboardingComplete()
                            }
                        }
                    },
                    onBack = {
                        when (currentConfigStep) {
                            ConfigurationStep.AI_FEATURES -> currentStage = OnboardingStage.WELCOME_CAROUSEL
                            ConfigurationStep.NOTIFICATIONS -> currentConfigStep = ConfigurationStep.AI_FEATURES
                            ConfigurationStep.ANALYTICS -> currentConfigStep = ConfigurationStep.NOTIFICATIONS
                            ConfigurationStep.THEME_SETTINGS -> currentConfigStep = ConfigurationStep.ANALYTICS
                            ConfigurationStep.LANGUAGE_SETTINGS -> currentConfigStep = ConfigurationStep.THEME_SETTINGS
                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep = ConfigurationStep.LANGUAGE_SETTINGS
                            ConfigurationStep.COMPLETE -> currentConfigStep = ConfigurationStep.DATA_PRIVACY
                        }
                    },
                    onSkip = {
                        // Track onboarding skip
                        analyticsManager.trackEvent(AnalyticsManager.EVENT_ONBOARDING_SKIPPED)
                        onOnboardingComplete()
                    }
                )
            }
        }
    }

    // Notification success dialog
    if (showNotificationSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationSuccessDialog = false },
            title = {
                Text(
                    text = "Great! 🎉",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    text = "You'll now receive gentle reminders to help you stay on top of your mental health journey.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationSuccessDialog = false }
                ) {
                    Text("Awesome!")
                }
            }
        )
    }

    // Notification denied dialog
    if (showNotificationDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDeniedDialog = false },
            title = {
                Text(
                    text = "No Problem! 👍",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "You can always enable notifications later in the app settings if you change your mind.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDeniedDialog = false }
                ) {
                    Text("Got it!")
                }
            }
        )
    }
}
