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

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _loginExitoso = MutableStateFlow<Boolean?>(null)
    val loginExitoso: StateFlow<Boolean?> = _loginExitoso

    private val _registroExitoso = MutableStateFlow<Boolean?>(null)
    val registroExitoso: StateFlow<Boolean?> = _registroExitoso

    private val _usuarioId = MutableStateFlow(0)
    val usuarioId: StateFlow<Int> = _usuarioId

    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre

    private val _errorCorreo = MutableStateFlow<String?>(null)
    val errorCorreo: StateFlow<String?> = _errorCorreo

    private val _errorContrasena = MutableStateFlow<String?>(null)
    val errorContrasena: StateFlow<String?> = _errorContrasena

    private val _errorGeneralRegistro = MutableStateFlow<String?>(null)
    val errorGeneralRegistro: StateFlow<String?> = _errorGeneralRegistro

    private val _errorCorreoLogin = MutableStateFlow<String?>(null)
    val errorCorreoLogin: StateFlow<String?> = _errorCorreoLogin

    private val _errorContrasenaLogin = MutableStateFlow<String?>(null)
    val errorContrasenaLogin: StateFlow<String?> = _errorContrasenaLogin

    private val _errorGeneralLogin = MutableStateFlow<String?>(null)
    val errorGeneralLogin: StateFlow<String?> = _errorGeneralLogin

    private var restauracionIniciada = false

    /** Recupera la sesión guardada, validando el JWT contra GET /auth/me. */
    fun restaurarSesion() {
        if (restauracionIniciada) return
        restauracionIniciada = true
        if (!repository.hayTokenGuardado()) return

        viewModelScope.launch {
            _cargando.value = true
            try {
                val usuario = repository.restaurarSesion()
                if (usuario != null) {
                    _usuarioId.value = usuario.id
                    _loginExitoso.value = true
                    _errorGeneralLogin.value = null
                }
            } catch (error: Exception) {
                _errorGeneralLogin.value = error.message
                    ?: "No se pudo validar la sesión. Comprueba tu conexión e inicia sesión nuevamente."
            } finally {
                _cargando.value = false
            }
        }
    }

    /** El registro se realiza en AuthUsuarios; Room no almacena la contraseña. */
    fun registrarUsuario(
        nombreCompleto: String,
        correo: String,
        contrasena: String,
        rol: String = ROL_VECINO
    ) {
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

        if (rol != "Vecino" && rol != "Municipal") {
            _errorGeneralRegistro.value = "Selecciona un perfil válido."
            hayError = true
        } else {
            _errorGeneralRegistro.value = null
        }

        if (hayError) return

        viewModelScope.launch {
            _cargando.value = true
            _registroExitoso.value = null
            try {
                repository.registrarUsuario(
                    Usuario(
                        nombreCompleto = nombreCompleto.trim(),
                        correo = correo.trim().lowercase(),
                        // La contraseña se envía directamente a Xano y no se guarda en Room.
                        contrasena = contrasena,
                        rol = rol
                    )
                )
                _registroExitoso.value = true
                _errorGeneralRegistro.value = null
            } catch (error: Exception) {
                _registroExitoso.value = false
                _errorGeneralRegistro.value = error.message
                    ?: "No se pudo completar el registro. Inténtalo nuevamente."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarErrorNombre() { _errorNombre.value = null }
    fun limpiarErrorCorreo() { _errorCorreo.value = null }
    fun limpiarErrorContrasena() { _errorContrasena.value = null }

    fun limpiarErrorGeneralRegistro() { _errorGeneralRegistro.value = null }

    fun iniciarSesion(correo: String, contrasena: String) {
        var hayError = false

        if (correo.isBlank()) {
            _errorCorreoLogin.value = "Ingresa tu correo electrónico"
            hayError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) {
            _errorCorreoLogin.value = "Ingresa un correo electrónico válido"
            hayError = true
        } else {
            _errorCorreoLogin.value = null
        }

        if (contrasena.isBlank()) {
            _errorContrasenaLogin.value = "Ingresa tu contraseña"
            hayError = true
        } else {
            _errorContrasenaLogin.value = null
        }

        if (hayError) return

        _errorGeneralLogin.value = null
        viewModelScope.launch {
            _cargando.value = true
            _loginExitoso.value = null
            try {
                val usuario = repository.iniciarSesion(correo.trim(), contrasena)
                _usuarioId.value = usuario.id
                _loginExitoso.value = true
                _errorGeneralLogin.value = null
            } catch (error: Exception) {
                _loginExitoso.value = false
                _errorGeneralLogin.value = error.message
                    ?: "No se pudo iniciar sesión. Comprueba tus credenciales y conexión."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarErrorCorreoLogin() {
        _errorCorreoLogin.value = null
        _errorGeneralLogin.value = null
    }

    fun limpiarErrorContrasenaLogin() {
        _errorContrasenaLogin.value = null
        _errorGeneralLogin.value = null
    }

    fun resetEstados() {
        _cargando.value = false
        _loginExitoso.value = null
        _registroExitoso.value = null
        _errorNombre.value = null
        _errorCorreo.value = null
        _errorContrasena.value = null
        _errorGeneralRegistro.value = null
        _errorCorreoLogin.value = null
        _errorContrasenaLogin.value = null
        _errorGeneralLogin.value = null
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
