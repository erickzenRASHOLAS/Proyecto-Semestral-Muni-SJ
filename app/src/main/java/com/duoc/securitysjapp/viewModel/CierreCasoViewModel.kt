package com.duoc.securitysjapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CierreCasoViewModel(
    private val casoId: Long,
) : ViewModel() {

    val idCasoFormateado: String
        get() = "#%04d".format(casoId)

    val mensajeCierre: String
        get() = "El caso $idCasoFormateado ha sido cerrado correctamente."

    var navegarAlInicio by mutableStateOf(false)
        private set

    fun volverAlInicio() {
        navegarAlInicio = true
    }

    fun consumirNavegacionAlInicio() {
        navegarAlInicio = false
    }

    companion object {
        fun provideFactory(casoId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CierreCasoViewModel(casoId) as T
                }
            }
    }
}
