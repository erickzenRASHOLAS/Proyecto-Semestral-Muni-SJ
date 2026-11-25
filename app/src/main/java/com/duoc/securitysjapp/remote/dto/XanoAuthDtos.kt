package com.duoc.securitysjapp.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

// Solicitud de registro: el servidor resuelve roles_id a partir del perfil seleccionado.
data class XanoAuthSignupRequest(
    @SerializedName("nombre_completo") val nombreCompleto: String,
    val correo: String,
    val contrasenia: String,
    val perfil: String
)

// Solicitud de inicio de sesión en AuthUsuarios.
data class XanoAuthLoginRequest(
    val correo: String,
    val contrasenia: String
)

// Xano documenta user_id como texto, aunque algunas respuestas lo serializan como número.
data class XanoAuthTokenDto(
    val authToken: String,
    @SerializedName("user_id") val userId: JsonElement?
) {
    // El Swagger lo declara como texto, pero Xano también puede serializar el ID como número.
    fun userIdAsLong(): Long? {
        val primitive = userId?.asJsonPrimitive ?: return null
        return runCatching {
            if (primitive.isNumber) primitive.asLong else primitive.asString.toLongOrNull()
        }.getOrNull()
    }
}

// Respuesta de GET /auth/me. Xano excluye deliberadamente el campo contrasenia.
data class XanoAuthUserDto(
    val id: Long,
    @SerializedName("created_at") val createdAt: Long? = null,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    val correo: String,
    @SerializedName("terminos_aceptados") val terminosAceptados: Boolean? = null,
    @SerializedName("roles_id") val rolesId: Long
)
