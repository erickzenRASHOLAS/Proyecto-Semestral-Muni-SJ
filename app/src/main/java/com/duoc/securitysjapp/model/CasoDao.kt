package com.duoc.securitysjapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CasoDao {
    @Insert
    suspend fun insert(caso: Caso): Long

    @Update
    suspend fun update(caso: Caso)

    // Casos del usuario, del más nuevo al más antiguo
    @Query("SELECT * FROM casos WHERE usuarioId = :usuarioId ORDER BY id DESC")
    suspend fun getAll(usuarioId: Int): List<Caso>

    @Query("SELECT * FROM casos WHERE id = :id")
    suspend fun getById(id: Long): Caso?
}
