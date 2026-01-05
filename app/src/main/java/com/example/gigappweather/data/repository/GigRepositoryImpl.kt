package com.example.gigappweather.data.repository

import com.example.gigappweather.data.local.dao.GigDao
import com.example.gigappweather.data.mapper.toDomain
import com.example.gigappweather.data.mapper.toEntity
import com.example.gigappweather.domain.model.Gig
import com.example.gigappweather.domain.repository.GigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GigRepositoryImpl(
    private val gigDao: GigDao,
) : GigRepository {

    override fun getAll(): Flow<List<Gig>> = gigDao
        .getAll()
        .map { entities -> entities.map { it.toDomain() } }

    override suspend fun insert(gig: Gig): Long = gigDao.insert(gig.toEntity())

    override suspend fun deleteById(id: Long): Int = gigDao.deleteById(id)
}
