// presentation/player/PlayerScreen.kt
package com.example.radiostreaming.presentation.player

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.radiostreaming.domain.player.PlaybackError
import com.example.radiostreaming.presentation.components.LivePill

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onToggleTheme: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    val station = uiState.station

    // La portada "late" suavemente mientras la radio suena
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulseScale"
    )
    val coverScale = if (uiState.isPlaying) pulse else 1f

    Scaffold(containerColor = colors.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Barra de arriba: volver y cambiar de modo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    description = "Volver",
                    onClick = onBackClick
                )
                Spacer(modifier = Modifier.weight(1f))
                CircleIconButton(
                    icon = Icons.Filled.Palette,
                    description = "Cambiar de modo",
                    onClick = onToggleTheme
                )
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Portada: dos círculos, uno dentro del otro
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer {
                            scaleX = coverScale
                            scaleY = coverScale
                        }
                        .clip(CircleShape)
                        .background(colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Radio,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(84.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = station?.name ?: "Emisora no encontrada",
                    color = colors.onBackground,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                val subtitle = listOfNotNull(station?.genre, station?.country)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        color = colors.onSurfaceVariant,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (station?.isLive == true) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LivePill()
                }

                // Error de reproducción (si hay)
                uiState.playbackError?.let { error ->
                    val message = when (error) {
                        PlaybackError.NoInternet -> "Sin conexión a internet. Revisá tu red."
                        PlaybackError.StreamUnavailable -> "Esta emisora no está disponible ahora."
                        PlaybackError.Unknown -> "Ocurrió un error al reproducir."
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = message, color = Color(0xFFFF6B6B), fontSize = 15.sp)
                    TextButton(onClick = { viewModel.retry() }) {
                        Text("Reintentar", color = colors.primary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Controles: favorito - play - espacio (para que el play quede centrado)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (station != null) {
                        CircleIconButton(
                            icon = if (station.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            description = if (station.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                            onClick = { viewModel.onFavoriteClick() },
                            size = 56.dp
                        )
                    } else {
                        Spacer(modifier = Modifier.size(56.dp))
                    }

                    FloatingActionButton(
                        onClick = { viewModel.togglePlayPause() },
                        shape = CircleShape,
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                        modifier = Modifier.size(88.dp)
                    ) {
                        if (uiState.isBuffering) {
                            CircularProgressIndicator(
                                color = colors.onPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (uiState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (uiState.isPlaying) "Pausar" else "Reproducir",
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(56.dp))
                }
            }

            // Volumen en una tarjeta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(colors.surface)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when {
                        uiState.volume == 0f -> Icons.Filled.VolumeOff
                        uiState.volume < 0.5f -> Icons.Filled.VolumeDown
                        else -> Icons.Filled.VolumeUp
                    },
                    contentDescription = "Volumen",
                    tint = colors.primary,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Slider(
                    value = uiState.volume,
                    onValueChange = { viewModel.setVolume(it) },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = colors.primary,
                        activeTrackColor = colors.primary,
                        inactiveTrackColor = colors.surfaceVariant
                    )
                )
            }
        }
    }
}

// Botón circular reutilizable (volver, paleta, favorito)
@Composable
private fun CircleIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.surface)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = colors.primary
        )
    }
}