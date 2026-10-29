package com.example.radiostreaming.presentation.player

import androidx.lifecycle.ViewModel
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.player.RadioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val radioPlayer: RadioPlayer
) : ViewModel() {

    val currentStation: StateFlow<Station?> = radioPlayer.currentStation

    val isPlaying: StateFlow<Boolean> = radioPlayer.isPlaying

    fun togglePlayPause() {
        val station = currentStation.value ?: return
        if (isPlaying.value) radioPlayer.pause() else radioPlayer.play(station)
    }
}