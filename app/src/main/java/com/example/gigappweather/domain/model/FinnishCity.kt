package com.example.gigappweather.domain.model

import androidx.annotation.StringRes

data class FinnishCity(
    val id: String,
    @StringRes val displayNameRes: Int,
    val latitude: Double,
    val longitude: Double,
)
