package com.example.radiostreaming.data.local
// data/local/FavoriteDao.kt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteStationEntity)

    @Query("DELETE FROM favorite_stations WHERE stationId = :stationId")
    suspend fun removeFavorite(stationId: String)

    @Query("SELECT stationId FROM favorite_stations")
    fun getFavoriteIds(): Flow<List<String>>

    @Query("SELECT * FROM favorite_stations")
    fun getFavorites(): Flow<List<FavoriteStationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE stationId = :stationId)")
    suspend fun isFavorite(stationId: String): Boolean
}