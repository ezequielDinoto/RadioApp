// presentation/stationlist/StationListUiState.kt
package com.example.radiostreaming.presentation.stationlist

import com.example.radiostreaming.domain.model.Station

data class StationListUiState(
    val isLoading: Boolean = true,
    val stations: List<Station> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCountry: String? = null,
    val selectedGenre: String? = null,
    val availableCountries: List<String> = emptyList(),
    val availableGenres: List<String> = emptyList()
)