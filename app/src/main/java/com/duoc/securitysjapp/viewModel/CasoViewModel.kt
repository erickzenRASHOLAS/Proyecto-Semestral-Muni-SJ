package com.duoc.securitysjapp.viewModel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class CasoViewModel(private val repository: CasoRepository) : ViewModel() {

    var casoForm: CasoForm by mutableStateOf(CasoForm())
    var guardadoExitoso by mutableStateOf(false)
    var guardando by mutableStateOf(false)
    var obteniendoUbicacion by mutableStateOf(false)

    // Coordenadas obtenidas con el botón (null si la ubicación se escribió a mano)
    private var latitud: Double? = null
    private var longitud: Double? = null

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
                    latitud = latitud,
                    longitud = longitud,
                )
            )
            guardando = false
            guardadoExitoso = true
            limpiarFormularioYErrores()
        }
    }

    // Lee la ubicación del dispositivo y la escribe en el campo "Ubicación"
    @SuppressLint("MissingPermission") // el permiso se revisa justo abajo
    fun usarMiUbicacion(context: Context) {
        val tienePermiso =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!tienePermiso) {
            permisoUbicacionDenegado()
            return
        }

        obteniendoUbicacion = true
        errorUbicacion = null

        // Cliente de ubicación de Google (el mismo que usa GPS.kt del ejemplo)
        val cliente = LocationServices.getFusedLocationProviderClient(context)
        cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { ubicacion ->
                obteniendoUbicacion = false
                if (ubicacion != null) {
                    // Se guardan las coordenadas y se muestran en el campo como texto
                    latitud = ubicacion.latitude
                    longitud = ubicacion.longitude
                    casoForm.ubicacion = String.format(Locale.US, "%.5f, %.5f", ubicacion.latitude, ubicacion.longitude)
                } else {
                    errorUbicacion = "No se pudo obtener la ubicación. Revisa que esté activada"
                }
            }
            .addOnFailureListener {
                obteniendoUbicacion = false
                errorUbicacion = "No se pudo obtener la ubicación"
            }
    }

    // Si el usuario escribe la ubicación a mano, las coordenadas anteriores ya no sirven
    fun limpiarCoordenadas() {
        latitud = null
        longitud = null
    }

    fun permisoUbicacionDenegado() {
        errorUbicacion = "Permiso de ubicación denegado"
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
        limpiarCoordenadas()
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
