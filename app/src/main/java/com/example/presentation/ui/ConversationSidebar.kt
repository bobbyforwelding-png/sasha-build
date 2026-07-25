package com.example.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.conversation.Conversation
import com.example.data.conversation.ConversationManager

@Composable
fun ConversationSidebar(
    isOpen: Boolean,
    manager: ConversationManager,
    currentConvoId: String?,
    onSelect: (Conversation) -> Unit,
    onNew: () -> Unit,
    onClose: () -> Unit
) {
    val neonCyan = Color(0xFF00E5FF)
    val darkBg = Color(0xFF0A0A0F)
    val darkSurface = Color(0xFF111118)
    val darkCard = Color(0xFF1A1A24)
    var conversations by remember { mutableStateOf(listOf<Conversation>()) }
    var showRenameDialog by remember { mutableStateOf<Conversation?>(null) }

    LaunchedEffect(isOpen) {
        if (isOpen) conversations = manager.getAll()
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { -it }),
        exit = slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Box(modifier = Modifier.fillMaxHeight().width(280.dp).background(darkBg).border(1.dp, neonCyan.copy(alpha = 0.2f))) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = neonCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CONVERSATIONS", color = neonCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // New conversation button
                Button(
                    onClick = {
                        onNew()
                        conversations = manager.getAll()
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = neonCyan.copy(alpha = 0.15f), contentColor = neonCyan)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("NEW CONVERSATION", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Conversation list
                LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                    val pinned = conversations.filter { it.pinned }
                    val unpinned = conversations.filter { !it.pinned }

                    if (pinned.isNotEmpty()) {
                        item {
                            Text("PINNED", color = Color(0xFF555577), fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
                        }
                        items(pinned) { convo ->
                            ConversationItem(convo, convo.id == currentConvoId, manager, neonCyan, darkCard, darkSurface, {
                                onSelect(it)
                                onClose()
                            }, {
                                conversations = manager.getAll()
                            }, { showRenameDialog = it })
                        }
                    }

                    if (unpinned.isNotEmpty()) {
                        item {
                            Text("RECENT", color = Color(0xFF555577), fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
                        }
                        items(unpinned) { convo ->
                            ConversationItem(convo, convo.id == currentConvoId, manager, neonCyan, darkCard, darkSurface, {
                                onSelect(it)
                                onClose()
                            }, {
                                conversations = manager.getAll()
                            }, { showRenameDialog = it })
                        }
                    }

                    if (conversations.isEmpty()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF333355), modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No conversations yet", color = Color(0xFF555577), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    showRenameDialog?.let { convo ->
        RenameDialog(
            currentTitle = convo.title,
            onDismiss = { showRenameDialog = null },
            onConfirm = { newTitle ->
                manager.rename(convo, newTitle)
                conversations = manager.getAll()
                showRenameDialog = null
            }
        )
    }
}

@Composable
fun ConversationItem(
    convo: Conversation,
    isActive: Boolean,
    manager: ConversationManager,
    neonCyan: Color,
    darkCard: Color,
    darkSurface: Color,
    onSelect: (Conversation) -> Unit,
    onRefresh: () -> Unit,
    onRename: (Conversation) -> Unit
) {
    val msgCount = convo.messages.size
    val preview = convo.messages.lastOrNull { it.role == "SASHA" }?.text?.take(60) ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) neonCyan.copy(alpha = 0.1f) else Color.Transparent)
            .then(if (isActive) Modifier.border(1.dp, neonCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp)) else Modifier)
            .clickable { onSelect(convo) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (convo.pinned) Icons.Filled.PushPin else Icons.Filled.ChatBubbleOutline,
            contentDescription = null,
            tint = if (convo.pinned) Color(0xFFFBBF24) else Color(0xFF555577),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                convo.title,
                color = if (isActive) neonCyan else Color(0xFFCCCCDD),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (preview.isNotBlank()) {
                Text(
                    preview,
                    color = Color(0xFF555577),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                "${manager.formatDate(convo.timestamp)} • $msgCount msgs",
                color = Color(0xFF444466),
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp
            )
        }

        // Actions
        IconButton(onClick = { manager.togglePin(convo); onRefresh() }, modifier = Modifier.size(24.dp)) {
            Icon(
                if (convo.pinned) Icons.Filled.PushPin else Icons.Filled.PushPin,
                contentDescription = "Pin",
                tint = if (convo.pinned) Color(0xFFFBBF24) else Color(0xFF555577),
                modifier = Modifier.size(12.dp)
            )
        }
        IconButton(onClick = { onRename(convo) }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = "Rename", tint = Color(0xFF555577), modifier = Modifier.size(12.dp))
        }
        IconButton(onClick = { manager.delete(convo.id); onRefresh() }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFF553333), modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
fun RenameDialog(currentTitle: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(currentTitle) }
    val neonCyan = Color(0xFF00E5FF)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111118),
        titleContentColor = neonCyan,
        textContentColor = Color(0xFFCCCCDD),
        title = { Text("RENAME", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFFCCCCDD),
                    focusedBorderColor = neonCyan,
                    unfocusedBorderColor = Color(0xFF2A2A3A),
                    cursorColor = neonCyan
                ),
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { if (text.isNotBlank()) onConfirm(text.trim()) }, colors = ButtonDefaults.buttonColors(containerColor = neonCyan, contentColor = Color.Black)) {
                Text("SAVE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = Color(0xFF555577), fontFamily = FontFamily.Monospace) }
        }
    )
}
