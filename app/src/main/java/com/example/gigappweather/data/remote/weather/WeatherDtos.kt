package com.example.gigappweather.data.remote.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecastResponseDto(
    @SerialName("daily") val daily: DailyDto? = null,
)

@Serializable
data class DailyDto(
    @SerialName("time") val time: List<String> = emptyList(),
    @SerialName("temperature_2m_min") val temperature2mMin: List<Double> = emptyList(),
    @SerialName("temperature_2m_max") val temperature2mMax: List<Double> = emptyList(),
    @SerialName("precipitation_sum") val precipitationSum: List<Double> = emptyList(),
    @SerialName("wind_speed_10m_max") val windSpeed10mMax: List<Double> = emptyList(),
)
