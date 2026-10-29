package com.example.radiostreaming.di

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
    @Singleton
    fun provideExoPlayerRadioPlayer(
        @ApplicationContext context: Context
    ): ExoPlayerRadioPlayer = ExoPlayerRadioPlayer(context)

    @Provides
    @Singleton
    fun provideRadioPlayer(impl: ExoPlayerRadioPlayer): RadioPlayer = impl
}