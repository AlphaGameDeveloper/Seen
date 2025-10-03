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
