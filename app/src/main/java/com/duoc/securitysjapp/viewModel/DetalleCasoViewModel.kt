package com.duoc.securitysjapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.repository.CasoRepository
import kotlinx.coroutines.launch

class DetalleCasoViewModel(
    private val repository: CasoRepository,
    private val casoId: Long,
) : ViewModel() {

    var caso by mutableStateOf<Caso?>(null)
        private set

    init {
        cargarCaso()
    }

    fun cargarCaso() {
        viewModelScope.launch {
            caso = repository.getById(casoId)
        }
    }

    // Cambia el estado a "Cerrado" y avisa cuando ya quedó guardado
    fun cerrarCaso(onCerrado: () -> Unit) {
        val actual = caso ?: return
        viewModelScope.launch {
            repository.update(actual.copy(estado = "Cerrado"))
            caso = repository.getById(casoId)
            onCerrado()
        }
    }

    companion object {
        fun provideFactory(repository: CasoRepository, casoId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetalleCasoViewModel(repository, casoId) as T
                }
            }
    }
}
