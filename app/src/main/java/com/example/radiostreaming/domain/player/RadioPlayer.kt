package com.example.radiostreaming.domain.player

// domain/player/RadioPlayer.kt
// domain/player/RadioPlayer.kt

import com.example.radiostreaming.domain.model.Station
import kotlinx.coroutines.flow.StateFlow

interface RadioPlayer {
    val currentStation: StateFlow<Station?>      // NUEVO: la emisora que suena
    val isPlaying: StateFlow<Boolean>
    val isBuffering: StateFlow<Boolean>
    val playbackError: StateFlow<PlaybackError?>

    fun play(station: Station)                   // CAMBIADO: antes era play(streamUrl: String)
    fun pause()
    fun setVolume(volume: Float)
    fun release()
}

// Errores propios, para no exponer excepciones de ExoPlayer hacia arriba
sealed class PlaybackError {
    object NoInternet : PlaybackError()
    object StreamUnavailable : PlaybackError()
    object Unknown : PlaybackError()
}