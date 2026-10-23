package com.duoc.securitysjapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.respository.CasoRepository

class ListaCasosViewModel : ViewModel() {

    private val repository = CasoRepository

    var casos by mutableStateOf<List<Caso>>(emptyList())
        private set

    init {
        cargarCasos()
    }

    fun cargarCasos() {
        casos = repository.getAll().sortedByDescending { it.id }
    }
}
