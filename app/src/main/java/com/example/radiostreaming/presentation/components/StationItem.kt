package com.example.radiostreaming.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.radiostreaming.domain.model.Station

@Composable
fun StationItem(
    station: Station,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isPlaying: Boolean = false
) {
    val shape = RoundedCornerShape(28.dp)
    val colors = MaterialTheme.colorScheme
    val subtitle = listOf(station.genre, station.country)
        .filter { it.isNotBlank() }
        .joinToString(" · ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface)
            .then(if (isPlaying) Modifier.border(2.dp, colors.primary, shape) else Modifier)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Círculo con el ícono (si suena, cambia a las barritas de audio)
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.GraphicEq else Icons.Filled.Radio,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = station.name,
                color = colors.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = colors.onSurfaceVariant,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (station.isLive) {
                Spacer(modifier = Modifier.height(8.dp))
                LivePill()
            }
        }

        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (station.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (station.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

