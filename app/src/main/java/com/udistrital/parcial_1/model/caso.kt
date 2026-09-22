package com.udistrital.parcial_1.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject


data class Caso(
    val id: String,
    var titulo: String,
    var descripcion: String,
    var fecha: String,        // Fecha de registro
    var fechaInicio: String,  // Fecha de inicio del suceso/investigación
    var estado: String,       // "Abierto" o "Cerrado"
    val evidencias: MutableList<Evidencia> = mutableStateListOf()
)

/**
 * Hallazgo o evidencia asociada a un caso.
 * tipo puede ser: "Hallazgo" (texto), "Imagen" o "Documento".
 */
data class Evidencia(
    val id: String,
    val tipo: String,
    var descripcion: String,
    var uri: String? = null,
    var nombreArchivo: String? = null,
    val fecha: String
)

/**
 * Repositorio en memoria con persistencia en SharedPreferences.
 * Encapsula las operaciones CRUD sobre los casos.
 */
object CasoRepository {
    val listaCasos = mutableStateListOf<Caso>()
    private var isInitialized = false

    fun cargarCasos(context: Context) {
        if (isInitialized) return
        val prefs = context.getSharedPreferences("SmartTracePrefs", Context.MODE_PRIVATE)
        val jsonStr = prefs.getString("casos_guardados", null)

        listaCasos.clear()
        if (jsonStr.isNullOrEmpty()) {
            // Casos iniciales de ejemplo
            listaCasos.addAll(
                listOf(
                    Caso(
                        id = "CAS-001",
                        titulo = "Hurto en la Joyería Real",
                        descripcion = "Ingreso nocturno forzando la cerradura posterior. Sustracción de joyas.",
                        fecha = "15/09/2026",
                        fechaInicio = "14/09/2026",
                        estado = "Abierto"
                    ),
                    Caso(
                        id = "CAS-002",
                        titulo = "Fraude Bancario Digital",
                        descripcion = "Desvío de fondos mediante suplantación de identidad en pasarela de pagos.",
                        fecha = "10/09/2026",
                        fechaInicio = "01/09/2026",
                        estado = "Abierto"
                    )
                )
            )
            guardarCasos(context)
        } else {
            try {
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    val evidenciasList = mutableStateListOf<Evidencia>()
                    val evidenciasArray = obj.optJSONArray("evidencias")
                    if (evidenciasArray != null) {
                        for (j in 0 until evidenciasArray.length()) {
                            val eObj = evidenciasArray.getJSONObject(j)
                            evidenciasList.add(
                                Evidencia(
                                    id = eObj.optString("id", ""),
                                    tipo = eObj.optString("tipo", "Hallazgo"),
                                    descripcion = eObj.optString("descripcion", ""),
                                    uri = eObj.optString("uri", "").ifBlank { null },
                                    nombreArchivo = eObj.optString("nombreArchivo", "").ifBlank { null },
                                    fecha = eObj.optString("fecha", "")
                                )
                            )
                        }
                    }

                    listaCasos.add(
                        Caso(
                            id = obj.optString("id", ""),
                            titulo = obj.optString("titulo", ""),
                            descripcion = obj.optString("descripcion", ""),
                            fecha = obj.optString("fecha", ""),
                            fechaInicio = obj.optString("fechaInicio", obj.optString("fecha", "")),
                            estado = obj.optString("estado", "Abierto"),
                            evidencias = evidenciasList
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isInitialized = true
    }

    /**
     * Genera un ID secuencial consecutivo de 3 dígitos (ej: CAS-001, CAS-002, CAS-003).
     */
    fun generarSiguienteId(): String {
        val siguienteNum = listaCasos.size + 1
        return String.format("CAS-%03d", siguienteNum)
    }

    fun agregarCaso(context: Context, nuevoCaso: Caso) {
        if (!isInitialized) cargarCasos(context)
        listaCasos.add(0, nuevoCaso)
        guardarCasos(context)
    }

    fun actualizarCaso(context: Context, casoActualizado: Caso) {
        val index = listaCasos.indexOfFirst { it.id == casoActualizado.id }
        if (index != -1) {
            listaCasos[index] = casoActualizado
            guardarCasos(context)
        }
    }

    fun eliminarCaso(context: Context, casoId: String) {
        listaCasos.removeAll { it.id == casoId }
        guardarCasos(context)
    }

    fun cerrarCaso(context: Context, casoId: String) {
        val index = listaCasos.indexOfFirst { it.id == casoId }
        if (index != -1) {
            listaCasos[index] = listaCasos[index].copy(estado = "Cerrado")
            guardarCasos(context)
        }
    }

    fun agregarEvidencia(context: Context, casoId: String, evidencia: Evidencia) {
        val caso = listaCasos.find { it.id == casoId } ?: return
        caso.evidencias.add(0, evidencia)
        guardarCasos(context)
    }

    fun eliminarEvidencia(context: Context, casoId: String, evidenciaId: String) {
        val caso = listaCasos.find { it.id == casoId } ?: return
        caso.evidencias.removeAll { it.id == evidenciaId }
        guardarCasos(context)
    }

    private fun guardarCasos(context: Context) {
        val jsonArray = JSONArray()
        for (caso in listaCasos) {
            val evidenciasJsonArray = JSONArray()
            for (ev in caso.evidencias) {
                evidenciasJsonArray.put(
                    JSONObject().apply {
                        put("id", ev.id)
                        put("tipo", ev.tipo)
                        put("descripcion", ev.descripcion)
                        put("uri", ev.uri ?: "")
                        put("nombreArchivo", ev.nombreArchivo ?: "")
                        put("fecha", ev.fecha)
                    }
                )
            }

            jsonArray.put(
                JSONObject().apply {
                    put("id", caso.id)
                    put("titulo", caso.titulo)
                    put("descripcion", caso.descripcion)
                    put("fecha", caso.fecha)
                    put("fechaInicio", caso.fechaInicio)
                    put("estado", caso.estado)
                    put("evidencias", evidenciasJsonArray)
                }
            )
        }
        val prefs = context.getSharedPreferences("SmartTracePrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("casos_guardados", jsonArray.toString()).commit()
    }
}