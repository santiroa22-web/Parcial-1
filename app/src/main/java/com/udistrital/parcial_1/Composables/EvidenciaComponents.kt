package com.udistrital.parcial_1.composables

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial_1.model.Caso
import com.udistrital.parcial_1.model.CasoRepository
import com.udistrital.parcial_1.model.Evidencia
import com.udistrital.parcial_1.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun fechaHoraActual(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

/** Nombre "amigable" del archivo (documento) a partir de su Uri. */
fun obtenerNombreArchivo(context: android.content.Context, uri: Uri): String {
    var nombre = "Documento adjunto"
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                nombre = cursor.getString(nameIndex) ?: nombre
            }
        }
    } catch (_: Exception) { }
    return nombre
}

/**
 * Sección de hallazgos y evidencias asociada a un caso.
 * Soporta tres tipos: Hallazgo (texto), Imagen y Documento.
 */
@Composable
fun SeccionEvidencias(caso: Caso, habilitado: Boolean) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hallazgos y evidencias",
                fontSize = 13.sp,
                color = DetectiveAccentCyan,
                fontWeight = FontWeight.Bold
            )
            Surface(shape = RoundedCornerShape(8.dp), color = DetectiveBadgeBg) {
                Text(
                    text = "${caso.evidencias.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveAccentCyan,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (caso.evidencias.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DetectiveCardBg.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, DetectiveCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aún no se han registrado hallazgos ni evidencias.",
                    fontSize = 12.sp,
                    color = DetectiveTextSecondary,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                caso.evidencias.forEach { evidencia ->
                    EvidenciaItem(
                        evidencia = evidencia,
                        habilitado = habilitado,
                        onEliminar = {
                            CasoRepository.eliminarEvidencia(context, caso.id, evidencia.id)
                        }
                    )
                }
            }
        }

        if (habilitado) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = { showDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DetectiveAccentCyan),
                border = BorderStroke(1.dp, DetectiveAccentCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar hallazgo / evidencia", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDialog) {
        DialogAgregarEvidencia(
            onDismiss = { showDialog = false },
            onGuardar = { evidencia ->
                CasoRepository.agregarEvidencia(context, caso.id, evidencia)
                showDialog = false
            }
        )
    }
}

@Composable
fun EvidenciaItem(
    evidencia: Evidencia,
    habilitado: Boolean,
    onEliminar: () -> Unit
) {
    val context = LocalContext.current
    val esDocumentoAbrible = evidencia.tipo == "Documento" && evidencia.uri != null

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DetectiveCardBg,
        border = BorderStroke(1.dp, DetectiveCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .let { base ->
                if (esDocumentoAbrible) {
                    base.clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(Uri.parse(evidencia.uri), "*/*")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) { }
                    }
                } else base
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                evidencia.tipo == "Imagen" && evidencia.uri != null ->
                    ImagenEvidenciaThumbnail(uriString = evidencia.uri!!)
                else -> IconoTipoEvidencia(evidencia.tipo)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = evidencia.tipo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveAccentCyan
                )
                Text(
                    text = if (evidencia.tipo == "Documento")
                        (evidencia.nombreArchivo ?: evidencia.descripcion)
                    else evidencia.descripcion,
                    fontSize = 13.sp,
                    color = DetectiveTextPrimary,
                    maxLines = 3
                )
                if (evidencia.tipo == "Documento"
                    && !evidencia.nombreArchivo.isNullOrBlank()
                    && evidencia.descripcion.isNotBlank()
                ) {
                    Text(
                        text = evidencia.descripcion,
                        fontSize = 11.sp,
                        color = DetectiveTextSecondary,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = evidencia.fecha,
                    fontSize = 10.sp,
                    color = DetectiveTextSecondary
                )
            }
            if (habilitado) {
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun IconoTipoEvidencia(tipo: String) {
    val icon = when (tipo) {
        "Imagen" -> Icons.Default.Image
        "Documento" -> Icons.Default.Description
        else -> Icons.Default.FindInPage
    }
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(DetectiveBadgeBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DetectiveAccentCyan,
            modifier = Modifier.size(18.dp)
        )
    }
}

