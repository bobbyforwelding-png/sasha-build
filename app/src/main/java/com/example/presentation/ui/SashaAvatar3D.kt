package com.example.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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

    val avatarState = when {
        isSpeaking -> AvatarState.SPEAKING
        isThinking -> AvatarState.THINKING
        else -> AvatarState.IDLE
    }

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
            SashaHologramAvatar(
                state = avatarState,
                modifier = Modifier.fillMaxSize(),
                primaryColor = primaryColor
            )
        }
    }
}
