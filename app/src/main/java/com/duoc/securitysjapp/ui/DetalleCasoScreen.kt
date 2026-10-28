package com.duoc.securitysjapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.duoc.securitysjapp.ui.theme.TextoEtiquetaFormulario
import com.duoc.securitysjapp.ui.theme.TextoFechaCaso
import com.duoc.securitysjapp.viewModel.DetalleCasoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCasoScreen(
    viewModel: DetalleCasoViewModel,
    onNavigateBack: () -> Unit = {},
    onCasoCerrado: (Long) -> Unit = {},
) {
    LaunchedEffect(Unit) {
        viewModel.cargarCaso()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del caso",
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
        containerColor = Color.White,
    ) { innerPadding ->
        val caso = viewModel.caso
        if (caso == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No se encontró el caso",
                    color = TextoFechaCaso,
                    fontSize = 16.sp,
                )
            }
        } else {
            ContenidoDetalleCaso(
                viewModel = viewModel,
                caso = caso,
                creadoPor = viewModel.creadoPor,
                puedeCerrar = viewModel.puedeCambiarEstado,
                onCerrarCaso = { viewModel.cerrarCaso { onCasoCerrado(caso.id) } },
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun ContenidoDetalleCaso(
    viewModel: DetalleCasoViewModel,
    caso: Caso,
    creadoPor: String,
    puedeCerrar: Boolean,
    onCerrarCaso: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = formatearIdCaso(caso.id),
                color = AzulOscuroTopBar,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
            )
            BadgePrioridadDetalle(prioridad = caso.prioridad)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = tituloDetalleCaso(caso),
            color = AzulOscuroTopBar,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilaDatoDetalle(
            icono = Icons.Default.DateRange,
            etiqueta = "Fecha y hora",
            valor = formatearFechaDetalle(caso.fechaCreacionMillis),
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilaDatoDetalle(
            icono = Icons.Default.LocationOn,
            etiqueta = "Ubicación",
            valor = caso.ubicacion,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilaDatoDetalle(
            icono = Icons.Default.Shield,
            etiqueta = "Prioridad",
            valor = caso.prioridad,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilaDatoDetalle(
            icono = Icons.AutoMirrored.Filled.Message,
            etiqueta = "Estado",
            valor = caso.estado,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilaDatoDetalle(
            icono = Icons.Default.Person,
            etiqueta = "Creado por",
            valor = creadoPor,
        )

        if (viewModel.fotoBitmap != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Evidencia fotográfica",
                color = AzulOscuroTopBar,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = viewModel.fotoBitmap!!.asImageBitmap(),
                contentDescription = "Evidencia fotográfica del incidente",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Descripción",
            color = AzulOscuroTopBar,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = caso.descripcion,
            color = TextoEtiquetaFormulario,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        )

        // El botón solo aparece si el usuario puede cambiar el estado (lo decide el ViewModel)
        if (puedeCerrar) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onCerrarCaso,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulBotonGuardar,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "Cerrar caso",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FilaDatoDetalle(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(AzulNavActivo.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = etiqueta,
                tint = AzulOscuroTopBar,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = etiqueta,
                color = TextoFechaCaso,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valor,
                color = AzulOscuroTopBar,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
private fun BadgePrioridadDetalle(prioridad: String) {
    val (fondo, texto) = coloresPrioridadDetalle(prioridad)
    Text(
        text = prioridad,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        color = texto,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
    )
}

private fun coloresPrioridadDetalle(prioridad: String): Pair<Color, Color> {
    return when (prioridad.lowercase(Locale.getDefault())) {
        "alta" -> PrioridadAltaFondo to Color.White
        "media" -> PrioridadMediaFondo to PrioridadMediaTexto
        "baja" -> PrioridadBajaFondo to Color.White
        "urgente" -> PrioridadUrgenteFondo to Color.White
        else -> PrioridadMediaFondo to PrioridadMediaTexto
    }
}

private fun formatearIdCaso(id: Long): String = "#%04d".format(id)

private fun tituloDetalleCaso(caso: Caso): String {
    return caso.categoria.ifBlank { caso.descripcion }
}

private fun formatearFechaDetalle(millis: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return formato.format(Date(millis))
}