/** Miniatura de una evidencia de tipo Imagen, decodificada desde su Uri. */
@Composable
fun ImagenEvidenciaThumbnail(uriString: String) {
    val context = LocalContext.current
    var bitmap by remember(uriString) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uriString) {
        bitmap = try {
            context.contentResolver.openInputStream(Uri.parse(uriString))?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (_: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DetectiveBadgeBg),
        contentAlignment = Alignment.Center
    ) {
        val bmp = bitmap
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Evidencia fotográfica",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = DetectiveAccentCyan,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarEvidencia(
    onDismiss: () -> Unit,
    onGuardar: (Evidencia) -> Unit
) {
    val context = LocalContext.current
    var tipoSeleccionado by remember { mutableStateOf("Hallazgo") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var documentoUri by remember { mutableStateOf<Uri?>(null) }
    var documentoNombre by remember { mutableStateOf<String?>(null) }

    val seleccionarImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) imagenUri = uri
    }

    val seleccionarDocumentoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }
            documentoUri = uri
            documentoNombre = obtenerNombreArchivo(context, uri)
        }
    }

    val puedeGuardar = when (tipoSeleccionado) {
        "Imagen" -> imagenUri != null
        "Documento" -> documentoUri != null
        else -> descripcion.isNotBlank()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DetectiveCardBg,
        title = {
            Text(
                text = "Nuevo hallazgo / evidencia",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = DetectiveTextPrimary
            )
        },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Hallazgo", "Imagen", "Documento").forEach { tipo ->
                        FilterChip(
                            selected = tipoSeleccionado == tipo,
                            onClick = { tipoSeleccionado = tipo },
                            label = { Text(tipo, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (tipo) {
                                        "Imagen" -> Icons.Default.PhotoCamera
                                        "Documento" -> Icons.Default.UploadFile
                                        else -> Icons.Default.FindInPage
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DetectiveAccentBlue,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = DetectiveDarkBg,
                                labelColor = DetectiveTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (tipoSeleccionado) {
                        "Imagen" -> "Selecciona una foto o imagen de evidencia."
                        "Documento" -> "Adjunta un documento (PDF, Word, etc.) como evidencia."
                        else -> "Describe el hallazgo encontrado."
                    },
                    fontSize = 12.sp,
                    color = DetectiveTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (tipoSeleccionado == "Imagen") {
                    OutlinedButton(
                        onClick = { seleccionarImagenLauncher.launch("image/*") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DetectiveAccentCyan),
                        border = BorderStroke(1.dp, DetectiveAccentCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (imagenUri == null) "Seleccionar imagen" else "Cambiar imagen", fontSize = 13.sp)
                    }

                    if (imagenUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DetectiveDarkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            ImagenEvidenciaThumbnail(uriString = imagenUri.toString())
                        }
                    }
                }

                if (tipoSeleccionado == "Documento") {
                    OutlinedButton(
                        onClick = { seleccionarDocumentoLauncher.launch(arrayOf("*/*")) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DetectiveAccentCyan),
                        border = BorderStroke(1.dp, DetectiveAccentCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (documentoUri == null) "Seleccionar documento" else "Cambiar documento", fontSize = 13.sp)
                    }

                    if (documentoNombre != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DetectiveDarkBg,
                            border = BorderStroke(1.dp, DetectiveCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = DetectiveAccentCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = documentoNombre ?: "",
                                    fontSize = 12.sp,
                                    color = DetectiveTextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = {
                        Text(
                            text = if (tipoSeleccionado == "Hallazgo")
                                "Ej: Huella dactilar en la manija de la puerta"
                            else "Descripción (opcional)",
                            fontSize = 12.sp
                        )
                    },
                    minLines = if (tipoSeleccionado == "Hallazgo") 3 else 1,
                    maxLines = 5,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DetectiveTextPrimary,
                        unfocusedTextColor = DetectiveTextPrimary,
                        focusedBorderColor = DetectiveAccentCyan,
                        unfocusedBorderColor = DetectiveCardBorder,
                        focusedContainerColor = DetectiveDarkBg,
                        unfocusedContainerColor = DetectiveDarkBg
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nueva = when (tipoSeleccionado) {
                        "Imagen" -> imagenUri?.let { uri ->
                            Evidencia(
                                id = "EV-${System.currentTimeMillis()}",
                                tipo = "Imagen",
                                descripcion = descripcion.ifBlank { "Imagen adjunta como evidencia" },
                                uri = uri.toString(),
                                fecha = fechaHoraActual()
                            )
                        }
                        "Documento" -> documentoUri?.let { uri ->
                            Evidencia(
                                id = "EV-${System.currentTimeMillis()}",
                                tipo = "Documento",
                                descripcion = descripcion,
                                uri = uri.toString(),
                                nombreArchivo = documentoNombre,
                                fecha = fechaHoraActual()
                            )
                        }
                        else -> if (descripcion.isNotBlank()) {
                            Evidencia(
                                id = "EV-${System.currentTimeMillis()}",
                                tipo = "Hallazgo",
                                descripcion = descripcion,
                                fecha = fechaHoraActual()
                            )
                        } else null
                    }
                    if (nueva != null) onGuardar(nueva)
                },
                enabled = puedeGuardar,
                colors = ButtonDefaults.buttonColors(containerColor = DetectiveAccentBlue)
            ) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = DetectiveTextSecondary)
            }
        }
    )
}
