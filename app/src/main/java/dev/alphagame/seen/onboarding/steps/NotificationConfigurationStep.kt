package dev.alphagame.seen.onboarding.steps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dev.alphagame.seen.onboarding.components.ConfigurationStepLayout
import dev.alphagame.seen.onboarding.components.SettingToggleItem
import dev.alphagame.seen.onboarding.components.SettingsCard
import dev.alphagame.seen.translations.Translation

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
