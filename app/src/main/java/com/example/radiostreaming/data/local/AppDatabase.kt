package com.example.radiostreaming.data.local

// data/local/AppDatabase.kt

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteStationEntity::class], version = 3,
    exportSchema = false  // 👈 agregá esto
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}