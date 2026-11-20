package com.duoc.securitysjapp.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.CasoForm
import com.duoc.securitysjapp.repository.CasoRepository
import com.duoc.securitysjapp.repository.FotoRepository
import com.duoc.securitysjapp.repository.UbicacionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CasoViewModel(
    private val repository: CasoRepository,
    private val ubicacionRepository: UbicacionRepository,
    private val fotoRepository: FotoRepository,
) : ViewModel() {

    var casoForm: CasoForm by mutableStateOf(CasoForm())
    var guardadoExitoso by mutableStateOf(false)
    var guardando by mutableStateOf(false)

    // Evidencia fotográfica opcional
    var fotoBitmap by mutableStateOf<Bitmap?>(null)

    // Errores de validación
    var errorCategoria by mutableStateOf<String?>(null)
    var errorDescripcion by mutableStateOf<String?>(null)
    var errorUbicacion by mutableStateOf<String?>(null)
    var errorPrioridad by mutableStateOf<String?>(null)

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

    // Revisa los campos del formulario y devuelve true si toodo está correcto
    fun validarFormulario(): Boolean {
        var hayError = false

        if (casoForm.categoria.isBlank()) {
            errorCategoria = "Debes seleccionar una categoría"
            hayError = true
        } else {
            errorCategoria = null
        }

        if (casoForm.descripcion.isBlank()) {
            errorDescripcion = "La descripción es obligatoria"
            hayError = true
        } else if (casoForm.descripcion.trim().length < 10) {
            errorDescripcion = "La descripción debe tener al menos 10 caracteres"
            hayError = true
        } else {
            errorDescripcion = null
        }

        if (casoForm.ubicacion.isBlank()) {
            errorUbicacion = "Debes indicar una ubicación"
            hayError = true
        } else {
            errorUbicacion = null
        }

        if (casoForm.prioridad.isBlank()) {
            errorPrioridad = "Debes seleccionar una prioridad"
            hayError = true
        } else {
            errorPrioridad = null
        }

        return !hayError
    }

    // Guarda el caso. Si el usuario dio permiso, se guarda también su ubicación para mostrar el pin en el mapa
    fun guardar(usuarioId: Int) {
        viewModelScope.launch {
            guardando = true

            // El repositorio pide el GPS (devuelve null si no hay permiso o no responde)
            val ubicacionGps = ubicacionRepository.obtenerUbicacion()
            delay(1200) // Simula la respuesta al guardar el caso

            // Si hay foto, el repositorio la guarda como archivo y devuelve su ruta
            val foto = fotoBitmap
            val fotoRuta = if (foto != null) {
                fotoRepository.guardarFoto("evidencia_${System.currentTimeMillis()}.jpg", foto)
            } else {
                null
            }

            repository.insert(
                Caso(
                    usuarioId = usuarioId,
                    categoria = casoForm.categoria,
                    descripcion = casoForm.descripcion.trim(),
                    ubicacion = casoForm.ubicacion.trim(),
                    prioridad = casoForm.prioridad,
                    fotoRuta = fotoRuta,
                    latitud = ubicacionGps?.latitude,
                    longitud = ubicacionGps?.longitude,
                )
            )
            guardando = false
            guardadoExitoso = true
            limpiarFormularioYErrores()
        }
    }

    // Pregunta al repositorio si el usuario ya aceptó el permiso de ubicación
    fun tienePermisoUbicacion(): Boolean {
        return ubicacionRepository.tienePermisoUbicacion()
    }

    fun removerFoto() {
        fotoBitmap = null
    }

    fun limpiarErrorCategoria() { errorCategoria = null }
    fun limpiarErrorDescripcion() { errorDescripcion = null }
    fun limpiarErrorUbicacion() { errorUbicacion = null }
    fun limpiarErrorPrioridad() { errorPrioridad = null }

    fun limpiarFormularioYErrores() {
        casoForm = CasoForm()
        fotoBitmap = null
        errorCategoria = null
        errorDescripcion = null
        errorUbicacion = null
        errorPrioridad = null
    }

    fun consumirGuardadoExitoso() {
        guardadoExitoso = false
    }

    companion object {
        fun provideFactory(
            repository: CasoRepository,
            ubicacionRepository: UbicacionRepository,
            fotoRepository: FotoRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CasoViewModel(repository, ubicacionRepository, fotoRepository) as T
                }
            }
    }
}
