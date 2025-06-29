package dev.alphagame.seen.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.alphagame.seen.onboarding.ConfigurationStep
import dev.alphagame.seen.onboarding.steps.AIConfigurationStep
import dev.alphagame.seen.onboarding.steps.AnalyticsConfigurationStep
import dev.alphagame.seen.onboarding.steps.DataPrivacyConfigurationStep
import dev.alphagame.seen.onboarding.steps.LanguageConfigurationStep
import dev.alphagame.seen.onboarding.steps.NotificationConfigurationStep
import dev.alphagame.seen.onboarding.steps.SetupCompleteStep
import dev.alphagame.seen.onboarding.steps.ThemeConfigurationStep
import dev.alphagame.seen.translations.rememberTranslation

@Composable
fun ConfigurationFlow(
    currentStep: ConfigurationStep,
    aiEnabled: Boolean,
    notificationsEnabled: Boolean,
    remindersEnabled: Boolean,
    updateChecksEnabled: Boolean,
    analyticsEnabled: Boolean,
    selectedTheme: String,
    selectedLanguage: String,
    dataStorageEnabled: Boolean,
    onAIEnabledChange: (Boolean) -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onRemindersEnabledChange: (Boolean) -> Unit,
    onUpdateChecksEnabledChange: (Boolean) -> Unit,
    onAnalyticsEnabledChange: (Boolean) -> Unit,
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
            ConfigurationStep.ANALYTICS -> {
                AnalyticsConfigurationStep(
                    analyticsEnabled = analyticsEnabled,
                    onAnalyticsEnabledChange = onAnalyticsEnabledChange,
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
                modifier = Modifier.width(120.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = translation.back,
                    maxLines = 1
                )
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
