package com.example.radiostreaming.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Radio
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String? = null,
    val icon: ImageVector? = null
) {
    data object Splash:Screen("splash")
    data object StationList : Screen("station_list", "Emisoras", Icons.Filled.Radio)
    data object Favorites : Screen("favorites", "Favoritos", Icons.Filled.Favorite)
    data object Player : Screen("player/{stationId}") {
        fun createRoute(stationId: String) = "player/$stationId"
    }

    companion object {
        // "get() =" arma la lista cada vez que se pide, cuando los objetos ya existen
        val bottomBarScreens: List<Screen>
            get() = listOf(StationList, Favorites)
    }
}