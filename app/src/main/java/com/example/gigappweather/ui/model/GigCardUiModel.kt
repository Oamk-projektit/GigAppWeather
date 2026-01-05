package com.example.gigappweather.ui.model

data class GigCardUiModel(
    val id: Long,
    val title: String,
    val dateIso: String,
    val city: String,
    val score: Int?,
    val summary: WeatherDaySummaryUiModel?,
    val weatherStatus: GigWeatherStatus
)

sealed interface GigWeatherStatus {
    data object Loading : GigWeatherStatus
    data object Available : GigWeatherStatus
    data object CityNotFound : GigWeatherStatus
    data object ForecastNotAvailableYet : GigWeatherStatus
}

data class WeatherDaySummaryUiModel(
    val tempMinC: Double,
    val tempMaxC: Double,
    val precipitationSumMm: Double,
    val windSpeedMax: Double,
)
