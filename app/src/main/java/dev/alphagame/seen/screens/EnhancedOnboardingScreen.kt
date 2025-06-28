package dev.alphagame.seen.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.UpdateCheckManager
import dev.alphagame.seen.translations.rememberTranslation
import dev.alphagame.seen.translations.Translation
import kotlinx.coroutines.launch

enum class OnboardingStage {
    WELCOME_CAROUSEL,
    CONFIGURATION
}

enum class ConfigurationStep {
    AI_FEATURES,
    NOTIFICATIONS,
    THEME_SETTINGS,
    LANGUAGE_SETTINGS,
    DATA_PRIVACY,
    COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val updateCheckManager = remember { UpdateCheckManager(context) }
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var currentStage by remember { mutableStateOf(OnboardingStage.WELCOME_CAROUSEL) }
    var currentConfigStep by remember { mutableStateOf(ConfigurationStep.AI_FEATURES) }

    // Configuration states - Initialize with current preferences
    var aiEnabled by remember { mutableStateOf(false) } // AI features not yet implemented, so default false
    var notificationsEnabled by remember { mutableStateOf(preferencesManager.notificationsEnabled) }
    var remindersEnabled by remember { mutableStateOf(preferencesManager.notificationsEnabled) } // Default to same as notifications
    var updateChecksEnabled by remember { mutableStateOf(preferencesManager.backgroundUpdateChecksEnabled) }
    var selectedTheme by remember { mutableStateOf(preferencesManager.themeMode) }
    var selectedLanguage by remember { mutableStateOf(preferencesManager.language) }
    var dataStorageEnabled by remember { mutableStateOf(preferencesManager.isPhq9DataStorageEnabled) }

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
                            ConfigurationStep.NOTIFICATIONS -> currentConfigStep = ConfigurationStep.THEME_SETTINGS
                            ConfigurationStep.THEME_SETTINGS -> currentConfigStep = ConfigurationStep.LANGUAGE_SETTINGS
                            ConfigurationStep.LANGUAGE_SETTINGS -> currentConfigStep = ConfigurationStep.DATA_PRIVACY
                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep = ConfigurationStep.COMPLETE
                            ConfigurationStep.COMPLETE -> {
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
                            ConfigurationStep.THEME_SETTINGS -> currentConfigStep = ConfigurationStep.NOTIFICATIONS
                            ConfigurationStep.LANGUAGE_SETTINGS -> currentConfigStep = ConfigurationStep.THEME_SETTINGS
                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep = ConfigurationStep.LANGUAGE_SETTINGS
                            ConfigurationStep.COMPLETE -> currentConfigStep = ConfigurationStep.DATA_PRIVACY
                        }
                    },
                    onSkip = onOnboardingComplete
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

data class WelcomePage(
    val title: String,
    val description: String,
    val emoji: String
)

