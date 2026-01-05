package com.example.gigappweather.data.remote.geocoding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    @SerialName("results") val results: List<GeocodingResultDto> = emptyList(),
)

@Serializable
data class GeocodingResultDto(
    @SerialName("name") val name: String? = null,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("country") val country: String? = null,
    @SerialName("admin1") val admin1: String? = null,
)
