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
    onBackToResults: () -> Unit,
    scores: List<Int>
) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val resourceColor = Color(0xFFE6E6E6)
    var activateSuicideHotline = false

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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
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
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Text(
                        text = translation.resourcesAndSupport,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(modifier = Modifier.width(48.dp))
                }

                /*Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = translation.yourScore,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$totalScore - $levelText",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = color
                        )
                    }
                } */
            }
        }
        if (totalScore>19 || scores[8]>1) {
            activateSuicideHotline = true
        }
        //Start implimentation:
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                        .asPaddingValues()
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when {
                activateSuicideHotline -> {
                    ResourceCard(
                        title = translation.crisisSupport,
                        description = translation.severeSymptoms,
                        icon = Icons.Default.Phone,
                        backgroundColor = resourceColor,
                        onBackgroundColor = Color(0xFF000000)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(translation.talkToSomeone)
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://suicidepreventionlifeline.org/")
                                    )
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(translation.about988)
                            }
                        }
                    }
                }

                else -> {
                    ResourceCard(
                        title = translation.crisisSupport,
                        description = translation.crisisSupportDescription,
                        icon = Icons.Default.Phone,
                        backgroundColor = resourceColor,
                        onBackgroundColor = Color(0xFF000000)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(translation.talkToSomeone)
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://suicidepreventionlifeline.org/")
                                    )
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(translation.about988)
                            }
                        }
                    }
                }
            }

            //Continue here

            ResourceCard(
                title = translation.aboutDepression,
                description = translation.aboutDepressionDescription,
                icon = Icons.Default.Info,
                backgroundColor = resourceColor,
                onBackgroundColor = Color(0xFF000000)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.nimh.nih.gov/health/topics/depression")
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text(
                            translation.learnAboutDepression2)
                    }
                    Button(
                        onClick = {
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nimh.nih.gov/health/topics/caring-for-your-mental-health"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(translation.caringForMentalHealth)
                    }
                }
            }
            ResourceCard(
                title = translation.recommendedResources,
                description = translation.recommendedResourcesDescription,
                icon = Icons.Default.Settings,
                backgroundColor = resourceColor,
                onBackgroundColor = Color(0xFF000000)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (scores[0]>1) {
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.healthdirect.gov.au/amp/article/losing-interest")
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = translation.aboutLosingInterest
                            )

                        }
                    }
                    if (scores[1]>1) {
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/url?q=https://www.cdc.gov/howrightnow/emotion/sadness/&sa=D&source=docs&ust=1760257749423011&usg=AOvVaw2rKzzn5INCvzhiHVxPLZL2")
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = translation.aboutSadness
                            )

                        }
                    }
                    if (scores[2]>1 || scores[3]>1) {
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.nami.org/complimentary-health-approaches/5-sleep-tips-that-can-help-with-depression/")
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = translation.aboutSleep
                            )

                        }
                    }
                    //Appetite
                    if (scores[5]>1) {
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.mayoclinic.org/healthy-lifestyle/adult-health/in-depth/self-esteem/art-20045374")
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = translation.aboutFeelingBad
                            )

                        }
                    }
                    if (scores[6]>1) {
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.nami.org/depression-disorders/five-ways-to-stay-productive-during-depression/")
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(
                                text = translation.aboutConcentration
                            )

                        }
                    }
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://ocpsc.ca.gov/resources/?_post_tag=youth&_category=mental-health")
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text(
                            text = translation.additionalYouthResources
                        )

                    }
                    //moving speaking slowly, being figety restless
                    //suicidal
                    if (false) {
                    Button(
                        onClick = {
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mindful.org/"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("TEST")
                    }
                }}
            }

        }

    }

        if (false) {
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
                    text = "Back To Results",
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

