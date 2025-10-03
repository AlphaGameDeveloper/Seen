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

package dev.alphagame.seen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.alphagame.seen.health.HealthStatusManager
import dev.alphagame.seen.translations.rememberTranslation

/**
 * Health status dots component showing the status of Analytics, Releases, and AI services
 */
@Composable
fun HealthStatusDots(
    healthStatusManager: HealthStatusManager,
    modifier: Modifier = Modifier
) {
    val analyticsHealth by healthStatusManager.analyticsHealthState.collectAsState()
    val releasesHealth by healthStatusManager.releasesHealthState.collectAsState()
    val aiHealth by healthStatusManager.aiHealthState.collectAsState()
    var showHealthDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clickable { showHealthDialog = true }
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HealthDot(status = analyticsHealth)
        HealthDot(status = releasesHealth)
        HealthDot(status = aiHealth)
    }

    if (showHealthDialog) {
        HealthStatusDialog(
            analyticsHealth = analyticsHealth,
            releasesHealth = releasesHealth,
            aiHealth = aiHealth,
            onDismiss = { showHealthDialog = false },
            onRefresh = { healthStatusManager.checkAllServices() }
        )
    }
}

/**
 * Individual health status dot
 */
@Composable
private fun HealthDot(
    status: HealthStatusManager.HealthStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        HealthStatusManager.HealthStatus.HEALTHY -> Color(0xFF4CAF50) // Green
        HealthStatusManager.HealthStatus.UNHEALTHY -> Color(0xFFF44336) // Red
        HealthStatusManager.HealthStatus.UNKNOWN -> Color(0xFF9E9E9E) // Gray
    }

    Canvas(
        modifier = modifier.size(8.dp)
    ) {
        drawCircle(
            color = color,
            radius = size.width / 2
        )
    }
}

/**
 * Health status dialog showing detailed information
 */
@Composable
private fun HealthStatusDialog(
    analyticsHealth: HealthStatusManager.HealthStatus,
    releasesHealth: HealthStatusManager.HealthStatus,
    aiHealth: HealthStatusManager.HealthStatus,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit
) {
    val translation = rememberTranslation()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = translation.serviceHealthStatus,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Analytics Service
                ServiceHealthRow(
                    serviceName = translation.analyticsService,
                    status = analyticsHealth
                )

                // Releases Service
                ServiceHealthRow(
                    serviceName = translation.releasesService,
                    status = releasesHealth
                )

                // AI Service
                ServiceHealthRow(
                    serviceName = translation.aiService,
                    status = aiHealth
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onRefresh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(translation.refreshStatus)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(translation.closeDialog)
                    }
                }
            }
        }
    }
}

/**
 * Individual service health row in the dialog
 */
@Composable
private fun ServiceHealthRow(
    serviceName: String,
    status: HealthStatusManager.HealthStatus
) {
    val translation = rememberTranslation()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = serviceName,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HealthDot(status = status)
            Text(
                text = when (status) {
                    HealthStatusManager.HealthStatus.HEALTHY -> translation.healthyStatus
                    HealthStatusManager.HealthStatus.UNHEALTHY -> translation.unhealthyStatus
                    HealthStatusManager.HealthStatus.UNKNOWN -> translation.unknownStatus
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
