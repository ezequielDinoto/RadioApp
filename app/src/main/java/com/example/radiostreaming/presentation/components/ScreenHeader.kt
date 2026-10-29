package com.example.radiostreaming.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String?,
    onToggleTheme: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.onBackground,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = colors.onSurfaceVariant,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(colors.surface)
        ) {
            Icon(
                imageVector = Icons.Filled.Palette,
                contentDescription = "Cambiar de modo",
                tint = colors.primary
            )
        }
    }
}