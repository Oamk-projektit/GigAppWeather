package com.example.gigappweather.ui.model

import com.example.gigappweather.domain.logic.WeatherScoreBreakdown

data class GigDetailUiModel(
    val id: Long,
    val title: String,
    val dateIso: String,
    val city: String,
    val isOutdoor: Boolean,
    val createdAt: Long,
    val weather: WeatherDaySummaryUiModel?,
    val scoreBreakdown: WeatherScoreBreakdown?,
    val infoTextRes: Int?,
)
