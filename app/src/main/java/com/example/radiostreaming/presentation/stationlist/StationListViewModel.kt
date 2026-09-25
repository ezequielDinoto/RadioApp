package com.example.radiostreaming.presentation.stationlist
// presentation/stationlist/StationListViewModel.kt
// presentation/stationlist/StationListViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostreaming.domain.usecase.GetStationsUseCase
import com.example.radiostreaming.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class StationListViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationListUiState())
    val uiState: StateFlow<StationListUiState> = _uiState.asStateFlow()

    init {
        observeStations()
    }

    fun retry() = observeStations()

    fun onFavoriteClick(stationId: Int) {
        viewModelScope.launch {
            toggleFavoriteUseCase(stationId)
            // No hace falta recargar nada a mano: el Flow de getStations()
            // se actualiza solo apenas cambia la tabla de favoritos.
        }
    }

    private fun observeStations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            getStationsUseCase()
                .onEach { stations ->
                    _uiState.value = _uiState.value.copy(isLoading = false, stations = stations)
                }
                .catch { e ->
                    val message = if (e is IOException) {
                        "Sin conexión a internet. Revisá tu red."
                    } else {
                        "No se pudieron cargar las emisoras."
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
                }
                //.collect()
                .launchIn(viewModelScope)
            //launchIn(), que combina el lanzamiento de la corrutina y la recolección en un solo paso:
        }
    }
}