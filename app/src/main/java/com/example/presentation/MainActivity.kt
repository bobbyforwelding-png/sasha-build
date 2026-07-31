package com.example.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.presentation.ui.VaultScreen
import com.example.presentation.theme.AppTheme
import com.example.service.SashaOverlayService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingScreenShareCallback: ((resultCode: Int, data: Intent?) -> Unit)? = null

    private val screenShareLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        pendingScreenShareCallback?.invoke(result.resultCode, result.data)
        pendingScreenShareCallback = null
    }

    private val overlaySettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(this)) {
            startOverlay()
        }
    }

    private fun startOverlay() {
        startForegroundService(Intent(this, SashaOverlayService::class.java).apply {
            action = SashaOverlayService.ACTION_START
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                VaultScreen(
                    onRequestScreenShare = { callback ->
                        pendingScreenShareCallback = callback
                        val mgr = getSystemService(android.content.Context.MEDIA_PROJECTION_SERVICE) as android.media.projection.MediaProjectionManager
                        screenShareLauncher.launch(mgr.createScreenCaptureIntent())
                    },
                    onRequestOverlay = {
                        if (!Settings.canDrawOverlays(this)) {
                            overlaySettingsLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                        } else {
                            startOverlay()
                        }
                    },
                    onStopOverlay = {
                        stopService(Intent(this, SashaOverlayService::class.java).apply {
                            action = SashaOverlayService.ACTION_STOP
                        })
                    }
                )
            }
        }
    }
}
