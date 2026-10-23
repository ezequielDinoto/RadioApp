/// domain/repository/StationRepository.kt
package com.example.radiostreaming.domain.repository

import com.example.radiostreaming.domain.model.Station
import kotlinx.coroutines.flow.Flow

data class StationFilter(
    val query: String = "",
    val country: String? = null,
    val genre: String? = null
)

interface StationRepository {
    fun getStations(): Flow<List<Station>>
    fun searchStations(filter: StationFilter): Flow<List<Station>>
    fun getFavoriteStations(): Flow<List<Station>>
    suspend fun getStationById(id: String): Station?
    suspend fun toggleFavorite(station: Station)
    suspend fun getAvailableCountries(): List<String>
    suspend fun getAvailableGenres(): List<String>
}