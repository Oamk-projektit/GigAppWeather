package com.example.gigappweather.domain.model

data class WeatherSummary(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val daily: List<DailyWeather>,
)

data class DailyWeather(
    val dateIso: String,
    val tempMinC: Double,
    val tempMaxC: Double,
    val precipitationSumMm: Double,
    val windSpeedMax: Double,
)
