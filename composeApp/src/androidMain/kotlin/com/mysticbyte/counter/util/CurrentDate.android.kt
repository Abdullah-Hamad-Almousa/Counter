package com.mysticbyte.counter.util

actual fun currentEpochMillis(): Long {
    return java.lang.System.currentTimeMillis()
}
