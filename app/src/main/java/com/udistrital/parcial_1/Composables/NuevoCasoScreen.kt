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

/**
 * Pantalla para crear un caso nuevo.
 * Campos mínimos según el profesor: título, descripción.
 * Fecha se toma automáticamente y estado inicial es "Abierto".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoCasoScreen(
    onCasoGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

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

        CampoTextoDetective(
            label = "Título",
            value = titulo,
            onValueChange = { titulo = it },
            placeholder = "Ej: Robo en la tienda central"
        )

        Spacer(modifier = Modifier.height(14.dp))

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
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val nuevoCodigo = "CAS-${System.currentTimeMillis()}"

                    val nuevoCaso = Caso(
                        id = nuevoCodigo,
                        titulo = titulo,
                        descripcion = if (descripcion.isBlank()) "Sin descripción" else descripcion,
                        fecha = fechaActual,
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
