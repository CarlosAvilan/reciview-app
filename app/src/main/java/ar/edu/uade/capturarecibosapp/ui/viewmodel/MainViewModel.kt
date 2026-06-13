package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.domain.OcrManager
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val ocrManager = OcrManager()

    // Estado para controlar qué ticket se está editando/confirmando
    var ticketDetectado by mutableStateOf<Ticket?>(null)
        private set

    // Estado para saber si estamos procesando la imagen (loading)
    var isProcessing by mutableStateOf(false)
        private set

    fun procesarImagen(bitmap: Bitmap) {
        isProcessing = true

        ocrManager.analizarRecibo(bitmap) { ticket ->
            Log.d("ReciView", "OCR finalizado: ${ticket.establishment}")
            ticketDetectado = ticket
            isProcessing = false
        }
    }

    fun confirmarYSubir(ticket: Ticket) {
        viewModelScope.launch {
            try {
                Log.d("ReciView", "Subiendo: ${ticket.establishment}, Total: ${ticket.amount}, Desc: ${ticket.description}")
                // Aca iría la llamada a Retrofit
                // RetrofitClient.instance.enviarTicket(ticket)

                // Limpiamos el estado después de subir con éxito
                ticketDetectado = null
            } catch (e: Exception) {
                Log.e("ReciView", "Error al enviar: ${e.message}")
            }
        }
    }

    fun cancelarCaptura() {
        ticketDetectado = null
    }
}
