// navigation/RadioNavGraph.kt
package com.example.radiostreaming.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.radiostreaming.presentation.components.MiniPlayer
import com.example.radiostreaming.presentation.favorite.FavoriteScreen
import com.example.radiostreaming.presentation.player.NowPlayingViewModel
import com.example.radiostreaming.presentation.player.PlayerScreen
import com.example.radiostreaming.presentation.splash.SplashScreen
import com.example.radiostreaming.presentation.stationlist.StationListScreen
@Composable
fun RadioNavGraph(
    onToggleTheme: () -> Unit = {},
    nowPlayingViewModel: NowPlayingViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val isSplash = route == Screen.Splash.route
    val isPlayer = route == Screen.Player.route

    // Estas dos líneas son las que faltaban: de acá sale "nowPlaying"
    val nowPlaying by nowPlayingViewModel.currentStation.collectAsStateWithLifecycle()
    val isPlaying by nowPlayingViewModel.isPlaying.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!isSplash) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val station = nowPlaying
                    if (station != null && !isPlayer) {
                        MiniPlayer(
                            station = station,
                            isPlaying = isPlaying,
                            onToggle = { nowPlayingViewModel.togglePlayPause() },
                            onClick = {
                                navController.navigate(Screen.Player.createRoute(station.id))
                            }
                        )
                    }
                    RadioBottomBar(navController)
                }
            }
        }
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
                    onToggleTheme = onToggleTheme,
                    playingStationId = nowPlaying?.id
                )
            }

            composable(Screen.Favorites.route) {
                FavoriteScreen(
                    onStationClick = { station ->
                        navController.navigate(Screen.Player.createRoute(station.id))
                    },
                    onToggleTheme = onToggleTheme,
                    playingStationId = nowPlaying?.id
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
