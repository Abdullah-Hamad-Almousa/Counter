package com.mysticbyte.counter.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PrayerConfig(
    val country: String = "",
    val city: String = "",
    val method: Int = 4, // Umm al-Qura by default
    val offsetMinutes: Int = 15
)

@Serializable
enum class CalculationMethod(val id: Int, val displayName: String) {
    JAFARI(0, "Shia Ithna-Ashari"),
    KARACHI(1, "University of Islamic Sciences, Karachi"),
    ISNA(2, "Islamic Society of North America"),
    MWL(3, "Muslim World League"),
    MAKKAH(4, "Umm al-Qura, Makkah"),
    EGYPT(5, "Egyptian General Authority"),
    TEHRAN(7, "Institute of Geophysics, Tehran"),
    GULF(8, "Gulf Region"),
    KUWAIT(9, "Kuwait"),
    QATAR(10, "Qatar"),
    SINGAPORE(11, "Singapore"),
    FRANCE(12, "Union Organization, France"),
    TURKEY(13, "Diyanet, Turkey"),
    RUSSIA(14, "Spiritual Administration, Russia"),
    MOONSIGHTING(15, "Moonsighting Committee"),
    DUBAI(16, "Dubai"),
    JAKARTA(17, "Indonesia"),
    TUNISIA(18, "Tunisia"),
    ALGERIA(19, "Algeria"),
    PAKISTAN(20, "Pakistan"),
    EGYPT_BIS(21, "Egypt (Bis)"),
    KEMENAG(22, "Kemenag, Indonesia")
}
