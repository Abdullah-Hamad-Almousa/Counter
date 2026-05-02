package com.mysticbyte.counter.data.repository

import com.mysticbyte.counter.data.local.DataStoreManager
import com.mysticbyte.counter.data.model.AladhanResponse
import com.mysticbyte.counter.data.model.PrayerConfig
import com.mysticbyte.counter.data.remote.PrayerApiService
import com.mysticbyte.counter.util.todayFormattedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PrayerRepository(
    private val apiService: PrayerApiService,
    private val dataStoreManager: DataStoreManager,
    private val scope: CoroutineScope
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val _config = MutableStateFlow(PrayerConfig())
    val config: StateFlow<PrayerConfig> = _config.asStateFlow()

    private val _lastResponse = MutableStateFlow<AladhanResponse?>(null)
    val lastResponse: StateFlow<AladhanResponse?> = _lastResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        scope.launch {
            loadConfig()
        }
    }

    suspend fun loadConfig() {
        val loaded = dataStoreManager.loadPrayerConfig()
        _config.value = loaded
    }

    fun saveConfig(config: PrayerConfig) {
        _config.value = config
        scope.launch {
            dataStoreManager.savePrayerConfig(config)
        }
    }

    fun fetchTimes() {
        val currentConfig = _config.value
        if (currentConfig.city.isBlank() || currentConfig.country.isBlank()) {
            _error.value = "Please set your city and country in settings"
            return
        }

        _isLoading.value = true
        _error.value = null

        scope.launch {
            try {
                val response = apiService.getTimingsByCity(
                    city = currentConfig.city,
                    country = currentConfig.country,
                    method = currentConfig.method,
                    date = todayFormattedDate()
                )
                _lastResponse.value = response
                if (response.code != 200 || response.data == null) {
                    _error.value = "Failed to load prayer times. Please check your location settings."
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
