package ar.edu.uade.capturarecibosapp.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.capturarecibosapp.data.model.Ticket
import ar.edu.uade.capturarecibosapp.domain.OcrManager
import ar.edu.uade.capturarecibosapp.events.MainNavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val ocrManager = OcrManager()

    // Eventos de navegación para desacoplar la lógica de la UI
    private val _navigationEvents = MutableSharedFlow<MainNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // Estado para controlar qué ticket se está editando/confirmando
    var ticketDetectado by mutableStateOf<Ticket?>(null)
        private set

    // Guardamos el bitmap para poder guardarlo localmente luego si se confirma
    var bitmapDetectado by mutableStateOf<Bitmap?>(null)
        private set

    // Estado para saber si estamos procesando la imagen (loading)
    var isProcessing by mutableStateOf(value = false)
        private set

    fun procesarImagen(bitmap: Bitmap) {
        isProcessing = true
        bitmapDetectado = bitmap

        ocrManager.analizarRecibo(bitmap) { ticket ->
            Log.d("ReciView", "OCR finalizado: ${ticket.establishment}")
            ticketDetectado = ticket
            isProcessing = false
            
            // Disparamos el evento de navegación
            viewModelScope.launch {
                _navigationEvents.emit(MainNavigationEvent.NavigateToConfirmation)
            }
        }
    }

    fun cancelarCaptura() {
        ticketDetectado = null
        bitmapDetectado = null
    }
}
