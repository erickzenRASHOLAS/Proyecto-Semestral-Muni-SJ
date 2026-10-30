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

    private var todosLosCasos: List<Caso> = emptyList()

    var casosFiltrados by mutableStateOf<List<Caso>>(emptyList())
        private set

    var busquedaQuery by mutableStateOf("")
        private set

    var categoriaFiltro by mutableStateOf("Todas")
        private set

    val categoriasFiltro = listOf(
        "Todas",
        "Seguridad",
        "Convivencia",
        "Infraestructura",
        "Medio ambiente",
        "Otro",
    )

    init {
        cargarCasos()
    }

    fun cargarCasos() {
        viewModelScope.launch {
            todosLosCasos = if (soloMisCasos) {
                repository.getByUsuario(usuarioId)
            } else {
                repository.getAll()
            }
            aplicarFiltros()
        }
    }

    fun onBusquedaQueryChange(nuevaQuery: String) {
        busquedaQuery = nuevaQuery
        aplicarFiltros()
    }

    fun onCategoriaFiltroChange(nuevaCategoria: String) {
        categoriaFiltro = nuevaCategoria
        aplicarFiltros()
    }


    private fun aplicarFiltros() {
        val query = busquedaQuery.trim().lowercase()
        casosFiltrados = todosLosCasos.filter { caso ->
            val coincideTexto = query.isEmpty() ||
                    caso.descripcion.lowercase().contains(query) ||
                    caso.ubicacion.lowercase().contains(query) ||
                    caso.categoria.lowercase().contains(query) ||
                    caso.prioridad.lowercase().contains(query) ||
                    "#%04d".format(caso.id).contains(query)

            val coincideCategoria = categoriaFiltro == "Todas" ||
                    caso.categoria.equals(categoriaFiltro, ignoreCase = true)

            coincideTexto && coincideCategoria
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
