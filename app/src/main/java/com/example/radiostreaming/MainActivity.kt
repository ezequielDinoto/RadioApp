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
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            val mode by themeViewModel.themeMode.collectAsStateWithLifecycle()

            RadiostreamingTheme(mode = mode) {
                RadioNavGraph(onToggleTheme = { themeViewModel.toggle() })
            }
        }
    }
}