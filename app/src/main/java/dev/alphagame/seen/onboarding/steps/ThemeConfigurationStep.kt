package dev.alphagame.seen.onboarding.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.onboarding.components.ConfigurationStepLayout
import dev.alphagame.seen.onboarding.components.SelectableOptionCard
import dev.alphagame.seen.translations.Translation

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
