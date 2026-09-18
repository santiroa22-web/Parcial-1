package com.udistrital.parcial_1.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

data class Caso(
    val id: String,
    var titulo: String,
    var categoria: String,
    var fecha: String,
    var ubicacion: String,
    var implicados: String,
    var prioridad: String,
    var estado: String, // "Abierto" o "Cerrado"
    var descripcion: String
)

object CasoRepository {
    val listaCasos = mutableStateListOf<Caso>()
    private var isInitialized = false

    fun cargarCasos(context: Context) {
        if (isInitialized) return
        val prefs = context.getSharedPreferences("SmartTracePrefs", Context.MODE_PRIVATE)
        val jsonStr = prefs.getString("casos_guardados", null)

        listaCasos.clear()
        if (jsonStr.isNullOrEmpty()) {
            val casosIniciales = listOf(
                Caso(
                    id = "CAS-2026-001",
                    titulo = "Hurto en la Joyería Real",
                    categoria = "Robo",
                    fecha = "15/09/2026",
                    ubicacion = "Centro Histórico, Calle 11",
                    implicados = "Sujeto no identificado (Encapuchado)",
                    prioridad = "Alta",
                    estado = "Abierto",
                    descripcion = "Ingreso nocturno forzando la cerradura posterior. Sustracción de joyas evaluadas en $50M."
                ),
                Caso(
                    id = "CAS-2026-002",
                    titulo = "Fraude Bancario Digital",
                    categoria = "Ciberdelito",
                    fecha = "10/09/2026",
                    ubicacion = "Plataforma Virtual BankLocal",
                    implicados = "Alias 'Phisher' • Víctima: Empresa S.A.S",
                    prioridad = "Media",
                    estado = "Abierto",
                    descripcion = "Desvío de fondos mediante suplantación de identidad en la pasarela de pagos."
                )
            )
            listaCasos.addAll(casosIniciales)
            guardarCasos(context)
        } else {
            try {
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    listaCasos.add(
                        Caso(
                            id = obj.optString("id", ""),
                            titulo = obj.optString("titulo", ""),
                            categoria = obj.optString("categoria", ""),
                            fecha = obj.optString("fecha", ""),
                            ubicacion = obj.optString("ubicacion", ""),
                            implicados = obj.optString("implicados", ""),
                            prioridad = obj.optString("prioridad", ""),
                            estado = obj.optString("estado", "Abierto"),
                            descripcion = obj.optString("descripcion", "")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isInitialized = true
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

    fun cerrarCaso(context: Context, casoId: String) {
        val index = listaCasos.indexOfFirst { it.id == casoId }
        if (index != -1) {
            val casoModificado = listaCasos[index].copy(estado = "Cerrado")
            listaCasos[index] = casoModificado
            guardarCasos(context)
        }
    }

    private fun guardarCasos(context: Context) {
        val jsonArray = JSONArray()
        for (caso in listaCasos) {
            val obj = JSONObject().apply {
                put("id", caso.id)
                put("titulo", caso.titulo)
                put("categoria", caso.categoria)
                put("fecha", caso.fecha)
                put("ubicacion", caso.ubicacion)
                put("implicados", caso.implicados)
                put("prioridad", caso.prioridad)
                put("estado", caso.estado)
                put("descripcion", caso.descripcion)
            }
            jsonArray.put(obj)
        }
        val prefs = context.getSharedPreferences("SmartTracePrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("casos_guardados", jsonArray.toString()).commit()
    }
}