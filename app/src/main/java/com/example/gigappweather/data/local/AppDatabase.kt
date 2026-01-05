package com.example.gigappweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gigappweather.data.local.dao.GigDao
import com.example.gigappweather.data.local.entity.GigEntity

@Database(
    entities = [GigEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gigDao(): GigDao

    companion object {
        const val DB_NAME: String = "keikkadiili.db"
    }
}
