package com.duoc.securitysjapp.respository

import com.duoc.securitysjapp.model.Usuario
import com.duoc.securitysjapp.model.UsuarioDao

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun registrarUsuario(usuario: Usuario) {
        usuarioDao.registrarUsuario(usuario)
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        return usuarioDao.iniciarSesion(correo, contrasena)
    }
}