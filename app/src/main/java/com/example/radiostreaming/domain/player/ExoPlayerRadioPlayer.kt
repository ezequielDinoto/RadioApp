// ExoPlayerRadioPlayer.kt
// IMPORTANTE: dejá la primera línea "package ..." que ya tenga tu archivo
// (puede ser data.player o domain.player, según dónde lo tengas).
package com.example.radiostreaming.domain.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.radiostreaming.domain.model.Station
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExoPlayerRadioPlayer(context: Context) : RadioPlayer {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    // NUEVO: guarda cuál es la emisora que está sonando
    private val _currentStation = MutableStateFlow<Station?>(null)
    override val currentStation: StateFlow<Station?> = _currentStation.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isBuffering = MutableStateFlow(false)
    override val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _playbackError = MutableStateFlow<PlaybackError?>(null)
    override val playbackError: StateFlow<PlaybackError?> = _playbackError.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                _isPlaying.value = isPlayingNow
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _isBuffering.value = playbackState == Player.STATE_BUFFERING
            }

            override fun onPlayerError(error: PlaybackException) {
                _isBuffering.value = false
                _isPlaying.value = false
                _playbackError.value = mapError(error)
            }
        })
    }

    private fun mapError(error: PlaybackException): PlaybackError {
        return when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> PlaybackError.NoInternet

            PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED,
            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
            PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> PlaybackError.StreamUnavailable

            else -> PlaybackError.Unknown
        }
    }

    // CAMBIADO: antes recibía solo la URL (String), ahora recibe la emisora completa
    override fun play(station: Station) {
        _playbackError.value = null

        if (station.streamUrl.isBlank()) {
            _playbackError.value = PlaybackError.StreamUnavailable
            return
        }

        try {
            val currentUrl = exoPlayer.currentMediaItem?.localConfiguration?.uri?.toString()
            if (currentUrl != station.streamUrl) {
                exoPlayer.setMediaItem(MediaItem.fromUri(station.streamUrl))
                exoPlayer.prepare()
            }
            _currentStation.value = station   // NUEVO: anotamos cuál suena
            exoPlayer.play()
        } catch (e: Exception) {
            android.util.Log.e("ExoPlayerRadio", "Error al reproducir ${station.streamUrl}", e)
            _playbackError.value = PlaybackError.Unknown
        }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume.coerceIn(0f, 1f)
    }

    override fun release() {
        exoPlayer.release()
    }
}