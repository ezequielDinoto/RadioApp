package com.example.radiostreaming.presentation.favorite
// presentation/favorites/FavoritesUiState.kt

import com.example.radiostreaming.domain.model.Station

data class FavoriteUiState(
    val isLoading: Boolean = true,
    val favorites: List<Station> = emptyList()
)