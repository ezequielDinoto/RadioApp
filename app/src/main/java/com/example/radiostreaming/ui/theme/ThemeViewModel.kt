// presentation/theme/ThemeViewModel.kt
package com.example.radiostreaming.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiostreaming.domain.model.ThemeMode
import com.example.radiostreaming.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repository: ThemeRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.BLUE)

    fun toggle() {
        viewModelScope.launch {
            val next = if (themeMode.value == ThemeMode.BLUE) ThemeMode.YELLOW else ThemeMode.BLUE
            repository.setThemeMode(next)
        }
    }
}