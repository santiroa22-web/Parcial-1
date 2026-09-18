package com.udistrital.parcial_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.udistrital.parcial_1.composables.FirstScreen
import com.udistrital.parcial_1.composables.PaginaInicio
import com.udistrital.parcial_1.composables.SettingsScreen
import com.udistrital.parcial_1.ui.theme.DetectiveDarkBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Estado para controlar la pantalla actual
            var currentScreen by remember { mutableStateOf("first") }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DetectiveDarkBg
            ) {
                when (currentScreen) {
                    "first" -> FirstScreen(
                        onEnterApp = { currentScreen = "inicio" }
                    )
                    "inicio" -> PaginaInicio()
                    "settings" -> SettingsScreen()
                }
            }
        }
    }
}