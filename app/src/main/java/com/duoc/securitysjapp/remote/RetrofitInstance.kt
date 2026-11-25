package com.duoc.securitysjapp.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configura Retrofit con la URL base del Swagger TEVEO.
object RetrofitInstance {
    const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:PzS4KiRV/"

    val api: ApiService by lazy {
        crearApi(BASE_URL)
    }

    // Permite usar un servidor local simulado en las pruebas.
    fun crearApi(baseUrl: String = BASE_URL): ApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
}
