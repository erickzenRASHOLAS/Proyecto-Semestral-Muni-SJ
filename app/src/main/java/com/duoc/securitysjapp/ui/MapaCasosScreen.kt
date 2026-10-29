package com.duoc.securitysjapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.ui.theme.AzulBotonGuardar
import com.duoc.securitysjapp.ui.theme.AzulOscuroTopBar
import com.duoc.securitysjapp.ui.theme.TextoEtiquetaFormulario
import com.duoc.securitysjapp.ui.theme.TextoUbicacionCaso
import com.duoc.securitysjapp.viewModel.MapaCasosViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

// Punto inicial del mapa: San Joaquín (se usa mientras no haya casos)
private val SAN_JOAQUIN = LatLng(-33.4993, -70.6165)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaCasosScreen(
    viewModel: MapaCasosViewModel,
    onNavigateBack: () -> Unit,
    onVerDetalle: (Caso) -> Unit,
) {
    val casos = viewModel.casosConUbicacion
    val casoSeleccionado = viewModel.casoSeleccionado

    // Se crea una sola vez y guarda dónde está mirando la cámara del mapa
    val camaraEstado = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(SAN_JOAQUIN, 14f)
    }

    // Cuando llegan los casos de la base de datos, centra el mapa en el más reciente.
    // Solo se ejecuta cuando cambia la lista, no en cada redibujo.
    LaunchedEffect(casos) {
        val masReciente = casos.firstOrNull() ?: return@LaunchedEffect
        val latitud = masReciente.latitud ?: return@LaunchedEffect
        val longitud = masReciente.longitud ?: return@LaunchedEffect
        camaraEstado.move(CameraUpdateFactory.newLatLngZoom(LatLng(latitud, longitud), 15f))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Mapa de casos", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Un solo mapa en toda la pantalla (cada vez que se crea el mapa Google lo cuenta como una carga)
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camaraEstado,
                onMapClick = { viewModel.quitarSeleccion() }, // tocar el mapa vacío cierra la tarjeta
            ) {
                // Un pin por cada caso
                casos.forEach { caso ->
                    val latitud = caso.latitud ?: return@forEach
                    val longitud = caso.longitud ?: return@forEach

                    key(caso.id) {
                        // El color del pin depende de la prioridad (se calcula una vez por caso)
                        val iconoPin = remember(caso.prioridad) {
                            BitmapDescriptorFactory.defaultMarker(colorPin(caso.prioridad))
                        }
                        Marker(
                            state = rememberMarkerState(position = LatLng(latitud, longitud)),
                            icon = iconoPin,
                            onClick = {
                                viewModel.seleccionarCaso(caso)
                                true // true = no mostrar la burbuja por defecto, usamos nuestra tarjeta
                            },
                        )
                    }
                }
            }

            // Aviso cuando todavía no hay casos con ubicación
            if (casos.isEmpty()) {
                Text(
                    text = "Aún no hay casos con ubicación GPS",
                    color = TextoUbicacionCaso,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                )
            }

            // Tarjeta con el resumen del caso, aparece deslizándose desde abajo
            AnimatedVisibility(
                visible = casoSeleccionado != null,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = ExitTransition.None,
            ) {
                casoSeleccionado?.let { caso ->
                    TarjetaCaso(caso = caso, onVerDetalle = { onVerDetalle(caso) })
                }
            }
        }
    }
}

// Resumen del caso que se muestra al tocar un pin
@Composable
private fun TarjetaCaso(
    caso: Caso,
    onVerDetalle: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "#%04d · %s".format(caso.id, caso.prioridad),
                color = AzulOscuroTopBar,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = caso.categoria,
                color = TextoEtiquetaFormulario,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            Text(
                text = caso.descripcion,
                color = TextoUbicacionCaso,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estado: ${caso.estado}",
                color = TextoUbicacionCaso,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onVerDetalle,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulBotonGuardar,
                    contentColor = Color.White,
                ),
            ) {
                Text(text = "Ver detalle", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Color del pin según la prioridad (valores de matiz que entrega Google Maps)
private fun colorPin(prioridad: String): Float {
    return when (prioridad) {
        "Urgente" -> BitmapDescriptorFactory.HUE_RED
        "Alta" -> BitmapDescriptorFactory.HUE_ORANGE
        "Media" -> BitmapDescriptorFactory.HUE_YELLOW
        else -> BitmapDescriptorFactory.HUE_GREEN // Baja
    }
}
