package dev.alphagame.seen.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dev.alphagame.seen.FeatureFlags
import dev.alphagame.seen.data.PreferencesManager
import dev.alphagame.seen.translations.rememberTranslation
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String,
    val isNotificationPage: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val context = LocalContext.current
    val translation = rememberTranslation()
    val preferencesManager = remember { PreferencesManager(context) }

    val pages = mutableListOf<OnboardingPage>(
        OnboardingPage(
            title = translation.onboardingWelcomeTitle,
            description = translation.onboardingWelcomeDesc,
            emoji = "👋"
        ),
        OnboardingPage(
            title = translation.onboardingPHQ9Title,
            description = translation.onboardingPHQ9Desc,
            emoji = "📋"
        ),
        OnboardingPage(
            title = translation.onboardingNotesTitle,
            description = translation.onboardingNotesDesc,
            emoji = "📝"
        ),
        OnboardingPage(
            title = translation.onboardingPrivacyTitle,
            description = translation.onboardingPrivacyDesc,
            emoji = "🔒"
        ),

        OnboardingPage(
            title = translation.onboardingNotificationsTitle,
            description = translation.onboardingNotificationsDesc,
            emoji = "🔔",
            isNotificationPage = true
        )
    )

    if (FeatureFlags.ONBOARDING_NO_ADS) {
        pages.add(OnboardingPage(
            title = translation.onboardingNoAdsTitle,
            description = translation.onboardingNoAdsDesc,
            emoji = "🚫"))
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    // State for notification permission dialog
    var showNotificationSuccessDialog by remember { mutableStateOf(false) }
    var showNotificationDeniedDialog by remember { mutableStateOf(false) }

    // Notification permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        preferencesManager.notificationsEnabled = isGranted
        if (isGranted) {
            showNotificationSuccessDialog = true
        } else {
            showNotificationDeniedDialog = true
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onOnboardingComplete
            ) {
                Text(
                    text = translation.onboardingSkip,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(
                page = pages[page],
                modifier = Modifier.fillMaxSize(),
                onNotificationPermissionRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                            PackageManager.PERMISSION_GRANTED -> {
                                preferencesManager.notificationsEnabled = true
                                showNotificationSuccessDialog = true
                            }
                            else -> {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    } else {
                        // For older versions, notifications are enabled by default
                        preferencesManager.notificationsEnabled = true
                        showNotificationSuccessDialog = true
                    }
                }
            )
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        .padding(horizontal = 4.dp)
                )
                if (index < pages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            if (pagerState.currentPage > 0) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(translation.back)
                }
            } else {
                Spacer(modifier = Modifier.width(100.dp))
            }

            // Next/Get Started button
            val isLastPage = pagerState.currentPage == pages.size - 1
            Button(
                onClick = {
                    if (isLastPage) {
                        onOnboardingComplete()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isLastPage) translation.onboardingContinue else translation.next,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isLastPage) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Notification success dialog
    if (showNotificationSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationSuccessDialog = false },
            title = {
                Text(
                    text = "Great! 🎉",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    text = "You'll now receive gentle reminders to help you stay on top of your mental health journey.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationSuccessDialog = false }
                ) {
                    Text("Awesome!")
                }
            }
        )
    }

    // Notification denied dialog
    if (showNotificationDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDeniedDialog = false },
            title = {
                Text(
                    text = "No Problem! 👍",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "You can always enable notifications later in the app settings if you change your mind.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDeniedDialog = false }
                ) {
                    Text("Got it!")
                }
            }
        )
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
    onNotificationPermissionRequest: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Emoji icon
        Text(
            text = page.emoji,
            fontSize = 80.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Special notification page content
        if (page.isNotificationPage) {
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNotificationPermissionRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Allow Notifications",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { /* User chose to skip notifications */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Maybe Later",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}
