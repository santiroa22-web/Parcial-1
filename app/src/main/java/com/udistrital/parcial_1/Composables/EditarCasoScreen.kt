package com.udistrital.parcial_1.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditarCasoScreen(
    caso: Caso,
    onGuardado: () -> Unit,
    onCasoCerradoConfirmado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val esCasoCerrado = caso.estado == "Cerrado"

    var titulo by remember { mutableStateOf(caso.titulo) }
    var categoria by remember { mutableStateOf(caso.categoria) }
    var ubicacion by remember { mutableStateOf(caso.ubicacion) }
    var implicados by remember { mutableStateOf(caso.implicados) }
    var prioridad by remember { mutableStateOf(caso.prioridad) }
    var descripcion by remember { mutableStateOf(caso.descripcion) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val categorias = listOf("Robo", "Homicidio", "Fraude", "Ciberdelito", "Narcóticos")
    val prioridades = listOf("Baja", "Media", "Alta")

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
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    tint = DetectiveTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (esCasoCerrado) "Expediente Cerrado" else "Editar Expediente",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveTextPrimary,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = caso.id,
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
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "🔒 Este expediente ha sido cerrado. La información es de solo lectura y no se puede modificar.",
                    fontSize = 12.sp,
                    color = Color(0xFFFF8A80),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        CampoTextoDetectiveEditable(
            label = "Título del Caso",
            value = titulo,
            onValueChange = { titulo = it },
            placeholder = "Título",
            enabled = !esCasoCerrado
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Categoría del Delito",
            fontSize = 13.sp,
            color = DetectiveAccentCyan,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categorias.forEach { cat ->
                FilterChip(
                    selected = categoria == cat,
                    onClick = { if (!esCasoCerrado) categoria = cat },
                    enabled = !esCasoCerrado,
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

        CampoTextoDetectiveEditable(
            label = "Ubicación / Lugar",
            value = ubicacion,
            onValueChange = { ubicacion = it },
            placeholder = "Lugar",
            enabled = !esCasoCerrado
        )

        Spacer(modifier = Modifier.height(14.dp))

        CampoTextoDetectiveEditable(
            label = "Sospechosos e Implicados",
            value = implicados,
            onValueChange = { implicados = it },
            placeholder = "Implicados",
            enabled = !esCasoCerrado
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Nivel de Prioridad",
            fontSize = 13.sp,
            color = DetectiveAccentCyan,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            prioridades.forEach { prio ->
                FilterChip(
                    selected = prioridad == prio,
                    onClick = { if (!esCasoCerrado) prioridad = prio },
                    enabled = !esCasoCerrado,
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

        CampoTextoDetectiveEditable(
            label = "Descripción y Evidencias",
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = "Detalles...",
            minLines = 3,
            enabled = !esCasoCerrado
        )

        Spacer(modifier = Modifier.height(20.dp))

        SeccionEvidencias(caso = caso, habilitado = !esCasoCerrado)

        Spacer(modifier = Modifier.height(24.dp))

        if (!esCasoCerrado) {
            Button(
                onClick = {
                    if (titulo.isNotBlank()) {
                        val casoEditado = caso.copy(
                            titulo = titulo,
                            categoria = categoria,
                            ubicacion = ubicacion,
                            implicados = implicados,
                            prioridad = prioridad,
                            descripcion = descripcion
                        )
                        CasoRepository.actualizarCaso(context, casoEditado)
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
                Text("Guardar Cambios", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showConfirmDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFF5252))),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Caso", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = DetectiveCardBg,
            title = {
                Text(
                    text = "¿Cerrar Expediente?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DetectiveTextPrimary
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro? Esta acción no se puede deshacer y el expediente pasará a la sección de Casos Cerrados.",
                    fontSize = 13.sp,
                    color = DetectiveTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        CasoRepository.cerrarCaso(context, caso.id)
                        showConfirmDialog = false
                        onCasoCerradoConfirmado()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Confirmar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
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
            enabled = enabled,
            placeholder = { Text(placeholder, color = DetectiveTextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
            minLines = minLines,
            maxLines = if (minLines > 1) 5 else 1,
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