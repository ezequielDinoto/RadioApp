// data/remote/CountryDto.kt
package com.example.radiostreaming.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    @SerialName("name") val name: String,
    @SerialName("stationcount") val stationCount: Int = 0
)