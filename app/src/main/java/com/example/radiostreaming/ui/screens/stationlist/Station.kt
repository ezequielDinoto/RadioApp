package com.example.radiostreaming.ui.screens.stationlist

// ui/screens/stationlist/Station.kt

data class Station(
    val id: Int,
    val name: String,
    val genre: String,
    val isLive: Boolean = true
)

// Datos de prueba (mock) solo para ver la pantalla funcionando
val mockStations = listOf(
    Station(1, "Radio Amanecer", "Pop"),
    Station(2, "FM Nacional", "Noticias"),
    Station(3, "Rock 101", "Rock"),
    Station(4, "Onda Tropical", "Tropical"),
    Station(5, "Jazz Café", "Jazz"),
    Station(6, "Cumbia Total", "Cumbia")
)