package com.udistrital.parcial_1.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCasoScreen(
    caso: Caso,
    onGuardado: () -> Unit,
    onCasoCerradoConfirmado: () -> Unit,
    onEliminado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val esCasoCerrado = caso.estado == "Cerrado"

    var titulo by remember { mutableStateOf(caso.titulo) }
    var fechaInicio by remember { mutableStateOf(caso.fechaInicio) }
    var descripcion by remember { mutableStateOf(caso.descripcion) }
    var showConfirmCierre by remember { mutableStateOf(false) }
    var showConfirmEliminar by remember { mutableStateOf(false) }
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
            Column {
                Text(
                    text = if (esCasoCerrado) "Caso cerrado" else "Editar caso",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveTextPrimary,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "${caso.id}  •  Reg: ${caso.fecha}",
                    fontSize = 12.sp,
                    color = DetectiveAccentCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (esCasoCerrado) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x22D32F2F),
                border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "🔒 Caso cerrado. La información es de solo lectura.",
                    fontSize = 12.sp,
                    color = Color(0xFFFF8A80),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        CampoTextoDetectiveEditable(
            label = "Título",
            value = titulo,
            onValueChange = { titulo = it },
            placeholder = "Título",
            enabled = !esCasoCerrado
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Campo Fecha de Inicio con Calendario
        Column {
            Text("Fecha de inicio", fontSize = 12.sp, color = DetectiveTextSecondary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = fechaInicio,
                onValueChange = {},
                readOnly = true,
                enabled = !esCasoCerrado,
                trailingIcon = {
                    if (!esCasoCerrado) {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha", tint = DetectiveAccentCyan)
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DetectiveTextPrimary,
                    unfocusedTextColor = DetectiveTextPrimary,
                    disabledTextColor = DetectiveTextSecondary,
                    focusedBorderColor = DetectiveAccentCyan,
                    unfocusedBorderColor = DetectiveCardBorder,
                    disabledBorderColor = DetectiveCardBorder.copy(alpha = 0.3f),
                    focusedContainerColor = DetectiveCardBg,
                    unfocusedContainerColor = DetectiveCardBg,
                    disabledContainerColor = DetectiveCardBg.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !esCasoCerrado) { showDatePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        CampoTextoDetectiveEditable(
            label = "Descripción",
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = "Descripción del caso",
            minLines = 4,
            enabled = !esCasoCerrado
        )

        Spacer(modifier = Modifier.height(20.dp))

        SeccionEvidencias(caso = caso, habilitado = !esCasoCerrado)

        Spacer(modifier = Modifier.height(24.dp))

        if (!esCasoCerrado) {
            Button(
                onClick = {
                    if (titulo.isNotBlank()) {
                        CasoRepository.actualizarCaso(
                            context,
                            caso.copy(
                                titulo = titulo,
                                fechaInicio = fechaInicio,
                                descripcion = descripcion
                            )
                        )
                        onGuardado()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DetectiveAccentBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar cambios", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showConfirmCierre = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF57C00)),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(Color(0xFFF57C00))),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar caso", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = { showConfirmEliminar = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(Color(0xFFFF5252))),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Eliminar caso", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }

    // Diálogo del Calendario
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

    if (showConfirmCierre) {
        AlertDialog(
            onDismissRequest = { showConfirmCierre = false },
            containerColor = DetectiveCardBg,
            title = { Text("¿Cerrar caso?", fontWeight = FontWeight.Bold, color = DetectiveTextPrimary) },
            text = {
                Text(
                    "El caso pasará a la lista de cerrados y quedará como solo lectura.",
                    color = DetectiveTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        CasoRepository.cerrarCaso(context, caso.id)
                        showConfirmCierre = false
                        onCasoCerradoConfirmado()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00))
                ) {
                    Text("Confirmar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCierre = false }) {
                    Text("Cancelar", color = DetectiveTextSecondary)
                }
            }
        )
    }

    if (showConfirmEliminar) {
        AlertDialog(
            onDismissRequest = { showConfirmEliminar = false },
            containerColor = DetectiveCardBg,
            title = { Text("¿Eliminar caso?", fontWeight = FontWeight.Bold, color = DetectiveTextPrimary) },
            text = {
                Text(
                    "El caso y todas sus evidencias se eliminarán definitivamente.",
                    color = DetectiveTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        CasoRepository.eliminarCaso(context, caso.id)
                        showConfirmEliminar = false
                        onEliminado()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmEliminar = false }) {
                    Text("Cancelar", color = DetectiveTextSecondary)
                }
            }
        )
    }
}

@Composable
fun CampoTextoDetectiveEditable(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    enabled: Boolean = true
) {
    Column {
        Text(label, fontSize = 12.sp, color = DetectiveTextSecondary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = {
                Text(placeholder, color = DetectiveTextSecondary.copy(alpha = 0.5f), fontSize = 13.sp)
            },
            minLines = minLines,
            maxLines = if (minLines > 1) 6 else 1,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DetectiveTextPrimary,
                unfocusedTextColor = DetectiveTextPrimary,
                disabledTextColor = DetectiveTextSecondary,
                focusedBorderColor = DetectiveAccentCyan,
                unfocusedBorderColor = DetectiveCardBorder,
                disabledBorderColor = DetectiveCardBorder.copy(alpha = 0.3f),
                focusedContainerColor = DetectiveCardBg,
                unfocusedContainerColor = DetectiveCardBg,
                disabledContainerColor = DetectiveCardBg.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}