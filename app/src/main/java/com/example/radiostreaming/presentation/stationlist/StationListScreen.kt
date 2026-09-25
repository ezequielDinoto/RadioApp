package com.example.radiostreaming.presentation.stationlist
// presentation/stationlist/StationListScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.ui.theme.YellowPrimary
import com.example.radiostreaming.presentation.components.StationItem


// presentation/stationlist/StationListScreen.kt (mismo cambio)
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationListScreen(
    viewModel: StationListViewModel = hiltViewModel(),
    onStationClick: (Station) -> Unit = {},
    onFavoritesClick: () -> Unit = {}, // <- nuevo
    stations: List<com.example.radiostreaming.ui.screens.stationlist.Station>

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // ... el resto queda igual

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = YellowPrimary)
            }
        }

        // presentation/stationlist/StationListScreen.kt (reemplaza el bloque de error existente)
        uiState.errorMessage != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color(0xFFFF6B6B),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    TextButton(onClick = { viewModel.retry() }) {
                        Text("Reintentar", color = YellowPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        else -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = { Text("Emisoras", fontWeight = FontWeight.Bold) },
                        actions = {
                            IconButton(onClick = onFavoritesClick) { // <- nuevo parámetro de la función
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Ver favoritos",
                                    tint = YellowPrimary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        )
                    )

                }


                    ){ padding ->

                        if (uiState.stations.isEmpty()) {
                            // Caso borde: no hubo error, pero tampoco hay emisoras
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay emisoras disponibles",
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding)
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                items(uiState.stations) { station ->
                                    StationItem(
                                        station = station,
                                        onClick = { onStationClick(station) },
                                        onFavoriteClick = { viewModel.onFavoriteClick(station.id) } // <- nuevo

                                    )
                                }
                            }
                        }
                    }

        }
        }

    }

