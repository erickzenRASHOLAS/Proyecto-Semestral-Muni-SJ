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

class MapaCasosViewModel(private val repository: CasoRepository) : ViewModel() {

    // Casos que tienen coordenadas (solo esos se pueden mostrar como pin)
    var casosConUbicacion by mutableStateOf<List<Caso>>(emptyList())
        private set

    // Caso del pin que el usuario tocó (null = ningún pin seleccionado)
    var casoSeleccionado by mutableStateOf<Caso?>(null)
        private set

    init {
        cargarCasos()
    }

    // Se llama una sola vez al crear el ViewModel: lee la base de datos y guarda los casos con GPS
    private fun cargarCasos() {
        viewModelScope.launch {
            casosConUbicacion = repository.getAll().filter { it.latitud != null && it.longitud != null }
        }
    }

    fun seleccionarCaso(caso: Caso) {
        casoSeleccionado = caso
    }

    fun quitarSeleccion() {
        casoSeleccionado = null
    }

    companion object {
        fun provideFactory(repository: CasoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MapaCasosViewModel(repository) as T
                }
            }
    }
}
