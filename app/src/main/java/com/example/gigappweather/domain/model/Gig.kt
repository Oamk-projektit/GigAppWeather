package com.example.gigappweather.domain.model

data class Gig(
    val id: Long = 0L,
    val title: String,
    /** ISO-8601 date, e.g. 2026-01-05 */
    val dateIso: String,
    val city: String,
    val isOutdoor: Boolean,
    /** Epoch millis */
    val createdAt: Long,
)
