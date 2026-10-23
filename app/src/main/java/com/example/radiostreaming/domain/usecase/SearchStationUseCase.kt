// domain/usecase/SearchStationsUseCase.kt (actualizado)
package com.example.radiostreaming.domain.usecase

import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.repository.StationFilter
import com.example.radiostreaming.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchStationsUseCase @Inject constructor(
    private val repository: StationRepository
) {
    operator fun invoke(filter: StationFilter): Flow<List<Station>> = repository.searchStations(filter)
}