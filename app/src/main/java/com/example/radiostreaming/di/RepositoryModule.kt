package com.example.radiostreaming.di

// di/RepositoryModule.kt

import com.example.radiostreaming.data.repository.StationRepositoryImpl
import com.example.radiostreaming.domain.repository.StationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class) // vive mientras viva la app
abstract class RepositoryModule {

    @Binds
    abstract fun bindStationRepository(
        impl: StationRepositoryImpl
    ): StationRepository
}