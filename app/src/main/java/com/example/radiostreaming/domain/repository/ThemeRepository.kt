// domain/repository/ThemeRepository.kt
package com.example.radiostreaming.domain.repository

import com.example.radiostreaming.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
}