package com.example.radiostreaming.domain.usecase
// domain/usecase/GetStationsUseCase.kt

import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStationsUseCase @Inject constructor(
    private val repository: StationRepository
) {
    operator fun invoke(): Flow<List<Station>> = repository.getStations()
}