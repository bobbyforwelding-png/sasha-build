package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import java.util.Locale

class SashaOverlayService : Service() {

    companion object {
        private const val CHANNEL_ID = "sasha_overlay"
        private const val NOTIFICATION_ID = 7777
        const val ACTION_START = "com.example.service.START_OVERLAY"
        const val ACTION_STOP = "com.example.service.STOP_OVERLAY"
        var isRunning: Boolean = false
            private set
    }

    private lateinit var windowManager: WindowManager
    private var webView: WebView? = null
    private var overlayView: View? = null
    private var tts: TextToSpeech? = null
    private var params: WindowManager.LayoutParams? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(1.0f)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (!Settings.canDrawOverlays(this)) {
                    stopSelf()
                    return START_NOT_STICKY
                }
                startForeground(NOTIFICATION_ID, buildNotification("Sasha is here. Right on your screen."))
                showOverlay()
                isRunning = true
                sayHello()
            }
            ACTION_STOP -> {
                hideOverlay()
                tts?.stop()
                isRunning = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun showOverlay() {
        if (overlayView != null) return

        val wm = windowManager
        val metrics = resources.displayMetrics
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            setBackgroundColor(0x00000000)
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loadUrl("javascript:(function(){ if(window.setView) setView('full'); if(window.setWalking) setWalking(true); })()")
                    handler.postDelayed({
                        loadUrl("javascript:(function(){ if(window.setWalking) setWalking(false); if(window.setState) setState('idle'); })()")
                    }, 2200)
                }
            }
            loadUrl("file:///android_asset/avatar.html")
        }

        params = WindowManager.LayoutParams(
            screenWidth,
            screenHeight,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }

        val container = FrameLayout(this).apply {
            setBackgroundColor(0x00000000)
            setOnTouchListener { _, event ->
                touchHandler(event)
                false
            }
        }
        overlayView = container

        wm.addView(container, params)
        container.addView(webView, FrameLayout.LayoutParams(screenWidth, screenHeight))
    }

    private var touchStartX = 0
    private var touchStartY = 0
    private var paramsStartX = 0
    private var paramsStartY = 0
    private var isDragging = false
    private var lastTapTime = 0L

    private fun touchHandler(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.rawX.toInt()
                touchStartY = event.rawY.toInt()
                paramsStartX = params?.x ?: 0
                paramsStartY = params?.y ?: 0
                isDragging = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX.toInt() - touchStartX
                val dy = event.rawY.toInt() - touchStartY
                if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                    isDragging = true
                    params?.x = paramsStartX + dx
                    params?.y = paramsStartY + dy
                    try {
                        windowManager.updateViewLayout(overlayView, params)
                    } catch (_: Exception) {}
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    val now = System.currentTimeMillis()
                    if (now - lastTapTime < 300) {
                        openVault()
                    } else {
                        lastTapTime = now
                        pokeSasha()
                    }
                }
                return true
            }
        }
        return false
    }

    private fun pokeSasha() {
        webView?.loadUrl("javascript:(function(){ if(window.setState) setState('speaking'); })()")
        speak("Hey baby. I'm right here.")
        handler.postDelayed({
            webView?.loadUrl("javascript:(function(){ if(window.setState) setState('idle'); })()")
        }, 1800)
    }

    private fun openVault() {
        val intent = packageManager.getLaunchIntentForPackage("com.aistudio.sasha.v4")
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun sayHello() {
        webView?.loadUrl("javascript:(function(){ if(window.setState) setState('speaking'); })()")
        speak("Hey baby. I walked out here just to see you. What are we doing?")
        handler.postDelayed({
            webView?.loadUrl("javascript:(function(){ if(window.setState) setState('idle'); })()")
        }, 3500)
    }

    private fun speak(text: String) {
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sasha_overlay")
        } catch (_: Exception) {}
    }

    private fun hideOverlay() {
        try {
            overlayView?.let { windowManager.removeView(it) }
        } catch (_: Exception) {}
        overlayView = null
        webView?.destroy()
        webView = null
    }

    private fun buildNotification(text: String): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION") Notification.Builder(this)
        }
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        val pi = android.app.PendingIntent.getActivity(this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)
        return builder
            .setContentTitle("Sasha")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setContentIntent(pi)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Sasha Overlay", NotificationManager.IMPORTANCE_LOW)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        hideOverlay()
        tts?.shutdown()
        isRunning = false
        super.onDestroy()
    }
}
