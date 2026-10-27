package com.duoc.securitysjapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.securitysjapp.model.ROL_MUNICIPAL
import com.duoc.securitysjapp.model.ROL_VECINO
import com.duoc.securitysjapp.viewModel.UsuarioFormViewModel

@Composable
fun RegistroScreen(viewModel: UsuarioFormViewModel, onRegistroExitoso: () -> Unit) {

    var nombreCompleto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(ROL_VECINO) }
    val registroEstado by viewModel.registroExitoso.collectAsState()

    LaunchedEffect(registroEstado) {
        if (registroEstado == true) {
            viewModel.resetEstados()
            onRegistroExitoso()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { nombreCompleto = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tipo de perfil (el municipal puede cambiar el estado de cualquier caso)
        Text(text = "Tipo de perfil")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = rol == ROL_VECINO, onClick = { rol = ROL_VECINO })
            Text(text = ROL_VECINO)
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = rol == ROL_MUNICIPAL, onClick = { rol = ROL_MUNICIPAL })
            Text(text = ROL_MUNICIPAL)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.registrarUsuario(nombreCompleto, correo, contrasena, rol) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}