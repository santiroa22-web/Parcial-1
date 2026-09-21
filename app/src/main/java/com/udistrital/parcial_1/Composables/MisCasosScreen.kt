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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import com.udistrital.parcial_1.model.Caso
import com.udistrital.parcial_1.model.CasoRepository
import com.udistrital.parcial_1.ui.theme.*

@Composable
fun MisCasosScreen(
    filtroInicial: String = "Abierto",
    onCasoSeleccionado: (Caso) -> Unit
) {
    val context = LocalContext.current
    var filtroEstado by remember { mutableStateOf(filtroInicial) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(filtroInicial) {
        filtroEstado = filtroInicial
        CasoRepository.cargarCasos(context)
    }

    val todosLosCasos = CasoRepository.listaCasos
    val casosFiltrados = todosLosCasos.filter { caso ->
        caso.estado == filtroEstado &&
        (searchQuery.isBlank() ||
            caso.titulo.contains(searchQuery, ignoreCase = true) ||
            caso.id.contains(searchQuery, ignoreCase = true) ||
            caso.implicados.contains(searchQuery, ignoreCase = true) ||
            caso.ubicacion.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetectiveDarkBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Expedientes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveTextPrimary,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "${casosFiltrados.size} casos $filtroEstado(s)".lowercase(),
                    fontSize = 12.sp,
                    color = DetectiveTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Buscar por título, ID, lugar...",
                    color = DetectiveTextSecondary.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
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

        Spacer(modifier = Modifier.height(16.dp))

        if (casosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay expedientes en estado '$filtroEstado'.",
                    color = DetectiveTextSecondary,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(casosFiltrados) { caso ->
                    TarjetaCasoItem(
                        caso = caso,
                        onClick = { onCasoSeleccionado(caso) }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaCasoItem(
    caso: Caso,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DetectiveCardBg,
        border = BorderStroke(1.dp, DetectiveCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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

                // ESQUINA: MUESTRA "CERRADO" O LA PRIORIDAD CORRESPONDIENTE
                if (caso.estado == "Cerrado") {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x33D32F2F)
                    ) {
                        Text(
                            text = "CERRADO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5252),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (caso.prioridad) {
                            "Alta" -> Color(0x33D32F2F)
                            "Media" -> Color(0x33F57C00)
                            else -> DetectiveBadgeBg
                        }
                    ) {
                        Text(
                            text = "Prioridad ${caso.prioridad}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (caso.prioridad) {
                                "Alta" -> Color(0xFFFF5252)
                                "Media" -> Color(0xFFFFB74D)
                                else -> DetectiveAccentCyan
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
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

                Spacer(modifier = Modifier.width(14.dp))

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = DetectiveTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = caso.ubicacion, fontSize = 11.sp, color = DetectiveTextSecondary)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = DetectiveAccentCyan,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Implicados: ${caso.implicados}",
                    fontSize = 11.sp,
                    color = DetectiveTextPrimary,
                    fontWeight = FontWeight.Medium
                )
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