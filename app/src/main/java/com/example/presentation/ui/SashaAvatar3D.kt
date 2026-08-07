package com.example.presentation.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SashaAvatar3D(
    isSpeaking: Boolean = false,
    isThinking: Boolean = false,
    avatarUrl: String? = null,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF00BFFF),
    onAvatarUrlChange: (String) -> Unit = {}
) {
    var currentState by remember { mutableStateOf("idle") }
    val currentAvatarUrl = avatarUrl?.trim().takeUnless { it.isNullOrEmpty() }

    LaunchedEffect(isSpeaking, isThinking) {
        currentState = when {
            isSpeaking -> "speaking"
            isThinking -> "thinking"
            else -> "idle"
        }
    }

    Box(modifier = modifier.background(Color.Transparent), contentAlignment = Alignment.Center) {
        if (currentAvatarUrl != null) {
            AsyncImage(
                model = currentAvatarUrl,
                contentDescription = "SASHA avatar",
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.allowFileAccessFromFileURLs = true
                        settings.allowUniversalAccessFromFileURLs = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.setSupportZoom(false)
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        setBackgroundColor(0x00000000)
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        clearCache(true)

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
                            override fun onPageFinished(view: WebView?, url: String?) {
                                // Head/shoulders portrait framing
                                view?.evaluateJavascript("setView('portrait')", null)
                                view?.evaluateJavascript("setState('$currentState')", null)
                            }
                        }

                        loadUrl("file:///android_asset/avatar.html?v=human_v2")
                    }
                },
                update = { webView ->
                    webView.evaluateJavascript("setState('$currentState')", null)
                },
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
