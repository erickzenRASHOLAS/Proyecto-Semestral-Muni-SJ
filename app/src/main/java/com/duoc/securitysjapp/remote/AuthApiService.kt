package com.duoc.securitysjapp.remote

import com.duoc.securitysjapp.remote.dto.XanoAuthLoginRequest
import com.duoc.securitysjapp.remote.dto.XanoAuthSignupRequest
import com.duoc.securitysjapp.remote.dto.XanoAuthTokenDto
import com.duoc.securitysjapp.remote.dto.XanoAuthUserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/** Endpoints de autenticación publicados en el grupo AuthUsuarios de Xano. */
interface AuthApiService {
    @POST("auth/signup")
    suspend fun registrar(@Body request: XanoAuthSignupRequest): Response<XanoAuthTokenDto>

    @POST("auth/login")
    suspend fun iniciarSesion(@Body request: XanoAuthLoginRequest): Response<XanoAuthTokenDto>

    @GET("auth/me")
    suspend fun obtenerMiPerfil(
        @Header("Authorization") authorization: String
    ): Response<XanoAuthUserDto>
}
