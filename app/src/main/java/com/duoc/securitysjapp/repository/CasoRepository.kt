package com.duoc.securitysjapp.repository

import com.duoc.securitysjapp.model.Caso

object CasoRepository {

    private val casos = mutableListOf<Caso>()
    private var nextId = 1L

    fun insert(caso: Caso): Long {
        val nuevoCaso = caso.copy(id = nextId++)
        casos.add(nuevoCaso)
        return nuevoCaso.id
    }

    fun getAll(): List<Caso> = casos.toList()

    fun getById(id: Long): Caso? = casos.find { it.id == id }
}
