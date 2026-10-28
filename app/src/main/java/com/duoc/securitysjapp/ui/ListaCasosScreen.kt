package com.duoc.securitysjapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.ui.theme.AzulBotonGuardar
import com.duoc.securitysjapp.ui.theme.AzulOscuroTopBar
import com.duoc.securitysjapp.ui.theme.BordeCampoFormulario
import com.duoc.securitysjapp.ui.theme.FondoCampoFormulario
import com.duoc.securitysjapp.ui.theme.PrioridadAltaFondo
import com.duoc.securitysjapp.ui.theme.PrioridadBajaFondo
import com.duoc.securitysjapp.ui.theme.PrioridadMediaFondo
import com.duoc.securitysjapp.ui.theme.PrioridadMediaTexto
import com.duoc.securitysjapp.ui.theme.PrioridadUrgenteFondo
import com.duoc.securitysjapp.ui.theme.SeparadorListaCaso
import com.duoc.securitysjapp.ui.theme.TextoEtiquetaFormulario
import com.duoc.securitysjapp.ui.theme.TextoFechaCaso
import com.duoc.securitysjapp.ui.theme.TextoPlaceholder
import com.duoc.securitysjapp.ui.theme.TextoUbicacionCaso
import com.duoc.securitysjapp.viewModel.ListaCasosViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Se usa tanto para "Mis casos" como para "Todos los casos"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCasosScreen(
    viewModel: ListaCasosViewModel,
    titulo: String,
    mostrarBarraInferior: Boolean = true, // solo "Mis casos" la muestra
    onNavigateBack: () -> Unit = {},
    onCasoClick: (Caso) -> Unit = {},
    onInicioClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        viewModel.cargarCasos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titulo,
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
            if (mostrarBarraInferior) {
                BarraNavegacionInferior(
                    destinoSeleccionado = DestinoNavInferior.Casos,
                    onInicioClick = onInicioClick,
                    onCasosClick = { viewModel.cargarCasos() },
                    onPerfilClick = onPerfilClick,
                )
            }
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sección de Búsqueda
            OutlinedTextField(
                value = viewModel.busquedaQuery,
                onValueChange = { viewModel.onBusquedaQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar por descripción, ubicación...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextoPlaceholder
                    )
                },
                trailingIcon = {
                    if (viewModel.busquedaQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onBusquedaQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda",
                                tint = TextoPlaceholder
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = FondoCampoFormulario,
                    focusedContainerColor = FondoCampoFormulario,
                    unfocusedBorderColor = BordeCampoFormulario,
                    focusedBorderColor = AzulBotonGuardar,
                    unfocusedTextColor = TextoEtiquetaFormulario,
                    focusedTextColor = TextoEtiquetaFormulario,
                    unfocusedPlaceholderColor = TextoPlaceholder,
                    focusedPlaceholderColor = TextoPlaceholder,
                )
            )

            // Chips de Filtro por Categoría
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(viewModel.categoriasFiltro) { cat ->
                    val seleccionada = cat == viewModel.categoriaFiltro
                    FilterChip(
                        selected = seleccionada,
                        onClick = { viewModel.onCategoriaFiltroChange(cat) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulOscuroTopBar,
                            selectedLabelColor = Color.White,
                            containerColor = FondoCampoFormulario,
                            labelColor = TextoEtiquetaFormulario
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Lista o mensaje de sin resultados
            if (viewModel.casosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (viewModel.busquedaQuery.isNotEmpty() || viewModel.categoriaFiltro != "Todas")
                            "No se encontraron casos con los filtros aplicados"
                        else
                            "No hay casos registrados",
                        color = TextoFechaCaso,
                        fontSize = 16.sp,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = viewModel.casosFiltrados,
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
