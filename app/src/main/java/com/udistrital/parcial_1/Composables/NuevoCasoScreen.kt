package com.udistrital.parcial_1.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoCasoScreen(
    onCasoGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Robo") }
    var ubicacion by remember { mutableStateOf("") }
    var implicados by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }
    var descripcion by remember { mutableStateOf("") }

    val categorias = listOf("Robo", "Homicidio", "Fraude", "Ciberdelito", "Narcóticos")
    val prioridades = listOf("Baja", "Media", "Alta")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetectiveDarkBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Barra Superior / Volver
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    tint = DetectiveTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Registrar Nuevo Expediente",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DetectiveTextPrimary,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Título
        CampoTextoDetective(
            label = "Título del Caso / Incidente",
            value = titulo,
            onValueChange = { titulo = it },
            placeholder = "Ej: Robo en la tienda central"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Campo: Categoría
        Text(
            text = "Categoría del Delito",
            fontSize = 13.sp,
            color = DetectiveAccentCyan,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categorias.take(3).forEach { cat ->
                FilterChip(
                    selected = categoria == cat,
                    onClick = { categoria = cat },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DetectiveAccentBlue,
                        selectedLabelColor = Color.White,
                        containerColor = DetectiveCardBg,
                        labelColor = DetectiveTextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo: Ubicación
        CampoTextoDetective(
            label = "Ubicación / Lugar del Suceso",
            value = ubicacion,
            onValueChange = { ubicacion = it },
            placeholder = "Ej: Calle 26 #13-45"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Campo: Implicados
        CampoTextoDetective(
            label = "Sospechosos e Implicados",
            value = implicados,
            onValueChange = { implicados = it },
            placeholder = "Ej: Alias 'El Flaco', Testigo: Juan Pérez"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Campo: Prioridad
        Text(
            text = "Nivel de Prioridad",
            fontSize = 13.sp,
            color = DetectiveAccentCyan,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            prioridades.forEach { prio ->
                FilterChip(
                    selected = prioridad == prio,
                    onClick = { prioridad = prio },
                    label = { Text(prio, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when(prio) {
                            "Alta" -> Color(0xFFD32F2F)
                            "Media" -> Color(0xFFF57C00)
                            else -> DetectiveAccentBlue
                        },
                        selectedLabelColor = Color.White,
                        containerColor = DetectiveCardBg,
                        labelColor = DetectiveTextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo: Descripción
        CampoTextoDetective(
            label = "Descripción y Evidencias Iniciales",
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = "Detalle del hecho, hallazgos o pruebas recopiladas...",
            minLines = 3
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Botón Guardar
        Button(
            onClick = {
                if (titulo.isNotBlank()) {
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val nuevoCodigo = "CAS-2026-0${CasoRepository.listaCasos.size + 1}"

                    val nuevoCaso = Caso(
                        id = nuevoCodigo,
                        titulo = titulo,
                        categoria = categoria,
                        fecha = fechaActual,
                        ubicacion = if (ubicacion.isBlank()) "No especificada" else ubicacion,
                        implicados = if (implicados.isBlank()) "En investigación" else implicados,
                        prioridad = prioridad,
                        estado = "Abierto",
                        descripcion = if (descripcion.isBlank()) "Sin descripción detallada" else descripcion
                    )

                    CasoRepository.agregarCaso(context, nuevoCaso)
                    onCasoGuardado()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DetectiveAccentBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar Expediente", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CampoTextoDetective(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = DetectiveTextSecondary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = DetectiveTextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
            minLines = minLines,
            maxLines = if (minLines > 1) 5 else 1,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DetectiveTextPrimary,
                unfocusedTextColor = DetectiveTextPrimary,
                focusedBorderColor = DetectiveAccentCyan,
                unfocusedBorderColor = DetectiveCardBorder,
                focusedContainerColor = DetectiveCardBg,
                unfocusedContainerColor = DetectiveCardBg
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}