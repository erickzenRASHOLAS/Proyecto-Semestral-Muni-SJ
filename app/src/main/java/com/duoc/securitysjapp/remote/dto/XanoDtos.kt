package com.duoc.securitysjapp.remote.dto

import com.google.gson.annotations.SerializedName

// Representa un caso recibido desde Xano.
data class XanoCasoDto(
    val id: Long,
    @SerializedName("created_at") val createdAt: Long? = null,
    @SerializedName("usuarios_id") val usuarioId: Long,
    @SerializedName("categoria_id") val categoriaId: Long,
    val descripcion: String,
    val ubicacion: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val prioridad: String,
    val estado: String,
    val responsable: String? = null,
    @SerializedName("fecha_actualizacion") val fechaActualizacion: Long? = null
)

// Datos que se envían al crear o actualizar un caso.
data class XanoCasoRequest(
    @SerializedName("usuarios_id") val usuarioId: Long,
    @SerializedName("categoria_id") val categoriaId: Long,
    val descripcion: String,
    val ubicacion: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val prioridad: String,
    val estado: String,
    val responsable: String? = null,
    @SerializedName("fecha_actualizacion") val fechaActualizacion: Long? = null
)

data class XanoCategoriaDto(
    val id: Long,
    val nombre: String?,
    @SerializedName("caso_activo") val casoActivo: Boolean = true
)

data class XanoCategoriaRequest(
    val nombre: String,
    @SerializedName("caso_activo") val casoActivo: Boolean = true
)

data class XanoRolDto(
    val id: Long,
    val nombre: String?,
    val descripcion: String? = null
)

data class XanoRolRequest(
    val nombre: String,
    val descripcion: String? = null
)

data class XanoUsuarioDto(
    val id: Long,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    val correo: String,
    @SerializedName("edad") val edad: Int? = null,
    @SerializedName("terminos_aceptados") val terminosAceptados: Boolean? = null,
    @SerializedName("roles_id") val rolId: Long
)

// La API TEVEO no expone contrasenia en el POST de usuarios.
data class XanoUsuarioRequest(
    @SerializedName("nombre_completo") val nombreCompleto: String,
    val correo: String,
    val edad: Int? = null,
    @SerializedName("terminos_aceptados") val terminosAceptados: Boolean = false,
    @SerializedName("roles_id") val rolId: Long
)

data class XanoHistorialDto(
    val id: Long,
    @SerializedName("casos_id") val casoId: Long,
    @SerializedName("estado_anterior") val estadoAnterior: String,
    @SerializedName("estado_nuevo") val estadoNuevo: String,
    @SerializedName("cambiado_por_usuario_id") val usuarioId: Long,
    @SerializedName("motivo_cambio") val motivoCambio: String? = null,
    @SerializedName("fecha_cambio") val fechaCambio: Long? = null
)

data class XanoHistorialRequest(
    @SerializedName("casos_id") val casoId: Long,
    @SerializedName("estado_anterior") val estadoAnterior: String,
    @SerializedName("estado_nuevo") val estadoNuevo: String,
    @SerializedName("cambiado_por_usuario_id") val usuarioId: Long,
    @SerializedName("motivo_cambio") val motivoCambio: String? = null,
    @SerializedName("fecha_cambio") val fechaCambio: Long? = null
)

data class XanoObservacionDto(
    val id: Long,
    @SerializedName("casos_id") val casoId: Long,
    @SerializedName("usuarios_id") val usuarioId: Long,
    val observacion: String
)

data class XanoObservacionRequest(
    @SerializedName("casos_id") val casoId: Long,
    @SerializedName("usuarios_id") val usuarioId: Long,
    val observacion: String
)
