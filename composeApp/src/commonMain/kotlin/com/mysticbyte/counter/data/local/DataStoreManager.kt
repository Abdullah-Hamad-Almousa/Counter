package com.mysticbyte.counter.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mysticbyte.counter.data.model.AppTheme
import com.mysticbyte.counter.data.model.Counter
import com.mysticbyte.counter.data.model.CounterEvent
import com.mysticbyte.counter.data.model.PrayerConfig
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

class DataStoreManager(filePath: String) {

    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
        produceFile = { filePath.toPath() }
    )

    private val json = Json { ignoreUnknownKeys = true }

    private val countersKey = stringPreferencesKey("counters")
    private val themeKey = stringPreferencesKey("theme")
    private val historyKey = stringPreferencesKey("history")
    private val prayerConfigKey = stringPreferencesKey("prayer_config")

    suspend fun saveCounters(counters: List<Counter>) {
        val serialized = json.encodeToString(counters)
        dataStore.edit { prefs ->
            prefs[countersKey] = serialized
        }
    }

    suspend fun loadCounters(): List<Counter> {
        val prefs = dataStore.data.first()
        val serialized = prefs[countersKey] ?: return emptyList()
        return try {
            json.decodeFromString(serialized)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveTheme(theme: AppTheme) {
        val serialized = json.encodeToString(theme)
        dataStore.edit { prefs ->
            prefs[themeKey] = serialized
        }
    }

    suspend fun loadTheme(): AppTheme {
        val prefs = dataStore.data.first()
        val serialized = prefs[themeKey] ?: return AppTheme.LIGHT
        return try {
            json.decodeFromString(serialized)
        } catch (_: Exception) {
            AppTheme.LIGHT
        }
    }

    suspend fun saveHistory(events: List<CounterEvent>) {
        val serialized = json.encodeToString(events)
        dataStore.edit { prefs ->
            prefs[historyKey] = serialized
        }
    }

    suspend fun loadHistory(): List<CounterEvent> {
        val prefs = dataStore.data.first()
        val serialized = prefs[historyKey] ?: return emptyList()
        return try {
            json.decodeFromString(serialized)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun savePrayerConfig(config: PrayerConfig) {
        val serialized = json.encodeToString(config)
        dataStore.edit { prefs ->
            prefs[prayerConfigKey] = serialized
        }
    }

    suspend fun loadPrayerConfig(): PrayerConfig {
        val prefs = dataStore.data.first()
        val serialized = prefs[prayerConfigKey] ?: return PrayerConfig()
        return try {
            json.decodeFromString(serialized)
        } catch (_: Exception) {
            PrayerConfig()
        }
    }
}
