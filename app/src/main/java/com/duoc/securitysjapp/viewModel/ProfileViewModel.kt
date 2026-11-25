package com.duoc.securitysjapp.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.repository.FotoRepository
import com.duoc.securitysjapp.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UsuarioRepository,
    private val fotoRepository: FotoRepository,
) : ViewModel() {

    private val _nombreUsuario = MutableStateFlow("Cargando...")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _correoUsuario = MutableStateFlow("Cargando...")
    val correoUsuario: StateFlow<String> = _correoUsuario

    private val _rolUsuario = MutableStateFlow("Vecino")
    val rolUsuario: StateFlow<String> = _rolUsuario

    private val _fotoBitmap = MutableStateFlow<Bitmap?>(null)
    val fotoBitmap: StateFlow<Bitmap?> = _fotoBitmap

    private val _procesandoFoto = MutableStateFlow(false)
    val procesandoFoto: StateFlow<Boolean> = _procesandoFoto

    // Es true cuando el usuario guardado en la sesión ya no existe en la base de datos
    private val _sesionInvalida = MutableStateFlow(false)
    val sesionInvalida: StateFlow<Boolean> = _sesionInvalida

    // Estados para cambio de contraseña
    private val _errorNuevaContrasena = MutableStateFlow<String?>(null)
    val errorNuevaContrasena: StateFlow<String?> = _errorNuevaContrasena

    private val _errorConfirmarContrasena = MutableStateFlow<String?>(null)
    val errorConfirmarContrasena: StateFlow<String?> = _errorConfirmarContrasena

    private val _cambioContrasenaExitoso = MutableStateFlow<Boolean?>(null)
    val cambioContrasenaExitoso: StateFlow<Boolean?> = _cambioContrasenaExitoso

    // Estados para cambio de nombre
    private val _errorNuevoNombre = MutableStateFlow<String?>(null)
    val errorNuevoNombre: StateFlow<String?> = _errorNuevoNombre

    private val _cambioNombreExitoso = MutableStateFlow<Boolean?>(null)
    val cambioNombreExitoso: StateFlow<Boolean?> = _cambioNombreExitoso

    // Se llama cuando se abre la pantalla de inicio o la de perfil
    fun cargarPerfil(usuarioId: Int) {
        viewModelScope.launch {
            val usuario = repository.obtenerUsuarioPorId(usuarioId)
            if (usuario != null) {
                _nombreUsuario.value = usuario.nombreCompleto
                _correoUsuario.value = usuario.correo
                _rolUsuario.value = usuario.rol

                // El repositorio lee la foto desde el archivo (null si el usuario no tiene foto)
                _fotoBitmap.value = fotoRepository.cargarFoto(usuario.fotoRuta)
            } else {
                // La sesión guardada apunta a un usuario que ya no existe
                _sesionInvalida.value = true
            }
        }
    }

    fun guardarFotoPerfil(usuarioId: Int, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _procesandoFoto.value = true

                // El repositorio guarda el archivo y devuelve su ruta; esa ruta se guarda en Room
                val ruta = fotoRepository.guardarFoto("perfil_$usuarioId.jpg", bitmap)
                if (ruta != null) {
                    repository.actualizarFotoRuta(usuarioId, ruta)
                    _fotoBitmap.value = bitmap
                }
            } finally {
                _procesandoFoto.value = false
            }
        }
    }

    fun cambiarContrasena(usuarioId: Int, nuevaContrasena: String, confirmarContrasena: String) {
        var hayError = false

        if (nuevaContrasena.isBlank()) {
            _errorNuevaContrasena.value = "Ingresa la nueva contraseña"
            hayError = true
        } else if (nuevaContrasena.length < 6) {
            _errorNuevaContrasena.value = "La contraseña debe tener al menos 6 caracteres"
            hayError = true
        } else {
            _errorNuevaContrasena.value = null
        }

        if (confirmarContrasena.isBlank()) {
            _errorConfirmarContrasena.value = "Confirma la nueva contraseña"
            hayError = true
        } else if (nuevaContrasena != confirmarContrasena) {
            _errorConfirmarContrasena.value = "Las contraseñas no coinciden"
            hayError = true
        } else {
            _errorConfirmarContrasena.value = null
        }

        if (hayError) return

        // AuthUsuarios aún no publica un endpoint para cambiar contraseñas; no se altera solo Room.
        _cambioContrasenaExitoso.value = false
        _errorNuevaContrasena.value = "El cambio de contraseña aún no está conectado a Xano."
    }

    // Valida el nombre (misma regla del registro), lo guarda en Room y actualiza el que se ve en pantalla
    fun cambiarNombre(usuarioId: Int, nuevoNombre: String) {
        if (nuevoNombre.isBlank()) {
            _errorNuevoNombre.value = "Ingresa el nuevo nombre"
            return
        }
        if (nuevoNombre.trim().length < 3) {
            _errorNuevoNombre.value = "El nombre debe tener al menos 3 caracteres"
            return
        }

        _errorNuevoNombre.value = null
        viewModelScope.launch {
            repository.actualizarNombre(usuarioId, nuevoNombre.trim())
            _nombreUsuario.value = nuevoNombre.trim()
            _cambioNombreExitoso.value = true
        }
    }

    fun limpiarErrorNuevoNombre() {
        _errorNuevoNombre.value = null
    }

    fun resetEstadoCambioNombre() {
        _cambioNombreExitoso.value = null
        _errorNuevoNombre.value = null
    }

    fun limpiarErrorNuevaContrasena() {
        _errorNuevaContrasena.value = null
    }

    fun limpiarErrorConfirmarContrasena() {
        _errorConfirmarContrasena.value = null
    }

    fun resetEstadoCambioContrasena() {
        _cambioContrasenaExitoso.value = null
        _errorNuevaContrasena.value = null
        _errorConfirmarContrasena.value = null
    }
}

class ProfileViewModelFactory(
    private val repository: UsuarioRepository,
    private val fotoRepository: FotoRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository, fotoRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
