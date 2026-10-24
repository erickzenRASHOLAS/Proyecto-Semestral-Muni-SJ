package com.duoc.securitysjapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.respository.UsuarioRepository
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

    // Llama a esta función cuando se abre la pantalla de perfil
    fun cargarPerfil(usuarioId: Int) {
        viewModelScope.launch {
            val usuario = repository.obtenerUsuarioPorId(usuarioId)
            usuario?.let {
                _nombreUsuario.value = it.nombreCompleto
                _correoUsuario.value = it.correo
                _rolUsuario.value = it.rol ?: "Vecino"
            }
        }
    }
}