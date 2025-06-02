package com.teegarcs.ktools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.teegarcs.ktools.ui.theme.KToolsTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KToolsTheme {
                val state = viewModel.viewState.collectAsState()
                MainUI.BuildUI(state.value, viewModel)
            }
        }
    }
}