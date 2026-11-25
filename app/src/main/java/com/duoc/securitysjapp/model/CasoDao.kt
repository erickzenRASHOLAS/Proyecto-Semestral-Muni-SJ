package com.duoc.securitysjapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update

@Dao
interface CasoDao {
    @Insert
    suspend fun insert(caso: Caso): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarOActualizar(caso: Caso): Long

    @Update
    suspend fun update(caso: Caso)

    // Todos los casos, del más nuevo al más antiguo
    @Query("SELECT * FROM casos ORDER BY id DESC")
    suspend fun getAll(): List<Caso>

    // Solo los casos creados por un usuario
    @Query("SELECT * FROM casos WHERE usuarioId = :usuarioId ORDER BY id DESC")
    suspend fun getByUsuario(usuarioId: Int): List<Caso>

    // Solo los casos que tienen coordenadas GPS (los que se pueden mostrar en el mapa)
    @Query("SELECT * FROM casos WHERE latitud IS NOT NULL AND longitud IS NOT NULL ORDER BY id DESC")
    suspend fun getConUbicacion(): List<Caso>

    @Query("SELECT * FROM casos WHERE id = :id")
    suspend fun getById(id: Long): Caso?

    @Query("SELECT * FROM casos WHERE xanoId = :xanoId LIMIT 1")
    suspend fun getByXanoId(xanoId: Long): Caso?

    // Casos guardados localmente que todavía no tienen ID remoto.
    @Query("SELECT * FROM casos WHERE xanoId IS NULL ORDER BY id ASC")
    suspend fun getPendientes(): List<Caso>

    @Delete
    suspend fun eliminar(caso: Caso)
}
