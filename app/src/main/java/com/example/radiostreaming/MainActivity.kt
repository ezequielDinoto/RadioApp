package com.example.radiostreaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.radiostreaming.navigation.RadioNavGraph
import com.example.radiostreaming.ui.theme.RadiostreamingTheme
import com.example.radiostreaming.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            RadiostreamingTheme(mode = mode) {
                RadioNavGraph(onToggleTheme = { themeViewModel.toggle() })
            }
        }
    }
}