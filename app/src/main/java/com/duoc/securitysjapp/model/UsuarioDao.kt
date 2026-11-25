package com.duoc.securitysjapp.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Insert
    suspend fun registrarUsuario(usuario: Usuario): Long

    //Limit 1 para que se detenga en el primer registro
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena LIMIT 1")
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerUsuarioPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE xanoId = :xanoId LIMIT 1")
    suspend fun obtenerUsuarioPorXanoId(xanoId: Long): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo COLLATE NOCASE LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario?

    // Actualiza el perfil autenticado y elimina cualquier contraseña local antigua.
    @Query("UPDATE usuarios SET nombreCompleto = CASE WHEN xanoId = :xanoId THEN nombreCompleto ELSE :nombre END, correo = :correo, contrasena = '', rol = :rol, xanoId = :xanoId WHERE id = :id")
    suspend fun actualizarPerfilAutenticado(
        id: Int,
        nombre: String,
        correo: String,
        rol: String,
        xanoId: Long
    )

    @Query("UPDATE usuarios SET xanoId = :xanoId WHERE id = :id")
    suspend fun actualizarXanoId(id: Int, xanoId: Long)

    @Query("UPDATE usuarios SET fotoRuta = :fotoRuta WHERE id = :id")
    suspend fun actualizarFotoRuta(id: Int, fotoRuta: String?)

    @Query("UPDATE usuarios SET contrasena = :nuevaContrasena WHERE id = :id")
    suspend fun actualizarContrasena(id: Int, nuevaContrasena: String)

    @Query("UPDATE usuarios SET nombreCompleto = :nuevoNombre WHERE id = :id")
    suspend fun actualizarNombre(id: Int, nuevoNombre: String)
}
