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
    val fotoRuta: String? = null, // Evidencia fotográfica del caso
    val latitud: Double? = null, // Coordenadas GPS (null si no se dio permiso de ubicación)
    val longitud: Double? = null,
    val xanoId: Long? = null, // ID del caso en Xano
    val xanoUsuarioId: Long? = null // ID del usuario creador en Xano
)
