package com.example.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionName: String,
    val versionCode: Int,
    val downloadUrl: String,
    val releaseNotes: String,
    val forceUpdate: Boolean = false
)

class UpdateChecker(private val context: Context) {

    companion object {
        private const val UPDATE_CHECK_URL = "https://nicebros.ai/api/updates/sasha"
        private const val CURRENT_VERSION_CODE = 20
    }

    suspend fun checkForUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(UPDATE_CHECK_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.setRequestProperty("X-App-Version", CURRENT_VERSION_CODE.toString())
            conn.setRequestProperty("X-App-Package", context.packageName)

            if (conn.responseCode == 200) {
                val body = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(body)

                val latestCode = json.optInt("versionCode", 0)
                if (latestCode > CURRENT_VERSION_CODE) {
                    UpdateInfo(
                        versionName = json.optString("versionName", "Unknown"),
                        versionCode = latestCode,
                        downloadUrl = json.optString("downloadUrl", ""),
                        releaseNotes = json.optString("releaseNotes", "No release notes available."),
                        forceUpdate = json.optBoolean("forceUpdate", false)
                    )
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun downloadUpdate(info: UpdateInfo): Long {
        val request = DownloadManager.Request(Uri.parse(info.downloadUrl))
            .setTitle("Downloading Sasha v${info.versionName}")
            .setDescription("Update: ${info.releaseNotes.take(100)}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Sasha_v${info.versionName}.apk")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return dm.enqueue(request)
    }
}
