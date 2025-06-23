package dev.alphagame.seen.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alphagame.seen.MainActivity

@Composable
fun ResultScreen(score: Int, onRetakeQuiz: () -> Unit) {
    val context = LocalContext.current
    val buttonModifier = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        .fillMaxWidth()

    val (level, color) = when {
        score <= 4 -> "Minimal" to Color(0xFF6ECB63)
        score <= 9 -> "Mild" to Color(0xFFFFD700)
        score <= 14 -> "Moderate" to Color(0xFFFFA500)
        score <= 19 -> "Moderately Severe" to Color(0xFFFF6347)
        else -> "Severe" to Color(0xFFFF3C38)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your PHQ-9 Score:", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        Text("$score", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Depression Level: $level", fontSize = 20.sp, color = color)

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    // open phone dialer with 988
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:988"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(20.dp),
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("📞 Talk to Someone – 988", color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nimh.nih.gov/health/topics/depression"))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(20.dp),
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("📚 Learn About Depression", color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = onRetakeQuiz,
                shape = RoundedCornerShape(20.dp),
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("🔄 Return to Home", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
