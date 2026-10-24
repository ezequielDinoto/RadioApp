// presentation/stationlist/StationListScreen.kt
package com.example.radiostreaming.presentation.stationlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.radiostreaming.domain.model.Station
import com.example.radiostreaming.presentation.components.FilterDropdown
import com.example.radiostreaming.presentation.components.ScreenHeader
import com.example.radiostreaming.presentation.components.StationItem


@Composable
fun StationListScreen(
    viewModel: StationListViewModel = hiltViewModel(),
    onStationClick: (Station) -> Unit = {},
    onToggleTheme: () -> Unit = {},
    playingStationId: String? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    Scaffold(containerColor = colors.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScreenHeader(
                title = "Emisoras",
                subtitle = if (!uiState.isLoading && uiState.errorMessage == null)
                    "${uiState.stations.size} disponibles" else null,
                onToggleTheme = onToggleTheme
            )

            // Buscador en píldora
            TextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Buscar emisora...", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = colors.primary)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar búsqueda", tint = colors.onSurface)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface,
                    focusedPlaceholderColor = colors.onSurfaceVariant,
                    unfocusedPlaceholderColor = colors.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filtros en píldora
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterDropdown(
                    label = uiState.selectedCountry ?: "País",
                    options = uiState.availableCountries,
                    isSelected = uiState.selectedCountry != null,
                    onOptionSelected = { viewModel.onCountrySelected(it) },
                    onClear = { viewModel.onCountrySelected(null) },
                    modifier = Modifier.weight(1f)
                )
                FilterDropdown(
                    label = uiState.selectedGenre ?: "Género",
                    options = uiState.availableGenres,
                    isSelected = uiState.selectedGenre != null,
                    onOptionSelected = { viewModel.onGenreSelected(it) },
                    onClear = { viewModel.onGenreSelected(null) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Contenido
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                }

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
                                Text("Reintentar", color = colors.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                uiState.stations.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.searchQuery.isBlank())
                                "No hay emisoras disponibles"
                            else
                                "Sin resultados para \"${uiState.searchQuery}\"",
                            color = colors.onBackground.copy(alpha = 0.7f)
                        )
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
                        items(uiState.stations, key = { it.id }) { station ->
                            StationItem(
                                station = station,
                                isPlaying = station.id == playingStationId,
                                onClick = { onStationClick(station) },
                                onFavoriteClick = { viewModel.onFavoriteClick(station) }
                            )
                        }
                    }
                }
            }
        }
    }
}