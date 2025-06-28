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
