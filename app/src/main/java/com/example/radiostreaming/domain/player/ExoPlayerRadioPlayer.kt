package com.example.radiostreaming.domain.player

import android.content.ComponentName
import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.radiostreaming.domain.model.Station
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.media.AudioAttributes as FocusAudioAttributes

class ExoPlayerRadioPlayer(context: Context) : RadioPlayer {

    private companion object {
        const val TAG = "RadioPlayer"
        const val MAX_RETRIES = 8
        const val BASE_DELAY_MS = 2_000L
        const val MAX_DELAY_MS = 10_000L
        const val STALL_TIMEOUT_MS = 15_000L
        const val DUCK_FACTOR = 0.2f // volumen bajo cuando suena una notificación
    }

    private val appContext = context.applicationContext

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var retryJob: Job? = null
    private var stallJob: Job? = null
    private var retryCount = 0

    // true = el usuario quiere escuchar; si se corta, reconectamos.
    private var wantsToPlay = false

    // true = otra app nos sacó el audio y estamos esperando recuperarlo
    private var pausedByFocus = false

    private var userVolume = 1f

    // Conexión con el servicio de segundo plano
    private var controllerFuture: ListenableFuture<MediaController>? = null

    // ---------- Foco de audio (manejado a mano) ----------

    private val audioManager =
        appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var focusRequest: AudioFocusRequest? = null // Android 8+

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        onFocusChange(change)
    }

    private fun requestFocus(): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    FocusAudioAttributes.Builder()
                        .setUsage(FocusAudioAttributes.USAGE_MEDIA)
                        .setContentType(FocusAudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener(focusListener)
                .build()
                .also { focusRequest = it }
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        val granted = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        Log.d(TAG, "foco de audio pedido, concedido=$granted")
        return granted
    }

    private fun abandonFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusListener)
        }
    }

    private fun onFocusChange(change: Int) {
        Log.d(TAG, "cambio de foco=$change")
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                player.volume = userVolume
                if (pausedByFocus && wantsToPlay) {
                    // Recuperamos el audio: reconectamos al vivo
                    pausedByFocus = false
                    retryCount = 0
                    _playbackError.value = null
                    val station = _currentStation.value
                    if (station != null) {
                        try {
                            startStream(station)
                        } catch (e: Exception) {
                            Log.e(TAG, "no se pudo reanudar", e)
                        }
                    }
                }
            }

            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (wantsToPlay) {
                    pausedByFocus = true
                    retryJob?.cancel()
                    stallJob?.cancel()
                    player.pause()
                    _isBuffering.value = false
                }
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                player.volume = userVolume * DUCK_FACTOR
            }
        }
    }

    // ---------- Reproductor ----------

    private val player: ExoPlayer = ExoPlayer.Builder(appContext)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            false // el foco de audio lo manejamos nosotros
        )
        .setHandleAudioBecomingNoisy(true) // pausa si se desconectan los auriculares
        .setWakeMode(C.WAKE_MODE_NETWORK)  // mantiene Wi-Fi y CPU despiertos
        .build()

    /** El servicio de segundo plano usa este mismo reproductor. */
    val exoPlayer: ExoPlayer get() = player

    private val _currentStation = MutableStateFlow<Station?>(null)
    override val currentStation: StateFlow<Station?> = _currentStation

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isBuffering = MutableStateFlow(false)
    override val isBuffering: StateFlow<Boolean> = _isBuffering

    private val _playbackError = MutableStateFlow<PlaybackError?>(null)
    override val playbackError: StateFlow<PlaybackError?> = _playbackError

    init {
        player.addListener(object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d(TAG, "isPlaying=$isPlaying")
                // Mientras reintentamos, la app sigue mostrando "reproduciendo"
                if (!isPlaying && retryJob?.isActive == true) return
                _isPlaying.value = isPlaying
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                Log.d(TAG, "playWhenReady=$playWhenReady reason=$reason")
                when {
                    // Auriculares desconectados: no tiene que seguir sonando
                    !playWhenReady &&
                            reason == Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> {
                        wantsToPlay = false
                        pausedByFocus = false
                        retryJob?.cancel()
                        stallJob?.cancel()
                        abandonFocus()
                    }

                    // Pausa desde la notificación o la pantalla de bloqueo
                    !playWhenReady &&
                            reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST &&
                            !pausedByFocus -> {
                        wantsToPlay = false
                        retryJob?.cancel()
                        stallJob?.cancel()
                        abandonFocus()
                    }

                    // Play desde la notificación o la pantalla de bloqueo
                    playWhenReady &&
                            reason == Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST &&
                            (!wantsToPlay || pausedByFocus) -> {
                        wantsToPlay = true
                        pausedByFocus = false
                        retryCount = 0
                        requestFocus()
                    }
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                // 1 = IDLE, 2 = BUFFERING, 3 = READY, 4 = ENDED
                Log.d(TAG, "state=$state")
                stallJob?.cancel()
                when (state) {
                    Player.STATE_BUFFERING -> {
                        _isBuffering.value = true
                        // Si se queda cargando demasiado, lo damos por colgado
                        stallJob = scope.launch {
                            delay(STALL_TIMEOUT_MS)
                            if (wantsToPlay && !pausedByFocus &&
                                player.playbackState == Player.STATE_BUFFERING
                            ) {
                                Log.d(TAG, "stream colgado, reconectando")
                                scheduleRetry()
                            }
                        }
                    }

                    Player.STATE_READY -> {
                        _isBuffering.value = false
                        retryCount = 0
                        _playbackError.value = null
                    }

                    Player.STATE_ENDED -> {
                        // En una radio en vivo, "terminó" = el servidor cortó la conexión
                        if (wantsToPlay && !pausedByFocus) scheduleRetry()
                    }

                    else -> Unit
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "error ${error.errorCodeName} (${error.errorCode})", error)
                val permanent =
                    error.errorCode == PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED
                if (wantsToPlay && !pausedByFocus && !permanent && retryCount < MAX_RETRIES) {
                    scheduleRetry()
                } else {
                    giveUp(error)
                }
            }
        })
    }

    override fun play(station: Station) {
        // Si ya está sonando esta misma emisora, no hacemos nada
        if (_currentStation.value?.id == station.id && player.isPlaying) return

        if (station.streamUrl.isBlank()) {
            _playbackError.value = PlaybackError.StreamUnavailable
            return
        }

        retryJob?.cancel()
        stallJob?.cancel()
        retryCount = 0
        wantsToPlay = true
        pausedByFocus = false
        _playbackError.value = null
        _currentStation.value = station

        ensureServiceRunning()

        // Pedimos el audio; si no nos lo dan, igual intentamos reproducir
        requestFocus()
        player.volume = userVolume

        try {
            startStream(station)
        } catch (e: Exception) {
            Log.e(TAG, "no se pudo iniciar el stream", e)
            _playbackError.value = PlaybackError.Unknown
        }
    }

    override fun pause() {
        wantsToPlay = false
        pausedByFocus = false
        retryJob?.cancel()
        stallJob?.cancel()
        player.pause()
        abandonFocus() // el usuario pausó: soltamos el audio
        _isPlaying.value = false
        _isBuffering.value = false
    }

    override fun setVolume(volume: Float) {
        userVolume = volume.coerceIn(0f, 1f)
        player.volume = userVolume
    }

    override fun release() {
        wantsToPlay = false
        pausedByFocus = false
        retryJob?.cancel()
        stallJob?.cancel()
        abandonFocus()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        scope.cancel()
        player.release()
    }

    // ---------- Internas ----------

    /**
     * Conecta la app con el servicio de segundo plano. Al conectarse, el servicio
     * muestra la notificación y mantiene viva la app mientras suena la radio.
     */
    private fun ensureServiceRunning() {
        try {
            val current = controllerFuture
            if (current != null) {
                val connected = current.isDone &&
                        runCatching { current.get().isConnected }.getOrDefault(false)
                if (!current.isDone || connected) return
                MediaController.releaseFuture(current)
            }
            val token = SessionToken(
                appContext,
                ComponentName(appContext, RadioPlaybackService::class.java)
            )
            controllerFuture = MediaController.Builder(appContext, token).buildAsync()
        } catch (e: Exception) {
            // Si el servicio falla, la radio igual suena (solo sin notificación)
            Log.e(TAG, "no se pudo conectar con el servicio", e)
        }
    }
    private fun startStream(station: Station) {
        val item = MediaItem.Builder()
            .setUri(station.streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtist(station.genre)
                    .build()
            )
            .build()
        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    private fun scheduleRetry() {
        val station = _currentStation.value ?: return

        if (retryCount >= MAX_RETRIES) {
            Log.d(TAG, "intentos agotados")
            wantsToPlay = false
            _isPlaying.value = false
            _isBuffering.value = false
            _playbackError.value = PlaybackError.NoInternet
            return
        }

        retryJob?.cancel()
        stallJob?.cancel()
        retryCount++
        Log.d(TAG, "reintento $retryCount de $MAX_RETRIES")
        _isBuffering.value = true
        _isPlaying.value = true // la UI no "parpadea" a pausa mientras reconecta

        retryJob = scope.launch {
            delay((BASE_DELAY_MS * retryCount).coerceAtMost(MAX_DELAY_MS))
            if (wantsToPlay && !pausedByFocus) {
                try {
                    startStream(station)
                } catch (e: Exception) {
                    Log.e(TAG, "falló el reintento", e)
                    scheduleRetry()
                }
            }
        }
    }

    private fun giveUp(error: PlaybackException) {
        wantsToPlay = false
        _isPlaying.value = false
        _isBuffering.value = false
        _playbackError.value = when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
                PlaybackError.NoInternet

            PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED,
            PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
            PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED ->
                PlaybackError.StreamUnavailable

            else -> PlaybackError.Unknown
        }
    }
}