@Composable
fun WelcomeCarousel(
    pages: List<WelcomePage>,
    onGetStarted: () -> Unit,
    onSkip: () -> Unit
) {
    val translation = rememberTranslation()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onSkip) {
                Text(
                    text = translation.onboardingSkip,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            WelcomePageContent(
                page = pages[page],
                modifier = Modifier.fillMaxSize()
            )
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        .padding(horizontal = 4.dp)
                )
                if (index < pages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            if (pagerState.currentPage > 0) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(translation.back)
                }
            } else {
                Spacer(modifier = Modifier.width(100.dp))
            }

            // Next/Get Started button
            val isLastPage = pagerState.currentPage == pages.size - 1
            Button(
                onClick = {
                    if (isLastPage) {
                        onGetStarted()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isLastPage) translation.onboardingGetStarted else translation.next,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isLastPage) Icons.Default.Settings else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomePageContent(
    page: WelcomePage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Emoji icon
        Text(
            text = page.emoji,
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun ConfigurationFlow(
    currentStep: ConfigurationStep,
    aiEnabled: Boolean,
    notificationsEnabled: Boolean,
    remindersEnabled: Boolean,
    updateChecksEnabled: Boolean,
    selectedTheme: String,
    selectedLanguage: String,
    dataStorageEnabled: Boolean,
    onAIEnabledChange: (Boolean) -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onRemindersEnabledChange: (Boolean) -> Unit,
    onUpdateChecksEnabledChange: (Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onDataStorageEnabledChange: (Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    val translation = rememberTranslation()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentStep.ordinal + 1) / ConfigurationStep.entries.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )

        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onSkip) {
                Text(
                    text = translation.onboardingSkip,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Content
        when (currentStep) {
            ConfigurationStep.AI_FEATURES -> {
                AIConfigurationStep(
                    aiEnabled = aiEnabled,
                    onAIEnabledChange = onAIEnabledChange,
                    translation = translation
                )
            }
            ConfigurationStep.NOTIFICATIONS -> {
                NotificationConfigurationStep(
                    notificationsEnabled = notificationsEnabled,
                    remindersEnabled = remindersEnabled,
                    updateChecksEnabled = updateChecksEnabled,
                    onNotificationsEnabledChange = onNotificationsEnabledChange,
                    onRemindersEnabledChange = onRemindersEnabledChange,
                    onUpdateChecksEnabledChange = onUpdateChecksEnabledChange,
                    translation = translation
                )
            }
            ConfigurationStep.THEME_SETTINGS -> {
                ThemeConfigurationStep(
                    selectedTheme = selectedTheme,
                    onThemeChange = onThemeChange,
                    translation = translation
                )
            }
            ConfigurationStep.LANGUAGE_SETTINGS -> {
                LanguageConfigurationStep(
                    selectedLanguage = selectedLanguage,
                    onLanguageChange = onLanguageChange,
                    translation = translation
                )
            }
            ConfigurationStep.DATA_PRIVACY -> {
                DataPrivacyConfigurationStep(
                    dataStorageEnabled = dataStorageEnabled,
                    onDataStorageEnabledChange = onDataStorageEnabledChange,
                    translation = translation
                )
            }
            ConfigurationStep.COMPLETE -> {
                SetupCompleteStep(translation = translation)
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.width(100.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(translation.back)
            }

            // Next/Complete button
            Button(
                onClick = onNext,
                modifier = Modifier.width(if (currentStep == ConfigurationStep.COMPLETE) 160.dp else 140.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when (currentStep) {
                            ConfigurationStep.COMPLETE -> translation.onboardingReadyToUse
                            else -> translation.next
                        },
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (currentStep == ConfigurationStep.COMPLETE) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AIConfigurationStep(
    aiEnabled: Boolean,
    onAIEnabledChange: (Boolean) -> Unit,
    translation: Translation
) {
    ConfigurationStepLayout(
        emoji = "🤖",
        title = translation.onboardingAITitle,
        description = translation.onboardingAIDesc
    ) {
        // AI toggle options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectableOptionCard(
                selected = aiEnabled,
                onClick = { onAIEnabledChange(true) },
                title = translation.onboardingAIEnabled,
                description = "Get personalized insights and suggestions",
                icon = "🧠"
            )

            SelectableOptionCard(
                selected = !aiEnabled,
                onClick = { onAIEnabledChange(false) },
                title = translation.onboardingAIDisabled,
                description = "Use the app without AI features",
                icon = "📱"
            )
        }
    }
}

@Composable
fun NotificationConfigurationStep(
    notificationsEnabled: Boolean,
    remindersEnabled: Boolean,
    updateChecksEnabled: Boolean,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onRemindersEnabledChange: (Boolean) -> Unit,
    onUpdateChecksEnabledChange: (Boolean) -> Unit,
    translation: Translation
) {
    val context = LocalContext.current

    // Check if notification permission is already granted
    val hasNotificationPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Pre-API 33 doesn't require runtime permission
        }
    }

    // Update the notificationsEnabled state if permission is already granted
    LaunchedEffect(hasNotificationPermission) {
        if (hasNotificationPermission && !notificationsEnabled) {
            onNotificationsEnabledChange(true)
        }
    }

    ConfigurationStepLayout(
        emoji = "🔔",
        title = translation.onboardingNotificationConfigTitle,
        description = translation.onboardingNotificationConfigDesc
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main notification permission
            SettingsCard(
                title = "Notification Permission",
                icon = "🔔"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onNotificationsEnabledChange(true) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !hasNotificationPermission,
                        colors = if (hasNotificationPermission) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            ButtonDefaults.buttonColors()
                        }
                    ) {
                        Icon(
                            imageVector = if (hasNotificationPermission) Icons.Default.Check else Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasNotificationPermission) "Notifications Enabled ✓" else translation.onboardingAllowNotifications
                        )
                    }

                    if (hasNotificationPermission) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Reminder notifications
                        SettingToggleItem(
                            title = "Mental Health Reminders",
                            description = translation.onboardingEnableReminders,
                            checked = remindersEnabled,
                            onCheckedChange = onRemindersEnabledChange,
                            icon = "💭"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Update notifications
                        SettingToggleItem(
                            title = "Update Notifications",
                            description = translation.onboardingEnableUpdateChecks,
                            checked = updateChecksEnabled,
                            onCheckedChange = onUpdateChecksEnabledChange,
                            icon = "📱"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeConfigurationStep(
    selectedTheme: String,
    onThemeChange: (String) -> Unit,
    translation: Translation
) {
    ConfigurationStepLayout(
        emoji = "🎨",
        title = translation.onboardingChooseTheme,
        description = "Customize how the app looks to match your preference"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val themes = listOf(
                PreferencesManager.THEME_AUTO to "System Default" to "📱",
                PreferencesManager.THEME_LIGHT to "Light Theme" to "☀️",
                PreferencesManager.THEME_DARK to "Dark Theme" to "🌙"
            )

            themes.forEach { (codeLabel, icon) ->
                val (code, label) = codeLabel
                SelectableOptionCard(
                    selected = selectedTheme == code,
                    onClick = { onThemeChange(code) },
                    title = label,
                    description = when (code) {
                        PreferencesManager.THEME_AUTO -> "Automatically switch between light and dark based on your device settings"
                        PreferencesManager.THEME_LIGHT -> "A bright, clean interface perfect for daytime use"
                        PreferencesManager.THEME_DARK -> "A gentle, dark interface that's easier on your eyes"
                        else -> null
                    },
                    icon = icon
                )
            }
        }
    }
}

@Composable
fun LanguageConfigurationStep(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    translation: Translation
) {
    ConfigurationStepLayout(
        emoji = "🌎",
        title = translation.onboardingChooseLanguage,
        description = "Choose your preferred language for the app interface"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val languages = listOf(
                PreferencesManager.LANGUAGE_ENGLISH to "English" to "🇺🇸",
                PreferencesManager.LANGUAGE_FRENCH to "Français" to "🇫🇷",
                PreferencesManager.LANGUAGE_SPANISH to "Español" to "🇪🇸"
            )

            languages.forEach { (codeLabel, flag) ->
                val (code, label) = codeLabel
                SelectableOptionCard(
                    selected = selectedLanguage == code,
                    onClick = { onLanguageChange(code) },
                    title = label,
                    description = when (code) {
                        PreferencesManager.LANGUAGE_ENGLISH -> "Default language with full feature support"
                        PreferencesManager.LANGUAGE_FRENCH -> "Interface complète en français"
                        PreferencesManager.LANGUAGE_SPANISH -> "Interfaz completa en español"
                        else -> null
                    },
                    icon = flag
                )
            }
        }
    }
}

@Composable
fun DataPrivacyConfigurationStep(
    dataStorageEnabled: Boolean,
    onDataStorageEnabledChange: (Boolean) -> Unit,
    translation: Translation
) {
    ConfigurationStepLayout(
        emoji = "🔐",
        title = translation.onboardingDataPrivacyTitle,
        description = translation.onboardingDataPrivacyDesc
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsCard(
                title = "PHQ-9 Data Storage",
                icon = "📊"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SettingToggleItem(
                        title = "Enable Data Storage",
                        description = translation.onboardingEnableDataStorage,
                        checked = dataStorageEnabled,
                        onCheckedChange = onDataStorageEnabledChange,
                        icon = "💾"
                    )
                }
            }

            // Privacy promise card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    width = 1.dp
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "🔒",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = "Privacy Promise",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All your data stays on your device. We never collect, store, or share your personal information with third parties.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SetupCompleteStep(translation: Translation) {
    ConfigurationStepLayout(
        emoji = "🎉",
        title = translation.onboardingSetupComplete,
        description = translation.onboardingSetupCompleteDesc
    ) {
        // Success card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                width = 1.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = "You're all set!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "All your preferences have been saved and are ready to use.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

// Reusable components for consistent configuration step design

@Composable
fun ConfigurationStepLayout(
    emoji: String,
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Emoji header
        Text(
            text = emoji,
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Content
        content()
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
fun SelectableOptionCard(
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    description: String?,
    icon: String,
    compact: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                           else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
            width = 2.dp
        ) else CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            width = 1.dp
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 12.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = if (compact) 18.sp else 24.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = if (compact) 14.sp else 16.sp
                )
                if (description != null) {
                    Text(
                        text = description,
                        fontSize = if (compact) 11.sp else 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = if (compact) 14.sp else 16.sp
                    )
                }
            }

            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
