package com.example.gigappweather.domain.repository

import com.example.gigappweather.core.Outcome
import com.example.gigappweather.domain.model.WeatherSummary

interface WeatherRepository {
    suspend fun getDailyForecast(cityName: String): Outcome<WeatherSummary>
}
