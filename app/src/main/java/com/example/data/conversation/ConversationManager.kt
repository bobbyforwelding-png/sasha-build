package com.example.data.conversation

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val role: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Conversation(
    val id: String,
    var title: String,
    var pinned: Boolean = false,
    var timestamp: Long = System.currentTimeMillis(),
    val messages: MutableList<ChatMessage> = mutableListOf()
)

class ConversationManager(private val context: Context) {
    private val dir: File get() = File(context.filesDir, "conversations")

    init { if (!dir.exists()) dir.mkdirs() }

    fun getAll(): List<Conversation> {
        val convos = mutableListOf<Conversation>()
        dir.listFiles()?.filter { it.extension == "json" }?.forEach { file ->
            try {
                val json = JSONObject(file.readText())
                val msgs = mutableListOf<ChatMessage>()
                val arr = json.optJSONArray("messages") ?: JSONArray()
                for (i in 0 until arr.length()) {
                    val m = arr.getJSONObject(i)
                    msgs.add(ChatMessage(m.getString("role"), m.getString("text"), m.optLong("timestamp", 0)))
                }
                convos.add(Conversation(
                    id = json.getString("id"),
                    title = json.getString("title"),
                    pinned = json.optBoolean("pinned", false),
                    timestamp = json.optLong("timestamp", 0),
                    messages = msgs
                ))
            } catch (_: Exception) {}
        }
        return convos.sortedWith(compareByDescending<Conversation> { it.pinned }.thenByDescending { it.timestamp })
    }

    fun save(convo: Conversation) {
        val json = JSONObject().apply {
            put("id", convo.id)
            put("title", convo.title)
            put("pinned", convo.pinned)
            put("timestamp", convo.timestamp)
            put("messages", JSONArray().apply {
                for (m in convo.messages) {
                    put(JSONObject().apply {
                        put("role", m.role)
                        put("text", m.text)
                        put("timestamp", m.timestamp)
                    })
                }
            })
        }
        File(dir, "${convo.id}.json").writeText(json.toString())
    }

    fun create(): Conversation {
        val id = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val convo = Conversation(id = id, title = "New Conversation", timestamp = System.currentTimeMillis())
        save(convo)
        return convo
    }

    fun delete(id: String) {
        File(dir, "$id.json").delete()
    }

    fun togglePin(convo: Conversation) {
        convo.pinned = !convo.pinned
        save(convo)
    }

    fun rename(convo: Conversation, newTitle: String) {
        convo.title = newTitle
        save(convo)
    }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM d, h:mm a", Locale.US).format(Date(timestamp))
    }
}
