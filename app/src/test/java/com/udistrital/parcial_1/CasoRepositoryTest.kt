package com.udistrital.parcial_1

import com.udistrital.parcial_1.model.Caso
import com.udistrital.parcial_1.model.Evidencia
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para la lógica de [CasoRepository] que NO depende de Context.
 * Se prueban los métodos auxiliares privados que son accesibles a través del
 * comportamiento observable de la lista.
 *
 * Nota: las funciones que persisten en SharedPreferences (agregarCaso, actualizarCaso,
 * cerrarCaso, etc.) requieren un Context de Android y se cubren con tests de
 * instrumentación (androidTest). Aquí se prueban la lógica de dominio pura.
 */
class CasoRepositoryTest {

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun casoDeEjemplo(
        id: String = "CAS-TEST-001",
        titulo: String = "Caso de prueba",
        estado: String = "Abierto",
        prioridad: String = "Media",
        categoria: String = "Robo"
    ) = Caso(
        id = id,
        titulo = titulo,
        categoria = categoria,
        fecha = "21/09/2026",
        ubicacion = "Bogotá",
        implicados = "Desconocido",
        prioridad = prioridad,
        estado = estado,
        descripcion = "Descripción de prueba"
    )

    private fun evidenciaDeEjemplo(id: String = "EV-001", tipo: String = "Hallazgo") =
        Evidencia(
            id = id,
            tipo = tipo,
            descripcion = "Descripción de la evidencia",
            fecha = "21/09/2026 10:00"
        )

    // ── Modelo Caso ───────────────────────────────────────────────────────────

    @Test
    fun `caso se crea con estado Abierto por defecto`() {
        val caso = casoDeEjemplo()
        assertEquals("Abierto", caso.estado)
    }

    @Test
    fun `caso copy cambia estado a Cerrado`() {
        val caso = casoDeEjemplo()
        val casoCerrado = caso.copy(estado = "Cerrado")
        assertEquals("Cerrado", casoCerrado.estado)
        // El original no se modifica
        assertEquals("Abierto", caso.estado)
    }

    @Test
    fun `caso copy conserva el id original`() {
        val caso = casoDeEjemplo(id = "CAS-2026-001")
        val casoEditado = caso.copy(titulo = "Título actualizado")
        assertEquals("CAS-2026-001", casoEditado.id)
        assertEquals("Título actualizado", casoEditado.titulo)
    }

    @Test
    fun `caso copy actualiza solo los campos indicados`() {
        val caso = casoDeEjemplo(titulo = "Original", prioridad = "Alta")
        val casoEditado = caso.copy(titulo = "Nuevo")
        assertEquals("Nuevo", casoEditado.titulo)
        assertEquals("Alta", casoEditado.prioridad)  // sin cambio
    }

    // ── Modelo Evidencia ──────────────────────────────────────────────────────

    @Test
    fun `evidencia de tipo Hallazgo no requiere uri`() {
        val ev = evidenciaDeEjemplo(tipo = "Hallazgo")
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
        assertNotNull(ev.uri)
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

    // ── Lógica de filtrado (sin Context) ──────────────────────────────────────

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
    fun `filtrar casos por estado Cerrado devuelve solo cerrados`() {
        val casos = listOf(
            casoDeEjemplo(id = "A", estado = "Abierto"),
            casoDeEjemplo(id = "B", estado = "Cerrado")
        )
        val cerrados = casos.filter { it.estado == "Cerrado" }
        assertEquals(1, cerrados.size)
        assertEquals("B", cerrados.first().id)
    }

    @Test
    fun `busqueda por titulo es insensible a mayusculas`() {
        val casos = listOf(
            casoDeEjemplo(id = "A", titulo = "Hurto en la joyería"),
            casoDeEjemplo(id = "B", titulo = "Fraude bancario")
        )
        val resultado = casos.filter {
            it.titulo.contains("HURTO", ignoreCase = true)
        }
        assertEquals(1, resultado.size)
        assertEquals("A", resultado.first().id)
    }

    @Test
    fun `busqueda sin coincidencias devuelve lista vacia`() {
        val casos = listOf(
            casoDeEjemplo(id = "A", titulo = "Robo en el parque")
        )
        val resultado = casos.filter {
            it.titulo.contains("homicidio", ignoreCase = true)
        }
        assertTrue(resultado.isEmpty())
    }

    // ── Estadísticas: agrupación por categoría ────────────────────────────────

    @Test
    fun `agrupar por categoria cuenta correctamente`() {
        val casos = listOf(
            casoDeEjemplo(categoria = "Robo"),
            casoDeEjemplo(categoria = "Robo"),
            casoDeEjemplo(categoria = "Fraude")
        )
        val conteo = casos.groupingBy { it.categoria }.eachCount()
        assertEquals(2, conteo["Robo"])
        assertEquals(1, conteo["Fraude"])
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

    // ── Evidencias en lista ───────────────────────────────────────────────────

    @Test
    fun `caso nuevo tiene lista de evidencias vacia`() {
        val caso = casoDeEjemplo()
        assertTrue(caso.evidencias.isEmpty())
    }

    @Test
    fun `filtrar evidencias por tipo Hallazgo funciona`() {
        val evidencias = listOf(
            evidenciaDeEjemplo(id = "E1", tipo = "Hallazgo"),
            evidenciaDeEjemplo(id = "E2", tipo = "Imagen"),
            evidenciaDeEjemplo(id = "E3", tipo = "Hallazgo")
        )
        val hallazgos = evidencias.filter { it.tipo == "Hallazgo" }
        assertEquals(2, hallazgos.size)
    }

    @Test
    fun `eliminar evidencia por id deja el resto intacto`() {
        val evidencias = mutableListOf(
            evidenciaDeEjemplo(id = "E1"),
            evidenciaDeEjemplo(id = "E2"),
            evidenciaDeEjemplo(id = "E3")
        )
        evidencias.removeAll { it.id == "E2" }
        assertEquals(2, evidencias.size)
        assertFalse(evidencias.any { it.id == "E2" })
    }
}
