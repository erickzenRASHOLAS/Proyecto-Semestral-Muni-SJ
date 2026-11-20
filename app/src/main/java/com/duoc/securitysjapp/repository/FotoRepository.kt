package com.duoc.securitysjapp.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

// Guarda y lee las fotos como archivos en la carpeta privada de la app (en Room solo queda la ruta)
class FotoRepository(private val context: Context) {

    // Guarda la foto como JPEG y devuelve su ruta (null si no se pudo guardar)
    suspend fun guardarFoto(nombreArchivo: String, bitmap: Bitmap): String? {
        // Se hace en el hilo de entrada/salida para no congelar la pantalla
        return withContext(Dispatchers.IO) {
            try {
                val archivo = File(context.filesDir, nombreArchivo)
                FileOutputStream(archivo).use { salida ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, salida)
                }
                archivo.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }

    // Lee la foto de esa ruta. Devuelve null si no hay ruta o el archivo ya no existe
    suspend fun cargarFoto(ruta: String?): Bitmap? {
        if (ruta.isNullOrEmpty()) return null
        return withContext(Dispatchers.IO) {
            val archivo = File(ruta)
            if (archivo.exists()) BitmapFactory.decodeFile(archivo.absolutePath) else null
        }
    }
}
