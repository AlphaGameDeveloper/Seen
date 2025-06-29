package dev.alphagame.seen.onboarding.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.onboarding.components.ConfigurationStepLayout
import dev.alphagame.seen.onboarding.components.SelectableOptionCard
import dev.alphagame.seen.translations.Translation

@Composable
fun AnalyticsConfigurationStep(
    analyticsEnabled: Boolean,
    onAnalyticsEnabledChange: (Boolean) -> Unit,
    translation: Translation
) {
    ConfigurationStepLayout(
        emoji = "📊",
        title = translation.onboardingAnalyticsTitle,
        description = translation.onboardingAnalyticsDesc
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Analytics enabled option
            SelectableOptionCard(
                selected = analyticsEnabled,
                onClick = { onAnalyticsEnabledChange(true) },
                title = translation.onboardingAnalyticsEnabled,
                description = "Help improve Seen by sharing anonymous usage data",
                icon = "📈"
            )

            // Analytics disabled option
            SelectableOptionCard(
                selected = !analyticsEnabled,
                onClick = { onAnalyticsEnabledChange(false) },
                title = translation.onboardingAnalyticsDisabled,
                description = "Keep all usage data completely private",
                icon = "🥺"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy information card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔒 Privacy Information",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = translation.analyticsPrivacyNote,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = translation.analyticsDataUsage,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
