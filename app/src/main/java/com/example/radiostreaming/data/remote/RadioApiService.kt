// data/remote/RadioApiService.kt
package com.example.radiostreaming.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadioApiService {

    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("country") country: String? = null,
        @Query("tag") tag: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("hidebroken") hideBroken: Boolean = true,
        @Query("order") order: String = "clickcount",
        @Query("reverse") reverse: Boolean = true
    ): List<StationDto>



    @GET("json/stations/byuuid/{uuid}") // <- nuevo: busca una emisora puntual por su id
    suspend fun getStationByUuid(
        @Path("uuid") uuid: String
    ): List<StationDto> // la API devuelve una lista igual, aunque sea de un solo elemento

    @GET("json/countries")
    suspend fun getCountries(
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true
    ): List<CountryDto>

    @GET("json/tags")
    suspend fun getTags(
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true,
        @Query("limit") limit: Int = 60
    ): List<TagDto>
}