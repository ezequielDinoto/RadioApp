package com.example.radiostreaming.data.remote

// data/remote/StationMapper.kt

import com.example.radiostreaming.domain.model.Station
import java.util.Locale

fun StationDto.toDomain(isFavorite: Boolean): Station {
    return Station(
        id = stationUuid,
        name = name,
        genre = tags?.split(",")?.firstOrNull()?.trim()?.replaceFirstChar { it.uppercase() } ?: "Radio",
        streamUrl = urlResolved?.takeIf { it.isNotBlank() } ?: url.orEmpty(),
        isLive = lastCheckOk == 1,
        isFavorite = isFavorite,
        country = countryCode
            ?.takeIf { it.length == 2 }
            ?.let { Locale.Builder().setRegion(it).build().getDisplayCountry(Locale.forLanguageTag("es")) }
            ?: country.orEmpty(),
    )
}



















