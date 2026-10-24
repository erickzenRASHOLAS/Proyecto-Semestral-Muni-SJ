package com.duoc.securitysjapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.securitysjapp.viewModel.UsuarioFormViewModel

@Composable
fun LoginScreen(viewModel: UsuarioFormViewModel, onLoginExitoso: () -> Unit) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val loginEstado by viewModel.loginExitoso.collectAsState()
    var mensajeError by remember { mutableStateOf("") }

    LaunchedEffect(loginEstado) {
        when (loginEstado) {
            true -> {
                viewModel.resetEstados()
                onLoginExitoso()
            }
            false -> {
                mensajeError = "Credenciales incorrectas"
                viewModel.resetEstados()
            }
            null -> {}
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Inicio de Sesión", style = MaterialTheme.typography.headlineMedium)
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

        if (mensajeError.isNotEmpty()) {
            Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.iniciarSesion(correo, contrasena) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}