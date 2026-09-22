package com.udistrital.parcial_1.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial_1.model.Caso
import com.udistrital.parcial_1.model.CasoRepository
import com.udistrital.parcial_1.ui.theme.*

@Composable
fun PaginaInicio() {
    val context = LocalContext.current
    var showNuevoCaso by remember { mutableStateOf(false) }
    var casoSeleccionadoParaEditar by remember { mutableStateOf<Caso?>(null) }

    LaunchedEffect(Unit) {
        CasoRepository.cargarCasos(context)
    }

    when {
        showNuevoCaso -> NuevoCasoScreen(
            onCasoGuardado = { showNuevoCaso = false },
            onVolver = { showNuevoCaso = false }
        )

        casoSeleccionadoParaEditar != null -> EditarCasoScreen(
            caso = casoSeleccionadoParaEditar!!,
            onGuardado = { casoSeleccionadoParaEditar = null },
            onCasoCerradoConfirmado = { casoSeleccionadoParaEditar = null },
            onEliminado = { casoSeleccionadoParaEditar = null },
            onVolver = { casoSeleccionadoParaEditar = null }
        )

        else -> Scaffold(
            containerColor = DetectiveDarkBg,
            topBar = {
                Surface(color = DetectiveDarkBg) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "SmartTrace",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DetectiveTextPrimary,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Gestor de casos criminales",
                            fontSize = 12.sp,
                            color = DetectiveTextSecondary
                        )
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showNuevoCaso = true },
                    containerColor = DetectiveAccentBlue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo caso", fontWeight = FontWeight.Bold)
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                MisCasosScreen(
                    onCasoSeleccionado = { casoSeleccionadoParaEditar = it }
                )
            }
        }
    }
}