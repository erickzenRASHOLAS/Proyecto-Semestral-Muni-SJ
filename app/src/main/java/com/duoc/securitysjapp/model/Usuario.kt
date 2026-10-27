package com.duoc.securitysjapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreCompleto: String,
    val correo: String,
    val contrasena: String,
    val rol:String
)

// Roles de usuario
const val ROL_VECINO = "Vecino"
const val ROL_MUNICIPAL = "Municipal"
