// data/remote/TagDto.kt
package com.example.radiostreaming.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagDto(
    @SerialName("name") val name: String,
    @SerialName("stationcount") val stationCount: Int = 0
)
