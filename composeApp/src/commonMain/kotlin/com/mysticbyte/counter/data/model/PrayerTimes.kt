package com.mysticbyte.counter.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AladhanResponse(
    val code: Int,
    val status: String,
    val data: PrayerData? = null
)

@Serializable
data class PrayerData(
    val timings: Timings? = null,
    val date: PrayerDate? = null
)

@Serializable
data class Timings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Sunset: String,
    val Maghrib: String,
    val Isha: String,
    val Imsak: String? = null,
    val Midnight: String? = null,
    @SerialName("Firstthird") val FirstThird: String? = null,
    @SerialName("Lastthird") val LastThird: String? = null
)

@Serializable
data class PrayerDate(
    val readable: String? = null,
    val gregorian: GregorianDate? = null
)

@Serializable
data class GregorianDate(
    val date: String? = null,
    val day: String? = null,
    val month: MonthInfo? = null,
    val year: String? = null
)

@Serializable
data class MonthInfo(
    val number: Int? = null,
    val en: String? = null
)

data class PrayerTimeDisplay(
    val name: String,
    val callTime: String,
    val prayerTime: String,
    val isNext: Boolean = false
)
