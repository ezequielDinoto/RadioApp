package com.example.radiostreaming.domain.usecase

// domain/usecase/ToggleFavoriteUseCase.kt

import com.example.radiostreaming.domain.repository.StationRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: StationRepository
) {
    suspend operator fun invoke(stationId: Int) = repository.toggleFavorite(stationId)
}