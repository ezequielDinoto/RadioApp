package com.example.radiostreaming.data.local
// data/local/FavoriteDao.kt

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert
    suspend fun addFavorite(favorite: FavoriteStationEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoriteStationEntity)

    // Flow = se actualiza solo cada vez que la tabla cambia, sin pedirlo a mano
    @Query("SELECT stationId FROM favorite_stations")
    fun getFavoriteIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE stationId = :stationId)")
    suspend fun isFavorite(stationId: Int): Boolean
}