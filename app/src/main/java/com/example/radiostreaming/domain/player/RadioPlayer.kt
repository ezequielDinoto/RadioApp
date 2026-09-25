package com.example.radiostreaming.domain.player

// domain/player/RadioPlayer.kt
// domain/player/RadioPlayer.kt

import kotlinx.coroutines.flow.StateFlow

interface RadioPlayer {
    val isPlaying: StateFlow<Boolean>
    val isBuffering: StateFlow<Boolean>
    val playbackError: StateFlow<PlaybackError?> // <- nuevo

    fun play(streamUrl: String)
    fun pause()
    fun setVolume(volume: Float)
    fun release()
}

// Un modelo propio de error, para no exponer excepciones de ExoPlayer hacia arriba
sealed class PlaybackError {
    data object NoInternet : PlaybackError()
    data object StreamUnavailable : PlaybackError()
    data object Unknown : PlaybackError()
}