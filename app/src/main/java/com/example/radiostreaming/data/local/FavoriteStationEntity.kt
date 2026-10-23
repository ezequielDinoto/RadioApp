package com.example.radiostreaming.data.local

// data/local/FavoriteStationEntity.kt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stations")
data class FavoriteStationEntity(
    @PrimaryKey val stationId: String,
    val name: String,
    val genre: String,
    val streamUrl: String,
    val isLive: Boolean
)