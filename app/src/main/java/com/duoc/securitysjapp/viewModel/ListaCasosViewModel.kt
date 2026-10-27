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

class ListaCasosViewModel(
    private val repository: CasoRepository,
    private val usuarioId: Int,
    private val soloMisCasos: Boolean, // true = "Mis casos", false = "Todos los casos"
) : ViewModel() {

    var casos by mutableStateOf<List<Caso>>(emptyList())
        private set

    init {
        cargarCasos()
    }

    fun cargarCasos() {
        viewModelScope.launch {
            casos = if (soloMisCasos) {
                repository.getByUsuario(usuarioId)
            } else {
                repository.getAll()
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: CasoRepository,
            usuarioId: Int,
            soloMisCasos: Boolean,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ListaCasosViewModel(repository, usuarioId, soloMisCasos) as T
                }
            }
    }
}
