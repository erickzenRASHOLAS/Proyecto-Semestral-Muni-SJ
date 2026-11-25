package com.duoc.securitysjapp.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.ROL_MUNICIPAL
import com.duoc.securitysjapp.model.Usuario
import com.duoc.securitysjapp.repository.CasoRepository
import com.duoc.securitysjapp.repository.FotoRepository
import com.duoc.securitysjapp.repository.UsuarioRepository
import kotlinx.coroutines.launch

class DetalleCasoViewModel(
    private val casoRepository: CasoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val fotoRepository: FotoRepository,
    private val casoId: Long,
    private val usuarioId: Int, // usuario que tiene la sesión iniciada
) : ViewModel() {

    var caso by mutableStateOf<Caso?>(null)
        private set

    // Evidencia fotográfica
    var fotoBitmap by mutableStateOf<Bitmap?>(null)
        private set

    // Nombre de quien creó el caso
    var creadoPor by mutableStateOf("")
        private set

    private var usuarioActual by mutableStateOf<Usuario?>(null)

    var mensajeCierre by mutableStateOf<String?>(null)
        private set

    var cerrandoCaso by mutableStateOf(false)
        private set

    // Solo el creador del caso o un perfil municipal puede cambiar el estado
    val puedeCambiarEstado: Boolean
        get() {
            val c = caso ?: return false
            val u = usuarioActual ?: return false
            val esCreador = c.usuarioId == u.id
            val esMunicipal = u.rol == ROL_MUNICIPAL
            return c.estado != "Cerrado" && (esCreador || esMunicipal)
        }

    init {
        cargarCaso()
    }

    fun cargarCaso() {
        viewModelScope.launch {
            val casoCargado = casoRepository.getById(casoId)
            usuarioActual = usuarioRepository.obtenerUsuarioPorId(usuarioId)
            creadoPor = casoCargado?.let {
                usuarioRepository.obtenerUsuarioPorId(it.usuarioId)?.nombreCompleto
            } ?: "Desconocido"

            // El repositorio lee la foto desde el archivo (null si el caso no tiene foto)
            fotoBitmap = fotoRepository.cargarFoto(casoCargado?.fotoRuta)

            caso = casoCargado
        }
    }

    // Cierra el caso y solo navega cuando Xano también registra el historial.
    fun cerrarCaso(onCerrado: () -> Unit) {
        val actual = caso ?: return
        if (!puedeCambiarEstado || cerrandoCaso) return
        viewModelScope.launch {
            cerrandoCaso = true
            mensajeCierre = null
            try {
                val resultado = casoRepository.cerrarCaso(actual, usuarioId)
                caso = casoRepository.getById(casoId)
                if (resultado.actualizadoEnXano && resultado.historialRegistrado) {
                    onCerrado()
                } else {
                    mensajeCierre = resultado.error ?: "No se pudo completar la sincronización del cierre."
                }
            } catch (error: Exception) {
                mensajeCierre = "No se pudo cerrar el caso: ${error.message ?: "Error inesperado"}"
            } finally {
                cerrandoCaso = false
            }
        }
    }

    companion object {
        fun provideFactory(
            casoRepository: CasoRepository,
            usuarioRepository: UsuarioRepository,
            fotoRepository: FotoRepository,
            casoId: Long,
            usuarioId: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetalleCasoViewModel(casoRepository, usuarioRepository, fotoRepository, casoId, usuarioId) as T
                }
            }
    }
}
