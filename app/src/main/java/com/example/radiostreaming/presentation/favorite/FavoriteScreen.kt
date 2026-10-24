// presentation/favorites/FavoritesScreen.kt
package com.example.radiostreaming.presentation.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.presentation.components.ScreenHeader
import com.example.radiostreaming.presentation.components.StationItem
import com.example.radiostreaming.ui.theme.RadiostreamingTheme

// Versión "conectada": busca el ViewModel y se lo pasa a la que dibuja.
@Composable
fun FavoriteScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onStationClick: (Station) -> Unit = {},
    onToggleTheme: () -> Unit = {},
    playingStationId: String? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesContent(
        uiState = uiState,
        onStationClick = onStationClick,
        onFavoriteClick = { station -> viewModel.onFavoriteClick(station) },
        onToggleTheme = onToggleTheme,
        playingStationId = playingStationId
    )
}

// Versión "que solo dibuja": no conoce el ViewModel, por eso se puede previsualizar.
@Composable
fun FavoritesContent(
    uiState: FavoriteUiState,
    onStationClick: (Station) -> Unit,
    onFavoriteClick: (Station) -> Unit,
    onToggleTheme: () -> Unit = {},
    playingStationId: String? = null
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(containerColor = colors.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScreenHeader(
                title = "Favoritos",
                subtitle = if (!uiState.isLoading) "${uiState.favorites.size} guardadas" else null,
                onToggleTheme = onToggleTheme
            )

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                }

                uiState.favorites.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = colors.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Todavía no marcaste ninguna emisora como favorita",
                                color = colors.onSurfaceVariant,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(uiState.favorites, key = { it.id }) { station ->
                            StationItem(
                                station = station,
                                isPlaying = station.id == playingStationId,
                                onClick = { onStationClick(station) },
                                onFavoriteClick = { onFavoriteClick(station) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------- PREVIEWS ----------

private val previewStations = listOf(
    Station("1", "France Info", "Noticias", "https://example.com/1", isFavorite = true, country = "Francia"),
    Station("2", "RTL", "Généraliste", "https://example.com/2", isFavorite = true, country = "Francia"),
    Station("3", "Jazz Café", "Jazz", "https://example.com/3", isLive = false, isFavorite = true, country = "Argentina")
)

@Preview(showBackground = true, name = "Con favoritos")
@Composable
private fun FavoritesContentPreview() {
    RadiostreamingTheme {
        FavoritesContent(
            uiState = FavoriteUiState(isLoading = false, favorites = previewStations),
            onStationClick = {},
            onFavoriteClick = {},
            playingStationId = "1"
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