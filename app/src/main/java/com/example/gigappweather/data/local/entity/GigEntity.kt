package com.example.gigappweather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gigs")
data class GigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    /** ISO-8601 date, e.g. 2026-01-05 */
    val dateIso: String,
    /** FinnishCities id, e.g. "oulu" */
    val cityId: String,
    val isOutdoor: Boolean,
    /** Epoch millis */
    val createdAt: Long,
)
