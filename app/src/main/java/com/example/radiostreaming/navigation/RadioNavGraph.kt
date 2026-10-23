// navigation/RadioNavGraph.kt
package com.example.radiostreaming.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.radiostreaming.presentation.favorites.FavoriteScreen
import com.example.radiostreaming.presentation.player.PlayerScreen
import com.example.radiostreaming.presentation.splash.SplashScreen
import com.example.radiostreaming.presentation.stationlist.StationListScreen

@Composable
fun RadioNavGraph(onToggleTheme: () -> Unit = {}) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute == Screen.StationList.route ||
            currentRoute == Screen.Favorites.route
    val isSplash = backStackEntry?.destination?.route == Screen.Splash.route





    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { if (showBottomBar) RadioBottomBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Splash.route) {
                SplashScreen(
                    onFinished = {
                        navController.navigate(Screen.StationList.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.StationList.route) {
                StationListScreen(
                    onStationClick = { station ->
                        navController.navigate(Screen.Player.createRoute(station.id))
                    },
                    onToggleTheme = onToggleTheme
                )
            }

            composable(Screen.Favorites.route) {
                FavoriteScreen(
                    onStationClick = { station ->
                        navController.navigate(Screen.Player.createRoute(station.id))
                    },
                    onToggleTheme = onToggleTheme   // <- 3) a Favoritos

                )
            }
            composable(
                route = Screen.Player.route,
                arguments = listOf(navArgument("stationId") { type = NavType.StringType })
            ) {
                PlayerScreen(
                    onBackClick = { navController.popBackStack() },
                            onToggleTheme = onToggleTheme

                )
            }
        }
    }
}
// navigation/RadioNavGraph.kt (actualizar el composable de StationList)
