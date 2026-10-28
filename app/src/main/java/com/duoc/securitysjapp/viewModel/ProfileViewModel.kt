package com.duoc.securitysjapp.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _nombreUsuario = MutableStateFlow("Cargando...")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _correoUsuario = MutableStateFlow("Cargando...")
    val correoUsuario: StateFlow<String> = _correoUsuario

    private val _rolUsuario = MutableStateFlow("Vecino")
    val rolUsuario: StateFlow<String> = _rolUsuario

    private val _fotoBitmap = MutableStateFlow<Bitmap?>(null)
    val fotoBitmap: StateFlow<Bitmap?> = _fotoBitmap

    // Estados para cambio de contraseña
    private val _errorNuevaContrasena = MutableStateFlow<String?>(null)
    val errorNuevaContrasena: StateFlow<String?> = _errorNuevaContrasena

    private val _errorConfirmarContrasena = MutableStateFlow<String?>(null)
    val errorConfirmarContrasena: StateFlow<String?> = _errorConfirmarContrasena

    private val _cambioContrasenaExitoso = MutableStateFlow<Boolean?>(null)
    val cambioContrasenaExitoso: StateFlow<Boolean?> = _cambioContrasenaExitoso

    // Se llama cuando se abre la pantalla de perfil
    fun cargarPerfil(usuarioId: Int) {
        viewModelScope.launch {
            val usuario = repository.obtenerUsuarioPorId(usuarioId)
            if (usuario != null) {
                _nombreUsuario.value = usuario.nombreCompleto
                _correoUsuario.value = usuario.correo
                _rolUsuario.value = usuario.rol

                if (!usuario.fotoRuta.isNullOrEmpty()) {
                    withContext(Dispatchers.IO) {
                        val file = File(usuario.fotoRuta)
                        if (file.exists()) {
                            _fotoBitmap.value = BitmapFactory.decodeFile(file.absolutePath)
                        }
                    }
                }
            }
        }
    }

    fun guardarFotoPerfil(context: Context, usuarioId: Int, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, "perfil_$usuarioId.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                repository.actualizarFotoRuta(usuarioId, file.absolutePath)
                _fotoBitmap.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
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

        viewModelScope.launch {
            repository.actualizarContrasena(usuarioId, nuevaContrasena)
            _cambioContrasenaExitoso.value = true
        }
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
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
