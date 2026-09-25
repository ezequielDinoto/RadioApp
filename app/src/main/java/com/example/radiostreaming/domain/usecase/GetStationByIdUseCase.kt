// domain/usecase/GetStationByIdUseCase.kt
package com.example.radiostreaming.domain.usecase

import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.repository.StationRepository
import javax.inject.Inject

class GetStationByIdUseCase @Inject constructor(
    private val repository: StationRepository
) {
    suspend operator fun invoke(id: Int): Station? = repository.getStationById(id)
}