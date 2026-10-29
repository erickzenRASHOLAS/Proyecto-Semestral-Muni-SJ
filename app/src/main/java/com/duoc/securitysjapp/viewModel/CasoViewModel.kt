package com.duoc.securitysjapp.viewModel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.CasoForm
import com.duoc.securitysjapp.repository.CasoRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
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

    // Revisa los campos del formulario y devuelve true si todoo está correcto
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
    fun guardar(usuarioId: Int, context: Context) {
        viewModelScope.launch {
            guardando = true
            val ubicacionGps = obtenerUbicacion(context)
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
                    latitud = ubicacionGps?.latitude,
                    longitud = ubicacionGps?.longitude,
                )
            )
            guardando = false
            guardadoExitoso = true
            limpiarFormularioYErrores()
        }
    }

    // Indica si el usuario ya aceptó el permiso de ubicación (precisa o aproximada)
    fun tienePermisoUbicacion(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Pide la ubicación actual al GPS. Devuelve null si no hay permiso o no se pudo obtener
    @SuppressLint("MissingPermission") // el permiso se revisa en la primera línea
    private suspend fun obtenerUbicacion(context: Context): Location? {
        if (!tienePermisoUbicacion(context)) return null

        // Cliente de ubicación de Google (el mismo que usa GPS.kt del ejemplo)
        val cliente = LocationServices.getFusedLocationProviderClient(context)
        return try {
            // Espera máximo 8 segundos para no dejar la pantalla cargando
            withTimeoutOrNull(8000) {
                cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            }
        } catch (e: Exception) {
            null
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
