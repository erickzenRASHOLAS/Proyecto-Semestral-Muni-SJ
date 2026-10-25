package com.duoc.securitysjapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.CasoForm
import com.duoc.securitysjapp.repository.CasoRepository
import kotlinx.coroutines.launch

class CasoViewModel(private val repository: CasoRepository) : ViewModel() {

    var casoForm: CasoForm by mutableStateOf(CasoForm())
    var guardadoExitoso by mutableStateOf(false)

    val categorias = listOf(
        "Seguridad",
        "Convivencia",
        "Infraestructura",
        "Medio ambiente",
        "Otro",
    )

    val prioridades = listOf(
        "Baja",
        "Media",
        "Alta",
        "Urgente",
    )

    // Por ahora sin validaciones: guarda lo que haya en el formulario
    fun guardar(usuarioId: Int) {
        viewModelScope.launch {
            repository.insert(
                Caso(
                    usuarioId = usuarioId,
                    categoria = casoForm.categoria,
                    descripcion = casoForm.descripcion.trim(),
                    ubicacion = casoForm.ubicacion.trim(),
                    prioridad = casoForm.prioridad,
                )
            )
            guardadoExitoso = true
            casoForm = CasoForm()
        }
    }

    fun consumirGuardadoExitoso() {
        guardadoExitoso = false
    }

    companion object {
        fun provideFactory(repository: CasoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CasoViewModel(repository) as T
                }
            }
    }
}
