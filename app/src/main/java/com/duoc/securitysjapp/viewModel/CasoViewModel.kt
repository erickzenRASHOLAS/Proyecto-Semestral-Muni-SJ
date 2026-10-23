package com.duoc.securitysjapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.CasoForm
import com.duoc.securitysjapp.respository.CasoRepository

class CasoViewModel : ViewModel() {

    private val repository = CasoRepository()

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

    fun guardar(): Boolean {
        if (!formularioValido()) {
            guardadoExitoso = false
            return false
        }
        repository.insert(
            Caso(
                categoria = casoForm.categoria,
                descripcion = casoForm.descripcion.trim(),
                ubicacion = casoForm.ubicacion.trim(),
                prioridad = casoForm.prioridad,
            )
        )
        guardadoExitoso = true
        limpiarFormulario()
        return true
    }

    fun getAll(): List<Caso> = repository.getAll()

    fun consumirGuardadoExitoso() {
        guardadoExitoso = false
    }

    private fun formularioValido(): Boolean {
        return casoForm.categoria.isNotBlank() &&
            casoForm.descripcion.isNotBlank() &&
            casoForm.ubicacion.isNotBlank() &&
            casoForm.prioridad.isNotBlank()
    }

    private fun limpiarFormulario() {
        casoForm = CasoForm()
    }
}
