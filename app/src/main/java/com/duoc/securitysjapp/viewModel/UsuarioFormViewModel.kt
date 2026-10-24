package com.duoc.securitysjapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.model.Usuario
import com.duoc.securitysjapp.respository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuarioFormViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _loginExitoso = MutableStateFlow<Boolean?>(null)
    val loginExitoso: StateFlow<Boolean?> = _loginExitoso

    private val _registroExitoso = MutableStateFlow<Boolean?>(null)
    val registroExitoso: StateFlow<Boolean?> = _registroExitoso

    fun registrarUsuario(correo: String, contrasena: String) {
        viewModelScope.launch {
            repository.registrarUsuario(Usuario(correo = correo, contrasena = contrasena))
            _registroExitoso.value = true
        }
    }

    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            val usuario = repository.iniciarSesion(correo, contrasena)
            _loginExitoso.value = usuario != null
        }
    }

    fun resetEstados() {
        _loginExitoso.value = null
        _registroExitoso.value = null
    }
}

class UsuarioFormViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioFormViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioFormViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}