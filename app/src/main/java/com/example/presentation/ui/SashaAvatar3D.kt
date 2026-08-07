package com.example.presentation.ui

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

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

    LaunchedEffect(isSpeaking, isThinking) {
        currentState = when {
            isSpeaking -> "speaking"
            isThinking -> "thinking"
            else -> "idle"
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(false)

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
                        override fun onPageFinished(view: WebView?, url: String?) {
                            // Full-body framing + walk-in greet once the 3D scene is live
                            view?.evaluateJavascript("setView('full')", null)
                            view?.evaluateJavascript("setWalking(true)", null)
                            view?.evaluateJavascript("setState('$currentState')", null)
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                view?.evaluateJavascript("setWalking(false)", null)
                                view?.evaluateJavascript("setState('idle')", null)
                            }, 2200)
                        }
                    }

                    loadUrl("file:///android_asset/avatar.html")
                }
            },
            update = { webView ->
                webView.evaluateJavascript("setState('$currentState')", null)
            },
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
        )
    }
}
