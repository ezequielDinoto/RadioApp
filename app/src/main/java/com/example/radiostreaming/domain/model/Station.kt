package com.example.radiostreaming.domain.model
// domain/model/Station.kt

data class Station(
    val id: Int,
    val name: String,
    val genre: String,
    val streamUrl: String,
    val isLive: Boolean = true,
    val isFavorite: Boolean = false
)