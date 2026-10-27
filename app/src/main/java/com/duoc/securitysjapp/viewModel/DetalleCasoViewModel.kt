package com.duoc.securitysjapp.viewModel

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
import com.duoc.securitysjapp.repository.UsuarioRepository
import kotlinx.coroutines.launch

class DetalleCasoViewModel(
    private val casoRepository: CasoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val casoId: Long,
    private val usuarioId: Int, // usuario que tiene la sesión iniciada
) : ViewModel() {

    var caso by mutableStateOf<Caso?>(null)
        private set

    // Nombre de quien creó el caso
    var creadoPor by mutableStateOf("")
        private set

    private var usuarioActual by mutableStateOf<Usuario?>(null)

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
            caso = casoCargado
        }
    }

    // Cambia el estado a "Cerrado" y avisa cuando ya quedó guardado
    fun cerrarCaso(onCerrado: () -> Unit) {
        val actual = caso ?: return
        if (!puedeCambiarEstado) return
        viewModelScope.launch {
            casoRepository.update(actual.copy(estado = "Cerrado"))
            caso = casoRepository.getById(casoId)
            onCerrado()
        }
    }

    companion object {
        fun provideFactory(
            casoRepository: CasoRepository,
            usuarioRepository: UsuarioRepository,
            casoId: Long,
            usuarioId: Int,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetalleCasoViewModel(casoRepository, usuarioRepository, casoId, usuarioId) as T
                }
            }
    }
}
