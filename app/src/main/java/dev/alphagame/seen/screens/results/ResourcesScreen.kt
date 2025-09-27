package dev.alphagame.seen.screens.results

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.translations.rememberTranslation

@Composable
fun ResourcesScreen(
    totalScore: Int,
    levelText: String,
    color: Color,
    onBackToResults: () -> Unit
) {
    val context = LocalContext.current
    val translation = rememberTranslation()

    val buttonModifier = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        .fillMaxWidth()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top).asPaddingValues()),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackToResults) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = translation.backToResults,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Text(
                        text = translation.resourcesAndSupport,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.width(48.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = translation.yourScore,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$totalScore - $levelText",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = color
                        )
                    }
                }
            }
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom).asPaddingValues())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Crisis Support (always shown)
            ResourceCard(
                title = "Crisis Support",
                description = "If you're having thoughts of self-harm or suicide, please reach out immediately.",
                icon = Icons.Default.Phone,
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                onBackgroundColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = translation.talkToSomeone,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Score-specific resources
            when {
                totalScore <= 4 -> {
                    ResourceCard(
                        title = "Maintaining Mental Wellness",
                        description = "Your score indicates minimal symptoms. Here are resources to maintain your mental health.",
                        icon = Icons.Default.Info,
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onBackgroundColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nimh.nih.gov/health/topics/caring-for-your-mental-health"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Mental Health Tips")
                            }
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mindful.org/"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Mindfulness Resources")
                            }
                        }
                    }
                }

                totalScore <= 14 -> {
                    ResourceCard(
                        title = "Support & Treatment Options",
                        description = "Your score suggests mild to moderate symptoms. Consider these support options.",
                        icon = Icons.Default.Info,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        onBackgroundColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.psychologytoday.com/us/therapists"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Find a Therapist")
                            }
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nimh.nih.gov/health/topics/depression"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(translation.learnAboutDepression)
                            }
                        }
                    }
                }

                else -> {
                    ResourceCard(
                        title = "Immediate Professional Help",
                        description = "Your score indicates more severe symptoms. Professional help is strongly recommended.",
                        icon = Icons.Default.Phone,
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                        onBackgroundColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.psychologytoday.com/us/therapists"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Find Professional Help Now")
                            }
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://suicidepreventionlifeline.org/"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Crisis Resources")
                            }
                        }
                    }
                }
            }

            // General Mental Health Resources
            ResourceCard(
                title = "Educational Resources",
                description = "Learn more about mental health and depression.",
                icon = Icons.Default.Settings,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                onBackgroundColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nimh.nih.gov/health/topics/depression"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("NIMH Depression Info")
                    }
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mentalhealth.gov/"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("MentalHealth.gov")
                    }
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nami.org/"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("NAMI Resources")
                    }
                }
            }
        }

        // Bottom back button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            OutlinedButton(
                onClick = onBackToResults,
                shape = RoundedCornerShape(20.dp),
                modifier = buttonModifier
                    .padding(20.dp)
                    .border(width = 0.dp, color = Color.Transparent)
            ) {
                Text(
                    text = "Back to Results",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ResourceCard(
    title: String,
    description: String,
    icon: ImageVector,
    backgroundColor: Color,
    onBackgroundColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = onBackgroundColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = onBackgroundColor
                )
            }

            Text(
                text = description,
                fontSize = 14.sp,
                color = onBackgroundColor.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            content()
        }
    }
}
