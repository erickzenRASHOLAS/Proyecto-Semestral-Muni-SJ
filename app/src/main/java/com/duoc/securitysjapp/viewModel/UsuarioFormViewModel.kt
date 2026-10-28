package com.duoc.securitysjapp.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.model.ROL_VECINO
import com.duoc.securitysjapp.model.Usuario
import com.duoc.securitysjapp.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioFormViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _loginExitoso = MutableStateFlow<Boolean?>(null)
    val loginExitoso: StateFlow<Boolean?> = _loginExitoso

    private val _registroExitoso = MutableStateFlow<Boolean?>(null)
    val registroExitoso: StateFlow<Boolean?> = _registroExitoso

    // Id del usuario que inició sesión (se necesita para cargar su perfil)
    private val _usuarioId = MutableStateFlow(0)
    val usuarioId: StateFlow<Int> = _usuarioId

    // Errores de validación para el registro
    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre

    private val _errorCorreo = MutableStateFlow<String?>(null)
    val errorCorreo: StateFlow<String?> = _errorCorreo

    private val _errorContrasena = MutableStateFlow<String?>(null)
    val errorContrasena: StateFlow<String?> = _errorContrasena

    fun registrarUsuario(nombreCompleto: String, correo: String, contrasena: String, rol: String = ROL_VECINO) {
        var hayError = false

        if (nombreCompleto.isBlank()) {
            _errorNombre.value = "El nombre completo es obligatorio"
            hayError = true
        } else if (nombreCompleto.trim().length < 3) {
            _errorNombre.value = "El nombre debe tener al menos 3 caracteres"
            hayError = true
        } else {
            _errorNombre.value = null
        }

        if (correo.isBlank()) {
            _errorCorreo.value = "El correo electrónico es obligatorio"
            hayError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) {
            _errorCorreo.value = "Ingresa un correo electrónico válido (ej. usuario@dominio.com)"
            hayError = true
        } else {
            _errorCorreo.value = null
        }

        if (contrasena.isBlank()) {
            _errorContrasena.value = "La contraseña es obligatoria"
            hayError = true
        } else if (contrasena.length < 6) {
            _errorContrasena.value = "La contraseña debe tener al menos 6 caracteres"
            hayError = true
        } else {
            _errorContrasena.value = null
        }

        if (hayError) return

        viewModelScope.launch {
            repository.registrarUsuario(
                Usuario(
                    nombreCompleto = nombreCompleto.trim(),
                    correo = correo.trim(),
                    contrasena = contrasena,
                    rol = rol
                )
            )
            _registroExitoso.value = true
        }
    }

    fun limpiarErrorNombre() {
        _errorNombre.value = null
    }

    fun limpiarErrorCorreo() {
        _errorCorreo.value = null
    }

    fun limpiarErrorContrasena() {
        _errorContrasena.value = null
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            val usuario = repository.iniciarSesion(correo, contrasena)
            _usuarioId.value = usuario?.id ?: 0
            _loginExitoso.value = usuario != null
        }
    }

    fun resetEstados() {
        _loginExitoso.value = null
        _registroExitoso.value = null
        _errorNombre.value = null
        _errorCorreo.value = null
        _errorContrasena.value = null
    }
}

class UsuarioFormViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioFormViewModel::class.java)) {
            return UsuarioFormViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
