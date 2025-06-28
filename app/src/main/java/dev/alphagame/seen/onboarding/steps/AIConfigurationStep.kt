package dev.alphagame.seen.onboarding.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alphagame.seen.onboarding.components.ConfigurationStepLayout
import dev.alphagame.seen.onboarding.components.SelectableOptionCard
import dev.alphagame.seen.translations.Translation

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
