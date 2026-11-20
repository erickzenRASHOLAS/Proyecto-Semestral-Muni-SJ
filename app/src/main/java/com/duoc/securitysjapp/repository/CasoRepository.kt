package com.duoc.securitysjapp.repository

import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.CasoDao

class CasoRepository(private val dao: CasoDao) {
    suspend fun getAll() = dao.getAll()
    suspend fun getByUsuario(usuarioId: Int) = dao.getByUsuario(usuarioId)
    suspend fun getConUbicacion() = dao.getConUbicacion()
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(caso: Caso) = dao.insert(caso)

    // Cambia el estado del caso a "Cerrado" y lo guarda
    suspend fun cerrarCaso(caso: Caso) = dao.update(caso.copy(estado = "Cerrado"))
}
