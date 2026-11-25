package com.duoc.securitysjapp.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Retrofit independiente porque AuthUsuarios tiene un prefijo distinto a APITeveo. */
object AuthRetrofitInstance {
    const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:auth-usuarios/"

    val api: AuthApiService by lazy { crearApi(BASE_URL) }

    fun crearApi(baseUrl: String = BASE_URL): AuthApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
}
