package com.duoc.securitysjapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.repository.CasoRepository

class DetalleCasoViewModel(
    private val casoId: Long,
) : ViewModel() {

    private val repository = CasoRepository

    var caso by mutableStateOf<Caso?>(null)
        private set

    init {
        cargarCaso()
    }

    fun cargarCaso() {
        caso = repository.getById(casoId)
    }

    companion object {
        fun provideFactory(casoId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetalleCasoViewModel(casoId) as T
                }
            }
    }
}
