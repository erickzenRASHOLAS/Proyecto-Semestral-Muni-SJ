package com.duoc.securitysjapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.securitysjapp.ui.DetalleCasoScreen
import com.duoc.securitysjapp.ui.ListaCasosScreen
import com.duoc.securitysjapp.ui.theme.SecuritySJAppTheme
import com.duoc.securitysjapp.viewModel.ListaCasosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecuritySJAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    val listaCasosViewModel: ListaCasosViewModel = viewModel()
                    var casoDetalleId by remember { mutableLongStateOf(0L) }
                    var mostrarDetalle by remember { mutableStateOf(false) }

                    if (mostrarDetalle) {
                        DetalleCasoScreen(
                            casoId = casoDetalleId,
                            onNavigateBack = {
                                mostrarDetalle = false
                                listaCasosViewModel.cargarCasos()
                            },
                        )
                    } else {
                        ListaCasosScreen(
                            viewModel = listaCasosViewModel,
                            onNavigateBack = { finish() },
                            onCasoClick = { caso ->
                                casoDetalleId = caso.id
                                mostrarDetalle = true
                            },
                        )
                    }
                }
            }
        }
    }
}
