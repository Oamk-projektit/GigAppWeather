package com.example.gigappweather.data.remote.geocoding

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1,
    ): GeocodingResponseDto

    companion object {
        const val BASE_URL: String = "https://geocoding-api.open-meteo.com/"
    }
}
