package com.example.radiostreaming.domain.usecase

// domain/usecase/GetFavoriteStationsUseCase.kt


import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteStationsUseCase @Inject constructor(
    private val repository: StationRepository
) {
    operator fun invoke(): Flow<List<Station>> =
        repository.getStations().map { stations -> stations.filter { it.isFavorite } }
}