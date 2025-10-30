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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.data.UpdateCheckManager
import dev.alphagame.seen.onboarding.ConfigurationStep
import dev.alphagame.seen.onboarding.OnboardingStage
import dev.alphagame.seen.onboarding.components.ConfigurationFlow
import dev.alphagame.seen.translations.Translation
import dev.alphagame.seen.translations.rememberTranslation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val updateCheckManager = remember { UpdateCheckManager(context) }

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
                                when (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )) {
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
                        preferencesManager.backgroundUpdateChecksEnabled =
                            it && notificationsEnabled

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
                            ConfigurationStep.AI_FEATURES -> currentConfigStep =
                                ConfigurationStep.NOTIFICATIONS

                            ConfigurationStep.NOTIFICATIONS -> currentConfigStep =
                                ConfigurationStep.ANALYTICS

                            ConfigurationStep.ANALYTICS -> currentConfigStep =
                                ConfigurationStep.THEME_SETTINGS

                            ConfigurationStep.THEME_SETTINGS -> currentConfigStep =
                                ConfigurationStep.LANGUAGE_SETTINGS

                            ConfigurationStep.LANGUAGE_SETTINGS -> currentConfigStep =
                                ConfigurationStep.DATA_PRIVACY

                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep =
                                ConfigurationStep.COMPLETE

                            ConfigurationStep.COMPLETE -> {
                                // All settings are already applied immediately when changed
                                // Just complete the onboarding
                                onOnboardingComplete()
                            }
                        }
                    },
                    onBack = {
                        when (currentConfigStep) {
                            ConfigurationStep.AI_FEATURES -> currentStage =
                                OnboardingStage.WELCOME_CAROUSEL

                            ConfigurationStep.NOTIFICATIONS -> currentConfigStep =
                                ConfigurationStep.AI_FEATURES

                            ConfigurationStep.ANALYTICS -> currentConfigStep =
                                ConfigurationStep.NOTIFICATIONS

                            ConfigurationStep.THEME_SETTINGS -> currentConfigStep =
                                ConfigurationStep.ANALYTICS

                            ConfigurationStep.LANGUAGE_SETTINGS -> currentConfigStep =
                                ConfigurationStep.THEME_SETTINGS

                            ConfigurationStep.DATA_PRIVACY -> currentConfigStep =
                                ConfigurationStep.LANGUAGE_SETTINGS

                            ConfigurationStep.COMPLETE -> currentConfigStep =
                                ConfigurationStep.DATA_PRIVACY
                        }
                    },
                    onAnalyticsEnabledChange = {},
                    onSkip = {}
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

