package com.example.radiostreaming.data.repository

// data/repository/StationRepositoryImpl.kt
// data/repository/StationRepositoryImpl.kt

import com.example.radiostreaming.data.local.FavoriteDao
import com.example.radiostreaming.data.local.FavoriteStationEntity
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class StationRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : StationRepository {

    private val fakeStations = listOf(
        Station(1, "El Destape", "Noticiasd",  "https://ipanel.instream.audio/8004/stream"),
        Station(2, "FuturocOk", "Noticias", "https://ipanel.instream.audio:7004/stream"),
        Station(3, "AM530", "Rock", ": \"https://ipanel.instream.audio:7000/stream\""),
        Station(4, "RockAndPop", "Tropical",  "https://27593.live.streamtheworld.com/ROCKANDPOPAAC_SC"),
        Station(5, "La Red", "Jazz", "https://24363.live.streamtheworld.com/LA_RED_AM910AAC_SC"),
        Station(6, "DEL PLATA", "Cumbia",  "https://server7.hostradios.com/8108/stream")
    )

    override fun getStations(): Flow<List<Station>> {
        // "kotlinx.coroutines.flow.flowOf" simula el Flow de la lista fija (mock).
        // Cuando conectemos una API real, acá iría el Flow que venga de esa fuente.
        val stationsFlow = kotlinx.coroutines.flow.flowOf(fakeStations)

        // combine() junta dos Flows: la lista de emisoras + la lista de ids favoritos.
        // Cada vez que CUALQUIERA de los dos cambia, se recalcula el resultado.
        return stationsFlow.combine(favoriteDao.getFavoriteIds()) { stations, favoriteIds ->
            stations.map { station ->
                station.copy(isFavorite = station.id in favoriteIds)
            }
        }
    }

    override suspend fun getStationById(id: Int): Station? {
        val isFav = favoriteDao.isFavorite(id)
        return fakeStations.find { it.id == id }?.copy(isFavorite = isFav)
    }

    override suspend fun toggleFavorite(stationId: Int) {
        if (favoriteDao.isFavorite(stationId)) {
            favoriteDao.removeFavorite(FavoriteStationEntity(stationId))
        } else {
            favoriteDao.addFavorite(FavoriteStationEntity(stationId))
        }
    }
}