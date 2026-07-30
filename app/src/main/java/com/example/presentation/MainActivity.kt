package com.example.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import com.example.presentation.ui.VaultScreen
import com.example.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingScreenShareCallback: ((resultCode: Int, data: Intent?) -> Unit)? = null

    private val screenShareLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        pendingScreenShareCallback?.invoke(result.resultCode, result.data)
        pendingScreenShareCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                VaultScreen(
                    onRequestScreenShare = { callback ->
                        pendingScreenShareCallback = callback
                        val intent = android.media.projection.MediaProjectionManager::class.java
                        val mgr = getSystemService(android.content.Context.MEDIA_PROJECTION_SERVICE) as android.media.projection.MediaProjectionManager
                        screenShareLauncher.launch(mgr.createScreenCaptureIntent())
                    }
                )
            }
        }
    }
}
