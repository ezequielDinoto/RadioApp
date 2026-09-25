package com.example.radiostreaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.radiostreaming.navigation.RadioNavGraph
import com.example.radiostreaming.ui.theme.RadiostreamingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RadiostreamingTheme {
               // PlayerScreen()

                RadioNavGraph()
                }
            }
        }
    }


