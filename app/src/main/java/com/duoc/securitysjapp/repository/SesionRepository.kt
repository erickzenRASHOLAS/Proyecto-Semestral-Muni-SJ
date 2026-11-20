package com.duoc.securitysjapp.repository

import android.content.Context

// Guarda quién tiene la sesión iniciada (con SharedPreferences) para no pedir el login cada vez
class SesionRepository(context: Context) {

    // Archivo privado de la app donde se guarda el id del usuario
    private val preferencias = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

    // Guarda el id del usuario que inició sesión
    fun guardarSesion(usuarioId: Int) {
        preferencias.edit().putInt(CLAVE_USUARIO_ID, usuarioId).apply()
    }

    // Devuelve el id guardado, o SIN_SESION (-1) si nadie ha iniciado sesión
    fun obtenerUsuarioId(): Int {
        return preferencias.getInt(CLAVE_USUARIO_ID, SIN_SESION)
    }

    // Borra la sesión guardada (cerrar sesión)
    fun cerrarSesion() {
        preferencias.edit().clear().apply()
    }

    companion object {
        const val SIN_SESION = -1
        private const val CLAVE_USUARIO_ID = "usuario_id"
    }
}
