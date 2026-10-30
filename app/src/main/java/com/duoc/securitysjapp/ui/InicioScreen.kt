package com.duoc.securitysjapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.securitysjapp.R
import com.duoc.securitysjapp.ui.theme.AzulBotonGuardar
import com.duoc.securitysjapp.ui.theme.AzulOscuroTopBar
import com.duoc.securitysjapp.ui.theme.SeparadorListaCaso
import com.duoc.securitysjapp.ui.theme.TextoFechaCaso
import com.duoc.securitysjapp.viewModel.ProfileViewModel

// Menú principal: reutiliza ProfileViewModel para mostrar el nombre del usuario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    viewModel: ProfileViewModel,
    usuarioId: Int,
    onNuevoCasoClick: () -> Unit = {},
    onMisCasosClick: () -> Unit = {},
    onTodosCasosClick: () -> Unit = {},
    onMapaClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
) {
    LaunchedEffect(usuarioId) {
        viewModel.cargarPerfil(usuarioId)
    }
    val nombre by viewModel.nombreUsuario.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Logo de la municipalidad a la izquierda del título
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_muni_san_joaquin),
                            contentDescription = "Logo Municipalidad de San Joaquín",
                            modifier = Modifier.height(40.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "San Joaquín", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulOscuroTopBar,
                    titleContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            BarraNavegacionInferior(
                destinoSeleccionado = DestinoNavInferior.Inicio,
                onInicioClick = {},
                onCasosClick = onMisCasosClick,
                onPerfilClick = onPerfilClick,
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Hola, $nombre",
                color = AzulOscuroTopBar,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
            Text(
                text = "¿Qué deseas hacer hoy?",
                color = TextoFechaCaso,
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))

            BotonMenu(
                texto = "Registrar nuevo caso",
                icono = Icons.Default.Add,
                destacado = true,
                onClick = onNuevoCasoClick,
            )
            BotonMenu(
                texto = "Mis casos",
                icono = Icons.AutoMirrored.Filled.List,
                onClick = onMisCasosClick,
            )
            BotonMenu(
                texto = "Todos los casos",
                icono = Icons.Default.Search,
                onClick = onTodosCasosClick,
            )
            BotonMenu(
                texto = "Mapa de casos",
                icono = Icons.Default.LocationOn,
                onClick = onMapaClick,
            )
            BotonMenu(
                texto = "Mi perfil",
                icono = Icons.Default.Person,
                onClick = onPerfilClick,
            )
        }
    }
}

// Botón grande del menú (el principal va en azul)
@Composable
private fun BotonMenu(
    texto: String,
    icono: ImageVector,
    destacado: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (destacado) AzulBotonGuardar else SeparadorListaCaso,
            contentColor = if (destacado) Color.White else AzulOscuroTopBar,
        ),
    ) {
        Icon(imageVector = icono, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = texto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
        )
    }
}
