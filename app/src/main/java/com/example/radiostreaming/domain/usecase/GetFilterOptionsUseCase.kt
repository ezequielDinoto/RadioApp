// domain/usecase/GetFilterOptionsUseCase.kt
package com.example.radiostreaming.domain.usecase

import com.example.radiostreaming.domain.repository.StationRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

data class FilterOptions(
    val countries: List<String>,
    val genres: List<String>
)

class GetFilterOptionsUseCase @Inject constructor(
    private val repository: StationRepository
) {
    suspend operator fun invoke(): FilterOptions {
        // Pedimos los dos al mismo tiempo, no uno después del otro
        return coroutineScope {
            val countriesDeferred = async { repository.getAvailableCountries() }
            val genresDeferred = async { repository.getAvailableGenres() }
            FilterOptions(countriesDeferred.await(), genresDeferred.await())
        }
    }
}