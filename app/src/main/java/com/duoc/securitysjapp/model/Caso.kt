package com.duoc.securitysjapp.model

data class Caso(
    val id: Long = 0,
    val categoria: String,
    val descripcion: String,
    val ubicacion: String,
    val prioridad: String,
    val fechaCreacionMillis: Long = System.currentTimeMillis(),
)
