package com.example.radiostreaming.domain.player

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RadioPlaybackService : MediaSessionService() {

    @Inject
    lateinit var radioPlayer: ExoPlayerRadioPlayer

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, radioPlayer.exoPlayer).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        // Solo cerramos la sesión; el reproductor es de la app y no se destruye acá
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }
}