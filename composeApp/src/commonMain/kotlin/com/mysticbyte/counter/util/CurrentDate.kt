package com.mysticbyte.counter.util

expect fun currentEpochMillis(): Long

fun todayFormattedDate(): String {
    val epochMillis = currentEpochMillis()
    val seconds = epochMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val daysSince1970 = hours / 24

    var year = 1970
    var remainingDays = daysSince1970.toInt()
    while (remainingDays >= daysInYear(year)) {
        remainingDays -= daysInYear(year)
        year++
    }

    var month = 1
    while (remainingDays >= daysInMonth(year, month)) {
        remainingDays -= daysInMonth(year, month)
        month++
    }

    val day = remainingDays + 1
    return "${day.toString().padStart(2, '0')}-${month.toString().padStart(2, '0')}-${year}"
}

private fun daysInYear(year: Int): Int {
    return if (isLeapYear(year)) 366 else 365
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }
}
