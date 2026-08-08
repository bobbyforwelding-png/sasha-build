package com.example.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun SashaAvatar3D(
    isSpeaking: Boolean = false,
    isThinking: Boolean = false,
    avatarUrl: String? = null,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF00BFFF),
    onAvatarUrlChange: (String) -> Unit = {}
) {
    val currentAvatarUrl = avatarUrl?.trim().takeUnless { it.isNullOrEmpty() }
    var useImageAvatar by remember(currentAvatarUrl) { mutableStateOf(currentAvatarUrl != null) }

    val statusLabel = when {
        isSpeaking -> "● SPEAKING"
        isThinking -> "● THINKING..."
        else -> "● ONLINE"
    }
    val statusColor = when {
        isSpeaking -> Color(0xFF00FF88)
        isThinking -> Color(0xFFFBBF24)
        else -> primaryColor
    }

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(modifier = modifier.background(Color.Transparent), contentAlignment = Alignment.Center) {
        if (currentAvatarUrl != null && useImageAvatar) {
            AsyncImage(
                model = currentAvatarUrl,
                contentDescription = "SASHA avatar",
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                onError = { useImageAvatar = false }
            )
        } else {
            // Rock-solid Sasha nameplate — always renders
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0A0A1A), Color(0xFF0D1A2A), Color(0xFF0A0A1A))
                        )
                    )
                    .border(1.dp, primaryColor.copy(alpha = glowAlpha * 0.6f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Glow ring
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .border(2.dp, primaryColor.copy(alpha = glowAlpha), RoundedCornerShape(50))
                            .background(primaryColor.copy(alpha = 0.06f), RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "S",
                            color = primaryColor.copy(alpha = glowAlpha),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "SASHA",
                        color = primaryColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        letterSpacing = 8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "S . A . S . H . A .",
                        color = primaryColor.copy(alpha = 0.45f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        statusLabel,
                        color = statusColor.copy(alpha = glowAlpha),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
