package com.mysticbyte.counter.util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun currentEpochMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}
