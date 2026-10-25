package com.duoc.securitysjapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    fun registrarUsuario(nombreCompleto: String, correo: String, contrasena: String, rol: String = "Usuario") {
        viewModelScope.launch {
            repository.registrarUsuario(
                Usuario(
                    nombreCompleto = nombreCompleto,
                    correo = correo,
                    contrasena = contrasena,
                    rol = rol
                )
            )
            _registroExitoso.value = true
        }
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
