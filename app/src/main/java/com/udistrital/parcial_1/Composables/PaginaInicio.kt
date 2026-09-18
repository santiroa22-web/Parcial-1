package com.udistrital.parcial_1.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial_1.model.Caso
import com.udistrital.parcial_1.model.CasoRepository
import com.udistrital.parcial_1.ui.theme.*

@Composable
fun PaginaInicio() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var showNuevoCaso by remember { mutableStateOf(false) }
    var casoSeleccionadoParaEditar by remember { mutableStateOf<Caso?>(null) }
    var filtroCasos by remember { mutableStateOf("Abierto") }

    LaunchedEffect(Unit) {
        CasoRepository.cargarCasos(context)
    }

    if (showNuevoCaso) {
        NuevoCasoScreen(
            onCasoGuardado = {
                showNuevoCaso = false
                filtroCasos = "Abierto"
                selectedTab = 1
            },
            onVolver = { showNuevoCaso = false }
        )
    } else if (casoSeleccionadoParaEditar != null) {
        EditarCasoScreen(
            caso = casoSeleccionadoParaEditar!!,
            onGuardado = {
                casoSeleccionadoParaEditar = null
            },
            onCasoCerradoConfirmado = {
                casoSeleccionadoParaEditar = null
                filtroCasos = "Cerrado"
                selectedTab = 1
            },
            onVolver = { casoSeleccionadoParaEditar = null }
        )
    } else {
        Scaffold(
            containerColor = DetectiveDarkBg,
            bottomBar = {
                NavigationBar(
                    containerColor = DetectiveCardBg,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DetectiveAccentCyan,
                            selectedTextColor = DetectiveAccentCyan,
                            indicatorColor = DetectiveBadgeBg,
                            unselectedIconColor = DetectiveTextSecondary,
                            unselectedTextColor = DetectiveTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Casos") },
                        label = { Text("Casos", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DetectiveAccentCyan,
                            selectedTextColor = DetectiveAccentCyan,
                            indicatorColor = DetectiveBadgeBg,
                            unselectedIconColor = DetectiveTextSecondary,
                            unselectedTextColor = DetectiveTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Métricas") },
                        label = { Text("Estadísticas", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DetectiveAccentCyan,
                            selectedTextColor = DetectiveAccentCyan,
                            indicatorColor = DetectiveBadgeBg,
                            unselectedIconColor = DetectiveTextSecondary,
                            unselectedTextColor = DetectiveTextSecondary
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                        label = { Text("Ajustes", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DetectiveAccentCyan,
                            selectedTextColor = DetectiveAccentCyan,
                            indicatorColor = DetectiveBadgeBg,
                            unselectedIconColor = DetectiveTextSecondary,
                            unselectedTextColor = DetectiveTextSecondary
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    0 -> InicioContent(
                        onNavigateTab = { tab ->
                            filtroCasos = "Abierto"
                            selectedTab = tab
                        },
                        onAbrirCasosCerrados = {
                            filtroCasos = "Cerrado"
                            selectedTab = 1
                        },
                        onCrearCaso = { showNuevoCaso = true },
                        onCasoSeleccionado = { caso -> casoSeleccionadoParaEditar = caso }
                    )
                    1 -> MisCasosScreen(
                        filtroInicial = filtroCasos,
                        onCasoSeleccionado = { caso -> casoSeleccionadoParaEditar = caso }
                    )
                    2 -> { /* Próximamente: Estadísticas */ }
                    3 -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun InicioContent(
    onNavigateTab: (Int) -> Unit,
    onAbrirCasosCerrados: () -> Unit,
    onCrearCaso: () -> Unit,
    onCasoSeleccionado: (Caso) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val casos = CasoRepository.listaCasos

    val casosFiltrados = remember(searchQuery, casos) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            casos.filter { caso ->
                caso.titulo.contains(searchQuery, ignoreCase = true) ||
                        caso.id.contains(searchQuery, ignoreCase = true) ||
                        caso.implicados.contains(searchQuery, ignoreCase = true) ||
                        caso.categoria.contains(searchQuery, ignoreCase = true) ||
                        caso.ubicacion.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
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
                    text = "SmartTrace",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveTextPrimary,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Sistema de Control",
                    fontSize = 11.sp,
                    color = DetectiveTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DetectiveCardBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = DetectiveTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Bienvenido, Detective",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DetectiveTextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "“La verdad siempre deja rastro.”",
            fontSize = 12.sp,
            color = DetectiveAccentCyan,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OptionCard(
                    title = "Mis Casos",
                    subtitle = "Explora expedientes",
                    icon = Icons.Default.Folder,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(1) }
                )
                OptionCard(
                    title = "Nuevo Caso",
                    subtitle = "Registrar expediente",
                    icon = Icons.Default.Add,
                    modifier = Modifier.weight(1f),
                    onClick = onCrearCaso
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OptionCard(
                    title = "Estadísticas",
                    subtitle = "Métricas e informes",
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(2) }
                )
                OptionCard(
                    title = "Casos Cerrados",
                    subtitle = "Historial finalizado",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    onClick = onAbrirCasosCerrados
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Buscador de Expedientes",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DetectiveAccentCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Buscar por título, ID o implicados...",
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

        Spacer(modifier = Modifier.height(14.dp))

        if (searchQuery.isNotBlank()) {
            if (casosFiltrados.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DetectiveCardBg,
                    border = BorderStroke(1.dp, DetectiveCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No se encontraron expedientes con '$searchQuery'.",
                        fontSize = 12.sp,
                        color = DetectiveTextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Text(
                    text = "Resultados encontrados (${casosFiltrados.size}):",
                    fontSize = 12.sp,
                    color = DetectiveTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    casosFiltrados.forEach { caso ->
                        TarjetaCasoItem(
                            caso = caso,
                            onClick = { onCasoSeleccionado(caso) }
                        )
                    }
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DetectiveCardBg.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, DetectiveCardBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total de expedientes registrados: ${casos.size}",
                        fontSize = 12.sp,
                        color = DetectiveTextSecondary
                    )
                    Text(
                        text = "Ver todos →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DetectiveAccentCyan,
                        modifier = Modifier.clickable { onNavigateTab(1) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(180.dp))


        Text(
            text = "“La evidencia no miente.”",
            fontSize = 12.sp,
            color = DetectiveTextSecondary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun OptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DetectiveCardBg,
        border = BorderStroke(1.dp, DetectiveCardBorder),
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
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

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DetectiveTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = DetectiveTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}