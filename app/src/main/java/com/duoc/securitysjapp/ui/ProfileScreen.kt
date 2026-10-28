package com.duoc.securitysjapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.duoc.securitysjapp.viewModel.ProfileViewModel

// Pantalla con estado: lee los datos desde el ViewModel
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    usuarioId: Int,
    onNavigateBack: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onCasosClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var mostrarDialogoPassword by remember { mutableStateOf(false) }

    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    LaunchedEffect(usuarioId) {
        viewModel.cargarPerfil(usuarioId)
    }

    val nombre by viewModel.nombreUsuario.collectAsState()
    val correo by viewModel.correoUsuario.collectAsState()
    val rol by viewModel.rolUsuario.collectAsState()
    val fotoBitmap by viewModel.fotoBitmap.collectAsState()
    val procesandoFoto by viewModel.procesandoFoto.collectAsState()

    val errorNueva by viewModel.errorNuevaContrasena.collectAsState()
    val errorConfirmar by viewModel.errorConfirmarContrasena.collectAsState()
    val cambioExitoso by viewModel.cambioContrasenaExitoso.collectAsState()

    LaunchedEffect(cambioExitoso) {
        if (cambioExitoso == true) {
            Toast.makeText(context, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
            mostrarDialogoPassword = false
            nuevaContrasena = ""
            confirmarContrasena = ""
            viewModel.resetEstadoCambioContrasena()
        }
    }

    // Diálogo para Cambiar Contraseña
    if (mostrarDialogoPassword) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoPassword = false
                nuevaContrasena = ""
                confirmarContrasena = ""
                viewModel.resetEstadoCambioContrasena()
            },
            title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nuevaContrasena,
                        onValueChange = {
                            nuevaContrasena = it
                            if (errorNueva != null) viewModel.limpiarErrorNuevaContrasena()
                        },
                        label = { Text("Nueva contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = errorNueva != null,
                        supportingText = {
                            if (errorNueva != null) {
                                Text(text = errorNueva!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmarContrasena,
                        onValueChange = {
                            confirmarContrasena = it
                            if (errorConfirmar != null) viewModel.limpiarErrorConfirmarContrasena()
                        },
                        label = { Text("Confirmar contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = errorConfirmar != null,
                        supportingText = {
                            if (errorConfirmar != null) {
                                Text(text = errorConfirmar!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cambiarContrasena(usuarioId, nuevaContrasena, confirmarContrasena)
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoPassword = false
                        nuevaContrasena = ""
                        confirmarContrasena = ""
                        viewModel.resetEstadoCambioContrasena()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Launcher para tomar la foto (Selfie)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.guardarFotoPerfil(context, usuarioId, bitmap)
        }
    }

    // Launcher para solicitar permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        }
    }

    fun tomarFoto() {
        if (procesandoFoto) return
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            takePictureLauncher.launch(null)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    ProfileContent(
        nombreUsuario = nombre,
        correoUsuario = correo,
        rolUsuario = rol,
        fotoBitmap = fotoBitmap,
        procesandoFoto = procesandoFoto,
        onTomarFoto = ::tomarFoto,
        onNavigateBack = onNavigateBack,
        onChangePasswordClick = {
            mostrarDialogoPassword = true
            onChangePasswordClick()
        },
        onSettingsClick = onSettingsClick,
        onLogoutClick = onLogoutClick,
        onInicioClick = onInicioClick,
        onCasosClick = onCasosClick
    )
}

// Contenido sin estado (fácil de usar en @Preview)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    nombreUsuario: String = "Nombre de Usuario",
    correoUsuario: String = "usuario@correo.com",
    rolUsuario: String = "Vecino",
    fotoBitmap: Bitmap? = null,
    procesandoFoto: Boolean = false,
    onTomarFoto: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onCasosClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D47A1) // Azul institucional
                )
            )
        },
        bottomBar = {
            BarraNavegacionInferior(
                destinoSeleccionado = DestinoNavInferior.Perfil,
                onInicioClick = onInicioClick,
                onCasosClick = onCasosClick,
                onPerfilClick = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Avatar circular con botón de cámara e indicador de carga integrado
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                        .clickable(enabled = !procesandoFoto) { onTomarFoto() },
                    contentAlignment = Alignment.Center
                ) {
                    if (procesandoFoto) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = Color(0xFF0D47A1),
                            strokeWidth = 3.dp
                        )
                    } else if (fotoBitmap != null) {
                        Image(
                            bitmap = fotoBitmap.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar del usuario",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }
                }

                // Botón flotante de cámara
                IconButton(
                    onClick = onTomarFoto,
                    enabled = !procesandoFoto,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(34.dp)
                        .background(
                            if (procesandoFoto) Color.Gray else Color(0xFF0D47A1),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Tomar selfie de perfil",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Datos del registro
            Text(
                text = nombreUsuario,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = rolUsuario,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = correoUsuario,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Lista de opciones replicando el diseño
            ProfileMenuItem(
                icon = Icons.Default.Lock,
                text = "Cambiar contraseña",
                onClick = onChangePasswordClick
            )

            ProfileMenuItem(
                icon = Icons.Default.Settings,
                text = "Configuración",
                onClick = onSettingsClick
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                text = "Cerrar sesión",
                textColor = Color(0xFFD32F2F), // Rojo para destacar la salida
                iconColor = Color(0xFFD32F2F),
                onClick = onLogoutClick
            )
        }
    }
}

// Componente reutilizable para cada fila del menú
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.Black,
    iconColor: Color = Color.Gray,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Ir",
            tint = Color.Gray
        )
    }
}
