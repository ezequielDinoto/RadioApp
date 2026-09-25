package com.example.radiostreaming.presentation.player

// presentation/player/PlayerScreen.kt
// presentation/player/PlayerScreen.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.radiostreaming.domain.player.PlaybackError
import com.example.radiostreaming.ui.theme.YellowPrimary

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(), // <- una sola línea reemplaza todo el bloque anterior
    stationName: String
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // ... el resto de la pantalla queda exactamente igual

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        if (uiState.isLoading) {
            // Mientras el ViewModel busca los datos de la emisora
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YellowPrimary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // "Portada" de la emisora
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📻", fontSize = 64.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nombre real de la emisora, viene del uiState
            Text(
                text = uiState.station?.name ?: "Emisora no encontrada",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (uiState.station?.isLive == true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(YellowPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "EN VIVO",
                        color = YellowPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // presentation/player/PlayerScreen.kt (agregado dentro del Column, antes del FloatingActionButton)

            uiState.playbackError?.let { error ->
                val message = when (error) {
                    PlaybackError.NoInternet -> "Sin conexión a internet. Revisá tu red."
                    PlaybackError.StreamUnavailable -> "Esta emisora no está disponible ahora."
                    PlaybackError.Unknown -> "Ocurrió un error al reproducir."
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = message,
                        color = Color(0xFFFF6B6B), // rojo suave, para no salirnos del todo de la paleta
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.retry() }) {
                        Text("Reintentar", color = YellowPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón de play/pause: llama al ViewModel y muestra buffering si corresponde
            FloatingActionButton(
                onClick = { viewModel.togglePlayPause() },
                containerColor = YellowPrimary,
                contentColor = Color.Black,
                modifier = Modifier.size(72.dp)
            ) {
                if (uiState.isBuffering) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (uiState.isPlaying) "Pausar" else "Reproducir",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Control de volumen: lee y actualiza a través del ViewModel
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when {
                        uiState.volume == 0f -> Icons.Filled.VolumeOff
                        uiState.volume < 0.5f -> Icons.Filled.VolumeDown
                        else -> Icons.Filled.VolumeUp
                    },
                    contentDescription = "Volumen",
                    tint = YellowPrimary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Slider(
                    value = uiState.volume,
                    onValueChange = { newValue -> viewModel.setVolume(newValue) },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = YellowPrimary,
                        activeTrackColor = YellowPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}