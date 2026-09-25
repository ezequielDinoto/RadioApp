package com.example.radiostreaming.di

// di/PlayerModule.kt

import android.content.Context
import com.example.radiostreaming.domain.player.ExoPlayerRadioPlayer
import com.example.radiostreaming.domain.player.RadioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton // una sola instancia para toda la app (el reproductor es compartido)
    fun provideRadioPlayer(@ApplicationContext context: Context): RadioPlayer {
        return ExoPlayerRadioPlayer(context)
    }
}