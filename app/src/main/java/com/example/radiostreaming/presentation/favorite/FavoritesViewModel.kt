package com.example.radiostreaming.presentation.favorite

// presentation/favorites/FavoritesViewModel.kt


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostreaming.domain.usecase.GetFavoriteStationsUseCase
import com.example.radiostreaming.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getFavoriteStationsUseCase()
                .onEach { favorites ->
                    _uiState.value = _uiState.value.copy(isLoading = false, favorites = favorites)
                }
                .launchIn(viewModelScope)
            //launchIn(), que combina el lanzamiento de la corrutina y la recolección en un solo paso:
        }
    }

    fun onFavoriteClick(stationId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(stationId)
            // Si se desmarca acá, el Flow filtra de nuevo y la emisora
            // desaparece sola de esta lista.
        }
    }
}

