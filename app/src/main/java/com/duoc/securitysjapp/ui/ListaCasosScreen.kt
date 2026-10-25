package com.duoc.securitysjapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.ui.theme.AzulBotonGuardar
import com.duoc.securitysjapp.ui.theme.AzulNavActivo
import com.duoc.securitysjapp.ui.theme.AzulOscuroTopBar
import com.duoc.securitysjapp.ui.theme.PrioridadAltaFondo
import com.duoc.securitysjapp.ui.theme.PrioridadBajaFondo
import com.duoc.securitysjapp.ui.theme.PrioridadMediaFondo
import com.duoc.securitysjapp.ui.theme.PrioridadMediaTexto
import com.duoc.securitysjapp.ui.theme.PrioridadUrgenteFondo
import com.duoc.securitysjapp.ui.theme.SeparadorListaCaso
import com.duoc.securitysjapp.ui.theme.TextoEtiquetaFormulario
import com.duoc.securitysjapp.ui.theme.TextoFechaCaso
import com.duoc.securitysjapp.ui.theme.TextoNavInactivo
import com.duoc.securitysjapp.ui.theme.TextoUbicacionCaso
import com.duoc.securitysjapp.viewModel.ListaCasosViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class DestinoNavInferior {
    Inicio,
    Casos,
    Perfil,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCasosScreen(
    viewModel: ListaCasosViewModel,
    onNavigateBack: () -> Unit = {},
    onCasoClick: (Caso) -> Unit = {},
    onInicioClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onNuevoCasoClick: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        viewModel.cargarCasos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis casos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulOscuroTopBar,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            BarraNavegacionInferior(
                destinoSeleccionado = DestinoNavInferior.Casos,
                onInicioClick = onInicioClick,
                onCasosClick = { viewModel.cargarCasos() },
                onPerfilClick = onPerfilClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoCasoClick,
                containerColor = AzulBotonGuardar,
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo caso",
                )
            }
        },
        containerColor = Color.White,
    ) { innerPadding ->
        if (viewModel.casos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No hay casos registrados",
                    color = TextoFechaCaso,
                    fontSize = 16.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                items(
                    items = viewModel.casos,
                    key = { it.id },
                ) { caso ->
                    ItemCaso(
                        caso = caso,
                        onClick = { onCasoClick(caso) },
                    )
                    HorizontalDivider(
                        color = SeparadorListaCaso,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraNavegacionInferior(
    destinoSeleccionado: DestinoNavInferior,
    onInicioClick: () -> Unit,
    onCasosClick: () -> Unit,
    onPerfilClick: () -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ItemBarraInferior(
                etiqueta = "Inicio",
                icono = Icons.Default.Home,
                seleccionado = destinoSeleccionado == DestinoNavInferior.Inicio,
                onClick = onInicioClick,
            )
            ItemBarraInferior(
                etiqueta = "Casos",
                icono = Icons.AutoMirrored.Filled.List,
                seleccionado = destinoSeleccionado == DestinoNavInferior.Casos,
                onClick = onCasosClick,
            )
            ItemBarraInferior(
                etiqueta = "Perfil",
                icono = Icons.Default.Person,
                seleccionado = destinoSeleccionado == DestinoNavInferior.Perfil,
                onClick = onPerfilClick,
            )
        }
    }
}

@Composable
private fun ItemBarraInferior(
    etiqueta: String,
    icono: ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit,
) {
    val color = if (seleccionado) AzulNavActivo else TextoNavInactivo
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = etiqueta,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = etiqueta,
            color = color,
            fontSize = 12.sp,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun ItemCaso(
    caso: Caso,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = formatearIdCaso(caso.id),
                    color = AzulOscuroTopBar,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.width(12.dp))
                BadgePrioridad(prioridad = caso.prioridad)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tituloCaso(caso),
                color = TextoEtiquetaFormulario,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = caso.ubicacion,
                color = TextoUbicacionCaso,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatearFechaCaso(caso.fechaCreacionMillis),
                color = TextoFechaCaso,
                fontSize = 13.sp,
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ver detalle",
            tint = AzulOscuroTopBar,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun BadgePrioridad(prioridad: String) {
    val (fondo, texto) = coloresPrioridad(prioridad)
    Text(
        text = prioridad,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = texto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
    )
}

private fun coloresPrioridad(prioridad: String): Pair<Color, Color> {
    return when (prioridad.lowercase(Locale.getDefault())) {
        "alta" -> PrioridadAltaFondo to Color.White
        "media" -> PrioridadMediaFondo to PrioridadMediaTexto
        "baja" -> PrioridadBajaFondo to Color.White
        "urgente" -> PrioridadUrgenteFondo to Color.White
        else -> PrioridadMediaFondo to PrioridadMediaTexto
    }
}

private fun formatearIdCaso(id: Long): String = "#%04d".format(id)

private fun tituloCaso(caso: Caso): String {
    return caso.descripcion.ifBlank { caso.categoria }
}

private fun formatearFechaCaso(millis: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return formato.format(Date(millis))
}
