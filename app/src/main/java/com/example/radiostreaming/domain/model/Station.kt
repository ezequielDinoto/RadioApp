// domain/model/Station.kt
package com.example.radiostreaming.domain.model

data class Station(
    val id: String, // antes era Int
    val name: String,
    val genre: String,
    val streamUrl: String,
    val isLive: Boolean = true,
    val isFavorite: Boolean = false,
    val country: String = " "
)
