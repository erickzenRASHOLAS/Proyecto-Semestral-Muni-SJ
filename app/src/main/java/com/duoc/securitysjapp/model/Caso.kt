package com.duoc.securitysjapp.model

data class Caso(
    val id: Long = 0,
    val categoria: String,
    val descripcion: String,
    val ubicacion: String,
    val prioridad: String,
    val fechaCreacionMillis: Long = System.currentTimeMillis(),
    val estado: String = "En derivación",
    val responsable: String = "Carabineros",
    val observaciones: String = "En espera de personal en terreno.",
)
