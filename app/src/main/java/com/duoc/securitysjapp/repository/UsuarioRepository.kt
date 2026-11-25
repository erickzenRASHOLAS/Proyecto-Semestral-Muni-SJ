package com.duoc.securitysjapp.repository

import com.duoc.securitysjapp.model.Usuario
import com.duoc.securitysjapp.model.UsuarioDao
import com.duoc.securitysjapp.remote.AuthApiService
import com.duoc.securitysjapp.remote.dto.XanoAuthLoginRequest
import com.duoc.securitysjapp.remote.dto.XanoAuthSignupRequest
import com.duoc.securitysjapp.remote.dto.XanoAuthUserDto
import com.duoc.securitysjapp.remote.dto.XanoRolRequest
import com.duoc.securitysjapp.remote.dto.XanoUsuarioRequest
import org.json.JSONObject
import retrofit2.Response

/** Coordina la autenticación remota y conserva una ficha local para las pantallas y casos. */
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val xanoRepository: XanoRepository,
    private val authApi: AuthApiService,
    private val sesionRepository: SesionRepository
) {

    /** Registra en Xano; no guarda la contraseña en Room. */
    suspend fun registrarUsuario(usuario: Usuario) {
        val respuesta = validarRespuesta(
            authApi.registrar(
                XanoAuthSignupRequest(
                    nombreCompleto = usuario.nombreCompleto.trim(),
                    correo = usuario.correo.trim().lowercase(),
                    contrasenia = usuario.contrasena,
                    perfil = usuario.rol
                )
            ),
            "No se pudo registrar el usuario"
        )
        if (respuesta.authToken.isBlank() || respuesta.userIdAsLong() == null) {
            throw IllegalStateException("Xano registró la solicitud, pero devolvió una respuesta de autenticación incompleta.")
        }
    }

    /** Valida las credenciales con Xano y sincroniza el perfil autenticado en Room. */
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        val respuesta = validarRespuesta(
            authApi.iniciarSesion(
                XanoAuthLoginRequest(correo = correo.trim().lowercase(), contrasenia = contrasena)
            ),
            "No se pudo iniciar sesión"
        )
        if (respuesta.authToken.isBlank()) {
            throw IllegalStateException("Xano no devolvió un token de autenticación.")
        }
        val userIdRemoto = respuesta.userIdAsLong()
            ?: throw IllegalStateException("Xano no devolvió un user_id válido durante el inicio de sesión.")

        val usuario = cargarPerfilConToken(respuesta.authToken, userIdRemoto)
        // Guarda el token únicamente después de validar /auth/me y sincronizar el perfil.
        sesionRepository.guardarToken(respuesta.authToken)
        sesionRepository.guardarSesion(usuario.id)
        return usuario
    }

    /** Valida el token guardado al abrir la aplicación y recupera el perfil desde /auth/me. */
    suspend fun restaurarSesion(): Usuario? {
        val token = sesionRepository.obtenerToken() ?: return null
        return try {
            val usuario = cargarPerfilConToken(token, null)
            sesionRepository.guardarSesion(usuario.id)
            usuario
        } catch (error: XanoHttpException) {
            if (error.statusCode == 401 || error.statusCode == 403) {
                // Un token vencido o rechazado no debe permitir entrar a la aplicación.
                sesionRepository.cerrarSesion()
                null
            } else {
                throw error
            }
        }
    }

    fun hayTokenGuardado(): Boolean = sesionRepository.obtenerToken() != null

    private suspend fun cargarPerfilConToken(token: String, userIdEsperado: Long?): Usuario {
        val perfil = validarRespuesta(
            authApi.obtenerMiPerfil("Bearer $token"),
            "No se pudo validar la sesión con Xano"
        )
        if (userIdEsperado != null && perfil.id != userIdEsperado) {
            throw IllegalStateException("El ID devuelto por login no coincide con el usuario de /auth/me.")
        }

        val rol = xanoRepository.obtenerRoles()
            .firstOrNull { it.id == perfil.rolesId }
            ?.nombre
            ?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("No se encontró el rol asociado al usuario en la tabla roles de Xano.")

        return guardarPerfilLocal(perfil, rol)
    }

    private suspend fun guardarPerfilLocal(perfil: XanoAuthUserDto, rol: String): Usuario {
        val existente = usuarioDao.obtenerUsuarioPorXanoId(perfil.id)
            ?: usuarioDao.obtenerUsuarioPorCorreo(perfil.correo)

        if (existente != null) {
            usuarioDao.actualizarPerfilAutenticado(
                id = existente.id,
                nombre = perfil.nombreCompleto,
                correo = perfil.correo,
                rol = rol,
                xanoId = perfil.id
            )
            return usuarioDao.obtenerUsuarioPorId(existente.id)
                ?: throw IllegalStateException("No se pudo recuperar el perfil local actualizado.")
        }

        // Room necesita una columna antigua de contraseña; se deja vacía y nunca se usa para autenticar.
        val idLocal = usuarioDao.registrarUsuario(
            Usuario(
                nombreCompleto = perfil.nombreCompleto,
                correo = perfil.correo,
                contrasena = "",
                rol = rol,
                xanoId = perfil.id
            )
        ).toInt()

        return usuarioDao.obtenerUsuarioPorId(idLocal)
            ?: throw IllegalStateException("No se pudo crear la ficha local del usuario autenticado.")
    }

    private fun <T> validarRespuesta(response: Response<T>, operacion: String): T {
        if (!response.isSuccessful) {
            val detalle = extraerMensajeError(response.errorBody()?.string())
            throw XanoHttpException(response.code(), detalle ?: "$operacion (HTTP ${response.code()}).")
        }
        return response.body() ?: throw IllegalStateException("$operacion: Xano devolvió una respuesta vacía.")
    }

    private fun extraerMensajeError(body: String?): String? {
        if (body.isNullOrBlank()) return null
        return runCatching {
            val json = JSONObject(body)
            sequenceOf("message", "error", "details").mapNotNull { clave ->
                json.optString(clave).takeIf { it.isNotBlank() && it != "null" }
            }.firstOrNull()
        }.getOrNull() ?: body.take(300)
    }

    suspend fun obtenerUsuarioPorId(id: Int): Usuario? = usuarioDao.obtenerUsuarioPorId(id)

    suspend fun obtenerUsuarioPorXanoId(id: Long): Usuario? = usuarioDao.obtenerUsuarioPorXanoId(id)

    /** Devuelve el ID remoto y conserva la compatibilidad con usuarios locales antiguos. */
    suspend fun asegurarUsuarioXano(usuarioIdLocal: Int): Long {
        val usuario = usuarioDao.obtenerUsuarioPorId(usuarioIdLocal)
            ?: throw IllegalStateException("No existe el usuario local $usuarioIdLocal")

        usuario.xanoId?.let { return it }

        val usuarioRemoto = xanoRepository.obtenerUsuarios()
            .firstOrNull { it.correo.equals(usuario.correo, ignoreCase = true) }
            ?: run {
                val roles = xanoRepository.obtenerRoles()
                val rolExistente = roles.firstOrNull { it.nombre?.equals(usuario.rol, ignoreCase = true) == true }
                val rolId = rolExistente?.id ?: xanoRepository.crearRol(
                    XanoRolRequest(
                        nombre = usuario.rol,
                        descripcion = "Perfil de ${usuario.rol.lowercase()} de SecuritySJApp"
                    )
                ).id

                // Compatibilidad para cuentas locales anteriores que aún no tienen vínculo remoto.
                xanoRepository.crearUsuario(
                    XanoUsuarioRequest(
                        nombreCompleto = usuario.nombreCompleto,
                        correo = usuario.correo,
                        edad = null,
                        terminosAceptados = false,
                        rolId = rolId
                    )
                )
            }

        usuarioDao.actualizarXanoId(usuarioIdLocal, usuarioRemoto.id)
        return usuarioRemoto.id
    }

    suspend fun actualizarFotoRuta(id: Int, fotoRuta: String?) {
        usuarioDao.actualizarFotoRuta(id, fotoRuta)
    }

    suspend fun actualizarContrasena(id: Int, nuevaContrasena: String) {
        usuarioDao.actualizarContrasena(id, nuevaContrasena)
    }

    suspend fun actualizarNombre(id: Int, nuevoNombre: String) {
        usuarioDao.actualizarNombre(id, nuevoNombre)
    }
}

class XanoHttpException(val statusCode: Int, message: String) : Exception(message)
