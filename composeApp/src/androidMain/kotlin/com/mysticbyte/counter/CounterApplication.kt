package com.mysticbyte.counter

import android.app.Application
import com.mysticbyte.counter.di.appModule
import org.koin.core.context.startKoin

class CounterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            modules(appModule)
        }
    }

    companion object {
        lateinit var instance: CounterApplication
            private set
    }
}
