package com.example.gigappweather.domain.repository

import com.example.gigappweather.domain.model.Gig
import kotlinx.coroutines.flow.Flow

interface GigRepository {
    fun getAll(): Flow<List<Gig>>
    suspend fun insert(gig: Gig): Long
    suspend fun deleteById(id: Long): Int
}
