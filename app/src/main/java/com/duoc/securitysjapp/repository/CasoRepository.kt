package com.duoc.securitysjapp.repository

import com.duoc.securitysjapp.model.Caso
import com.duoc.securitysjapp.model.CasoDao
import com.duoc.securitysjapp.remote.dto.*

// Informa si un registro quedó guardado localmente y también en Xano.
data class ResultadoGuardadoCaso(
    val idLocal: Long,
    val sincronizado: Boolean,
    val error: String? = null
)

// Informa por separado si Xano actualizó el caso y si guardó el historial.
data class ResultadoCierreCaso(
    val actualizadoEnXano: Boolean,
    val historialRegistrado: Boolean,
    val error: String? = null
)

class CasoRepository(
    private val dao: CasoDao,
    private val xanoRepository: XanoRepository,
    private val usuarioRepository: UsuarioRepository
) {
    private val categoriasIniciales = listOf(
        "Seguridad", "Convivencia", "Infraestructura", "Medio ambiente", "Otro"
    )

    suspend fun getAll(): List<Caso> {
        // Room mantiene la consulta disponible aunque Xano no responda.
        runCatching { sincronizarPendientes() }
        runCatching { descargarCasosXano() }
        return dao.getAll()
    }

    suspend fun getByUsuario(usuarioId: Int): List<Caso> {
        getAll()
        return dao.getByUsuario(usuarioId)
    }

    suspend fun getConUbicacion(): List<Caso> {
        getAll()
        return dao.getConUbicacion()
    }

    suspend fun getById(id: Long): Caso? = dao.getById(id)

    // Guarda primero en Room; después intenta enviar el registro al servidor.
    suspend fun insert(caso: Caso): ResultadoGuardadoCaso {
        val idLocal = dao.insert(caso)
        val guardado = caso.copy(id = idLocal)
        return try {
            sincronizarCaso(guardado)
            ResultadoGuardadoCaso(idLocal, true)
        } catch (error: Exception) {
            ResultadoGuardadoCaso(idLocal, false, detalleError(error))
        }
    }

    suspend fun cerrarCaso(caso: Caso, usuarioLocalId: Int): ResultadoCierreCaso {
        val cerrado = caso.copy(estado = "Cerrado")
        val idRemoto = cerrado.xanoId
            ?: return ResultadoCierreCaso(
                actualizadoEnXano = false,
                historialRegistrado = false,
                error = "El caso todavía no tiene un ID remoto de Xano."
            )

        // Primero actualiza el caso; después registra el historial como operación separada.
        val usuarioPropietarioRemotoId: Long
        val usuarioQueCierraRemotoId: Long
        val categoriaId: Long
        try {
            usuarioPropietarioRemotoId = cerrado.xanoUsuarioId
                ?: usuarioRepository.asegurarUsuarioXano(cerrado.usuarioId)
            usuarioQueCierraRemotoId = usuarioRepository.asegurarUsuarioXano(usuarioLocalId)
            val categorias = xanoRepository.obtenerCategorias()
            categoriaId = categorias.firstOrNull {
                it.nombre?.equals(cerrado.categoria, ignoreCase = true) == true
            }?.id ?: throw IllegalStateException("La categoría no existe en Xano")

            // Confirma que el ID remoto pertenece a un caso existente antes de modificarlo.
            xanoRepository.obtenerCaso(idRemoto)
            xanoRepository.actualizarCaso(
                idRemoto,
                cerrado.aRequest(usuarioPropietarioRemotoId, categoriaId)
            )
        } catch (error: Exception) {
            return ResultadoCierreCaso(
                actualizadoEnXano = false,
                historialRegistrado = false,
                error = "No se pudo actualizar el caso en Xano: ${detalleError(error)}"
            )
        }

        return try {
            // Evita duplicar el historial si se reintenta un cierre ya enviado.
            val historialExiste = xanoRepository.obtenerHistorial().any {
                it.casoId == idRemoto &&
                    it.estadoAnterior == caso.estado &&
                    it.estadoNuevo == "Cerrado" &&
                    it.usuarioId == usuarioQueCierraRemotoId
            }
            if (!historialExiste) {
                xanoRepository.crearHistorial(
                    XanoHistorialRequest(
                        casoId = idRemoto,
                        estadoAnterior = caso.estado,
                        estadoNuevo = "Cerrado",
                        usuarioId = usuarioQueCierraRemotoId,
                        motivoCambio = "Cierre de caso desde SecuritySJApp",
                        fechaCambio = System.currentTimeMillis()
                    )
                )
            }
            // Solo actualiza Room cuando el caso y su historial están confirmados en Xano.
            dao.update(cerrado)
            ResultadoCierreCaso(
                actualizadoEnXano = true,
                historialRegistrado = true
            )
        } catch (error: Exception) {
            ResultadoCierreCaso(
                actualizadoEnXano = true,
                historialRegistrado = false,
                error = "Xano actualizó el caso, pero rechazó el registro del historial: ${detalleError(error)}"
            )
        }
    }

    // Devuelve el código y el mensaje de Xano para identificar errores de validación.
    private fun detalleError(error: Exception): String {
        if (error is retrofit2.HttpException) {
            val cuerpo = runCatching { error.response()?.errorBody()?.string() }
                .getOrNull()
                ?.take(500)
            return "HTTP ${error.code()}${if (cuerpo.isNullOrBlank()) "" else " - $cuerpo"}"
        }
        return error.message ?: error.javaClass.simpleName
    }

    suspend fun eliminarCaso(caso: Caso) {
        // Primero confirma la eliminación remota para no perder el registro local ante un error.
        caso.xanoId?.let { id -> xanoRepository.eliminarCaso(id) }
        dao.eliminar(caso)
    }

    suspend fun obtenerObservaciones(casoLocalId: Long): List<XanoObservacionDto> {
        val caso = dao.getById(casoLocalId) ?: return emptyList()
        val idRemoto = caso.xanoId ?: return emptyList()
        return xanoRepository.obtenerObservaciones().filter { it.casoId == idRemoto }
    }

    suspend fun agregarObservacion(casoLocalId: Long, usuarioLocalId: Int, texto: String) {
        val caso = dao.getById(casoLocalId) ?: throw IllegalStateException("Caso no encontrado")
        val casoRemotoId = caso.xanoId ?: throw IllegalStateException("El caso aún no está sincronizado")
        val usuarioRemotoId = usuarioRepository.asegurarUsuarioXano(usuarioLocalId)
        xanoRepository.crearObservacion(
            XanoObservacionRequest(casoRemotoId, usuarioRemotoId, texto.trim())
        )
    }

    suspend fun obtenerHistorial(casoLocalId: Long): List<XanoHistorialDto> {
        val caso = dao.getById(casoLocalId) ?: return emptyList()
        val idRemoto = caso.xanoId ?: return emptyList()
        return xanoRepository.obtenerHistorial().filter { it.casoId == idRemoto }
    }

    private suspend fun sincronizarPendientes() {
        // No se ocultan los errores: el llamador puede informar por qué no sincronizó.
        dao.getPendientes().forEach { pendiente -> sincronizarCaso(pendiente) }
    }

    private suspend fun sincronizarCaso(caso: Caso) {
        // Si ya existe ID remoto, comprueba que el registro siga disponible.
        caso.xanoId?.let { idRemoto ->
            try {
                xanoRepository.obtenerCaso(idRemoto)
                return
            } catch (error: retrofit2.HttpException) {
                if (error.code() != 404) throw error
                // Si el registro fue eliminado en Xano, se vuelve a crear.
                dao.update(caso.copy(xanoId = null, xanoUsuarioId = null))
            }
        }

        val usuarioRemotoId = usuarioRepository.asegurarUsuarioXano(caso.usuarioId)
        val categoriaId = asegurarCategoria(caso.categoria)
        val respuesta = xanoRepository.crearCaso(caso.aRequest(usuarioRemotoId, categoriaId))
        if (respuesta.id <= 0L) throw IllegalStateException("Xano no devolvió un ID válido al crear el caso")

        // Guarda el ID recibido para que un fallo de red posterior no duplique el caso.
        dao.update(caso.copy(xanoId = respuesta.id, xanoUsuarioId = respuesta.usuarioId))

        // Verifica el registro por su ID antes de confirmar la sincronización.
        val confirmado = xanoRepository.obtenerCaso(respuesta.id)
        if (confirmado.id != respuesta.id || confirmado.usuarioId != usuarioRemotoId) {
            throw IllegalStateException("Xano respondió al POST, pero el caso no coincide al consultarlo")
        }
        dao.update(caso.copy(xanoId = confirmado.id, xanoUsuarioId = confirmado.usuarioId))
    }

    private suspend fun asegurarCategoria(nombre: String): Long {
        val categorias = asegurarCategorias()
        return categorias.firstOrNull { it.nombre?.equals(nombre, ignoreCase = true) == true }?.id
            ?: throw IllegalStateException("No se encontró la categoría $nombre en Xano")
    }

    private suspend fun asegurarCategorias(): List<XanoCategoriaDto> {
        val categorias = xanoRepository.obtenerCategorias().toMutableList()
        categoriasIniciales.forEach { nombre ->
            if (categorias.none { it.nombre?.equals(nombre, ignoreCase = true) == true }) {
                categorias += xanoRepository.crearCategoria(XanoCategoriaRequest(nombre, true))
            }
        }
        return categorias
    }

    private suspend fun descargarCasosXano() {
        val categorias = asegurarCategorias()
        val casosRemotos = xanoRepository.obtenerCasos()
        casosRemotos.forEach { remoto ->
            val existente = dao.getByXanoId(remoto.id)
            val usuarioLocal = usuarioRepository.obtenerUsuarioPorXanoId(remoto.usuarioId)
            val usuarioId = usuarioLocal?.id ?: -remoto.usuarioId.toInt()
            val convertido = remoto.aCasoLocal(existente, categorias, usuarioId)
            if (existente == null) dao.insertarOActualizar(convertido) else dao.update(convertido)
        }
    }

    private fun Caso.aRequest(usuarioRemotoId: Long, categoriaRemotaId: Long) = XanoCasoRequest(
        usuarioId = usuarioRemotoId,
        categoriaId = categoriaRemotaId,
        descripcion = descripcion,
        ubicacion = ubicacion,
        latitud = latitud,
        longitud = longitud,
        prioridad = prioridad,
        estado = estado,
        responsable = null,
        fechaActualizacion = System.currentTimeMillis()
    )

    private fun XanoCasoDto.aCasoLocal(
        existente: Caso?,
        categorias: List<XanoCategoriaDto>,
        usuarioIdLocal: Int
    ) = Caso(
        id = existente?.id ?: 0L,
        usuarioId = usuarioIdLocal,
        categoria = categorias.firstOrNull { it.id == categoriaId }?.nombre ?: "Otro",
        descripcion = descripcion,
        ubicacion = ubicacion,
        prioridad = prioridad,
        fechaCreacionMillis = createdAt ?: existente?.fechaCreacionMillis ?: System.currentTimeMillis(),
        estado = estado,
        fotoRuta = existente?.fotoRuta,
        latitud = latitud,
        longitud = longitud,
        xanoId = id,
        xanoUsuarioId = usuarioId
    )
}
