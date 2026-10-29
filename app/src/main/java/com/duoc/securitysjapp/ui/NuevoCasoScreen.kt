package com.duoc.securitysjapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.duoc.securitysjapp.ui.theme.AzulBotonGuardar
import com.duoc.securitysjapp.ui.theme.AzulOscuroTopBar
import com.duoc.securitysjapp.ui.theme.BordeCampoFormulario
import com.duoc.securitysjapp.ui.theme.FondoCampoFormulario
import com.duoc.securitysjapp.ui.theme.TextoEtiquetaFormulario
import com.duoc.securitysjapp.ui.theme.TextoPlaceholder
import com.duoc.securitysjapp.viewModel.CasoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoCasoScreen(
    viewModel: CasoViewModel,
    usuarioId: Int,
    onNavigateBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val campoColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = FondoCampoFormulario,
        focusedContainerColor = FondoCampoFormulario,
        unfocusedBorderColor = BordeCampoFormulario,
        focusedBorderColor = AzulBotonGuardar,
        unfocusedTextColor = TextoEtiquetaFormulario,
        focusedTextColor = TextoEtiquetaFormulario,
        unfocusedPlaceholderColor = TextoPlaceholder,
        focusedPlaceholderColor = TextoPlaceholder,
    )
    val formaCampo = RoundedCornerShape(12.dp)

    // Launcher para tomar foto de evidencia
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.fotoBitmap = bitmap
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

    // Launcher para pedir permiso de ubicación (precisa y aproximada)
    val ubicacionPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Con o sin permiso el caso se guarda; sin permiso solo aparecerá en la lista, no en el mapa
        viewModel.guardar(usuarioId, context)
    }

    // Valida el formulario y, si falta el permiso de ubicación, lo pide antes de guardar
    fun guardarCaso() {
        if (!viewModel.validarFormulario()) return
        if (viewModel.tienePermisoUbicacion(context)) {
            viewModel.guardar(usuarioId, context)
        } else {
            ubicacionPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    fun tomarFoto() {
        if (viewModel.guardando) return
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

    if (viewModel.guardadoExitoso) {
        AlertDialog(
            onDismissRequest = {
                viewModel.consumirGuardadoExitoso()
                onNavigateBack()
            },
            title = { Text("Caso guardado") },
            text = { Text("El caso se registró correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.consumirGuardadoExitoso()
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo caso",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !viewModel.guardando
                    ) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            EtiquetaCampo("Categoría")
            Spacer(modifier = Modifier.height(8.dp))
            DropdownCampo(
                valor = viewModel.casoForm.categoria,
                placeholder = "Selecciona una categoría",
                opciones = viewModel.categorias,
                enabled = !viewModel.guardando,
                onSeleccion = {
                    viewModel.casoForm.categoria = it
                    viewModel.limpiarErrorCategoria()
                },
                isError = viewModel.errorCategoria != null,
                errorText = viewModel.errorCategoria,
                colors = campoColors,
                shape = formaCampo,
            )

            Spacer(modifier = Modifier.height(20.dp))
            EtiquetaCampo("Descripción")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.casoForm.descripcion,
                onValueChange = {
                    viewModel.casoForm.descripcion = it
                    if (viewModel.errorDescripcion != null) viewModel.limpiarErrorDescripcion()
                },
                enabled = !viewModel.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                placeholder = { Text("Describe la situación (mín. 10 caracteres)...") },
                isError = viewModel.errorDescripcion != null,
                supportingText = {
                    if (viewModel.errorDescripcion != null) {
                        Text(text = viewModel.errorDescripcion!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = campoColors,
                shape = formaCampo,
                singleLine = false,
            )

            Spacer(modifier = Modifier.height(20.dp))
            EtiquetaCampo("Ubicación (referencial)")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.casoForm.ubicacion,
                onValueChange = {
                    viewModel.casoForm.ubicacion = it
                    if (viewModel.errorUbicacion != null) viewModel.limpiarErrorUbicacion()
                },
                enabled = !viewModel.guardando,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Plaza, sector, calle...") },
                isError = viewModel.errorUbicacion != null,
                supportingText = {
                    if (viewModel.errorUbicacion != null) {
                        Text(text = viewModel.errorUbicacion!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = campoColors,
                shape = formaCampo,
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(20.dp))
            EtiquetaCampo("Prioridad")
            Spacer(modifier = Modifier.height(8.dp))
            DropdownCampo(
                valor = viewModel.casoForm.prioridad,
                placeholder = "Selecciona prioridad",
                opciones = viewModel.prioridades,
                enabled = !viewModel.guardando,
                onSeleccion = {
                    viewModel.casoForm.prioridad = it
                    viewModel.limpiarErrorPrioridad()
                },
                isError = viewModel.errorPrioridad != null,
                errorText = viewModel.errorPrioridad,
                colors = campoColors,
                shape = formaCampo,
            )

            Spacer(modifier = Modifier.height(20.dp))
            EtiquetaCampo("Evidencia fotográfica (opcional)")
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.fotoBitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(formaCampo)
                ) {
                    Image(
                        bitmap = viewModel.fotoBitmap!!.asImageBitmap(),
                        contentDescription = "Evidencia capturada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { viewModel.removerFoto() },
                        enabled = !viewModel.guardando,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar foto",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = ::tomarFoto,
                    enabled = !viewModel.guardando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = formaCampo,
                    border = BorderStroke(1.dp, BordeCampoFormulario),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = FondoCampoFormulario,
                        contentColor = TextoEtiquetaFormulario
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Tomar foto",
                        tint = AzulBotonGuardar,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tomar foto de evidencia", color = TextoEtiquetaFormulario)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = ::guardarCaso,
                enabled = !viewModel.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulBotonGuardar,
                    contentColor = Color.White,
                ),
            ) {
                if (viewModel.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Guardar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun EtiquetaCampo(texto: String) {
    Text(
        text = texto,
        color = TextoEtiquetaFormulario,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownCampo(
    valor: String,
    placeholder: String,
    opciones: List<String>,
    enabled: Boolean = true,
    onSeleccion: (String) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
    colors: androidx.compose.material3.TextFieldColors,
    shape: RoundedCornerShape,
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expandido && enabled,
            onExpandedChange = { if (enabled) expandido = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                placeholder = { Text(placeholder) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                colors = colors,
                shape = shape,
            )
            ExposedDropdownMenu(
                expanded = expandido && enabled,
                onDismissRequest = { expandido = false },
            ) {
                opciones.forEach { opcion ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onSeleccion(opcion)
                            expandido = false
                        },
                    )
                }
            }
        }
        if (isError && !errorText.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}
