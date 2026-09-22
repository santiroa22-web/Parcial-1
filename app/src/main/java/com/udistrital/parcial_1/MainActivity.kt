package com.udistrital.parcial_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.udistrital.parcial_1.composables.PaginaInicio
import com.udistrital.parcial_1.ui.theme.DetectiveDarkBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configuración para ocultar las barras del sistema (pantalla completa / modo inmersivo)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Oculta la barra de estado (hora, batería) y la barra de navegación del sistema
        insetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())

        // Permite que reaparezcan temporalmente de forma transparente solo si se desliza desde el borde
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DetectiveDarkBg
            ) {
                PaginaInicio()
            }
        }
    }
}