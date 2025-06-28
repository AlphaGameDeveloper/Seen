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
import kotlinx.coroutines.launch

enum class OnboardingStage {
    WELCOME_CAROUSEL,
    CONFIGURATION
}

enum class ConfigurationStep {
    AI_FEATURES,
    NOTIFICATIONS,
    GENERAL_SETTINGS,
    DATA_PRIVACY,
    COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val preferencesManager = remember { PreferencesManager(context) }
    val updateCheckManager = remember { UpdateCheckManager(context) }
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var currentStage by remember { mutableStateOf(OnboardingStage.WELCOME_CAROUSEL) }
    var currentConfigStep by remember { mutableStateOf(ConfigurationStep.AI_FEATURES) }

    // Configuration states
    var aiEnabled by remember { mutableStgc ateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(false) }
    var remindersEnabled by remember { mutableStateOf(false) }
    var updateChecksEnabled by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(PreferencesManager.THEME_AUTO) }
    var selectedLanguage by remember { mutableStateOf(preferencesManager.language) }
    var dataStorageEnabled by remember { mutableStateOf(false) }

    // Dialog states
    var showNotificationSuccessDialog by remember { mutableStateOf(false) }
    var showNotificationDeniedDialog by remember { mutableStateOf(false) }

    // Notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
        if (isGranted) {
            showNotificationSuccessDialog = true
        } else {
            showNotificationDeniedDialog = true
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
                    onAIEnabledChange = { aiEnabled = it },
                    onNotificationsEnabledChange = { enabled ->
                        if (enabled) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                                    PackageManager.PERMISSION_GRANTED -> {
                                        notificationsEnabled = true
                                        showNotificationSuccessDialog = true
                                    }
                                    else -> {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            } else {
                                notificationsEnabled = true
                                showNotificationSuccessDialog = true
                            }
                        } else {
                            notificationsEnabled = false
                        }
                    },
                    onRemindersEnabledChange = { remindersEnabled = it },
                    onUpdateChecksEnabledChange = { updateChecksEnabled = it },
                    onThemeChange = { selectedTheme = it },
                    onLanguageChange = { selectedLanguage = it },
                    onDataStorageEnabledChange = { dataStorageEnabled = it },
                    onNext = {
                        when (currentConfigStep) {
                            ConfigurationStep.AI_FEATURES -> currentConfigStep = ConfigurationStep.NOTIFICATIONS
                            ConfigurationStep.NOTIFICATIONS -> currentConfigStep = ConfigurationStep.GENERAL_SETTINGS
                            ConfigurationStep.GENERAL_SETTINGS -> currentConfigStep = ConfigurationStep.DATA_PRIVACY
                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep = ConfigurationStep.COMPLETE
                            ConfigurationStep.COMPLETE -> {
                                // Apply all settings
                                preferencesManager.notificationsEnabled = notificationsEnabled
                                preferencesManager.themeMode = selectedTheme
                                preferencesManager.language = selectedLanguage
                                preferencesManager.isPhq9DataStorageEnabled = dataStorageEnabled
                                preferencesManager.backgroundUpdateChecksEnabled = updateChecksEnabled && notificationsEnabled

                                // Start background update checks if enabled
                                if (updateChecksEnabled && notificationsEnabled) {
                                    updateCheckManager.startBackgroundUpdateChecks()
                                }

                                onOnboardingComplete()
                            }
                        }
                    },
                    onBack = {
                        when (currentConfigStep) {
                            ConfigurationStep.AI_FEATURES -> currentStage = OnboardingStage.WELCOME_CAROUSEL
                            ConfigurationStep.NOTIFICATIONS -> currentConfigStep = ConfigurationStep.AI_FEATURES
                            ConfigurationStep.GENERAL_SETTINGS -> currentConfigStep = ConfigurationStep.NOTIFICATIONS
                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep = ConfigurationStep.GENERAL_SETTINGS
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
                    onAIEnabledChange = onAIEnabledChange
                )
            }
            ConfigurationStep.NOTIFICATIONS -> {
                NotificationConfigurationStep(
                    notificationsEnabled = notificationsEnabled,
                    remindersEnabled = remindersEnabled,
                    updateChecksEnabled = updateChecksEnabled,
                    onNotificationsEnabledChange = onNotificationsEnabledChange,
                    onRemindersEnabledChange = onRemindersEnabledChange,
                    onUpdateChecksEnabledChange = onUpdateChecksEnabledChange
                )
            }
            ConfigurationStep.GENERAL_SETTINGS -> {
                GeneralSettingsConfigurationStep(
                    selectedTheme = selectedTheme,
                    selectedLanguage = selectedLanguage,
                    onThemeChange = onThemeChange,
                    onLanguageChange = onLanguageChange
                )
            }
            ConfigurationStep.DATA_PRIVACY -> {
                DataPrivacyConfigurationStep(
                    dataStorageEnabled = dataStorageEnabled,
                    onDataStorageEnabledChange = onDataStorageEnabledChange
                )
            }
            ConfigurationStep.COMPLETE -> {
                SetupCompleteStep()
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
                modifier = Modifier.width(140.dp),
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
                        fontWeight = FontWeight.SemiBold
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
    onAIEnabledChange: (Boolean) -> Unit
) {
    val translation = rememberTranslation()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🤖",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = translation.onboardingAITitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = translation.onboardingAIDesc,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // AI toggle options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = aiEnabled,
                        onClick = { onAIEnabledChange(true) },
                        role = Role.RadioButton
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (aiEnabled) MaterialTheme.colorScheme.primaryContainer
                                   else MaterialTheme.colorScheme.surface
                ),
                border = if (aiEnabled) CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    width = 2.dp
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = aiEnabled,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = translation.onboardingAIEnabled,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Get personalized insights and suggestions",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = !aiEnabled,
                        onClick = { onAIEnabledChange(false) },
                        role = Role.RadioButton
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (!aiEnabled) MaterialTheme.colorScheme.primaryContainer
                                   else MaterialTheme.colorScheme.surface
                ),
                border = if (!aiEnabled) CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    width = 2.dp
                ) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !aiEnabled,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = translation.onboardingAIDisabled,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Use the app without AI features",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
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
    onUpdateChecksEnabledChange: (Boolean) -> Unit
) {
    val translation = rememberTranslation()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔔",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = translation.onboardingNotificationConfigTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = translation.onboardingNotificationConfigDesc,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Main notification permission
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Button(
                    onClick = { onNotificationsEnabledChange(true) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !notificationsEnabled
                ) {
                    Icon(
                        imageVector = if (notificationsEnabled) Icons.Default.Check else Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (notificationsEnabled) "Notifications Enabled ✓" else translation.onboardingAllowNotifications
                    )
                }

                if (notificationsEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Reminder notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mental Health Reminders",
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = translation.onboardingEnableReminders,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = onRemindersEnabledChange
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Update notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Update Notifications",
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = translation.onboardingEnableUpdateChecks,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = updateChecksEnabled,
                            onCheckedChange = onUpdateChecksEnabledChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralSettingsConfigurationStep(
    selectedTheme: String,
    selectedLanguage: String,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val translation = rememberTranslation()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎨",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = translation.onboardingGeneralSettingsTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = translation.onboardingGeneralSettingsDesc,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Theme selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = translation.onboardingChooseTheme,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                val themes = listOf(
                    PreferencesManager.THEME_AUTO to "System Default",
                    PreferencesManager.THEME_LIGHT to "Light",
                    PreferencesManager.THEME_DARK to "Dark"
                )

                themes.forEach { (code, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedTheme == code,
                                onClick = { onThemeChange(code) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTheme == code,
                            onClick = null
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = translation.onboardingChooseLanguage,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                val languages = listOf(
                    PreferencesManager.LANGUAGE_ENGLISH to "English",
                    PreferencesManager.LANGUAGE_FRENCH to "Français",
                    PreferencesManager.LANGUAGE_SPANISH to "Español"
                )

                languages.forEach { (code, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedLanguage == code,
                                onClick = { onLanguageChange(code) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == code,
                            onClick = null
                        )
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DataPrivacyConfigurationStep(
    dataStorageEnabled: Boolean,
    onDataStorageEnabledChange: (Boolean) -> Unit
) {
    val translation = rememberTranslation()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔐",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = translation.onboardingDataPrivacyTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = translation.onboardingDataPrivacyDesc,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PHQ-9 Data Storage",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = translation.onboardingEnableDataStorage,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 16.sp
                        )
                    }
                    Switch(
                        checked = dataStorageEnabled,
                        onCheckedChange = onDataStorageEnabledChange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🔒 Privacy Promise",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All your data stays on your device. We never collect, store, or share your personal information with third parties.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SetupCompleteStep() {
    val translation = rememberTranslation()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = translation.onboardingSetupComplete,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = translation.onboardingSetupCompleteDesc,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✨ You're ready to begin your mental health journey with Seen!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
