package com.example.radiostreaming.presentation.player
// presentation/player/PlayerUiState.kt

// presentation/player/PlayerUiState.kt

import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.player.PlaybackError

data class PlayerUiState(
    val station: Station? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val playbackError: PlaybackError? = null, // <- nuevo
    val volume: Float = 0.7f,
    val isLoading: Boolean = true
)