package com.example.gigappweather.data.mapper

import com.example.gigappweather.data.local.entity.GigEntity
import com.example.gigappweather.domain.model.Gig

fun GigEntity.toDomain(): Gig = Gig(
    id = id,
    title = title,
    dateIso = dateIso,
    city = city,
    isOutdoor = isOutdoor,
    createdAt = createdAt,
)

fun Gig.toEntity(): GigEntity = GigEntity(
    id = id,
    title = title,
    dateIso = dateIso,
    city = city,
    isOutdoor = isOutdoor,
    createdAt = createdAt,
)
