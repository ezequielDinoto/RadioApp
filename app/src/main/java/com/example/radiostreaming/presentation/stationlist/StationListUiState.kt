package com.example.radiostreaming.presentation.stationlist

// presentation/stationlist/StationListUiState.kt

import com.example.radiostreaming.domain.model.Station

data class StationListUiState(
    val isLoading: Boolean = true,
    val stations: List<Station> = emptyList(),
    val errorMessage: String? = null
)