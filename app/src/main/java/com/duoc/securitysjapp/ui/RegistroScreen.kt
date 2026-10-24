package com.duoc.securitysjapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.securitysjapp.viewModel.UsuarioFormViewModel

@Composable
fun RegistroScreen(viewModel: UsuarioFormViewModel, onRegistroExitoso: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val registroEstado by viewModel.registroExitoso.collectAsState()

    // Observar si el registro fue exitoso
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
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.registrarUsuario(correo, contrasena) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}