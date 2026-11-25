package com.duoc.securitysjapp.repository

import com.duoc.securitysjapp.remote.RetrofitInstance
import com.duoc.securitysjapp.remote.dto.XanoCasoRequest
import com.duoc.securitysjapp.remote.dto.XanoHistorialRequest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CasoRemoteRepositoryTest {

    private lateinit var servidor: MockWebServer
    private lateinit var repository: XanoRepository

    @Before
    fun preparar() {
        // Inicia un servidor simulado para probar las solicitudes sin conectar a Xano real.
        servidor = MockWebServer()
        servidor.start()

        val api = RetrofitInstance.crearApi(servidor.url("/api:PzS4KiRV/").toString())
        repository = XanoRepository(api)
    }

    @After
    fun limpiar() {
        // Cierra el servidor simulado después de cada prueba.
        servidor.shutdown()
    }

    @Test
    fun `GET casos convierte la respuesta de Xano`() = runTest {
        // Simula la respuesta de Xano con un caso registrado.
        servidor.enqueue(
            MockResponse()
                .setBody(
                    """[{"id":12,"created_at":1795000000000,"usuarios_id":3,"categoria_id":1,"descripcion":"Luminaria apagada en la plaza","ubicacion":"Plaza central","latitud":-33.5,"longitud":-70.6,"prioridad":"Media","estado":"En derivación"}]"""
                )
                .addHeader("Content-Type", "application/json")
        )

        val resultado = repository.obtenerCasos()

        // Comprueba que el caso y sus datos principales se conviertan correctamente.
        assertEquals(1, resultado.size)
        assertEquals(12L, resultado.first().id)
        assertEquals("Luminaria apagada en la plaza", resultado.first().descripcion)

        val solicitud = servidor.takeRequest()
        assertEquals("GET", solicitud.method)
        assertEquals("/api:PzS4KiRV/casos", solicitud.path)
    }

    @Test
    fun `POST casos envia los campos esperados`() = runTest {
        // Simula la respuesta de Xano al crear un caso.
        servidor.enqueue(
            MockResponse()
                .setBody(
                    """{"id":19,"usuarios_id":3,"categoria_id":2,"descripcion":"Basura acumulada en la esquina","ubicacion":"Pasaje Norte","prioridad":"Baja","estado":"En derivación"}"""
                )
                .addHeader("Content-Type", "application/json")
        )

        val solicitudCaso = XanoCasoRequest(
            usuarioId = 3,
            categoriaId = 2,
            descripcion = "Basura acumulada en la esquina",
            ubicacion = "Pasaje Norte",
            prioridad = "Baja",
            estado = "En derivación"
        )

        val resultado = repository.crearCaso(solicitudCaso)

        // Comprueba que Xano devuelva el ID y que la app envíe los datos correctos.
        assertEquals(19L, resultado.id)

        val solicitud = servidor.takeRequest()
        assertEquals("POST", solicitud.method)
        assertEquals("/api:PzS4KiRV/casos", solicitud.path)

        val cuerpo = solicitud.body.readUtf8()
        assertTrue(cuerpo.contains("\"usuarios_id\":3"))
        assertTrue(cuerpo.contains("\"categoria_id\":2"))
        assertTrue(cuerpo.contains("Basura acumulada en la esquina"))
    }

    @Test
    fun `POST historial envia los identificadores y estados esperados`() = runTest {
        // Simula la respuesta de Xano al guardar un cambio de estado.
        servidor.enqueue(
            MockResponse()
                .setBody(
                    """{"id":7,"casos_id":19,"estado_anterior":"En derivación","estado_nuevo":"Cerrado","cambiado_por_usuario_id":3,"motivo_cambio":"Cierre desde app","fecha_cambio":1795000000000}"""
                )
                .addHeader("Content-Type", "application/json")
        )

        val resultado = repository.crearHistorial(
            XanoHistorialRequest(
                casoId = 19,
                estadoAnterior = "En derivación",
                estadoNuevo = "Cerrado",
                usuarioId = 3,
                motivoCambio = "Cierre desde app",
                fechaCambio = 1795000000000
            )
        )

        // Comprueba que se guarde el historial con el caso, usuario y estado correctos.
        assertEquals(7L, resultado.id)

        val solicitud = servidor.takeRequest()
        assertEquals("POST", solicitud.method)
        assertEquals("/api:PzS4KiRV/historial_estados_caso", solicitud.path)

        val cuerpo = solicitud.body.readUtf8()
        assertTrue(cuerpo.contains("\"casos_id\":19"))
        assertTrue(cuerpo.contains("\"cambiado_por_usuario_id\":3"))
        assertTrue(cuerpo.contains("\"estado_nuevo\":\"Cerrado\""))
    }

    @Test
    fun `GET caso por ID consulta el registro remoto`() = runTest {
        // Simula la respuesta de Xano al consultar un caso específico.
        servidor.enqueue(
            MockResponse()
                .setBody(
                    """{"id":21,"usuarios_id":3,"categoria_id":1,"descripcion":"Bache en la calle","ubicacion":"Calle 1","prioridad":"Alta","estado":"Pendiente"}"""
                )
                .addHeader("Content-Type", "application/json")
        )

        val resultado = repository.obtenerCaso(21)

        // Comprueba que se recupere el caso solicitado.
        assertEquals(21L, resultado.id)
        assertEquals("Bache en la calle", resultado.descripcion)

        val solicitud = servidor.takeRequest()
        assertEquals("GET", solicitud.method)
        assertEquals("/api:PzS4KiRV/casos/21", solicitud.path)
    }
}
