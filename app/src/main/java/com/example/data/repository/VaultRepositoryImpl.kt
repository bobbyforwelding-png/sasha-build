package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.domain.model.Job
import com.example.domain.repository.VaultRepository

class VaultRepositoryImpl(private val context: Context) : VaultRepository {

    private val sharedPreferences: SharedPreferences = openEncryptedPrefs(context)

    companion object {
        private const val PREFS_NAME = "secure_vault_prefs"
        private const val PREFS_FALLBACK = "secure_vault_prefs_plain"
        private const val TAG = "VaultRepository"

        private fun openEncryptedPrefs(context: Context): SharedPreferences {
            return try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                // Corrupted or stale encrypted prefs (common after reinstall when Keystore
                // entries are wiped but the prefs file survives). Delete and retry once.
                Log.w(TAG, "EncryptedSharedPreferences open failed, wiping and retrying", e)
                context.deleteSharedPreferences(PREFS_NAME)
                try {
                    val masterKey = MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()
                    EncryptedSharedPreferences.create(
                        context,
                        PREFS_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                } catch (e2: Exception) {
                    // Encryption not available on this device — fall back to plain prefs.
                    Log.e(TAG, "EncryptedSharedPreferences unavailable, using plain prefs", e2)
                    context.getSharedPreferences(PREFS_FALLBACK, Context.MODE_PRIVATE)
                }
            }
        }
    }

    override fun getPin(): String? = sharedPreferences.getString("VAULT_PIN", null)

    override fun savePin(pin: String) {
        sharedPreferences.edit().putString("VAULT_PIN", pin).apply()
    }

    override fun getJobs(): List<Job> {
        val serialized = sharedPreferences.getString("JOBS_DATA", null) ?: return emptyList()
        return serialized.split("\u001E").mapNotNull { record ->
            val parts = record.split("\u001F")
            if (parts.size >= 8) {
                Job(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7])
            } else null
        }
    }

    override fun saveJobs(jobs: List<Job>) {
        val serialized = jobs.joinToString(separator = "\u001E") { job ->
            listOf(job.id, job.name, job.customer, job.metalType, job.process, job.status, job.price, job.notes).joinToString(separator = "\u001F")
        }
        sharedPreferences.edit().putString("JOBS_DATA", serialized).apply()
    }

    override fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    override fun getSafetyConfig(): Map<String, Boolean> {
        return mapOf(
            "PPE" to sharedPreferences.getBoolean("SAFETY_PPE", false),
            "VENT" to sharedPreferences.getBoolean("SAFETY_VENT", false),
            "GROUND" to sharedPreferences.getBoolean("SAFETY_GROUND", false),
            "GAS" to sharedPreferences.getBoolean("SAFETY_GAS", false),
            "FIRE" to sharedPreferences.getBoolean("SAFETY_FIRE", false)
        )
    }

    override fun saveSafetyConfig(config: Map<String, Boolean>) {
        val editor = sharedPreferences.edit()
        config.forEach { (key, value) ->
            editor.putBoolean("SAFETY_$key", value)
        }
        editor.apply()
    }

    override fun getRunCode(): String? = sharedPreferences.getString("SAFETY_RUNCODE", null)

    override fun saveRunCode(code: String?) {
        sharedPreferences.edit().putString("SAFETY_RUNCODE", code).apply()
    }
}
