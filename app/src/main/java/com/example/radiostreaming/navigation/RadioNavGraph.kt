package com.example.radiostreaming.navigation

// navigation/RadioNavGraph.kt

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.radiostreaming.presentation.favorite.FavoriteScreen
import com.example.radiostreaming.presentation.player.PlayerScreen
import com.example.radiostreaming.presentation.stationlist.StationListScreen
import com.example.radiostreaming.ui.screens.stationlist.mockStations

@Composable
fun RadioNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.StationList.route
    ) {
        composable(Screen.StationList.route) {
            StationListScreen(
                stations = mockStations,
                onStationClick = { station ->
                    navController.navigate(Screen.Player.createRoute(station.id))
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("stationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getInt("stationId") ?: 0
            val station = mockStations.find { it.id == stationId }

            PlayerScreen(
                stationName = station?.name ?: "Emisora desconocida"

            )
        }


        // navigation/RadioNavGraph.kt (agregado dentro del NavHost)
        composable(Screen.Favorites.route) {
            FavoriteScreen(
                onStationClick = { station ->
                    navController.navigate(Screen.Player.createRoute(station.id))
                }
            )
        }

    }
}

// navigation/RadioNavGraph.kt (actualizar el composable de StationList)
