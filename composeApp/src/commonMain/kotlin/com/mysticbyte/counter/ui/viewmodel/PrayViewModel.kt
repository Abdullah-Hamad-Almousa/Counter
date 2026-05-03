package com.mysticbyte.counter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mysticbyte.counter.data.model.PrayerConfig
import com.mysticbyte.counter.data.model.PrayerTimeDisplay
import com.mysticbyte.counter.data.model.Timings
import com.mysticbyte.counter.data.repository.PrayerRepository
import com.mysticbyte.counter.util.currentEpochMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PrayViewModel(
    private val repository: PrayerRepository
) : ViewModel() {

    val config = repository.config
    val lastResponse = repository.lastResponse
    val isLoading = repository.isLoading
    val error = repository.error

    private val _nextPrayerInfo = MutableStateFlow("")
    val nextPrayerInfo: StateFlow<String> = _nextPrayerInfo.asStateFlow()

    private val _prayerTimes = MutableStateFlow<List<PrayerTimeDisplay>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTimeDisplay>> = _prayerTimes.asStateFlow()

    private var observeJob: Job? = null

    init {
        viewModelScope.launch {
            repository.loadConfig()
            val cfg = config.value
            if (cfg.city.isNotBlank() && cfg.country.isNotBlank()) {
                fetchTimes()
            }
        }
        observeJob = viewModelScope.launch {
            lastResponse.collectLatest { response ->
                response?.data?.timings?.let { timings ->
                    val offset = config.value.offsetMinutes
                    computePrayerDisplays(timings, offset)
                    computeNextPrayer(timings, offset)
                }
            }
        }
    }

    fun fetchTimes() {
        repository.fetchTimes()
    }

    fun updateConfig(country: String, city: String, method: Int, offsetMinutes: Int) {
        val newConfig = PrayerConfig(
            country = country.trim(),
            city = city.trim(),
            method = method,
            offsetMinutes = offsetMinutes.coerceAtLeast(0)
        )
        repository.saveConfig(newConfig)
        fetchTimes()
    }

    fun clearError() {
        // Repository handles error state internally
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
    }

    private fun computePrayerDisplays(timings: Timings, offset: Int) {
        val prayers = listOf(
            "Fajr" to timings.Fajr,
            "Dhuhr" to timings.Dhuhr,
            "Asr" to timings.Asr,
            "Maghrib" to timings.Maghrib,
            "Isha" to timings.Isha
        )

        val instant = Instant.fromEpochMilliseconds(currentEpochMillis())
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinutes = now.hour * 60 + now.minute

        val displays = prayers.map { (name, callTimeStr) ->
            val prayerTimeStr = addMinutes(callTimeStr, offset)
            val callMinutes = timeToMinutes(callTimeStr)
            val isNext = callMinutes > currentMinutes && prayers.none { (n, t) ->
                n != name && timeToMinutes(t) > currentMinutes && timeToMinutes(t) < callMinutes
            }
            PrayerTimeDisplay(
                name = name,
                callTime = callTimeStr,
                prayerTime = prayerTimeStr,
                isNext = isNext
            )
        }

        _prayerTimes.value = displays
    }

    private fun computeNextPrayer(timings: Timings, offset: Int) {
        val instant = Instant.fromEpochMilliseconds(currentEpochMillis())
        val currentDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinutes = currentDateTime.hour * 60 + currentDateTime.minute

        val prayers = listOf(
            "Fajr" to timings.Fajr,
            "Dhuhr" to timings.Dhuhr,
            "Asr" to timings.Asr,
            "Maghrib" to timings.Maghrib,
            "Isha" to timings.Isha
        )

        var nextPrayer: Pair<String, String>? = null
        var smallestDiff = Int.MAX_VALUE

        for ((name, callTimeStr) in prayers) {
            val callMinutes = timeToMinutes(callTimeStr)
            val diff = callMinutes - currentMinutes
            if (diff > 0 && diff < smallestDiff) {
                smallestDiff = diff
                nextPrayer = name to callTimeStr
            }
        }

        _nextPrayerInfo.value = if (nextPrayer != null) {
            val hours = smallestDiff / 60
            val minutes = smallestDiff % 60
            val timeStr = buildString {
                if (hours > 0) append("${hours}h ")
                append("${minutes}m")
            }
            "${nextPrayer.first} Call in $timeStr"
        } else {
            "All prayers passed for today"
        }
    }

    private fun timeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        if (parts.size < 2) return 0
        return (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
    }

    private fun addMinutes(timeStr: String, minutes: Int): String {
        val parts = timeStr.split(":")
        if (parts.size < 2) return timeStr
        val hour = parts[0].toIntOrNull() ?: 0
        val minute = parts[1].toIntOrNull() ?: 0
        val totalMinutes = hour * 60 + minute + minutes
        val newHour = (totalMinutes / 60) % 24
        val newMinute = totalMinutes % 60
        return "${newHour.toString().padStart(2, '0')}:${newMinute.toString().padStart(2, '0')}"
    }
}
