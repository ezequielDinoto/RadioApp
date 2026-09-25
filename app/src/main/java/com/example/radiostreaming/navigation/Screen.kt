package com.example.radiostreaming.navigation

// navigation/Screen.kt


// navigation/Screen.kt (actualizado)

sealed class Screen(val route: String) {
    data object StationList : Screen("station_list")
    data object Favorites : Screen("favorites") // <- nuevo
    data object Player : Screen("player/{stationId}") {
        fun createRoute(stationId: Int) = "player/$stationId"
    }
}