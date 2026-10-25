package com.duoc.securitysjapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _nombreUsuario = MutableStateFlow("Cargando...")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _correoUsuario = MutableStateFlow("Cargando...")
    val correoUsuario: StateFlow<String> = _correoUsuario

    private val _rolUsuario = MutableStateFlow("Vecino")
    val rolUsuario: StateFlow<String> = _rolUsuario

    // Se llama cuando se abre la pantalla de perfil
    fun cargarPerfil(usuarioId: Int) {
        viewModelScope.launch {
            val usuario = repository.obtenerUsuarioPorId(usuarioId)
            if (usuario != null) {
                _nombreUsuario.value = usuario.nombreCompleto
                _correoUsuario.value = usuario.correo
                _rolUsuario.value = usuario.rol
            }
        }
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
