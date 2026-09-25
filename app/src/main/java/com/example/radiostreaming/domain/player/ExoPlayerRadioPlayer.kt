package com.example.radiostreaming.domain.player

// data/player/ExoPlayerRadioPlayer.kt

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.radiostreaming.domain.player.PlaybackError
import com.example.radiostreaming.domain.player.RadioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ExoPlayerRadioPlayer @Inject constructor(
    context: Context
) : RadioPlayer {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

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
                // Acá "traducimos" el error técnico a algo que el ViewModel entiende
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

            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
            PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> PlaybackError.StreamUnavailable

            else -> PlaybackError.Unknown
        }
    }

    override fun play(streamUrl: String) {
        _playbackError.value = null // limpiamos el error anterior al intentar de nuevo
        val currentUrl = exoPlayer.currentMediaItem?.localConfiguration?.uri?.toString()
        if (currentUrl != streamUrl) {
            val mediaItem = MediaItem.fromUri(streamUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
        exoPlayer.play()
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