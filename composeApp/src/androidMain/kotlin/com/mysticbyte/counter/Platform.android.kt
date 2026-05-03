package com.mysticbyte.counter

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getDataStoreFilePath(): String {
    return CounterApplication.instance.filesDir.resolve("datastore/counter.preferences_pb").absolutePath
}
