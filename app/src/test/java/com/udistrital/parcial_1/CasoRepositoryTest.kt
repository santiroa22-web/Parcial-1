package com.udistrital.parcial_1

import com.udistrital.parcial_1.model.Caso
import com.udistrital.parcial_1.model.Evidencia
import org.junit.Assert.*
import org.junit.Test


class CasoRepositoryTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun casoDeEjemplo(
        id: String = "CAS-TEST-001",
        titulo: String = "Caso de prueba",
        descripcion: String = "Descripción de prueba",
        estado: String = "Abierto"
    ) = Caso(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        fecha = "21/09/2026",
        estado = estado,
        fechaInicio = TODO(),
        evidencias = TODO(),
    )

    private fun hallazgoDeEjemplo(id: String = "EV-001") = Evidencia(
        id = id,
        tipo = "Hallazgo",
        descripcion = "Huella en la puerta",
        fecha = "21/09/2026 10:00"
    )

    // ── Crear caso ────────────────────────────────────────────────────────────

    @Test
    fun `caso se crea con estado Abierto por defecto`() {
        val caso = casoDeEjemplo()
        assertEquals("Abierto", caso.estado)
    }

    @Test
    fun `caso se crea con los campos requeridos por el profesor`() {
        val caso = casoDeEjemplo(titulo = "Robo", descripcion = "Detalles")
        assertEquals("Robo", caso.titulo)
        assertEquals("Detalles", caso.descripcion)
        assertEquals("21/09/2026", caso.fecha)
        assertEquals("Abierto", caso.estado)
    }

    // ── Editar caso ───────────────────────────────────────────────────────────

    @Test
    fun `editar caso conserva el id original`() {
        val caso = casoDeEjemplo(id = "CAS-2026-001")
        val editado = caso.copy(titulo = "Título actualizado")
        assertEquals("CAS-2026-001", editado.id)
        assertEquals("Título actualizado", editado.titulo)
    }

    @Test
    fun `editar caso actualiza solo los campos indicados`() {
        val caso = casoDeEjemplo(titulo = "Original", descripcion = "Vieja")
        val editado = caso.copy(descripcion = "Nueva")
        assertEquals("Original", editado.titulo)
        assertEquals("Nueva", editado.descripcion)
    }

    // ── Estado y cierre ───────────────────────────────────────────────────────

    @Test
    fun `cerrar caso cambia estado a Cerrado`() {
        val caso = casoDeEjemplo()
        val cerrado = caso.copy(estado = "Cerrado")
        assertEquals("Cerrado", cerrado.estado)
        assertEquals("Abierto", caso.estado)
    }

    @Test
    fun `conteo de abiertos y cerrados es correcto`() {
        val casos = listOf(
            casoDeEjemplo(estado = "Abierto"),
            casoDeEjemplo(estado = "Abierto"),
            casoDeEjemplo(estado = "Cerrado")
        )
        assertEquals(2, casos.count { it.estado == "Abierto" })
        assertEquals(1, casos.count { it.estado == "Cerrado" })
    }

    // ── Búsqueda y listado ────────────────────────────────────────────────────

    @Test
    fun `filtrar casos por estado Abierto devuelve solo abiertos`() {
        val casos = listOf(
            casoDeEjemplo(id = "A", estado = "Abierto"),
            casoDeEjemplo(id = "B", estado = "Cerrado"),
            casoDeEjemplo(id = "C", estado = "Abierto")
        )
        val abiertos = casos.filter { it.estado == "Abierto" }
        assertEquals(2, abiertos.size)
        assertTrue(abiertos.all { it.estado == "Abierto" })
    }

    @Test
    fun `busqueda por titulo es insensible a mayusculas`() {
        val casos = listOf(
            casoDeEjemplo(id = "A", titulo = "Hurto en la joyería"),
            casoDeEjemplo(id = "B", titulo = "Fraude bancario")
        )
        val resultado = casos.filter { it.titulo.contains("HURTO", ignoreCase = true) }
        assertEquals(1, resultado.size)
        assertEquals("A", resultado.first().id)
    }

    @Test
    fun `busqueda por descripcion encuentra coincidencias`() {
        val casos = listOf(
            casoDeEjemplo(id = "A", descripcion = "Robo con violencia"),
            casoDeEjemplo(id = "B", descripcion = "Suplantación de identidad")
        )
        val resultado = casos.filter { it.descripcion.contains("suplantación", ignoreCase = true) }
        assertEquals(1, resultado.size)
        assertEquals("B", resultado.first().id)
    }

    @Test
    fun `busqueda sin coincidencias devuelve lista vacia`() {
        val casos = listOf(casoDeEjemplo(id = "A", titulo = "Robo en el parque"))
        val resultado = casos.filter { it.titulo.contains("homicidio", ignoreCase = true) }
        assertTrue(resultado.isEmpty())
    }

    // ── Eliminar caso ─────────────────────────────────────────────────────────

    @Test
    fun `eliminar caso por id deja el resto intacto`() {
        val casos = mutableListOf(
            casoDeEjemplo(id = "A"),
            casoDeEjemplo(id = "B"),
            casoDeEjemplo(id = "C")
        )
        casos.removeAll { it.id == "B" }
        assertEquals(2, casos.size)
        assertFalse(casos.any { it.id == "B" })
    }

    // ── Hallazgos y evidencias ────────────────────────────────────────────────

    @Test
    fun `caso nuevo tiene lista de evidencias vacia`() {
        val caso = casoDeEjemplo()
        assertTrue(caso.evidencias.isEmpty())
    }

    @Test
    fun `evidencia de tipo Hallazgo no requiere uri`() {
        val ev = hallazgoDeEjemplo()
        assertEquals("Hallazgo", ev.tipo)
        assertNull(ev.uri)
    }

    @Test
    fun `evidencia de tipo Imagen guarda uri`() {
        val ev = Evidencia(
            id = "EV-002",
            tipo = "Imagen",
            descripcion = "Foto de la escena",
            uri = "content://media/image.jpg",
            fecha = "21/09/2026 10:15"
        )
        assertEquals("Imagen", ev.tipo)
        assertEquals("content://media/image.jpg", ev.uri)
    }

    @Test
    fun `evidencia de tipo Documento guarda nombre de archivo`() {
        val ev = Evidencia(
            id = "EV-003",
            tipo = "Documento",
            descripcion = "Informe forense",
            uri = "content://media/informe.pdf",
            nombreArchivo = "informe_forense.pdf",
            fecha = "21/09/2026 11:00"
        )
        assertEquals("informe_forense.pdf", ev.nombreArchivo)
    }

    @Test
    fun `filtrar evidencias por tipo Hallazgo funciona`() {
        val evidencias = listOf(
            hallazgoDeEjemplo(id = "E1"),
            Evidencia(id = "E2", tipo = "Imagen", descripcion = "foto", uri = "u", fecha = "f"),
            hallazgoDeEjemplo(id = "E3")
        )
        val hallazgos = evidencias.filter { it.tipo == "Hallazgo" }
        assertEquals(2, hallazgos.size)
    }

    @Test
    fun `eliminar evidencia por id deja el resto intacto`() {
        val evidencias = mutableListOf(
            hallazgoDeEjemplo(id = "E1"),
            hallazgoDeEjemplo(id = "E2"),
            hallazgoDeEjemplo(id = "E3")
        )
        evidencias.removeAll { it.id == "E2" }
        assertEquals(2, evidencias.size)
        assertFalse(evidencias.any { it.id == "E2" })
    }
}
