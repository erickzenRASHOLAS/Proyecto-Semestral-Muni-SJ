package com.duoc.securitysjapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "casos")
data class Caso(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Int, // quién creó el caso
    val categoria: String,
    val descripcion: String,
    val ubicacion: String,
    val prioridad: String,
    val fechaCreacionMillis: Long = System.currentTimeMillis(),
    val estado: String = "En derivación",
)
