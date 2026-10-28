package com.duoc.securitysjapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.duoc.securitysjapp.viewModel.UsuarioFormViewModel

@Composable
fun LoginScreen(
    viewModel: UsuarioFormViewModel,
    onLoginExitoso: (usuarioId: Int) -> Unit,
    onIrARegistro: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val loginEstado by viewModel.loginExitoso.collectAsState()
    val errorCorreo by viewModel.errorCorreoLogin.collectAsState()
    val errorContrasena by viewModel.errorContrasenaLogin.collectAsState()
    val errorGeneral by viewModel.errorGeneralLogin.collectAsState()

    LaunchedEffect(loginEstado) {
        if (loginEstado == true) {
            val id = viewModel.usuarioId.value
            viewModel.resetEstados()
            onLoginExitoso(id)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Inicio de Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                if (errorCorreo != null || errorGeneral != null) viewModel.limpiarErrorCorreoLogin()
            },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errorCorreo != null || errorGeneral != null,
            supportingText = {
                if (errorCorreo != null) {
                    Text(text = errorCorreo!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                if (errorContrasena != null || errorGeneral != null) viewModel.limpiarErrorContrasenaLogin()
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorContrasena != null || errorGeneral != null,
            supportingText = {
                if (errorContrasena != null) {
                    Text(text = errorContrasena!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorGeneral != null) {
            Text(
                text = errorGeneral!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.iniciarSesion(correo, contrasena) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}
