package com.duoc.securitysjapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.securitysjapp.ui.theme.AzulNavActivo
import com.duoc.securitysjapp.ui.theme.TextoNavInactivo

enum class DestinoNavInferior {
    Inicio,
    Casos, // "Casos" muestra Mis casos
    Perfil,
}

// Barra inferior que comparten Inicio, Mis casos y Perfil
@Composable
fun BarraNavegacionInferior(
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
