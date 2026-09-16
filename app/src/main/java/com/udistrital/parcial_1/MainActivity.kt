package com.udistrital.parcial_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.udistrital.parcial_1.composables.FirstScreen
import com.udistrital.parcial_1.ui.theme.DetectiveDarkBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DetectiveDarkBg
            ) {
                FirstScreen(
                    onEnterApp = {

                    }
                )
            }
        }
    }
}