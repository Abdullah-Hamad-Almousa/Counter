package com.mysticbyte.counter.data.remote

import com.mysticbyte.counter.data.model.AladhanResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PrayerApiService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }

    suspend fun getTimingsByCity(
        city: String,
        country: String,
        method: Int,
        date: String
    ): AladhanResponse {
        return client
            .get("https://api.aladhan.com/v1/timingsByCity/$date") {
                url {
                    parameters.append("city", city)
                    parameters.append("country", country)
                    parameters.append("method", method.toString())
                }
            }
            .body()
    }
}
