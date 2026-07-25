package com.example.domain.repository

import com.example.domain.model.Job

interface VaultRepository {
    fun getPin(): String?
    fun savePin(pin: String)
    fun getJobs(): List<Job>
    fun saveJobs(jobs: List<Job>)
    fun clearAll()
    fun getSafetyConfig(): Map<String, Boolean>
    fun saveSafetyConfig(config: Map<String, Boolean>)
    fun getRunCode(): String?
    fun saveRunCode(code: String?)
}
