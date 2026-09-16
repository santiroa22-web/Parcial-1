package com.udistrital.parcial_1.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial_1.R
import com.udistrital.parcial_1.ui.theme.*

@Composable
fun FirstScreen(
    onEnterApp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DetectiveDarkBg)
    ) {
        // Imagen de Fondo
        Image(
            painter = painterResource(id = R.drawable.detective_bg),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Capa de degradado sobrio
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DetectiveDarkBg.copy(alpha = 0.5f),
                            DetectiveDarkBg.copy(alpha = 0.85f),
                            DetectiveDarkBg
                        )
                    )
                )
        )

        // Contenido Principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // SECCIÓN SUPERIOR
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Insignia superior
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = DetectiveAccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SISTEMA DE INVESTIGACIÓN LOCAL",
                        color = DetectiveTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                }


                Spacer(modifier = Modifier.height(115.dp))

                Text(
                    text = "SmartTrace",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveTextPrimary,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Gestión de Expedientes y Evidencias",
                    fontSize = 13.sp,
                    color = DetectiveAccentCyan,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }

            // SECCIÓN INFERIOR: Tarjeta explicativa + Botón de Ingreso
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DetectiveCardBg.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, DetectiveCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Acceso al Expediente",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DetectiveTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Plataforma para el registro, seguimiento y análisis de casos criminales.",
                            fontSize = 13.sp,
                            color = DetectiveTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onEnterApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DetectiveAccentBlue
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Ingresar al Sistema",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = DetectiveTextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Universidad Distrital Francisco José de Caldas",
                        fontSize = 11.sp,
                        color = DetectiveTextSecondary
                    )
                }
            }
        }
    }
}