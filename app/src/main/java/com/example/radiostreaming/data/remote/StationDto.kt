package com.example.radiostreaming.data.remote

// data/remote/StationDto.kt

// data/remote/StationDto.kt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationDto(
    @SerialName("stationuuid") val stationUuid: String,
    @SerialName("name") val name: String,
    @SerialName("url_resolved") val urlResolved: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("tags") val tags: String? = null,
    @SerialName("lastcheckok") val lastCheckOk: Int? = null // 1 = el stream anda, 0 = está caído
)