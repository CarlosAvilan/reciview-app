package ar.edu.uade.capturarecibosapp.domain

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import ar.edu.uade.capturarecibosapp.data.model.TicketData
import com.google.mlkit.vision.text.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class OcrManager {

    fun analizarRecibo(image: InputImage, onResult: (TicketData) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // DEBUG: Ver bloques en Logcat para entender la estructura
                visionText.textBlocks.forEachIndexed { index, block ->
                    Log.d("OCR_DEBUG", "BLOCK $index: ${block.text.replace("\n", " | ")}")
                }

                val bloques = visionText.textBlocks
                val lineas = bloques.flatMap { it.lines }.map { it.text.trim() }.filter { it.isNotBlank() }

                val comercio = detectarComercio(bloques)
                val total = detectarTotal(lineas)

                onResult(TicketData(
                    comercio = comercio,
                    total = total,
                    fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ))
            }
            .addOnFailureListener {
                onResult(TicketData("Error", 0.0, ""))
            }
    }

    private fun detectarComercio(bloques: List<Text.TextBlock>): String {
        val blacklist = listOf("CUIT", "IVA", "RESPONSABLE", "DOMICILIO", "FACTURA", "FECHA", "HORA", "ORIGINAL", "PUNTO DE VENTA")
        val lineasPosibles = bloques.take(4).flatMap { it.lines }.map { it.text.trim() }
        
        val mejor = lineasPosibles.firstOrNull { l ->
            val upper = l.uppercase()
            l.length > 3 && blacklist.none { upper.contains(it) } && l.any { it.isLetter() }
        }?.split("\n")?.firstOrNull()?.trim() ?: "Comercio"
        
        return formatearNombre(mejor)
    }

    private fun detectarTotal(lineas: List<String>): Double {
        val keywords = listOf("TOTAL", "IMPORTE", "PAGAR", "FINAL", "SUMA")
        
        // 1. ESTRATEGIA: BUSCAR CERCA DE PALABRAS CLAVE (De abajo hacia arriba)
        for (i in lineas.indices.reversed()) {
            val actual = lineas[i].uppercase()
            
            if (keywords.any { actual.contains(it) } && !actual.contains("SUBTOTAL")) {
                val candidatos = mutableListOf<Double>()
                
                // Ventana amplia: 2 arriba y hasta 10 abajo (Mamuschka necesita esto porque el valor está lejos)
                for (offset in -2..10) {
                    val idx = i + offset
                    if (idx in lineas.indices) {
                        val monto = extraerMontoSiEsValido(lineas[idx])
                        if (monto > 0) candidatos.add(monto)
                    }
                }

                // Prioridad absoluta a montos que parecen precios (tienen decimales ,00)
                // En el log de Mamuschka, 25000,00 es perfecto. 202 (de la fecha) no tiene decimales.
                val mejorCandidato = candidatos.filter { (it * 100) % 100 != 0.0 }.maxOrNull() 
                    ?: candidatos.filter { it > 1.0 }.maxOrNull()
                
                if (mejorCandidato != null) {
                    Log.d("OcrManager", "Total detectado: $mejorCandidato")
                    return mejorCandidato
                }
            }
        }

        // 2. FALLBACK: Si no hay keyword, buscamos el máximo razonable ignorando fechas
        return lineas.mapNotNull { extraerMontoSiEsValido(it) }
            .filter { it in 10.0..500000.0 }
            .maxOrNull() ?: 0.0
    }

    private fun extraerMontoSiEsValido(texto: String): Double {
        val t = texto.uppercase()
        
        // REGLAS DE EXCLUSIÓN: Ignorar fechas (tiene /), facturas (tiene -), o palabras de ruido
        if (t.contains("/") || t.contains("FECHA") || t.contains("HORA")) return 0.0
        if (t.contains("-") && t.count { it == '-' } >= 1) return 0.0

        val regex = Regex("""[\d.,]+""")
        val matches = regex.findAll(texto).toList()
        if (matches.isEmpty()) return 0.0
        
        // Tomamos el último número de la línea (el precio suele estar a la derecha)
        val match = matches.last().value.trim('.', ',')
        
        if (match.length < 2) return 0.0

        return try {
            when {
                // Caso Argentina: 25.000,00
                match.contains(",") && match.contains(".") -> match.replace(".", "").replace(",", ".").toDoubleOrNull()
                match.contains(",") -> match.replace(",", ".").toDoubleOrNull()
                match.contains(".") -> {
                    val partes = match.split(".")
                    if (partes.last().length == 3) match.replace(".", "").toDoubleOrNull()
                    else match.toDoubleOrNull()
                }
                else -> match.toDoubleOrNull()
            } ?: 0.0
        } catch (e: Exception) { 0.0 }
    }

    private fun formatearNombre(texto: String): String {
        return texto.lowercase().split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
    }
}
