// data/repository/StationRepositoryImpl.kt
package com.example.radiostreaming.data.repository

import com.example.radiostreaming.data.local.FavoriteDao
import com.example.radiostreaming.data.local.FavoriteStationEntity
import com.example.radiostreaming.data.remote.RadioApiService
import com.example.radiostreaming.data.remote.toDomain
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.domain.repository.StationFilter
import com.example.radiostreaming.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

import kotlinx.coroutines.flow.map
import javax.inject.Inject

  class StationRepositoryImpl @Inject constructor(
      private val apiService: RadioApiService,
      private val favoriteDao: FavoriteDao,

  ) : StationRepository {

      override fun getStations(): Flow<List<Station>> {
          // flow { } arma un Flow "a mano": pide los datos a la API una vez y los emite.
          val stationsFlow = flow {
              emit(apiService.searchStations())
          }

          return stationsFlow.combine(favoriteDao.getFavoriteIds()) { dtos, favoriteIds ->
              dtos.map { dto -> dto.toDomain(isFavorite = dto.stationUuid in favoriteIds) }
          }
      }

      override fun searchStations(filter: StationFilter): Flow<List<Station>> {
          val stationsFlow = flow {
              emit(
                  apiService.searchStations(
                      name = filter.query.takeIf { it.isNotBlank() },
                      country = filter.country,
                      tag = filter.genre
                  )
              )
          }

          return stationsFlow.combine(favoriteDao.getFavoriteIds()) { dtos, favoriteIds ->
              dtos.map { dto -> dto.toDomain(isFavorite = dto.stationUuid in favoriteIds) }
          }
      }

      override fun getFavoriteStations(): Flow<List<Station>> =
          favoriteDao.getFavorites().map { list ->
              list.map {
                  Station(
                      id = it.stationId,
                      name = it.name,
                      genre = it.genre,
                      streamUrl = it.streamUrl,
                      isLive = it.isLive,
                      isFavorite = true,
                      country = it.country
                  )
              }
          }


      // data/repository/StationRepositoryImpl.kt (reemplazar este método)
      override suspend fun getStationById(id: String): Station? {
          val isFav = favoriteDao.isFavorite(id)
          return apiService.getStationByUuid(id)
              .firstOrNull() // la API devuelve una lista; tomamos el primero si existe
              ?.toDomain(isFavorite = isFav)
      }

      override suspend fun toggleFavorite(station: Station) {
          if (favoriteDao.isFavorite(station.id)) {
              favoriteDao.removeFavorite(station.id)
          } else {
              favoriteDao.addFavorite(
                  FavoriteStationEntity(
                      stationId = station.id,
                      name = station.name,
                      genre = station.genre,
                      streamUrl = station.streamUrl,
                      isLive = station.isLive,
                      country = station.country
                  )
              )
          }
          }

          override suspend fun getAvailableCountries(): List<String> {
              return apiService.getCountries()
                  .filter { it.stationCount > 0 && it.name.isNotBlank() }
                  .map { it.name }
          }

          override suspend fun getAvailableGenres(): List<String> {
              return apiService.getTags()
                  .filter { it.stationCount > 0 && it.name.isNotBlank() }
                  .map { it.name }
          }

      }