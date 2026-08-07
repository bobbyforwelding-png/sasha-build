package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

private val NeonCyan = Color(0xFF00E5FF)
private val NeonPurple = Color(0xFF8B5CF6)
private val NeonPink = Color(0xFFFF6B9D)
private val NeonGreen = Color(0xFF00FF66)
private val DarkBg = Color(0xFF0A0A0F)
private val DarkSurface = Color(0xFF111118)
private val DarkCard = Color(0xFF1A1A24)
private val DarkBorder = Color(0xFF2A2A3A)
private val TextPrimary = Color(0xFFE0E0E0)
private val TextSecondary = Color(0xFF888899)

data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color = NeonCyan,
    val action: String = ""
)

@Composable
fun SettingsScreen(onClose: () -> Unit) {
    var showAbout by remember { mutableStateOf(false) }
    var showAppearance by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showVoice by remember { mutableStateOf(false) }
    var showDebug by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    fun showComingSoon(feature: String) {
        scope.launch { snackbarHostState.showSnackbar("$feature — coming soon") }
    }

    Scaffold(
        containerColor = DarkBg,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DarkCard,
                    contentColor = NeonCyan
                )
            }
        }
    ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().background(DarkBg).padding(innerPadding)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SETTINGS", color = NeonCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            item { SettingsHeader("PROFILE") }
            item {
                SettingsCard(
                    title = "Sasha Identity",
                    subtitle = "Name, gender, voice, persona",
                    icon = Icons.Filled.Person,
                    color = NeonCyan,
                    onClick = { showAbout = true }
                )
            }
            item {
                SettingsCard(
                    title = "Voice & Speech",
                    subtitle = "Voice cloning, TTS engine, speed, pitch",
                    icon = Icons.Filled.RecordVoiceOver,
                    color = NeonPink,
                    onClick = { showVoice = true }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SettingsHeader("APPEARANCE") }
            item {
                SettingsCard(
                    title = "Theme & Display",
                    subtitle = "Colors, font size, hologram avatar",
                    icon = Icons.Filled.Palette,
                    color = NeonPurple,
                    onClick = { showAppearance = true }
                )
            }
            item {
                SettingsCard(
                    title = "Notifications",
                    subtitle = "Alert sounds, vibration, badge count",
                    icon = Icons.Filled.Notifications,
                    color = NeonGreen,
                    onClick = { showNotifications = true }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SettingsHeader("SECURITY & PRIVACY") }
            item {
                SettingsCard(
                    title = "Vault Security",
                    subtitle = "PIN, biometric lock, auto-lock timer",
                    icon = Icons.Filled.Lock,
                    color = NeonPink,
                    onClick = { showPrivacy = true }
                )
            }
            item {
                SettingsCard(
                    title = "Data & Privacy",
                    subtitle = "Chat history, data export, clear data",
                    icon = Icons.Filled.Shield,
                    color = NeonGreen,
                    onClick = { showPrivacy = true }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SettingsHeader("CONVERSATIONS") }
            item {
                SettingsCard(
                    title = "Conversation History",
                    subtitle = "Manage saved chats, export, backup",
                    icon = Icons.Filled.ChatBubble,
                    color = NeonCyan,
                    comingSoon = true,
                    onClick = { showComingSoon("Conversation History") }
                )
            }
            item {
                SettingsCard(
                    title = "Memory & Context",
                    subtitle = "How much history to remember, context window",
                    icon = Icons.Filled.Memory,
                    color = NeonPurple,
                    comingSoon = true,
                    onClick = { showComingSoon("Memory & Context") }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SettingsHeader("TOOLS & INTEGRATIONS") }
            item {
                SettingsCard(
                    title = "Device Permissions",
                    subtitle = "Contacts, SMS, phone, camera, files",
                    icon = Icons.Filled.Apps,
                    color = NeonGreen,
                    comingSoon = true,
                    onClick = { showComingSoon("Device Permissions") }
                )
            }
            item {
                SettingsCard(
                    title = "API Configuration",
                    subtitle = "Gemini API key, model selection, debug mode",
                    icon = Icons.Filled.Code,
                    color = NeonCyan,
                    onClick = { showDebug = true }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SettingsHeader("SUPPORT") }
            item {
                SettingsCard(
                    title = "Help & Documentation",
                    subtitle = "FAQs, tutorials, getting started guide",
                    icon = Icons.Filled.Help,
                    color = NeonGreen,
                    onClick = { showHelp = true }
                )
            }
            item {
                SettingsCard(
                    title = "Troubleshooting",
                    subtitle = "Fix connection issues, reset app, diagnostics",
                    icon = Icons.Filled.Build,
                    color = Color(0xFFFFAA00),
                    onClick = { showHelp = true }
                )
            }
            item {
                SettingsCard(
                    title = "Contact Support",
                    subtitle = "Report a bug, request a feature, get help",
                    icon = Icons.Filled.SupportAgent,
                    color = NeonPink,
                    onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                            data = android.net.Uri.parse("mailto:support@nicebros.ai")
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Sasha Support Request")
                        }
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsCard(
                    title = "Rate Sasha",
                    subtitle = "Love the app? Tell the world.",
                    icon = Icons.Filled.Star,
                    color = Color(0xFFFFD700),
                    comingSoon = true,
                    onClick = { showComingSoon("Rate Sasha") }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    "Sasha v2.0 — Crypto Trading Bot",
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    if (showAbout) SettingsDetailDialog("Sasha Identity", onClose = { showAbout = false }) {
        SettingsToggleRow("Name", "Sasha", NeonCyan)
        SettingsToggleRow("Gender", "Female", NeonPink)
        SettingsToggleRow("Voice", "Sultry & confident", NeonPurple)
        SettingsToggleRow("Primary Persona", "Sasha — The Master", NeonCyan)
        Text("These are set during onboarding. Reset onboarding to change.", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
    }

    if (showVoice) SettingsDetailDialog("Voice & Speech", onClose = { showVoice = false }) {
        SettingsToggleRow("TTS Engine", "Voice Clone Server", NeonPink)
        SettingsToggleRow("Voice Profile", "Sasha (cloned)", NeonCyan)
        SettingsToggleRow("Fallback", "Android System TTS", NeonGreen)
        SettingsToggleRow("Speed", "1.0x", NeonPurple)
        SettingsToggleRow("Auto-speak responses", "Enabled", NeonCyan)
        Text("Voice cloning uses your cloned voice profile. Falls back to Android TTS if the server is unavailable.", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
    }

    if (showAppearance) SettingsDetailDialog("Theme & Display", onClose = { showAppearance = false }) {
        SettingsToggleRow("Theme", "Cyberpunk Dark", NeonPurple)
        SettingsToggleRow("Primary Color", "Neon Cyan", NeonCyan)
        SettingsToggleRow("Avatar Style", "3D Holographic", NeonPurple)
        SettingsToggleRow("Font", "Monospace", TextSecondary)
        SettingsToggleRow("Animations", "Enabled", NeonGreen)
    }

    if (showNotifications) SettingsDetailDialog("Notifications", onClose = { showNotifications = false }) {
        SettingsToggleRow("Push Notifications", "Enabled", NeonGreen)
        SettingsToggleRow("Sound", "Enabled", NeonCyan)
        SettingsToggleRow("Vibration", "Enabled", NeonPurple)
        SettingsToggleRow("Badge Count", "Enabled", NeonPink)
    }

    if (showPrivacy) SettingsDetailDialog("Vault Security", onClose = { showPrivacy = false }) {
        SettingsToggleRow("Vault PIN", "Set", NeonPink)
        SettingsToggleRow("Biometric Lock", "Not configured", Color(0xFFFFAA00))
        SettingsToggleRow("Auto-lock Timer", "5 minutes", NeonGreen)
        SettingsToggleRow("Chat History Retention", "Unlimited", NeonCyan)
        Text("The Vault is completely unrestricted. No content filtering, no censorship.", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
    }

    if (showDebug) SettingsDetailDialog("API Configuration", onClose = { showDebug = false }) {
        SettingsToggleRow("API Provider", "Google Gemini", NeonCyan)
        SettingsToggleRow("Model (Chat)", "gemini-2.5-flash", NeonGreen)
        SettingsToggleRow("Model (Image)", "gemini-2.5-flash-image", NeonPurple)
        SettingsToggleRow("Safety Filters", "BLOCK_NONE (All categories)", NeonPink)
        SettingsToggleRow("Max Output Tokens", "8192", TextSecondary)
        SettingsToggleRow("Temperature", "1.0", NeonCyan)
        Text("All safety filters are disabled for the Vault. Console uses BLOCK_NONE on all categories.", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
    }

    if (showHelp) SettingsDetailDialog("Help & Support", onClose = { showHelp = false }) {
        SettingsToggleRow("Getting Started", "Tap 'Get Started' in onboarding →", NeonCyan)
        SettingsToggleRow("Chat with Sasha", "Type or tap mic to speak →", NeonGreen)
        SettingsToggleRow("Use Tools", "Sasha auto-calls tools when you ask →", NeonPurple)
        SettingsToggleRow("Vault Access", "Enter PIN to unlock Vault →", NeonPink)
        SettingsToggleRow("Image Generation", "Say 'generate image of...' →", NeonCyan)
        SettingsToggleRow("Voice Input", "Tap mic icon → speak → auto-send →", NeonGreen)
        SettingsToggleRow("Code Generation", "Say 'write me a Kotlin function...' →", NeonPurple)
        SettingsToggleRow("App Launch", "Say 'open Chrome' or 'launch camera' →", NeonCyan)
        Spacer(modifier = Modifier.height(8.dp))
        Text("TROUBLESHOOTING", color = Color(0xFFFFAA00), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        SettingsToggleRow("Voice not working?", "Voice server may be offline. Android TTS fallback is automatic.", Color(0xFFFFAA00))
        SettingsToggleRow("Images flagged?", "This build has no restrictions. Try a different image.", Color(0xFFFFAA00))
        SettingsToggleRow("Chat stuck?", "The app auto-recovers from API errors. Try sending again.", Color(0xFFFFAA00))
        SettingsToggleRow("App slow?", "Close other apps. The tablet has limited RAM.", Color(0xFFFFAA00))
    }
    } // end Scaffold
}

@Composable
private fun SettingsHeader(title: String) {
    Text(
        title,
        color = TextSecondary,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    comingSoon: Boolean = false,
    onClick: () -> Unit
) {
    val cardAlpha = if (comingSoon) 0.45f else 1f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard.copy(alpha = cardAlpha))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color.copy(alpha = cardAlpha), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary.copy(alpha = cardAlpha), fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextSecondary.copy(alpha = cardAlpha), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
        }
        if (comingSoon) {
            Text("SOON", color = TextSecondary.copy(alpha = 0.6f), fontFamily = FontFamily.Monospace, fontSize = 9.sp)
        } else {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsDetailDialog(title: String, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = DarkSurface,
        titleContentColor = NeonCyan,
        textContentColor = TextPrimary,
        title = { Text(title, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        },
        confirmButton = {
            Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DarkBg)) {
                Text("CLOSE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    )
}

@Composable
private fun SettingsToggleRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(value, color = color, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
