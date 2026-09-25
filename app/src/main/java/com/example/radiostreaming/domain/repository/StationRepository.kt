package com.example.radiostreaming.domain.repository
// domain/repository/StationRepository.kt


import com.example.radiostreaming.domain.model.Station
import kotlinx.coroutines.flow.Flow

// Es un "contrato": dice QUÉ se puede hacer, no CÓMO.
interface StationRepository {
    fun getStations(): Flow<List<Station>> // <- ahora es Flow, se actualiza solo
    suspend fun getStationById(id: Int): Station?
    suspend fun toggleFavorite(stationId: Int)
}