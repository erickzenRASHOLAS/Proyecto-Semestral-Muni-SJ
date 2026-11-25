package com.duoc.securitysjapp.repository

import com.duoc.securitysjapp.remote.ApiService
import com.duoc.securitysjapp.remote.dto.*

// Centraliza las llamadas HTTP para mantener los ViewModels independientes de Retrofit.
class XanoRepository(private val api: ApiService) {
    // Casos
    suspend fun obtenerCasos() = api.obtenerCasos()
    suspend fun obtenerCaso(id: Long) = api.obtenerCaso(id)
    suspend fun crearCaso(caso: XanoCasoRequest) = api.crearCaso(caso)
    suspend fun actualizarCaso(id: Long, caso: XanoCasoRequest) = api.actualizarCaso(id, caso)
    suspend fun eliminarCaso(id: Long) = api.eliminarCaso(id)

    // Categorías
    suspend fun obtenerCategorias() = api.obtenerCategorias()
    suspend fun obtenerCategoria(id: Long) = api.obtenerCategoria(id)
    suspend fun crearCategoria(categoria: XanoCategoriaRequest) = api.crearCategoria(categoria)
    suspend fun actualizarCategoria(id: Long, categoria: XanoCategoriaRequest) = api.actualizarCategoria(id, categoria)
    suspend fun eliminarCategoria(id: Long) = api.eliminarCategoria(id)

    // Roles
    suspend fun obtenerRoles() = api.obtenerRoles()
    suspend fun obtenerRol(id: Long) = api.obtenerRol(id)
    suspend fun crearRol(rol: XanoRolRequest) = api.crearRol(rol)
    suspend fun actualizarRol(id: Long, rol: XanoRolRequest) = api.actualizarRol(id, rol)
    suspend fun eliminarRol(id: Long) = api.eliminarRol(id)

    // Usuarios
    suspend fun obtenerUsuarios() = api.obtenerUsuarios()
    suspend fun obtenerUsuario(id: Long) = api.obtenerUsuario(id)
    suspend fun crearUsuario(usuario: XanoUsuarioRequest) = api.crearUsuario(usuario)
    suspend fun actualizarUsuario(id: Long, usuario: XanoUsuarioRequest) = api.actualizarUsuario(id, usuario)
    suspend fun eliminarUsuario(id: Long) = api.eliminarUsuario(id)

    // Historial
    suspend fun obtenerHistorial() = api.obtenerHistorial()
    suspend fun obtenerRegistroHistorial(id: Long) = api.obtenerRegistroHistorial(id)
    suspend fun crearHistorial(historial: XanoHistorialRequest) = api.crearHistorial(historial)
    suspend fun actualizarHistorial(id: Long, historial: XanoHistorialRequest) = api.actualizarHistorial(id, historial)
    suspend fun eliminarHistorial(id: Long) = api.eliminarHistorial(id)

    // Observaciones
    suspend fun obtenerObservaciones() = api.obtenerObservaciones()
    suspend fun obtenerObservacion(id: Long) = api.obtenerObservacion(id)
    suspend fun crearObservacion(observacion: XanoObservacionRequest) = api.crearObservacion(observacion)
    suspend fun actualizarObservacion(id: Long, observacion: XanoObservacionRequest) = api.actualizarObservacion(id, observacion)
    suspend fun eliminarObservacion(id: Long) = api.eliminarObservacion(id)
}
