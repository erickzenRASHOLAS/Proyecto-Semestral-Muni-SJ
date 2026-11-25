package com.duoc.securitysjapp.repository

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Conserva el ID local y cifra el token JWT usando una clave del Android Keystore. */
class SesionRepository(context: Context) {
    private val preferencias = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

    fun guardarSesion(usuarioId: Int) {
        preferencias.edit().putInt(CLAVE_USUARIO_ID, usuarioId).apply()
    }

    fun obtenerUsuarioId(): Int = preferencias.getInt(CLAVE_USUARIO_ID, SIN_SESION)

    fun guardarToken(token: String) {
        require(token.isNotBlank()) { "Xano devolvió un token vacío." }
        val cipher = Cipher.getInstance(TRANSFORMACION)
        cipher.init(Cipher.ENCRYPT_MODE, obtenerOCrearClave())
        val cifrado = cipher.doFinal(token.toByteArray(StandardCharsets.UTF_8))
        preferencias.edit()
            .putString(CLAVE_TOKEN_IV, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .putString(CLAVE_TOKEN_CIFRADO, Base64.encodeToString(cifrado, Base64.NO_WRAP))
            .apply()
    }

    fun obtenerToken(): String? {
        val ivGuardado = preferencias.getString(CLAVE_TOKEN_IV, null) ?: return null
        val tokenCifrado = preferencias.getString(CLAVE_TOKEN_CIFRADO, null) ?: return null
        return try {
            val cipher = Cipher.getInstance(TRANSFORMACION)
            val iv = Base64.decode(ivGuardado, Base64.NO_WRAP)
            val contenido = Base64.decode(tokenCifrado, Base64.NO_WRAP)
            cipher.init(Cipher.DECRYPT_MODE, obtenerOCrearClave(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(contenido), StandardCharsets.UTF_8)
        } catch (_: Exception) {
            // Si la clave o el contenido ya no son válidos, descarta la sesión dañada.
            borrarDatosSesion()
            null
        }
    }

    fun cerrarSesion() = borrarDatosSesion()

    private fun borrarDatosSesion() {
        preferencias.edit().clear().apply()
    }

    private fun obtenerOCrearClave(): SecretKey {
        val keyStore = KeyStore.getInstance(PROVEEDOR_KEYSTORE).apply { load(null) }
        val existente = keyStore.getKey(ALIAS_CLAVE, null) as? SecretKey
        if (existente != null) return existente

        val generador = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVEEDOR_KEYSTORE)
        generador.init(
            KeyGenParameterSpec.Builder(
                ALIAS_CLAVE,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generador.generateKey()
    }

    companion object {
        const val SIN_SESION = -1
        private const val PROVEEDOR_KEYSTORE = "AndroidKeyStore"
        private const val ALIAS_CLAVE = "securitysjapp_auth_token_key"
        private const val TRANSFORMACION = "AES/GCM/NoPadding"
        private const val CLAVE_USUARIO_ID = "usuario_id"
        private const val CLAVE_TOKEN_IV = "auth_token_iv"
        private const val CLAVE_TOKEN_CIFRADO = "auth_token_encrypted"
    }
}
