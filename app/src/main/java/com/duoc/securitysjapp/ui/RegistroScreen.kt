package com.duoc.securitysjapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

    val cargando by viewModel.cargando.collectAsState()
    val registroEstado by viewModel.registroExitoso.collectAsState()
    val errorNombre by viewModel.errorNombre.collectAsState()
    val errorCorreo by viewModel.errorCorreo.collectAsState()
    val errorContrasena by viewModel.errorContrasena.collectAsState()

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
            onValueChange = {
                nombreCompleto = it
                if (errorNombre != null) viewModel.limpiarErrorNombre()
            },
            label = { Text("Nombre completo") },
            // Ícono de persona al inicio del campo
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            enabled = !cargando,
            isError = errorNombre != null,
            supportingText = {
                if (errorNombre != null) {
                    Text(text = errorNombre!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                if (errorCorreo != null) viewModel.limpiarErrorCorreo()
            },
            label = { Text("Correo electrónico") },
            // Ícono de carta al inicio del campo
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            enabled = !cargando,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errorCorreo != null,
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
                if (errorContrasena != null) viewModel.limpiarErrorContrasena()
            },
            label = { Text("Contraseña") },
            // Ícono de candado al inicio del campo
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            enabled = !cargando,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = errorContrasena != null,
            supportingText = {
                if (errorContrasena != null) {
                    Text(text = errorContrasena!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tipo de perfil (el municipal puede cambiar el estado de cualquier caso)
        Text(text = "Tipo de perfil")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = rol == ROL_VECINO,
                onClick = { rol = ROL_VECINO },
                enabled = !cargando
            )
            Text(text = ROL_VECINO)
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = rol == ROL_MUNICIPAL,
                onClick = { rol = ROL_MUNICIPAL },
                enabled = !cargando
            )
            Text(text = ROL_MUNICIPAL)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.registrarUsuario(nombreCompleto, correo, contrasena, rol) },
            enabled = !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text("Registrarse")
            }
        }
    }
}
