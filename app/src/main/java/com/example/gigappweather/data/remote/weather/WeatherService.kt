package com.example.gigappweather.data.remote.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("v1/forecast")
    suspend fun getDailyForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_min,temperature_2m_max,precipitation_sum,wind_speed_10m_max",
        @Query("timezone") timezone: String = "auto",
    ): WeatherForecastResponseDto

    companion object {
        const val BASE_URL: String = "https://api.open-meteo.com/"
    }
}
