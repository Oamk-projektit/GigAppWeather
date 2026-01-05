package com.example.gigappweather.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gigappweather.data.local.entity.GigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GigDao {
    @Query("SELECT * FROM gigs ORDER BY createdAt DESC")
    fun getAll(): Flow<List<GigEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(gig: GigEntity): Long

    @Query("DELETE FROM gigs WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
