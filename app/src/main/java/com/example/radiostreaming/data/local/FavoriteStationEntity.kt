package com.example.radiostreaming.data.local

// data/local/FavoriteStationEntity.kt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stations")
data class FavoriteStationEntity(
    @PrimaryKey val stationId: Int
)