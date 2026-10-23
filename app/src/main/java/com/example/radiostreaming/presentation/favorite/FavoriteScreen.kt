// presentation/favorites/FavoritesScreen.kt
package com.example.radiostreaming.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.presentation.components.StationItem
import com.example.radiostreaming.presentation.favorite.FavoriteUiState
import com.example.radiostreaming.presentation.favorite.FavoritesViewModel
import com.example.radiostreaming.ui.theme.RadiostreamingTheme

// 1) Versión "conectada": obtiene el ViewModel y se lo pasa a la que dibuja.
//    Esta es la que se llama desde RadioNavGraph.
@Composable
fun FavoriteScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onStationClick: (Station) -> Unit = {},
    onToggleTheme: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesContent(
        uiState = uiState,
        onStationClick = onStationClick,
        onFavoriteClick = { station -> viewModel.onFavoriteClick(station) },
        onToggleTheme = onToggleTheme
    )
}

// 2) Versión "que solo dibuja": no conoce el ViewModel, por eso se puede previsualizar.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    uiState: FavoriteUiState,
    onStationClick: (Station) -> Unit,
    onFavoriteClick: (Station) -> Unit,
    onToggleTheme: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Favoritos", fontWeight = FontWeight.Bold) },
                actions = {
                    // Botón para cambiar entre modo azul y modo amarillo
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Cambiar de modo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.favorites.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Todavía no marcaste ninguna emisora como favorita",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(uiState.favorites, key = { it.id }) { station ->
                        StationItem(
                            station = station,
                            onClick = { onStationClick(station) },
                            onFavoriteClick = { onFavoriteClick(station) }
                        )
                    }
                }
            }
        }
    }
}

// ---------- PREVIEWS ----------

private val previewStations = listOf(
    Station("1", "Radio Amanecer", "Pop", "https://example.com/1", isFavorite = true),
    Station("2", "Rock 101", "Rock", "https://example.com/2", isFavorite = true),
    Station("3", "Jazz Café", "Jazz", "https://example.com/3", isLive = false, isFavorite = true)
)

@Preview(showBackground = true, name = "Con favoritos")
@Composable
private fun FavoritesContentPreview() {
    RadiostreamingTheme {
        FavoritesContent(
            uiState = FavoriteUiState(isLoading = false, favorites = previewStations),
            onStationClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Sin favoritos")
@Composable
private fun FavoritesEmptyPreview() {
    RadiostreamingTheme {
        FavoritesContent(
            uiState = FavoriteUiState(isLoading = false, favorites = emptyList()),
            onStationClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Cargando")
@Composable
private fun FavoritesLoadingPreview() {
    RadiostreamingTheme {
        FavoritesContent(
            uiState = FavoriteUiState(isLoading = true),
            onStationClick = {},
            onFavoriteClick = {}
        )
    }
}