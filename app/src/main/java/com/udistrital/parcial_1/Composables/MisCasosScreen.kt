package com.udistrital.parcial_1.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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

/**
 * Listado de casos con búsqueda por título/descripción y filtro por estado.
 * Cumple con el requisito: "El listado debe permitir buscar casos y
 * visualizar su estado".
 */
@Composable
fun MisCasosScreen(
    onCasoSeleccionado: (Caso) -> Unit
) {
    val context = LocalContext.current
    var filtroEstado by remember { mutableStateOf("Abierto") }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        CasoRepository.cargarCasos(context)
    }

    val todosLosCasos = CasoRepository.listaCasos
    val casosFiltrados = todosLosCasos.filter { caso ->
        caso.estado == filtroEstado &&
            (searchQuery.isBlank() ||
                caso.titulo.contains(searchQuery, ignoreCase = true) ||
                caso.descripcion.contains(searchQuery, ignoreCase = true) ||
                caso.id.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetectiveDarkBg)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Expedientes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DetectiveTextPrimary,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "${casosFiltrados.size} caso(s) ${filtroEstado.lowercase()}(s)",
            fontSize = 12.sp,
            color = DetectiveTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filtro por estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { filtroEstado = "Abierto" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (filtroEstado == "Abierto") DetectiveAccentBlue else DetectiveCardBg,
                    contentColor = if (filtroEstado == "Abierto") Color.White else DetectiveTextSecondary
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, DetectiveCardBorder),
                modifier = Modifier.weight(1f)
            ) {
                Text("Abiertos", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(
                onClick = { filtroEstado = "Cerrado" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (filtroEstado == "Cerrado") DetectiveAccentBlue else DetectiveCardBg,
                    contentColor = if (filtroEstado == "Cerrado") Color.White else DetectiveTextSecondary
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, DetectiveCardBorder),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cerrados", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Buscador (requisito del profesor)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Buscar caso...", color = DetectiveTextSecondary.copy(alpha = 0.6f), fontSize = 13.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = DetectiveAccentCyan,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar",
                            tint = DetectiveTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DetectiveTextPrimary,
                unfocusedTextColor = DetectiveTextPrimary,
                focusedBorderColor = DetectiveAccentCyan,
                unfocusedBorderColor = DetectiveCardBorder,
                focusedContainerColor = DetectiveCardBg,
                unfocusedContainerColor = DetectiveCardBg
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (casosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotBlank())
                        "No hay casos que coincidan con '$searchQuery'."
                    else
                        "No hay expedientes en estado '$filtroEstado'.",
                    color = DetectiveTextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(casosFiltrados) { caso ->
                    TarjetaCasoItem(caso = caso, onClick = { onCasoSeleccionado(caso) })
                }
            }
        }
    }
}

@Composable
fun TarjetaCasoItem(caso: Caso, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DetectiveCardBg,
        border = BorderStroke(1.dp, DetectiveCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = caso.id,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveAccentCyan
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (caso.estado == "Cerrado") Color(0x33D32F2F) else Color(0x3322C55E)
                ) {
                    Text(
                        text = caso.estado.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (caso.estado == "Cerrado") Color(0xFFFF5252) else Color(0xFF4ADE80),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = caso.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DetectiveTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = DetectiveTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = caso.fecha, fontSize = 11.sp, color = DetectiveTextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = caso.descripcion,
                fontSize = 12.sp,
                color = DetectiveTextSecondary,
                maxLines = 2,
                lineHeight = 15.sp
            )
        }
    }
}
