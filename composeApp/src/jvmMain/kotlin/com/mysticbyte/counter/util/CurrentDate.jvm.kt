package com.mysticbyte.counter.util

actual fun currentEpochMillis(): Long {
    return System.currentTimeMillis()
}
