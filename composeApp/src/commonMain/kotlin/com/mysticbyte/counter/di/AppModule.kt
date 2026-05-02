package com.mysticbyte.counter.di

import com.mysticbyte.counter.data.local.DataStoreManager
import com.mysticbyte.counter.data.remote.PrayerApiService
import com.mysticbyte.counter.data.repository.CountersRepository
import com.mysticbyte.counter.data.repository.HistoryRepository
import com.mysticbyte.counter.data.repository.PrayerRepository
import com.mysticbyte.counter.data.repository.SettingsRepository
import com.mysticbyte.counter.ui.viewmodel.CountersViewModel
import com.mysticbyte.counter.ui.viewmodel.HistoryViewModel
import com.mysticbyte.counter.ui.viewmodel.PrayViewModel
import com.mysticbyte.counter.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { DataStoreManager() }
    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    single { PrayerApiService() }
    single { HistoryRepository(get(), get()) }
    single { CountersRepository(get(), get(), get()) }
    single { SettingsRepository(get(), get()) }
    single { PrayerRepository(get(), get(), get()) }

    viewModel { CountersViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { PrayViewModel(get()) }
}
