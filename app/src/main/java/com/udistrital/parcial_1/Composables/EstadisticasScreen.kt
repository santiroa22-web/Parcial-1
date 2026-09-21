package com.udistrital.parcial_1.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial_1.model.CasoRepository
import com.udistrital.parcial_1.ui.theme.*
import kotlin.math.roundToInt

private val PALETA_TORTA = listOf(
    DetectiveAccentBlue,
    DetectiveAccentCyan,
    DetectiveGold,
    DetectiveChartRed,
    DetectiveChartPurple,
    DetectiveChartOrange,
    DetectiveGreenStatus
)

@Composable
fun EstadisticasScreen() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        CasoRepository.cargarCasos(context)
    }

    val casos = CasoRepository.listaCasos
    var vistaSeleccionada by remember { mutableStateOf("Categoría") }

    val abiertos = casos.count { it.estado == "Abierto" }
    val cerrados = casos.count { it.estado == "Cerrado" }

    val datosCategoria = casos
        .groupingBy { it.categoria }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }

    val datosPrioridad = listOf("Alta", "Media", "Baja")
        .map { prio -> prio to casos.count { it.prioridad == prio } }
        .filter { it.second > 0 }

    val datosMostrados = if (vistaSeleccionada == "Categoría") datosCategoria else datosPrioridad

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetectiveDarkBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Estadísticas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DetectiveTextPrimary,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "Resumen general de los expedientes",
            fontSize = 12.sp,
            color = DetectiveTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaMetrica(
                titulo = "Total",
                valor = casos.size.toString(),
                color = DetectiveAccentCyan,
                modifier = Modifier.weight(1f)
            )
            TarjetaMetrica(
                titulo = "Abiertos",
                valor = abiertos.toString(),
                color = DetectiveAccentBlue,
                modifier = Modifier.weight(1f)
            )
            TarjetaMetrica(
                titulo = "Cerrados",
                valor = cerrados.toString(),
                color = DetectiveGreenStatus,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (casos.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DetectiveCardBg,
                border = BorderStroke(1.dp, DetectiveCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aún no hay expedientes registrados para generar estadísticas.",
                    fontSize = 13.sp,
                    color = DetectiveTextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Column
        }

        Text(
            text = "Distribución de Casos",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DetectiveAccentCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("Categoría", "Prioridad").forEach { opcion ->
                Button(
                    onClick = { vistaSeleccionada = opcion },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vistaSeleccionada == opcion) DetectiveAccentBlue else DetectiveCardBg,
                        contentColor = if (vistaSeleccionada == opcion) Color.White else DetectiveTextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DetectiveCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(opcion, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DetectiveCardBg,
            border = BorderStroke(1.dp, DetectiveCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Casos por $vistaSeleccionada",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveTextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                GraficoTorta(
                    datos = datosMostrados,
                    colores = PALETA_TORTA,
                    modifier = Modifier.size(190.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val total = datosMostrados.sumOf { it.second }.coerceAtLeast(1)
                    datosMostrados.forEachIndexed { index, (etiqueta, cantidad) ->
                        LeyendaItem(
                            color = PALETA_TORTA[index % PALETA_TORTA.size],
                            etiqueta = etiqueta,
                            cantidad = cantidad,
                            porcentaje = (cantidad * 100f / total).roundToInt()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TarjetaMetrica(
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DetectiveCardBg,
        border = BorderStroke(1.dp, DetectiveCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = DetectiveTextSecondary
            )
        }
    }
}

/** Dibuja un gráfico de torta simple a partir de pares (etiqueta, cantidad). */
@Composable
fun GraficoTorta(
    datos: List<Pair<String, Int>>,
    colores: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = datos.sumOf { it.second }.coerceAtLeast(1)

    Canvas(modifier = modifier) {
        var anguloInicial = -90f
        datos.forEachIndexed { index, (_, cantidad) ->
            val angulo = 360f * cantidad / total
            drawArc(
                color = colores[index % colores.size],
                startAngle = anguloInicial,
                sweepAngle = angulo,
                useCenter = true
            )
            anguloInicial += angulo
        }
    }
}

@Composable
fun LeyendaItem(
    color: Color,
    etiqueta: String,
    cantidad: Int,
    porcentaje: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = etiqueta,
            fontSize = 13.sp,
            color = DetectiveTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$cantidad ($porcentaje%)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = DetectiveTextSecondary
        )
    }
}
