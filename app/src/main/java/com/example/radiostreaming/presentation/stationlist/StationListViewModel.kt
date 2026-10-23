// presentation/stationlist/StationListViewModel.kt
package com.example.radiostreaming.presentation.stationlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.repository.StationFilter
import com.example.radiostreaming.domain.usecase.GetFilterOptionsUseCase
import com.example.radiostreaming.domain.usecase.GetStationsUseCase
import com.example.radiostreaming.domain.usecase.SearchStationsUseCase
import com.example.radiostreaming.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class StationListViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val searchStationsUseCase: SearchStationsUseCase,
    private val getFilterOptionsUseCase: GetFilterOptionsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StationListUiState())
    val uiState: StateFlow<StationListUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")
    private val countryFlow = MutableStateFlow<String?>(null)
    private val genreFlow = MutableStateFlow<String?>(null)

    init {
        observeSearch()
        loadFilterOptions()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchQueryFlow.value = query
    }

    fun onCountrySelected(country: String?) {
        _uiState.value = _uiState.value.copy(selectedCountry = country)
        countryFlow.value = country
    }

    fun onGenreSelected(genre: String?) {
        _uiState.value = _uiState.value.copy(selectedGenre = genre)
        genreFlow.value = genre
    }

    fun clearFilters() {
        onCountrySelected(null)
        onGenreSelected(null)
    }

    fun retry() {
        searchQueryFlow.value = searchQueryFlow.value
    }

    fun onFavoriteClick(station: Station) {
        viewModelScope.launch {
            toggleFavoriteUseCase(station)
        }
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            try {
                val options = getFilterOptionsUseCase()
                _uiState.value = _uiState.value.copy(
                    availableCountries = options.countries,
                    availableGenres = options.genres
                )
            } catch (e: Exception) {
                // Si fallan los filtros, no rompemos la pantalla: simplemente quedan vacíos
            }
        }
    }

    private fun observeSearch() {

        // combine junta los 3 Flows en una sola "foto" cada vez que CUALQUIERA cambia
        combine(
            searchQueryFlow.debounce(400),
            countryFlow,
            genreFlow
        ) { query, country, genre ->
            StationFilter(query = query, country = country, genre = genre)
        }
            .distinctUntilChanged()
            .flatMapLatest { filter ->
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                val isDefault =
                    filter.query.isBlank() && filter.country == null && filter.genre == null
                if (isDefault) {
                    getStationsUseCase()
                } else {
                    searchStationsUseCase(filter)
                }
            }
            .onEach { stations ->
                _uiState.value = _uiState.value.copy(isLoading = false, stations = stations)
            }
            .catch { e ->

                android.util.Log.e("StationListVM", "Error cargando emisoras", e) // <- nuevo, imprime el error completo

                val message = if (e is IOException) {
                    "Sin conexión a internet. Revisá tu red."
                } else {
                    "No se pudieron cargar las emisoras."
                }
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
            }
            .launchIn(viewModelScope)

        }

}