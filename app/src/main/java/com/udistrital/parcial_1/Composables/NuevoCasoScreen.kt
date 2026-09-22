package com.udistrital.parcial_1.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.util.TimeZone

/**
 * Pantalla para crear un caso nuevo con ID consecutivo (ej: CAS-003) y selector de fecha.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoCasoScreen(
    onCasoGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val fechaHoy = remember { sdf.format(Date()) }

    var titulo by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf(fechaHoy) }
    var descripcion by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetectiveDarkBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            IconButton(onClick = onVolver) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = DetectiveTextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nuevo caso",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DetectiveTextPrimary,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Título
        CampoTextoDetective(
            label = "Título",
            value = titulo,
            onValueChange = { titulo = it },
            placeholder = "Ej: Robo en la tienda central"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Campo: Fecha de Inicio con Calendario
        Column {
            Text(text = "Fecha de inicio", fontSize = 12.sp, color = DetectiveTextSecondary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = fechaInicio,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha", tint = DetectiveAccentCyan)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DetectiveTextPrimary,
                    unfocusedTextColor = DetectiveTextPrimary,
                    focusedBorderColor = DetectiveAccentCyan,
                    unfocusedBorderColor = DetectiveCardBorder,
                    focusedContainerColor = DetectiveCardBg,
                    unfocusedContainerColor = DetectiveCardBg
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Campo: Descripción
        CampoTextoDetective(
            label = "Descripción",
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = "Detalles del hecho...",
            minLines = 4
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                if (titulo.isNotBlank()) {
                    // Genera ID en orden secuencial consecutivo
                    val nuevoCodigo = CasoRepository.generarSiguienteId()

                    val nuevoCaso = Caso(
                        id = nuevoCodigo,
                        titulo = titulo,
                        descripcion = if (descripcion.isBlank()) "Sin descripción" else descripcion,
                        fecha = fechaHoy,
                        fechaInicio = fechaInicio,
                        estado = "Abierto"
                    )

                    CasoRepository.agregarCaso(context, nuevoCaso)
                    onCasoGuardado()
                }
            },
            enabled = titulo.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = DetectiveAccentBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar caso", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }

    // Modal / Diálogo del Calendario
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            fechaInicio = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = DetectiveAccentCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = DetectiveTextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
        Text(text = label, fontSize = 12.sp, color = DetectiveTextSecondary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, color = DetectiveTextSecondary.copy(alpha = 0.5f), fontSize = 13.sp)
            },
            minLines = minLines,
            maxLines = if (minLines > 1) 6 else 1,
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