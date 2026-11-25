package com.duoc.securitysjapp.remote

import com.duoc.securitysjapp.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// Declara los endpoints CRUD publicados en el Swagger TEVEO.
interface ApiService {
    // Casos
    @GET("casos") suspend fun obtenerCasos(): List<XanoCasoDto>
    @GET("casos/{casos_id}") suspend fun obtenerCaso(@Path("casos_id") id: Long): XanoCasoDto
    @POST("casos") suspend fun crearCaso(@Body caso: XanoCasoRequest): XanoCasoDto
    @PATCH("casos/{casos_id}") suspend fun actualizarCaso(@Path("casos_id") id: Long, @Body caso: XanoCasoRequest): XanoCasoDto
    @DELETE("casos/{casos_id}") suspend fun eliminarCaso(@Path("casos_id") id: Long)

    // Categorías
    @GET("categorias_caso") suspend fun obtenerCategorias(): List<XanoCategoriaDto>
    @GET("categorias_caso/{categorias_caso_id}") suspend fun obtenerCategoria(@Path("categorias_caso_id") id: Long): XanoCategoriaDto
    @POST("categorias_caso") suspend fun crearCategoria(@Body categoria: XanoCategoriaRequest): XanoCategoriaDto
    @PATCH("categorias_caso/{categorias_caso_id}") suspend fun actualizarCategoria(@Path("categorias_caso_id") id: Long, @Body categoria: XanoCategoriaRequest): XanoCategoriaDto
    @DELETE("categorias_caso/{categorias_caso_id}") suspend fun eliminarCategoria(@Path("categorias_caso_id") id: Long)

    // Roles
    @GET("roles") suspend fun obtenerRoles(): List<XanoRolDto>
    @GET("roles/{roles_id}") suspend fun obtenerRol(@Path("roles_id") id: Long): XanoRolDto
    @POST("roles") suspend fun crearRol(@Body rol: XanoRolRequest): XanoRolDto
    @PATCH("roles/{roles_id}") suspend fun actualizarRol(@Path("roles_id") id: Long, @Body rol: XanoRolRequest): XanoRolDto
    @DELETE("roles/{roles_id}") suspend fun eliminarRol(@Path("roles_id") id: Long)

    // Usuarios: el Swagger TEVEO no incluye contrasenia en estos cuerpos.
    @GET("usuarios") suspend fun obtenerUsuarios(): List<XanoUsuarioDto>
    @GET("usuarios/{usuarios_id}") suspend fun obtenerUsuario(@Path("usuarios_id") id: Long): XanoUsuarioDto
    @POST("usuarios") suspend fun crearUsuario(@Body usuario: XanoUsuarioRequest): XanoUsuarioDto
    @PATCH("usuarios/{usuarios_id}") suspend fun actualizarUsuario(@Path("usuarios_id") id: Long, @Body usuario: XanoUsuarioRequest): XanoUsuarioDto
    @DELETE("usuarios/{usuarios_id}") suspend fun eliminarUsuario(@Path("usuarios_id") id: Long)

    // Historial de estados
    @GET("historial_estados_caso") suspend fun obtenerHistorial(): List<XanoHistorialDto>
    @GET("historial_estados_caso/{historial_estados_caso_id}") suspend fun obtenerRegistroHistorial(@Path("historial_estados_caso_id") id: Long): XanoHistorialDto
    @POST("historial_estados_caso") suspend fun crearHistorial(@Body historial: XanoHistorialRequest): XanoHistorialDto
    @PATCH("historial_estados_caso/{historial_estados_caso_id}") suspend fun actualizarHistorial(@Path("historial_estados_caso_id") id: Long, @Body historial: XanoHistorialRequest): XanoHistorialDto
    @DELETE("historial_estados_caso/{historial_estados_caso_id}") suspend fun eliminarHistorial(@Path("historial_estados_caso_id") id: Long)

    // Observaciones
    @GET("observaciones_caso") suspend fun obtenerObservaciones(): List<XanoObservacionDto>
    @GET("observaciones_caso/{observaciones_caso_id}") suspend fun obtenerObservacion(@Path("observaciones_caso_id") id: Long): XanoObservacionDto
    @POST("observaciones_caso") suspend fun crearObservacion(@Body observacion: XanoObservacionRequest): XanoObservacionDto
    @PATCH("observaciones_caso/{observaciones_caso_id}") suspend fun actualizarObservacion(@Path("observaciones_caso_id") id: Long, @Body observacion: XanoObservacionRequest): XanoObservacionDto
    @DELETE("observaciones_caso/{observaciones_caso_id}") suspend fun eliminarObservacion(@Path("observaciones_caso_id") id: Long)
}
