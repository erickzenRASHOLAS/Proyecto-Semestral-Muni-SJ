package com.duoc.securitysjapp.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

// Obtiene la ubicación del dispositivo con el GPS (servicios de Google)
class UbicacionRepository(private val context: Context) {

    // Indica si el usuario ya aceptó el permiso de ubicación (precisa o aproximada)
    fun tienePermisoUbicacion(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Pide la ubicación actual al GPS. Devuelve null si no hay permiso o no se pudo obtener
    @SuppressLint("MissingPermission") // el permiso se revisa en la primera línea
    suspend fun obtenerUbicacion(): Location? {
        if (!tienePermisoUbicacion()) return null

        val cliente = LocationServices.getFusedLocationProviderClient(context)
        return try {
            // Espera máximo 8 segundos para no dejar la pantalla cargando
            withTimeoutOrNull(8000) {
                cliente.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            }
        } catch (e: Exception) {
            null
        }
    }
}
