package com.duoc.securitysjapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                onSeleccion = { viewModel.casoForm.categoria = it },
                colors = campoColors,
                shape = formaCampo,
            )

            Spacer(modifier = Modifier.height(20.dp))
            EtiquetaCampo("Descripción")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.casoForm.descripcion,
                onValueChange = { viewModel.casoForm.descripcion = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Describe la situación...") },
                colors = campoColors,
                shape = formaCampo,
                singleLine = false,
            )

            Spacer(modifier = Modifier.height(20.dp))
            EtiquetaCampo("Ubicación (referencial)")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.casoForm.ubicacion,
                onValueChange = { viewModel.casoForm.ubicacion = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Plaza, sector, calle...") },
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
                onSeleccion = { viewModel.casoForm.prioridad = it },
                colors = campoColors,
                shape = formaCampo,
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.guardar(usuarioId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulBotonGuardar,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "Guardar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
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
    onSeleccion: (String) -> Unit,
    colors: androidx.compose.material3.TextFieldColors,
    shape: RoundedCornerShape,
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            placeholder = { Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            colors = colors,
            shape = shape,
        )
        ExposedDropdownMenu(
            expanded = expandido,
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
}
