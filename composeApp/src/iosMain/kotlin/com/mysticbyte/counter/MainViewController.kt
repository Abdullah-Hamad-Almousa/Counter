package com.mysticbyte.counter

import androidx.compose.ui.window.ComposeUIViewController
import com.mysticbyte.counter.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule)
    }
    App()
}