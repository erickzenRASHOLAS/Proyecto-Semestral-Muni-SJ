package com.duoc.securitysjapp.viewModel

import android.content.Context
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CasoViewModel(private val repository: CasoRepository) : ViewModel() {

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

    fun guardar(usuarioId: Int, context: Context) {
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

        if (hayError) return

        viewModelScope.launch {
            guardando = true
            delay(1200) // Simula la respuesta al guardar el caso

            var fotoRuta: String? = null
            fotoBitmap?.let { bitmap ->
                withContext(Dispatchers.IO) {
                    try {
                        val file = File(context.filesDir, "evidencia_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(file).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        }
                        fotoRuta = file.absolutePath
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            repository.insert(
                Caso(
                    usuarioId = usuarioId,
                    categoria = casoForm.categoria,
                    descripcion = casoForm.descripcion.trim(),
                    ubicacion = casoForm.ubicacion.trim(),
                    prioridad = casoForm.prioridad,
                    fotoRuta = fotoRuta,
                )
            )
            guardando = false
            guardadoExitoso = true
            limpiarFormularioYErrores()
        }
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
        fun provideFactory(repository: CasoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CasoViewModel(repository) as T
                }
            }
    }
}
