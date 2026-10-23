// presentation/player/PlayerViewModel.kt
package com.example.radiostreaming.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostreaming.domain.player.RadioPlayer
import com.example.radiostreaming.domain.usecase.GetStationByIdUseCase
import com.example.radiostreaming.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getStationByIdUseCase: GetStationByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val radioPlayer: RadioPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        val stationId: String = savedStateHandle.get<String>("stationId") ?: ""
        loadStation(stationId)
        observePlayerState()
    }

    private fun loadStation(id: String) {
        viewModelScope.launch {
            val station = getStationByIdUseCase(id)
            _uiState.value = _uiState.value.copy(station = station, isLoading = false)
            radioPlayer.setVolume(_uiState.value.volume)
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            radioPlayer.isPlaying.collect { playing ->
                _uiState.value = _uiState.value.copy(isPlaying = playing)
            }
        }
        viewModelScope.launch {
            radioPlayer.isBuffering.collect { buffering ->
                _uiState.value = _uiState.value.copy(isBuffering = buffering)
            }
        }
        viewModelScope.launch {
            radioPlayer.playbackError.collect { error ->
                _uiState.value = _uiState.value.copy(playbackError = error)
            }
        }
    }

    fun togglePlayPause() {
        val station = _uiState.value.station ?: return
        if (_uiState.value.isPlaying) radioPlayer.pause() else radioPlayer.play(station.streamUrl)
    }

    fun retry() {
        val station = _uiState.value.station ?: return
        radioPlayer.play(station.streamUrl)
    }

    fun onFavoriteClick() {
        val station = _uiState.value.station ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(station)
            _uiState.value = _uiState.value.copy(
                station = station.copy(isFavorite = !station.isFavorite)
            )
        }
    }

    fun setVolume(newVolume: Float) {
        _uiState.value = _uiState.value.copy(volume = newVolume)
        radioPlayer.setVolume(newVolume)
    }

    /*override fun onCleared() {
        super.onCleared()
        radioPlayer.pause()
    }*/
}