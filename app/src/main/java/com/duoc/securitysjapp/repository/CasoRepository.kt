package com.duoc.securitysjapp.repository

import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.CasoDao

class CasoRepository(private val dao: CasoDao) {
    suspend fun getAll() = dao.getAll()
    suspend fun getByUsuario(usuarioId: Int) = dao.getByUsuario(usuarioId)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(caso: Caso) = dao.insert(caso)
    suspend fun update(caso: Caso) = dao.update(caso)
}
